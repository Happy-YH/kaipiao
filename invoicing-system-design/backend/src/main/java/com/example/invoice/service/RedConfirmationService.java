package com.example.invoice.service;

import com.example.invoice.dto.request.RedCancelRequest;
import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.Customer;
import com.example.invoice.entity.Invoice;
import com.example.invoice.entity.InvoiceDetail;
import com.example.invoice.entity.RedConfirmation;
import com.example.invoice.entity.RepaymentRecord;
import com.example.invoice.kingdee.KingdeeInvoiceRequest;
import com.example.invoice.kingdee.KingdeeInvoiceResponse;
import com.example.invoice.kingdee.KingdeeRedInvoiceRequest;
import com.example.invoice.kingdee.KingdeeService;
import com.example.invoice.mapper.CustomerMapper;
import com.example.invoice.mapper.InvoiceDetailMapper;
import com.example.invoice.mapper.InvoiceMapper;
import com.example.invoice.mapper.RedConfirmationMapper;
import com.example.invoice.mapper.RepaymentRecordMapper;
import com.example.invoice.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 红冲确认服务类，处理红字确认单的创建、确认、取消以及红冲后的发票重开逻辑
 */
@Slf4j
@Service
public class RedConfirmationService {
    @Autowired
    private RedConfirmationMapper redConfirmationMapper;
    @Autowired
    private InvoiceMapper invoiceMapper;
    @Autowired
    private InvoiceDetailMapper invoiceDetailMapper;
    @Autowired
    private RepaymentRecordMapper repaymentRecordMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private KingdeeService kingdeeService;

    @Value("${kingdee.seller-name:}")
    private String sellerName;

    @Value("${kingdee.seller-tax-no:}")
    private String sellerTaxNo;

    /**
     * 查询所有红字确认单列表
     *
     * @return 所有红字确认单列表
     */
    public Result<List<RedConfirmation>> getAllConfirmations() {
        List<RedConfirmation> confirmations = redConfirmationMapper.selectAll();
        return Result.success(confirmations);
    }

    /**
     * 根据ID查询红字确认单
     *
     * @param id 确认单ID
     * @return 确认单信息，不存在则返回错误
     */
    public Result<RedConfirmation> getConfirmationById(Long id) {
        RedConfirmation confirmation = redConfirmationMapper.selectById(id);
        if (confirmation == null) {
            return Result.error("红字确认单不存在");
        }
        return Result.success(confirmation);
    }

    /**
     * 根据确认单号查询红字确认单
     *
     * @param confirmationNo 确认单号
     * @return 确认单信息，不存在则返回错误
     */
    public Result<RedConfirmation> getConfirmationByNo(String confirmationNo) {
        RedConfirmation confirmation = redConfirmationMapper.selectByConfirmationNo(confirmationNo);
        if (confirmation == null) {
            return Result.error("红字确认单不存在");
        }
        return Result.success(confirmation);
    }

    /**
     * 根据发票ID查询关联的红字确认单列表
     *
     * @param invoiceId 发票ID
     * @return 关联的红字确认单列表
     */
    public Result<List<RedConfirmation>> getConfirmationsByInvoice(Long invoiceId) {
        List<RedConfirmation> confirmations = redConfirmationMapper.selectByInvoiceId(invoiceId);
        return Result.success(confirmations);
    }

    /**
     * 根据状态查询红字确认单列表
     *
     * @param status 确认单状态
     * @return 符合状态的确认单列表
     */
    public Result<List<RedConfirmation>> getConfirmationsByStatus(String status) {
        List<RedConfirmation> confirmations = redConfirmationMapper.selectByStatus(status);
        return Result.success(confirmations);
    }

