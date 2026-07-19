package com.example.invoice.service;

import com.example.invoice.dto.request.InvoiceCreateRequest;
import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.Customer;
import com.example.invoice.entity.Invoice;
import com.example.invoice.entity.InvoiceDetail;
import com.example.invoice.entity.RepaymentRecord;
import com.example.invoice.entity.TaxClassification;
import com.example.invoice.kingdee.KingdeeInvoiceRequest;
import com.example.invoice.kingdee.KingdeeInvoiceResponse;
import com.example.invoice.kingdee.KingdeeService;
import com.example.invoice.mapper.CustomerMapper;
import com.example.invoice.mapper.InvoiceDetailMapper;
import com.example.invoice.mapper.InvoiceMapper;
import com.example.invoice.mapper.RepaymentRecordMapper;
import com.example.invoice.mapper.TaxClassificationMapper;
import com.example.invoice.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发票服务类，处理发票的创建、开具、交付、删除等核心业务逻辑
 */
@Slf4j
@Service
public class InvoiceService {
    @Autowired
    private InvoiceMapper invoiceMapper;
    @Autowired
    private InvoiceDetailMapper invoiceDetailMapper;
    @Autowired
    private TaxClassificationMapper taxClassificationMapper;
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
     * 查询所有发票列表
     *
     * @return 所有发票列表
     */
    public Result<List<Invoice>> getAllInvoices() {
        List<Invoice> invoices = invoiceMapper.selectAll();
        return Result.success(invoices);
    }

