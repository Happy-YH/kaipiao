package com.example.invoice.kingdee;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 金蝶红冲请求类，封装红字发票开具所需的全部参数
 */
@Data
public class KingdeeRedInvoiceRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 原发票号码 */
    private String originalInvoiceNo;
    /** 原发票代码 */
    private String originalInvoiceCode;
    /** 红冲原因 */
    private String redReason;
    /** 红冲原因代码 */
    private String redReasonCode;
    /** 红字确认单号 */
    private String redConfirmationNo;
    /** 客户名称 */
    private String customerName;
    /** 客户税号 */
    private String customerTaxNo;
    /** 红冲金额 */
    private BigDecimal redAmount;
    /** 红冲税额 */
    private BigDecimal redTaxAmount;
    /** 备注 */
    private String remark;
    /** 明细列表 */
    private List<KingdeeInvoiceRequest.KingdeeInvoiceDetailRequest> details;
}
