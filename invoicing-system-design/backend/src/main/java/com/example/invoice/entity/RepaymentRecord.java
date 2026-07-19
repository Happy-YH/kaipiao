package com.example.invoice.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 还款记录实体类，记录每笔还款及开票状态
 */
@Data
public class RepaymentRecord {
    private Long id; // 还款记录ID
    private String recordNo; // 还款记录编号
    private Long contractId; // 关联合同ID
    private Date repaymentDate; // 还款日期
    private String feeType; // 费用类型（INTEREST-利息/FEE-手续费/PENALTY-罚息/COMMITMENT-承诺费）
    private BigDecimal principalAmount; // 本金金额
    private BigDecimal interestAmount; // 利息金额
    private BigDecimal totalAmount; // 价税合计
    private BigDecimal taxRate; // 税率(%)
    private BigDecimal taxAmount; // 税额
    private BigDecimal taxableAmount; // 可开票金额
    private BigDecimal invoicedAmount; // 已开票金额
    private BigDecimal remainingAmount; // 剩余可开票金额
    private String invoiceStatus; // 开票状态（UNINVOICED-未开票/INVOICED-已开票/PARTIAL_INVOICED-部分开票/RED_CANCELLED-已红冲）
    private Date createdAt; // 创建时间
    private Date updatedAt; // 更新时间
}