    /**
     * 根据ID查询发票
     *
     * @param id 发票ID
     * @return 发票信息，不存在则返回错误
     */
    public Result<Invoice> getInvoiceById(Long id) {
        Invoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            return Result.error("发票不存在");
        }
        return Result.success(invoice);
    }

    /**
     * 根据发票号查询发票
     *
     * @param invoiceNo 发票号码
     * @return 发票信息，不存在则返回错误
     */
    public Result<Invoice> getInvoiceByNo(String invoiceNo) {
        Invoice invoice = invoiceMapper.selectByInvoiceNo(invoiceNo);
        if (invoice == null) {
            return Result.error("发票不存在");
        }
        return Result.success(invoice);
    }

    /**
     * 根据客户ID查询发票列表
     *
     * @param customerId 客户ID
     * @return 该客户关联的发票列表
     */
    public Result<List<Invoice>> getInvoicesByCustomer(Long customerId) {
        List<Invoice> invoices = invoiceMapper.selectByCustomerId(customerId);
        return Result.success(invoices);
    }

    /**
     * 根据状态查询发票列表
     *
     * @param status 发票状态
     * @return 符合状态的发票列表
     */
    public Result<List<Invoice>> getInvoicesByStatus(String status) {
        List<Invoice> invoices = invoiceMapper.selectByStatus(status);
        return Result.success(invoices);
    }

    /**
     * 根据发票种类查询发票列表
     *
     * @param kind 发票种类（BLUE/RED）
     * @return 符合种类的发票列表
     */
    public Result<List<Invoice>> getInvoicesByKind(String kind) {
        List<Invoice> invoices = invoiceMapper.selectByInvoiceKind(kind);
        return Result.success(invoices);
    }

    /**
     * 多维度组合查询发票列表，支持客户ID、状态、种类、合同号、发票类型、开票日期范围
     */
    public Result<List<Invoice>> queryInvoices(Long customerId, String status, String kind,
                                               String contractNo, String invoiceType,
                                               String startDate, String endDate,
                                               int page, int size) {
        // 简单分页
        int offset = Math.max(0, (page - 1) * size);
        int limit = Math.max(1, Math.min(size, 1000));
        List<Invoice> invoices = invoiceMapper.selectByCondition(status, startDate, endDate, offset, limit, contractNo, invoiceType);
        // 客户ID和发票种类在内存中过滤（selectByCondition 未直接支持）
        if (customerId != null) {
            invoices.removeIf(inv -> !customerId.equals(inv.getCustomerId()));
        }
        if (kind != null && !kind.isEmpty()) {
            invoices.removeIf(inv -> !kind.equalsIgnoreCase(inv.getInvoiceKind()));
        }
        return Result.success(invoices);
    }

    /**
     * 创建发票，根据请求信息生成草稿发票及明细，自动匹配税目分类
     *
     * @param request 发票创建请求，包含客户ID、发票类型、明细等信息
     * @return 创建成功的发票信息
     */
    @Transactional
    public Result<Invoice> createInvoice(InvoiceCreateRequest request) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNo(IdGenerator.generateInvoiceNo());
        invoice.setInvoiceType(request.getInvoiceType());
        invoice.setInvoiceKind("BLUE");
        invoice.setCustomerId(request.getCustomerId());
        invoice.setStatus("DRAFT");
        invoice.setIssueDate(request.getIssueDate() != null ? request.getIssueDate() : new Date());
        invoice.setRemark(request.getRemark());

        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal taxableAmount = BigDecimal.ZERO;

        for (InvoiceCreateRequest.InvoiceDetailRequest detailReq : request.getDetails()) {
            BigDecimal detailTotal = detailReq.getTotalAmount() != null ? detailReq.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal interestAmount = detailReq.getInterestAmount() != null ? detailReq.getInterestAmount() : BigDecimal.ZERO;
            BigDecimal taxRate = detailReq.getTaxRate() != null ? detailReq.getTaxRate() : new BigDecimal("6.00");
            // 税额 = 利息金额 × 税率 / 100，使用 2 位小数四舍五入
            BigDecimal detailTax = interestAmount.multiply(taxRate)
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            BigDecimal detailTaxable = detailTotal.subtract(detailTax);

            totalAmount = totalAmount.add(detailTotal);
            taxAmount = taxAmount.add(detailTax);
            taxableAmount = taxableAmount.add(detailTaxable);
        }

        invoice.setTotalAmount(totalAmount);
        invoice.setTaxAmount(taxAmount);
        invoice.setTaxableAmount(taxableAmount);

        invoiceMapper.insert(invoice);

        for (InvoiceCreateRequest.InvoiceDetailRequest detailReq : request.getDetails()) {
            InvoiceDetail detail = new InvoiceDetail();
            detail.setInvoiceId(invoice.getId());
            detail.setRepaymentRecordId(detailReq.getRepaymentRecordId());
            detail.setContractId(detailReq.getContractId());
            detail.setContractNo(detailReq.getContractNo());
            detail.setFeeType(detailReq.getFeeType());
            detail.setInterestStartDate(detailReq.getInterestStartDate());
            detail.setInterestEndDate(detailReq.getInterestEndDate());
            detail.setPrincipalAmount(detailReq.getPrincipalAmount());
            detail.setAnnualRate(detailReq.getAnnualRate());
            detail.setInterestAmount(detailReq.getInterestAmount());

            TaxClassification taxClass = taxClassificationMapper.selectByFeeType(detailReq.getFeeType());
            if (taxClass != null) {
                detail.setTaxClassCode(taxClass.getTaxCode());
                detail.setTaxClassName(taxClass.getTaxName());
                detail.setTaxRate(taxClass.getTaxRate());
            } else {
                detail.setTaxRate(detailReq.getTaxRate() != null ? detailReq.getTaxRate() : new BigDecimal("6.00"));
            }

            BigDecimal interestAmount = detail.getInterestAmount() != null ? detail.getInterestAmount() : BigDecimal.ZERO;
            BigDecimal detailTax = interestAmount.multiply(detail.getTaxRate())
                    .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            detail.setTaxAmount(detailTax);
            detail.setTotalAmount(detailReq.getTotalAmount());

            invoiceDetailMapper.insert(detail);
        }

        return Result.success("创建成功", invoice);
    }

    /**
     * 开具发票，将草稿或已审核的发票调用金蝶接口进行开票，成功后更新发票状态及还款记录开票状态
     *
     * @param id 发票ID
     * @return 开具成功或失败的发票信息
     */
    @Transactional
    public Result<Invoice> issueInvoice(Long id) {
        Invoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            return Result.error("发票不存在");
        }
        if (!"DRAFT".equals(invoice.getStatus()) && !"REVIEWED".equals(invoice.getStatus())) {
            return Result.error("发票状态不允许开具");
        }

        invoice.setStatus("ISSUING");
        invoiceMapper.update(invoice);

        try {
            KingdeeInvoiceRequest kingdeeRequest = buildKingdeeInvoiceRequest(invoice);
            KingdeeInvoiceResponse kingdeeResponse = kingdeeService.issueBlueInvoice(kingdeeRequest);

            log.info("金蝶开票成功，发票号：{}，金蝶发票ID：{}", kingdeeResponse.getInvoiceNo(), kingdeeResponse.getInvoiceId());

            invoice.setStatus("ISSUED");
            invoice.setIssueDate(kingdeeResponse.getIssueDate() != null ? kingdeeResponse.getIssueDate() : new Date());
            invoice.setExternalInvoiceNo(kingdeeResponse.getInvoiceNo());
            invoice.setExternalInvoiceCode(kingdeeResponse.getInvoiceCode());
            invoice.setExternalInvoiceId(kingdeeResponse.getInvoiceId());
            invoice.setPdfUrl(kingdeeResponse.getPdfUrl());
            invoice.setQrCodeUrl(kingdeeResponse.getQrCodeUrl());
            invoice.setCheckCode(kingdeeResponse.getCheckCode());
            invoice.setMachineNo(kingdeeResponse.getMachineNo());
            invoiceMapper.update(invoice);

            updateRepaymentRecordInvoicedStatus(id);

            return Result.success("开具成功", invoice);
        } catch (Exception e) {
            log.error("金蝶开票失败", e);
            invoice.setStatus("FAILED");
            invoice.setFailReason(e.getMessage());
            invoiceMapper.update(invoice);
            return Result.error("开具失败：" + e.getMessage());
        }
    }

    /**
     * 构建金蝶开票请求对象，组装销方信息、购方信息及明细行
     *
     * @param invoice 发票实体
     * @return 金蝶开票请求对象
     */
    private KingdeeInvoiceRequest buildKingdeeInvoiceRequest(Invoice invoice) {
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
     * 更新还款记录的开票状态，根据发票明细中关联的还款记录，累计已开票金额并更新开票状态
     *
     * @param invoiceId 发票ID
     */
    private void updateRepaymentRecordInvoicedStatus(Long invoiceId) {
        List<InvoiceDetail> details = invoiceDetailMapper.selectByInvoiceId(invoiceId);
        for (InvoiceDetail detail : details) {
            if (detail.getRepaymentRecordId() != null) {
                RepaymentRecord record = repaymentRecordMapper.selectById(detail.getRepaymentRecordId());
                if (record != null) {
                    BigDecimal newInvoiced = record.getInvoicedAmount() != null ?
                            record.getInvoicedAmount().add(detail.getTotalAmount()) : detail.getTotalAmount();
                    BigDecimal totalTaxable = record.getTaxableAmount() != null ? record.getTaxableAmount() : BigDecimal.ZERO;
                    BigDecimal remaining = totalTaxable.subtract(newInvoiced);
                    record.setInvoicedAmount(newInvoiced);
                    record.setRemainingAmount(remaining);
                    String status;
                    if (remaining.compareTo(BigDecimal.ZERO) == 0) {
                        status = "INVOICED";
                    } else if (newInvoiced.compareTo(BigDecimal.ZERO) > 0) {
                        status = "PARTIAL_INVOICED";
                    } else {
                        status = "UNINVOICED";
                    }
                    record.setInvoiceStatus(status);
                    repaymentRecordMapper.update(record);
                }
            }
        }
    }

    /**
     * 提交审核，将草稿状态的发票提交到待审核
     *
     * @param id 发票ID
     * @return 提交结果
     */
    public Result<Invoice> submitForReview(Long id) {
        Invoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            return Result.error("发票不存在");
        }
        if (!"DRAFT".equals(invoice.getStatus())) {
            return Result.error("仅草稿状态可以提交审核");
        }
        invoice.setStatus("PENDING_REVIEW");
        invoiceMapper.update(invoice);
        return Result.success("已提交审核", invoice);
    }

    /**
     * 审核发票，审核通过或驳回
     *
     * @param id 发票ID
     * @param pass 是否通过
     * @param rejectReason 驳回原因（驳回时必填）
     * @return 审核结果
     */
    @Transactional
    public Result<Invoice> reviewInvoice(Long id, boolean pass, String rejectReason) {
        Invoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            return Result.error("发票不存在");
        }
        if (!"PENDING_REVIEW".equals(invoice.getStatus())) {
            return Result.error("仅待审核状态的发票可以审核");
        }
        if (pass) {
            invoice.setStatus("REVIEWED");
            invoiceMapper.update(invoice);
            return Result.success("审核通过", invoice);
        } else {
            invoice.setStatus("DRAFT");
            invoice.setRemark("驳回原因：" + (rejectReason != null ? rejectReason : "审核不通过"));
            invoiceMapper.update(invoice);
            return Result.success("已驳回", invoice);
        }
    }

    /**
     * 查询发票详情及明细行
     *
     * @param id 发票ID
     * @return 包含发票信息和明细行的结果
     */
    public Result<Map<String, Object>> getInvoiceDetailWithDetails(Long id) {
        Invoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            return Result.error("发票不存在");
        }
        List<InvoiceDetail> details = invoiceDetailMapper.selectByInvoiceId(id);
        Map<String, Object> result = new HashMap<>();
        result.put("invoice", invoice);
        result.put("details", details);
        return Result.success(result);
    }

    /**
     * 发票交付，对已开具的发票调用金蝶接口进行交付，成功后状态变更为已交付
     *
     * @param id 发票ID
     * @return 交付结果
     */
    @Transactional
    public Result<Invoice> deliverInvoice(Long id) {
        Invoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            return Result.error("发票不存在");
        }
        if (!"ISSUED".equals(invoice.getStatus())) {
            return Result.error("发票状态不允许交付");
        }

        try {
            Customer customer = customerMapper.selectById(invoice.getCustomerId());
            String email = null;
            String phone = null;
            if (customer != null) {
                email = customer.getEmail();
                phone = customer.getPhone();
            }
            kingdeeService.deliverInvoice(
                invoice.getExternalInvoiceNo() != null ? invoice.getExternalInvoiceNo() : invoice.getInvoiceNo(),
                email, phone);

            invoice.setStatus("DELIVERED");
            invoiceMapper.update(invoice);
            return Result.success("交付成功", invoice);
        } catch (Exception e) {
            log.error("发票交付失败", e);
            return Result.error("交付失败：" + e.getMessage());
        }
    }

    /**
     * 删除草稿发票，仅草稿状态的发票允许删除，删除后状态标记为DELETED
     *
     * @param id 发票ID
     * @return 删除结果
     */
    public Result<Invoice> deleteInvoice(Long id) {
        Invoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            return Result.error("发票不存在");
        }
        if (!"DRAFT".equals(invoice.getStatus())) {
            return Result.error("仅草稿状态的发票可以删除");
        }
        invoice.setStatus("DELETED");
        invoiceMapper.update(invoice);
        return Result.success("删除成功", invoice);
    }

    /**
     * 批量创建发票，逐条调用创建发票逻辑
     *
     * @param requests 发票创建请求列表
     * @return 批量创建成功的发票列表
     */
    @Transactional
    public Result<List<Invoice>> batchCreateInvoice(List<InvoiceCreateRequest> requests) {
        List<Invoice> invoices = new ArrayList<>();
        for (InvoiceCreateRequest request : requests) {
            Result<Invoice> result = createInvoice(request);
            if (result.getCode() == 200) {
                invoices.add(result.getData());
            }
        }
        return Result.success("批量创建成功", invoices);
    }

    /**
     * 开票失败重试，将FAILED状态的发票重新提交开具
     *
     * @param id 发票ID
     * @return 重试结果
     */
    @Transactional
    public Result<Invoice> retryInvoice(Long id) {
        Invoice invoice = invoiceMapper.selectById(id);
        if (invoice == null) {
            return Result.error("发票不存在");
        }
        if (!"FAILED".equals(invoice.getStatus())) {
            return Result.error("仅开票失败的发票可以重试");
        }
        // 重置失败原因
        invoice.setFailReason(null);
        invoiceMapper.update(invoice);
        // 重新调用开具流程
        return issueInvoice(id);
    }
}