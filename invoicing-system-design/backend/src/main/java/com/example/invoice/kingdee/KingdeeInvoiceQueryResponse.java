package com.example.invoice.kingdee;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 金蝶发票查询响应类，封装查询到的发票详细信息
 */
@Data
public class KingdeeInvoiceQueryResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 金蝶发票ID */
    private String invoiceId;
    /** 发票号码 */
    private String invoiceNo;
    /** 发票代码 */
    private String invoiceCode;
    /** 发票类型 */
    private String invoiceType;
    /** 发票种类 */
    private String invoiceKind;
    /** 状态 */
    private String status;
    /** 状态描述 */
    private String statusText;
    /** 客户名称 */
    private String customerName;
    /** 客户税号 */
    private String customerTaxNo;
    /** 价税合计 */
    private BigDecimal totalAmount;
    /** 税额 */
    private BigDecimal taxAmount;
    /** 不含税金额 */
    private BigDecimal taxableAmount;
    /** 开票日期 */
    private Date issueDate;
    /** PDF地址 */
    private String pdfUrl;
    /** 校验码 */
    private String checkCode;
    /** 明细列表 */
    private List<KingdeeInvoiceDetail> details;

    /**
     * 发票查询明细
     */
    @Data
    public static class KingdeeInvoiceDetail implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 税目编码 */
        private String taxClassCode;
        /** 税目名称 */
        private String taxClassName;
        /** 商品名称 */
        private String goodsName;
        /** 金额 */
        private BigDecimal amount;
        /** 税率 */
        private BigDecimal taxRate;
        /** 税额 */
        private BigDecimal taxAmount;
    }
}
