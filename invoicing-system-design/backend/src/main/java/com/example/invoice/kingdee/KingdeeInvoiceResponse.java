package com.example.invoice.kingdee;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 金蝶开票响应类，封装金蝶返回的发票信息
 */
@Data
public class KingdeeInvoiceResponse implements Serializable {
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
    /** XML地址 */
    private String xmlUrl;
    /** 二维码地址 */
    private String qrCodeUrl;
    /** 校验码 */
    private String checkCode;
    /** 机器编号 */
    private String machineNo;
}
