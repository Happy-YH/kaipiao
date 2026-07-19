package com.example.invoice.kingdee;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 金蝶服务真实实现，对接金蝶开票系统API（待开发）
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "kingdee.mock.enabled", havingValue = "false")
public class KingdeeServiceImpl implements KingdeeService {

    @Value("${kingdee.base-url:https://api.kingdee.com}")
    private String baseUrl;

    @Value("${kingdee.app-id:}")
    private String appId;

    @Value("${kingdee.app-secret:}")
    private String appSecret;

    @Value("${kingdee.tenant-id:}")
    private String tenantId;

    /**
     * 获取金蝶API访问令牌（待开发）
     */
    @Override
    public KingdeeTokenResponse getToken() {
        log.warn("金蝶真实实现未配置，请配置 kingdee.mock.enabled=false 并配置相关参数");
        throw new UnsupportedOperationException("金蝶真实接口实现待开发");
    }

    /**
     * 开具蓝字发票（待开发）
     */
    @Override
    public KingdeeInvoiceResponse issueBlueInvoice(KingdeeInvoiceRequest request) {
        log.warn("金蝶真实实现未配置，请配置 kingdee.mock.enabled=false 并配置相关参数");
        throw new UnsupportedOperationException("金蝶真实接口实现待开发");
    }

    /**
     * 开具红字发票（红冲）（待开发）
     */
    @Override
    public KingdeeInvoiceResponse issueRedInvoice(KingdeeRedInvoiceRequest request) {
        log.warn("金蝶真实实现未配置，请配置 kingdee.mock.enabled=false 并配置相关参数");
        throw new UnsupportedOperationException("金蝶真实接口实现待开发");
    }

    @Override
    public KingdeeInvoiceQueryResponse queryInvoice(String invoiceNo) {
        log.warn("金蝶真实实现未配置，请配置 kingdee.mock.enabled=false 并配置相关参数");
        throw new UnsupportedOperationException("金蝶真实接口实现待开发");
    }

    /**
     * 作废发票（待开发）
     */
    @Override
    public KingdeeResult cancelInvoice(String invoiceNo) {
        log.warn("金蝶真实实现未配置，请配置 kingdee.mock.enabled=false 并配置相关参数");
        throw new UnsupportedOperationException("金蝶真实接口实现待开发");
    }

    /**
     * 交付发票（邮件/短信推送）（待开发）
     */
    @Override
    public KingdeeResult deliverInvoice(String invoiceNo, String email, String phone) {
        log.warn("金蝶真实实现未配置，请配置 kingdee.mock.enabled=false 并配置相关参数");
        throw new UnsupportedOperationException("金蝶真实接口实现待开发");
    }

    /**
     * 获取税目分类列表（待开发）
     */
    @Override
    public KingdeeResult<Map<String, Object>> getTaxClassificationList() {
        log.warn("金蝶真实实现未配置，请配置 kingdee.mock.enabled=false 并配置相关参数");
        throw new UnsupportedOperationException("金蝶真实接口实现待开发");
    }
}
