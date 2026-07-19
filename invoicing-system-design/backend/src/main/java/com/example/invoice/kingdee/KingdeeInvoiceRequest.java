package com.example.invoice.kingdee;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 金蝶开票请求类，封装蓝字发票开具所需的全部参数
 */
@Data
public class KingdeeInvoiceRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 客户名称 */
    private String customerName;
    /** 客户税号 */
    private String customerTaxNo;
    /** 客户地址 */
    private String customerAddress;
    /** 客户电话 */
    private String customerPhone;
    /** 开户银行 */
    private String customerBankName;
    /** 银行账号 */
    private String customerBankAccount;
    /** 发票类型 */
    private String invoiceType;
    /** 发票种类 */
    private String invoiceKind;
    /** 备注 */
    private String remark;
    /** 开票日期 */
    private Date issueDate;
    /** 销方名称 */
    private String sellerName;
    /** 销方税号 */
    private String sellerTaxNo;
    /** 明细列表 */
    private List<KingdeeInvoiceDetailRequest> details;

    /**
     * 开票明细请求项
     */
    @Data
    public static class KingdeeInvoiceDetailRequest implements Serializable {
        private static final long serialVersionUID = 1L;

        /** 税目编码 */
        private String taxClassCode;
        /** 税目名称 */
        private String taxClassName;
        /** 商品名称 */
        private String goodsName;
        /** 规格型号 */
        private String specification;
        /** 单位 */
        private String unit;
        /** 数量 */
        private BigDecimal quantity;
        /** 单价 */
        private BigDecimal unitPrice;
        /** 金额 */
        private BigDecimal amount;
        /** 税率 */
        private BigDecimal taxRate;
        /** 税额 */
        private BigDecimal taxAmount;
        /** 费用类型 */
        private String feeType;
    }
}