    /**
     * 创建红冲申请，对已开具或已交付的发票发起红字确认单，支持全额和部分红冲
     *
     * @param request 红冲请求，包含原发票ID、红冲类型、红冲原因等
     * @return 创建成功的红字确认单
     */
    @Transactional
    public Result<RedConfirmation> createRedConfirmation(RedCancelRequest request) {
        Invoice originalInvoice = invoiceMapper.selectById(request.getInvoiceId());
        if (originalInvoice == null) {
            return Result.error("原发票不存在");
        }
        if (!"ISSUED".equals(originalInvoice.getStatus()) && !"DELIVERED".equals(originalInvoice.getStatus())) {
            return Result.error("原发票状态不允许红冲");
        }

        RedConfirmation confirmation = new RedConfirmation();
        confirmation.setConfirmationNo(IdGenerator.generateRedConfirmationNo());
        confirmation.setInvoiceId(request.getInvoiceId());
        confirmation.setCustomerId(originalInvoice.getCustomerId());
        confirmation.setCancelType(request.getCancelType());
        confirmation.setCancelReason(request.getCancelReason());

        if ("FULL".equals(request.getCancelType())) {
            confirmation.setCancelAmount(originalInvoice.getTotalAmount());
        } else {
            confirmation.setCancelAmount(request.getCancelAmount());
        }

        confirmation.setStatus("PENDING_CONFIRM");
        confirmation.setInitiatorId(1L);
        confirmation.setInitiatorName("系统管理员");
        confirmation.setConfirmerId(originalInvoice.getCustomerId());
        confirmation.setInitiateTime(new Date());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 72);
        confirmation.setExpireTime(cal.getTime());
        confirmation.setRemark(request.getRemark());
        confirmation.setRedRepaymentRecordIds(request.getRedRepaymentRecordIds());

        redConfirmationMapper.insert(confirmation);

