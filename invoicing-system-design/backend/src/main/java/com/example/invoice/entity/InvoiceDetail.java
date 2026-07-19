package com.example.invoice.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 发票明细实体类，记录每张发票的明细行项目
 */
@Data
public class InvoiceDetail {
    private Long id; // 明细ID
    private Long invoiceId; // 关联发票ID
    private Long repaymentRecordId; // 关联还款记录ID
    private Long contractId; // 关联合同ID
    private String contractNo; // 合同编号
    private String feeType; // 费用类型（INTEREST-利息/FEE-手续费/PENALTY-罚息/COMMITMENT-承诺费）
    private String taxClassCode; // 税目编码
    private String taxClassName; // 税目名称
    private Date interestStartDate; // 计息开始日期
    private Date interestEndDate; // 计息结束日期
    private BigDecimal principalAmount; // 本金金额
    private BigDecimal annualRate; // 年利率(%)
    private BigDecimal interestAmount; // 利息金额
    private BigDecimal taxRate; // 税率(%)
    private BigDecimal taxAmount; // 税额
    private BigDecimal totalAmount; // 价税合计
}