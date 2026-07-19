package com.example.invoice.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 贷款合同实体类，记录贷款借据/合同的核心信息
 */
@Data
public class LoanContract {
    private Long id; // 合同ID
    private String contractNo; // 合同编号
    private Long customerId; // 客户ID
    private BigDecimal principal; // 贷款金额
    private BigDecimal annualRate; // 利率(%)
    private Date startDate; // 合同开始日期
    private Date endDate; // 合同结束日期
    private String status; // 合同状态（ACTIVE-生效/COMPLETED-结清/OVERDUE-逾期/CANCELLED-取消）
    private Date createdAt; // 创建时间
    private Date updatedAt; // 更新时间
}