        return Result.success("红字确认单已发起", confirmation);
    }

    /**
     * 确认红冲，将确认单状态变更为已确认，原发票状态变更为已红冲，并自动生成红字发票；
     * 若为部分红冲，则按还款记录逐笔重开蓝字发票
     *
     * @param id 确认单ID
     * @return 红冲确认结果
     */
    @Transactional
    public Result<RedConfirmation> confirmRed(Long id) {
        RedConfirmation confirmation = redConfirmationMapper.selectById(id);
        if (confirmation == null) {
            return Result.error("红字确认单不存在");
        }
        if (!"PENDING_CONFIRM".equals(confirmation.getStatus())) {
            return Result.error("确认单状态不允许确认");
        }
        // 超时校验
        if (confirmation.getExpireTime() != null && confirmation.getExpireTime().before(new Date())) {
            confirmation.setStatus("EXPIRED");
            confirmation.setRemark("系统自动处理：确认超时未响应，已自动作废");
            redConfirmationMapper.update(confirmation);
            return Result.error("红字确认单已超时，无法确认");
        }

        confirmation.setStatus("CONFIRMED");
        confirmation.setConfirmTime(new Date());
        redConfirmationMapper.update(confirmation);

        Invoice originalInvoice = invoiceMapper.selectById(confirmation.getInvoiceId());
        if (originalInvoice != null) {
            originalInvoice.setStatus("RED_CANCELLED");
            invoiceMapper.update(originalInvoice);
        }

        createRedInvoice(confirmation);

        if ("PARTIAL".equals(confirmation.getCancelType())) {
            reissueBlueInvoice(confirmation);
        }

        return Result.success("红冲完成", confirmation);
    }

    /**
     * 生成红字发票，调用金蝶红冲接口开具红字发票，并复制原发票明细（金额取反）
     *
     * @param confirmation 红字确认单
     */
    private void createRedInvoice(RedConfirmation confirmation) {
        Invoice originalInvoice = invoiceMapper.selectById(confirmation.getInvoiceId());

        KingdeeRedInvoiceRequest redRequest = buildKingdeeRedInvoiceRequest(confirmation, originalInvoice);
        KingdeeInvoiceResponse kingdeeResponse = kingdeeService.issueRedInvoice(redRequest);

        log.info("金蝶红冲开票成功，红冲发票号：{}，原发票号：{}", kingdeeResponse.getInvoiceNo(), originalInvoice.getInvoiceNo());

        Invoice redInvoice = new Invoice();
        redInvoice.setInvoiceNo(IdGenerator.generateInvoiceNo());
        redInvoice.setInvoiceType(originalInvoice.getInvoiceType());
        redInvoice.setInvoiceKind("RED");
        redInvoice.setCustomerId(originalInvoice.getCustomerId());
        // 红字发票金额始终等于原发票全额（取反），保证对冲关系
        redInvoice.setTotalAmount(originalInvoice.getTotalAmount().negate());
        redInvoice.setTaxAmount(originalInvoice.getTaxAmount().negate());
        redInvoice.setTaxableAmount(originalInvoice.getTaxableAmount().negate());
        redInvoice.setStatus("ISSUED");
        redInvoice.setRedConfirmationNo(confirmation.getConfirmationNo());
        redInvoice.setOriginalInvoiceId(confirmation.getInvoiceId());
        redInvoice.setIssueDate(kingdeeResponse.getIssueDate() != null ? kingdeeResponse.getIssueDate() : new Date());
        redInvoice.setRemark("红冲原因：" + confirmation.getCancelReason());
        redInvoice.setExternalInvoiceId(kingdeeResponse.getInvoiceId());
        redInvoice.setExternalInvoiceNo(kingdeeResponse.getInvoiceNo());
        redInvoice.setExternalInvoiceCode(kingdeeResponse.getInvoiceCode());
        redInvoice.setPdfUrl(kingdeeResponse.getPdfUrl());
        redInvoice.setQrCodeUrl(kingdeeResponse.getQrCodeUrl());
        redInvoice.setCheckCode(kingdeeResponse.getCheckCode());
        redInvoice.setMachineNo(kingdeeResponse.getMachineNo());

        invoiceMapper.insert(redInvoice);

        List<InvoiceDetail> originalDetails = invoiceDetailMapper.selectByInvoiceId(confirmation.getInvoiceId());
        for (InvoiceDetail originalDetail : originalDetails) {
            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoiceId(redInvoice.getId());
            detail.setRepaymentRecordId(originalDetail.getRepaymentRecordId());
            detail.setContractId(originalDetail.getContractId());
            detail.setContractNo(originalDetail.getContractNo());
            detail.setFeeType(originalDetail.getFeeType());
            detail.setTaxClassCode(originalDetail.getTaxClassCode());
            detail.setTaxClassName(originalDetail.getTaxClassName());
            detail.setInterestStartDate(originalDetail.getInterestStartDate());
            detail.setInterestEndDate(originalDetail.getInterestEndDate());
            detail.setPrincipalAmount(originalDetail.getPrincipalAmount());
            detail.setAnnualRate(originalDetail.getAnnualRate());
            detail.setInterestAmount(originalDetail.getInterestAmount() != null ? originalDetail.getInterestAmount().negate() : null);
            detail.setTaxRate(originalDetail.getTaxRate());
            detail.setTaxAmount(originalDetail.getTaxAmount() != null ? originalDetail.getTaxAmount().negate() : null);
            detail.setTotalAmount(originalDetail.getTotalAmount().negate());
            invoiceDetailMapper.insert(detail);
        }
    }

    /**
     * 构建金蝶红冲请求对象，组装原发票信息、红冲原因及明细行（金额取反）
     *
     * @param confirmation 红字确认单
     * @param originalInvoice 原发票实体
     * @return 金蝶红冲请求对象
     */
    private KingdeeRedInvoiceRequest buildKingdeeRedInvoiceRequest(RedConfirmation confirmation, Invoice originalInvoice) {
        KingdeeRedInvoiceRequest request = new KingdeeRedInvoiceRequest();
        request.setOriginalInvoiceNo(originalInvoice.getExternalInvoiceNo() != null ? originalInvoice.getExternalInvoiceNo() : originalInvoice.getInvoiceNo());
        request.setOriginalInvoiceCode(originalInvoice.getExternalInvoiceCode());
        request.setRedReason(confirmation.getCancelReason());
        request.setRedReasonCode("WRONG_INFO");
        request.setRedConfirmationNo(confirmation.getConfirmationNo());
        request.setRedAmount(confirmation.getCancelAmount());
        request.setRedTaxAmount(originalInvoice.getTaxAmount());
        request.setRemark(confirmation.getRemark());

        List<InvoiceDetail> originalDetails = invoiceDetailMapper.selectByInvoiceId(originalInvoice.getId());
        List<KingdeeInvoiceRequest.KingdeeInvoiceDetailRequest> detailRequests = new ArrayList<>();
        for (InvoiceDetail originalDetail : originalDetails) {
            KingdeeInvoiceRequest.KingdeeInvoiceDetailRequest detailReq = new KingdeeInvoiceRequest.KingdeeInvoiceDetailRequest();
            detailReq.setTaxClassCode(originalDetail.getTaxClassCode());
            detailReq.setTaxClassName(originalDetail.getTaxClassName());
            detailReq.setGoodsName(originalDetail.getTaxClassName() != null ? originalDetail.getTaxClassName() : "贷款服务");
            detailReq.setAmount(originalDetail.getTotalAmount().negate());
            detailReq.setTaxRate(originalDetail.getTaxRate());
            detailReq.setTaxAmount(originalDetail.getTaxAmount().negate());
            detailReq.setFeeType(originalDetail.getFeeType());
            detailRequests.add(detailReq);
        }
        request.setDetails(detailRequests);

        return request;
    }

    /**
     * 重开蓝字发票，部分红冲时按还款记录逐笔重开未被红冲的蓝字发票，并调用金蝶接口完成开票，
     * 同时更新还款记录开票状态。
     * 支持两种部分红冲场景：
     *   1) 1:1 场景（原发票仅关联一笔还款记录）：全额红冲原发票后，按差额(原发票金额-实际冲销金额)重开一张蓝票
     *   2) 1:N 场景（原发票关联多笔还款记录）：对未被红冲的还款记录分别重开蓝票
     *
     * @param confirmation 红字确认单
     */
    private void reissueBlueInvoice(RedConfirmation confirmation) {
        Invoice originalInvoice = invoiceMapper.selectById(confirmation.getInvoiceId());
        List<InvoiceDetail> originalDetails = invoiceDetailMapper.selectByInvoiceId(confirmation.getInvoiceId());

        // 确定被红冲的还款记录ID集合
        Set<Long> redRecordIds = new HashSet<>();
        if (confirmation.getRedRepaymentRecordIds() != null && !confirmation.getRedRepaymentRecordIds().isEmpty()) {
            for (String idStr : confirmation.getRedRepaymentRecordIds().split(",")) {
                try {
                    redRecordIds.add(Long.parseLong(idStr.trim()));
                } catch (NumberFormatException ignore) {
                }
            }
        }

        // 按还款记录分组原发票明细
        Map<Long, List<InvoiceDetail>> detailsByRecord = new LinkedHashMap<>();
        for (InvoiceDetail detail : originalDetails) {
            Long recordId = detail.getRepaymentRecordId();
            if (recordId != null) {
                detailsByRecord.computeIfAbsent(recordId, k -> new ArrayList<>()).add(detail);
            }
        }

        // 判断 1:1 场景：原发票仅关联一笔还款记录，且该记录被红冲
        boolean isOneToOne = detailsByRecord.size() == 1
                && redRecordIds.equals(detailsByRecord.keySet());

        if (isOneToOne) {
            // 1:1 场景：按差额重开一张蓝票 (差额 = 原发票金额 - 实际冲销金额)
            Long onlyRecordId = detailsByRecord.keySet().iterator().next();
            List<InvoiceDetail> onlyDetails = detailsByRecord.get(onlyRecordId);
            BigDecimal originalAmount = originalInvoice.getTotalAmount();
            BigDecimal cancelAmount = confirmation.getCancelAmount() != null ? confirmation.getCancelAmount() : BigDecimal.ZERO;
            BigDecimal reissueAmount = originalAmount.subtract(cancelAmount);
            if (reissueAmount.compareTo(BigDecimal.ZERO) <= 0) {
                // 全额冲销，无需重开
                RepaymentRecord record = repaymentRecordMapper.selectById(onlyRecordId);
                if (record != null) {
                    record.setInvoicedAmount(BigDecimal.ZERO);
                    record.setRemainingAmount(record.getTaxableAmount());
                    record.setInvoiceStatus("UNINVOICED");
                    repaymentRecordMapper.update(record);
                }
                return;
            }
            // 按比例分摊税额
            BigDecimal ratio = originalAmount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                    : reissueAmount.divide(originalAmount, 6, BigDecimal.ROUND_HALF_UP);
            BigDecimal reissueTax = originalInvoice.getTaxAmount().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
            BigDecimal reissueTaxable = reissueAmount.subtract(reissueTax);

            Invoice blueInvoice = new Invoice();
            blueInvoice.setInvoiceNo(IdGenerator.generateInvoiceNo());
            blueInvoice.setInvoiceType(originalInvoice.getInvoiceType());
            blueInvoice.setInvoiceKind("BLUE");
            blueInvoice.setCustomerId(originalInvoice.getCustomerId());
            blueInvoice.setTotalAmount(reissueAmount);
            blueInvoice.setTaxAmount(reissueTax);
            blueInvoice.setTaxableAmount(reissueTaxable);
            blueInvoice.setStatus("ISSUING");
            blueInvoice.setOriginalInvoiceId(confirmation.getInvoiceId());
            blueInvoice.setRemark("1:1部分红冲后差额重开，红冲单号：" + confirmation.getConfirmationNo());

            invoiceMapper.insert(blueInvoice);

            // 复制明细（按比例缩减金额）
            for (InvoiceDetail originalDetail : onlyDetails) {
                InvoiceDetail detail = new InvoiceDetail();
                detail.setInvoiceId(blueInvoice.getId());
                detail.setRepaymentRecordId(originalDetail.getRepaymentRecordId());
                detail.setContractId(originalDetail.getContractId());
                detail.setContractNo(originalDetail.getContractNo());
                detail.setFeeType(originalDetail.getFeeType());
                detail.setTaxClassCode(originalDetail.getTaxClassCode());
                detail.setTaxClassName(originalDetail.getTaxClassName());
                detail.setInterestStartDate(originalDetail.getInterestStartDate());
                detail.setInterestEndDate(originalDetail.getInterestEndDate());
                detail.setPrincipalAmount(originalDetail.getPrincipalAmount());
                detail.setAnnualRate(originalDetail.getAnnualRate());
                BigDecimal detailTotal = originalDetail.getTotalAmount().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal detailTax = originalDetail.getTaxAmount() != null
                        ? originalDetail.getTaxAmount().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;
                BigDecimal detailInterest = originalDetail.getInterestAmount() != null
                        ? originalDetail.getInterestAmount().multiply(ratio).setScale(2, BigDecimal.ROUND_HALF_UP) : null;
                detail.setInterestAmount(detailInterest);
                detail.setTaxRate(originalDetail.getTaxRate());
                detail.setTaxAmount(detailTax);
                detail.setTotalAmount(detailTotal);
                invoiceDetailMapper.insert(detail);
            }

            // 调用金蝶接口
            try {
                KingdeeInvoiceRequest kingdeeRequest = buildKingdeeReissueRequest(blueInvoice);
                KingdeeInvoiceResponse kingdeeResponse = kingdeeService.issueBlueInvoice(kingdeeRequest);
                log.info("金蝶1:1差额重开蓝字发票成功，发票号：{}, 关联还款记录：{}", kingdeeResponse.getInvoiceNo(), onlyRecordId);
                blueInvoice.setStatus("ISSUED");
                blueInvoice.setIssueDate(kingdeeResponse.getIssueDate() != null ? kingdeeResponse.getIssueDate() : new Date());
                blueInvoice.setExternalInvoiceId(kingdeeResponse.getInvoiceId());
                blueInvoice.setExternalInvoiceNo(kingdeeResponse.getInvoiceNo());
                blueInvoice.setExternalInvoiceCode(kingdeeResponse.getInvoiceCode());
                blueInvoice.setPdfUrl(kingdeeResponse.getPdfUrl());
                blueInvoice.setQrCodeUrl(kingdeeResponse.getQrCodeUrl());
                blueInvoice.setCheckCode(kingdeeResponse.getCheckCode());
                blueInvoice.setMachineNo(kingdeeResponse.getMachineNo());
            } catch (Exception e) {
                log.error("1:1差额重开蓝票失败", e);
                blueInvoice.setStatus("FAILED");
                blueInvoice.setFailReason(e.getMessage());
            }
            invoiceMapper.update(blueInvoice);

            // 更新还款记录为部分开票
            RepaymentRecord record = repaymentRecordMapper.selectById(onlyRecordId);
            if (record != null) {
                record.setInvoicedAmount(reissueAmount);
                if (record.getTaxableAmount() != null) {
                    record.setRemainingAmount(record.getTaxableAmount().subtract(reissueAmount));
                }
                record.setInvoiceStatus("INVOICED");
                repaymentRecordMapper.update(record);
            }
            return;
        }

        // 1:N 场景：为每笔未红冲的还款记录分别重开蓝票
        Map<Long, List<InvoiceDetail>> remainingByRecord = new LinkedHashMap<>();
        for (Map.Entry<Long, List<InvoiceDetail>> entry : detailsByRecord.entrySet()) {
            if (!redRecordIds.contains(entry.getKey())) {
                remainingByRecord.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<Long, List<InvoiceDetail>> entry : remainingByRecord.entrySet()) {
            Long recordId = entry.getKey();
            List<InvoiceDetail> details = entry.getValue();

            BigDecimal reissueAmount = details.stream()
                .map(InvoiceDetail::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal reissueTax = details.stream()
                .map(d -> d.getTaxAmount() != null ? d.getTaxAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            // taxableAmount = 总额 - 税额
            BigDecimal reissueTaxable = reissueAmount.subtract(reissueTax);

            Invoice blueInvoice = new Invoice();
            blueInvoice.setInvoiceNo(IdGenerator.generateInvoiceNo());
            blueInvoice.setInvoiceType(originalInvoice.getInvoiceType());
            blueInvoice.setInvoiceKind("BLUE");
            blueInvoice.setCustomerId(originalInvoice.getCustomerId());
            blueInvoice.setTotalAmount(reissueAmount);
            blueInvoice.setTaxAmount(reissueTax);
            blueInvoice.setTaxableAmount(reissueTaxable);
            blueInvoice.setStatus("ISSUING");
            blueInvoice.setOriginalInvoiceId(confirmation.getInvoiceId());
            blueInvoice.setRemark("1:N部分红冲后重开，红冲单号：" + confirmation.getConfirmationNo());

            invoiceMapper.insert(blueInvoice);

            for (InvoiceDetail originalDetail : details) {
                InvoiceDetail detail = new InvoiceDetail();
                detail.setInvoiceId(blueInvoice.getId());
                detail.setRepaymentRecordId(originalDetail.getRepaymentRecordId());
                detail.setContractId(originalDetail.getContractId());
                detail.setContractNo(originalDetail.getContractNo());
                detail.setFeeType(originalDetail.getFeeType());
                detail.setTaxClassCode(originalDetail.getTaxClassCode());
                detail.setTaxClassName(originalDetail.getTaxClassName());
                detail.setInterestStartDate(originalDetail.getInterestStartDate());
                detail.setInterestEndDate(originalDetail.getInterestEndDate());
                detail.setPrincipalAmount(originalDetail.getPrincipalAmount());
                detail.setAnnualRate(originalDetail.getAnnualRate());
                detail.setInterestAmount(originalDetail.getInterestAmount());
                detail.setTaxRate(originalDetail.getTaxRate());
                detail.setTaxAmount(originalDetail.getTaxAmount());
                detail.setTotalAmount(originalDetail.getTotalAmount());
                invoiceDetailMapper.insert(detail);
            }

            // 调用金蝶接口
            try {
                KingdeeInvoiceRequest kingdeeRequest = buildKingdeeReissueRequest(blueInvoice);
                KingdeeInvoiceResponse kingdeeResponse = kingdeeService.issueBlueInvoice(kingdeeRequest);
                log.info("金蝶重开蓝字发票成功，发票号：{}, 关联还款记录：{}", kingdeeResponse.getInvoiceNo(), recordId);
                blueInvoice.setStatus("ISSUED");
                blueInvoice.setIssueDate(kingdeeResponse.getIssueDate() != null ? kingdeeResponse.getIssueDate() : new Date());
                blueInvoice.setExternalInvoiceId(kingdeeResponse.getInvoiceId());
                blueInvoice.setExternalInvoiceNo(kingdeeResponse.getInvoiceNo());
                blueInvoice.setExternalInvoiceCode(kingdeeResponse.getInvoiceCode());
                blueInvoice.setPdfUrl(kingdeeResponse.getPdfUrl());
                blueInvoice.setQrCodeUrl(kingdeeResponse.getQrCodeUrl());
                blueInvoice.setCheckCode(kingdeeResponse.getCheckCode());
                blueInvoice.setMachineNo(kingdeeResponse.getMachineNo());
            } catch (Exception e) {
                log.error("1:N重开蓝票失败, recordId={}", recordId, e);
                blueInvoice.setStatus("FAILED");
                blueInvoice.setFailReason(e.getMessage());
            }
            invoiceMapper.update(blueInvoice);

            // 更新还款记录状态
            RepaymentRecord record = repaymentRecordMapper.selectById(recordId);
            if (record != null) {
                record.setInvoiceStatus("INVOICED");
                repaymentRecordMapper.update(record);
            }
        }

        // 更新被红冲的还款记录状态为未开票
        for (Long redRecordId : redRecordIds) {
            RepaymentRecord record = repaymentRecordMapper.selectById(redRecordId);
            if (record != null) {
                record.setInvoicedAmount(BigDecimal.ZERO);
                record.setRemainingAmount(record.getTaxableAmount());
                record.setInvoiceStatus("UNINVOICED");
                repaymentRecordMapper.update(record);
            }
        }
    }

    /**
     * 构建金蝶重开请求对象，组装销方信息、购方信息及明细行
     *
     * @param invoice 重开的蓝字发票实体
     * @return 金蝶开票请求对象
     */
    private KingdeeInvoiceRequest buildKingdeeReissueRequest(Invoice invoice) {
        KingdeeInvoiceRequest request = new KingdeeInvoiceRequest();
        request.setInvoiceType(invoice.getInvoiceType());
        request.setInvoiceKind(invoice.getInvoiceKind());
        request.setRemark(invoice.getRemark());
        request.setIssueDate(invoice.getIssueDate());
        request.setSellerName(sellerName);
        request.setSellerTaxNo(sellerTaxNo);

        Customer customer = customerMapper.selectById(invoice.getCustomerId());
        if (customer != null) {
            request.setCustomerName(customer.getCustomerName());
            request.setCustomerTaxNo(customer.getTaxId());
            request.setCustomerAddress(customer.getAddress());
            request.setCustomerPhone(customer.getPhone());
            request.setCustomerBankName(customer.getBankName());
            request.setCustomerBankAccount(customer.getBankAccount());
        }

        List<InvoiceDetail> details = invoiceDetailMapper.selectByInvoiceId(invoice.getId());
        List<KingdeeInvoiceRequest.KingdeeInvoiceDetailRequest> detailRequests = new ArrayList<>();
        for (InvoiceDetail detail : details) {
            KingdeeInvoiceRequest.KingdeeInvoiceDetailRequest detailReq = new KingdeeInvoiceRequest.KingdeeInvoiceDetailRequest();
            detailReq.setTaxClassCode(detail.getTaxClassCode());
            detailReq.setTaxClassName(detail.getTaxClassName());
            detailReq.setGoodsName(detail.getTaxClassName() != null ? detail.getTaxClassName() : "贷款服务");
            detailReq.setSpecification("");
            detailReq.setUnit("项");
            detailReq.setQuantity(BigDecimal.ONE);
            detailReq.setUnitPrice(detail.getTotalAmount());
            detailReq.setAmount(detail.getTotalAmount());
            detailReq.setTaxRate(detail.getTaxRate());
            detailReq.setTaxAmount(detail.getTaxAmount());
            detailReq.setFeeType(detail.getFeeType());
            detailRequests.add(detailReq);
        }
        request.setDetails(detailRequests);

        return request;
    }

    /**
     * 取消红冲，将待确认的确认单状态变更为已取消
     *
     * @param id 确认单ID
     * @return 取消结果
     */
    public Result<RedConfirmation> cancelRed(Long id) {
        RedConfirmation confirmation = redConfirmationMapper.selectById(id);
        if (confirmation == null) {
            return Result.error("红字确认单不存在");
        }
        if (!"PENDING_CONFIRM".equals(confirmation.getStatus())) {
            return Result.error("确认单状态不允许取消");
        }
        confirmation.setStatus("CANCELLED");
        redConfirmationMapper.update(confirmation);
        return Result.success("红冲已取消", confirmation);
    }

    /**
     * 拒绝红冲，受票方驳回红字确认单
     *
     * @param id 确认单ID
     * @param rejectReason 拒绝原因
     * @return 拒绝结果
     */
    public Result<RedConfirmation> rejectRed(Long id, String rejectReason) {
        RedConfirmation confirmation = redConfirmationMapper.selectById(id);
        if (confirmation == null) {
            return Result.error("红字确认单不存在");
        }
        if (!"PENDING_CONFIRM".equals(confirmation.getStatus())) {
            return Result.error("确认单状态不允许拒绝");
        }
        confirmation.setStatus("REJECTED");
        confirmation.setConfirmTime(new Date());
        confirmation.setRemark("拒绝原因：" + (rejectReason != null ? rejectReason : "受票方拒绝"));
        redConfirmationMapper.update(confirmation);
        return Result.success("红冲已被拒绝", confirmation);
    }

    /**
     * 批量处理超时的红字确认单，将状态置为已超时并取消
     *
     * @return 处理的记录数
     */
    @Transactional
    public int processExpiredConfirmations() {
        List<RedConfirmation> pendingList = redConfirmationMapper.selectByStatus("PENDING_CONFIRM");
        int count = 0;
        Date now = new Date();
        for (RedConfirmation conf : pendingList) {
            if (conf.getExpireTime() != null && conf.getExpireTime().before(now)) {
                conf.setStatus("EXPIRED");
                conf.setRemark("系统自动处理：确认超时未响应，已自动作废");
                redConfirmationMapper.update(conf);
                count++;
            }
        }
        if (count > 0) {
            log.info("自动处理超时红字确认单，共处理 {} 条", count);
        }
        return count;
    }
}