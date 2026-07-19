package com.example.invoice.kingdee;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 金蝶服务挡板实现，用于本地开发和测试时模拟金蝶接口返回，所有接口默认返回成功
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "kingdee.mock.enabled", havingValue = "true", matchIfMissing = true)
public class KingdeeMockServiceImpl implements KingdeeService {

    private static final String MOCK_TOKEN = "mock-kingdee-token-" + System.currentTimeMillis();
    private static final long TOKEN_EXPIRES_IN = 7200L;

    /**
     * 模拟获取金蝶访问令牌，返回固定格式的模拟令牌
     */
    @Override
    public KingdeeTokenResponse getToken() {
        log.info("[金蝶挡板] 获取金蝶访问令牌 - 成功");
        KingdeeTokenResponse response = new KingdeeTokenResponse();
        response.setAccessToken(MOCK_TOKEN);
        response.setTokenType("Bearer");
        response.setExpiresIn(TOKEN_EXPIRES_IN);
        response.setExpireTime(new Date(System.currentTimeMillis() + TOKEN_EXPIRES_IN * 1000));
        response.setScope("all");
        return response;
    }

    /**
     * 模拟开具蓝字发票，生成随机发票号码和模拟金额数据
     */
    @Override
    public KingdeeInvoiceResponse issueBlueInvoice(KingdeeInvoiceRequest request) {
        log.info("[金蝶挡板] 开具蓝字发票 - 客户: {}, 金额: {}", request.getCustomerName(),
                request.getDetails() != null ? request.getDetails().stream()
                        .map(KingdeeInvoiceRequest.KingdeeInvoiceDetailRequest::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO);

        KingdeeInvoiceResponse response = new KingdeeInvoiceResponse();
        response.setInvoiceId(UUID.randomUUID().toString().replace("-", ""));
        response.setInvoiceNo(generateMockInvoiceNo("FP"));
        response.setInvoiceCode("044" + String.format("%06d", new Random().nextInt(1000000)));
        response.setInvoiceType(request.getInvoiceType() != null ? request.getInvoiceType() : "SPECIAL");
        response.setInvoiceKind("BLUE");
        response.setStatus("ISSUED");
        response.setStatusText("已开具");
        response.setTotalAmount(request.getDetails() != null ?
                request.getDetails().stream()
                        .map(KingdeeInvoiceRequest.KingdeeInvoiceDetailRequest::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO);
        response.setTaxAmount(request.getDetails() != null ?
                request.getDetails().stream()
                        .map(KingdeeInvoiceRequest.KingdeeInvoiceDetailRequest::getTaxAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO);
        response.setTaxableAmount(response.getTotalAmount().subtract(response.getTaxAmount()));
        response.setIssueDate(new Date());
        response.setPdfUrl("https://mock-kingdee/invoice/" + response.getInvoiceNo() + ".pdf");
        response.setXmlUrl("https://mock-kingdee/invoice/" + response.getInvoiceNo() + ".xml");
        response.setQrCodeUrl("https://mock-kingdee/invoice/" + response.getInvoiceNo() + "/qrcode");
        response.setCheckCode(generateCheckCode());
        response.setMachineNo(String.format("%08d", new Random().nextInt(100000000)));

        simulateDelay();
        return response;
    }

    /**
     * 模拟开具红字发票（红冲），生成红冲发票号码并以负数返回金额
     */
    @Override
    public KingdeeInvoiceResponse issueRedInvoice(KingdeeRedInvoiceRequest request) {
        log.info("[金蝶挡板] 开具红字发票 - 原发票号: {}, 红冲金额: {}",
                request.getOriginalInvoiceNo(), request.getRedAmount());

        KingdeeInvoiceResponse response = new KingdeeInvoiceResponse();
        response.setInvoiceId(UUID.randomUUID().toString().replace("-", ""));
        response.setInvoiceNo(generateMockInvoiceNo("HZ"));
        response.setInvoiceCode("044" + String.format("%06d", new Random().nextInt(1000000)));
        response.setInvoiceType("SPECIAL");
        response.setInvoiceKind("RED");
        response.setStatus("ISSUED");
        response.setStatusText("已红冲");
        response.setTotalAmount(request.getRedAmount() != null ? request.getRedAmount().negate() : BigDecimal.ZERO);
        response.setTaxAmount(request.getRedTaxAmount() != null ? request.getRedTaxAmount().negate() : BigDecimal.ZERO);
        response.setTaxableAmount(response.getTotalAmount().subtract(response.getTaxAmount()));
        response.setIssueDate(new Date());
        response.setPdfUrl("https://mock-kingdee/invoice/" + response.getInvoiceNo() + ".pdf");
        response.setXmlUrl("https://mock-kingdee/invoice/" + response.getInvoiceNo() + ".xml");
        response.setQrCodeUrl("https://mock-kingdee/invoice/" + response.getInvoiceNo() + "/qrcode");
        response.setCheckCode(generateCheckCode());
        response.setMachineNo(String.format("%08d", new Random().nextInt(100000000)));

        simulateDelay();
        return response;
    }

    /**
     * 模拟查询发票信息，返回固定的模拟发票数据及明细
     */
    @Override
    public KingdeeInvoiceQueryResponse queryInvoice(String invoiceNo) {
        log.info("[金蝶挡板] 查询发票 - 发票号: {}", invoiceNo);

        KingdeeInvoiceQueryResponse response = new KingdeeInvoiceQueryResponse();
        response.setInvoiceId(UUID.randomUUID().toString().replace("-", ""));
        response.setInvoiceNo(invoiceNo);
        response.setInvoiceCode("044" + String.format("%06d", new Random().nextInt(1000000)));
        response.setInvoiceType("SPECIAL");
        response.setInvoiceKind("BLUE");
        response.setStatus("ISSUED");
        response.setStatusText("已开具");
        response.setCustomerName("模拟客户有限公司");
        response.setCustomerTaxNo("91440000MA00000000");
        response.setTotalAmount(new BigDecimal("10600.00"));
        response.setTaxAmount(new BigDecimal("600.00"));
        response.setTaxableAmount(new BigDecimal("10000.00"));
        response.setIssueDate(new Date());
        response.setPdfUrl("https://mock-kingdee/invoice/" + invoiceNo + ".pdf");
        response.setCheckCode(generateCheckCode());

        List<KingdeeInvoiceQueryResponse.KingdeeInvoiceDetail> details = new ArrayList<>();
        KingdeeInvoiceQueryResponse.KingdeeInvoiceDetail detail = new KingdeeInvoiceQueryResponse.KingdeeInvoiceDetail();
        detail.setTaxClassCode("30601");
        detail.setTaxClassName("贷款服务");
        detail.setGoodsName("利息收入");
        detail.setAmount(new BigDecimal("10000.00"));
        detail.setTaxRate(new BigDecimal("6.00"));
        detail.setTaxAmount(new BigDecimal("600.00"));
        details.add(detail);
        response.setDetails(details);

        return response;
    }

    /**
     * 模拟作废发票，直接返回作废成功
     */
    @Override
    public KingdeeResult cancelInvoice(String invoiceNo) {
        log.info("[金蝶挡板] 作废发票 - 发票号: {}", invoiceNo);
        simulateDelay();
        return KingdeeResult.success("作废成功", null);
    }

    /**
     * 模拟交付发票（邮件/短信推送），直接返回交付成功及模拟交付信息
     */
    @Override
    public KingdeeResult deliverInvoice(String invoiceNo, String email, String phone) {
        log.info("[金蝶挡板] 交付发票 - 发票号: {}, 邮箱: {}, 手机: {}", invoiceNo, email, phone);
        simulateDelay();
        Map<String, Object> data = new HashMap<>();
        data.put("deliveryId", UUID.randomUUID().toString().replace("-", ""));
        data.put("status", "DELIVERED");
        data.put("deliveryTime", new Date());
        return KingdeeResult.success("交付成功", data);
    }

    /**
     * 模拟获取税目分类列表，返回贷款服务和直接收费金融服务两条模拟数据
     */
    @Override
    public KingdeeResult<Map<String, Object>> getTaxClassificationList() {
        log.info("[金蝶挡板] 获取税目分类列表");

        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> tax1 = new HashMap<>();
        tax1.put("taxCode", "30601");
        tax1.put("taxName", "贷款服务");
        tax1.put("feeType", "INTEREST");
        tax1.put("taxRate", new BigDecimal("6.00"));
        tax1.put("description", "各种占用、拆借资金取得的收入");
        tax1.put("status", "ACTIVE");
        list.add(tax1);

        Map<String, Object> tax2 = new HashMap<>();
        tax2.put("taxCode", "30602");
        tax2.put("taxName", "直接收费金融服务");
        tax2.put("feeType", "FEE");
        tax2.put("taxRate", new BigDecimal("6.00"));
        tax2.put("description", "提供货币兑换等金融服务");
        tax2.put("status", "ACTIVE");
        list.add(tax2);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());

        return KingdeeResult.success(result);
    }

    private String generateMockInvoiceNo(String prefix) {
        String dateStr = String.format("%tY%<tm%<td", new Date());
        int random = new Random().nextInt(1000000);
        return prefix + dateStr + String.format("%06d", random);
    }

    private String generateCheckCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private void simulateDelay() {
        try {
            Thread.sleep(500 + new Random().nextInt(500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
