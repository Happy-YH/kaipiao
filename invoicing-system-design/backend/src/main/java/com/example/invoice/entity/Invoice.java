package com.example.invoice.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票实体类，记录蓝字/红字发票的完整信息
 */
@Data
public class Invoice {
    private Long id; // 发票ID
    private String invoiceNo; // 系统发票编号
    private String invoiceCode; // 发票代码
    private String invoiceType; // 发票类型（SPECIAL-专票/GENERAL-普票）
    private String invoiceKind; // 发票种类（BLUE-蓝字/RED-红字）
    private Long customerId; // 客户ID
    private BigDecimal totalAmount; // 价税合计金额
    private BigDecimal taxAmount; // 税额
    private BigDecimal taxableAmount; // 不含税金额
    private String status; // 发票状态（DRAFT-草稿/PENDING_REVIEW-待审核/REVIEWED-已审核/ISSUING-开票中/ISSUED-已开具/DELIVERED-已交付/RED_CANCELLED-已红冲/FAILED-开票失败/DELETED-已删除）
    private String redConfirmationNo; // 红字确认单编号
    private Long originalInvoiceId; // 原发票ID（红冲时关联原蓝字发票）
    private String remark; // 备注
    private Date issueDate; // 开票日期
    private String externalInvoiceId; // 金蝶系统发票ID
    private String externalInvoiceNo; // 金蝶系统发票号码
    private String externalInvoiceCode; // 金蝶系统发票代码
    private String pdfUrl; // 发票PDF下载地址
    private String xmlUrl; // 发票XML下载地址
    private String qrCodeUrl; // 发票二维码地址
    private String checkCode; // 发票校验码
    private String machineNo; // 开票机号
    private String failReason; // 开票失败原因
    private Long createdBy; // 创建人ID
    private Date createdAt; // 创建时间
    private Date updatedAt; // 更新时间
}