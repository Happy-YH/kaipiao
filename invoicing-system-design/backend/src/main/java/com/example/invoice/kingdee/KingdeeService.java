package com.example.invoice.kingdee;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 金蝶服务接口，定义与金蝶开票系统的所有交互方法
 */
public interface KingdeeService {

    /**
     * 获取金蝶API访问令牌
     *
     * @return 金蝶令牌响应信息
     */
    KingdeeTokenResponse getToken();

    /**
     * 开具蓝字发票
     *
     * @param request 蓝字发票开具请求参数
     * @return 金蝶开票响应信息
     */
    KingdeeInvoiceResponse issueBlueInvoice(KingdeeInvoiceRequest request);

    /**
     * 开具红字发票（红冲）
     *
     * @param request 红字发票开具请求参数
     * @return 金蝶开票响应信息
     */
    KingdeeInvoiceResponse issueRedInvoice(KingdeeRedInvoiceRequest request);

    /**
     * 查询发票信息
     *
     * @param invoiceNo 发票号码
     * @return 发票查询响应信息
     */
    KingdeeInvoiceQueryResponse queryInvoice(String invoiceNo);

    /**
     * 作废发票
     *
     * @param invoiceNo 发票号码
     * @return 操作结果
     */
    KingdeeResult cancelInvoice(String invoiceNo);

    /**
     * 交付发票（邮件/短信推送）
     *
     * @param invoiceNo 发票号码
     * @param email     接收邮箱
     * @param phone     接收手机号
     * @return 操作结果
     */
    KingdeeResult deliverInvoice(String invoiceNo, String email, String phone);

    /**
     * 获取税目分类列表
     *
     * @return 税目分类列表
     */
    KingdeeResult<Map<String, Object>> getTaxClassificationList();
}
