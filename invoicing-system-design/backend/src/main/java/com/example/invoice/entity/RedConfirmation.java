package com.example.invoice.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 红字确认单实体类，记录红冲申请及确认流程
 */
@Data
public class RedConfirmation {
    private Long id; // 确认单ID
    private String confirmationNo; // 确认单编号
    private Long invoiceId; // 关联原发票ID
    private Long customerId; // 客户ID
    private String cancelType; // 红冲类型
    private String cancelReason; // 红冲原因
    private BigDecimal cancelAmount; // 红冲金额
    private String status; // 状态（PENDING-待确认/CONFIRMED-已确认/CANCELLED-已取消/REJECTED-已拒绝/EXPIRED-已超时）
    private Long initiatorId; // 发起人ID
    private String initiatorName; // 发起人姓名
    private Long confirmerId; // 确认人ID
    private String confirmerName; // 确认人姓名
    private Date initiateTime; // 发起时间
    private Date confirmTime; // 确认时间
    private Date expireTime; // 过期时间
    private String remark; // 备注
    private String redRepaymentRecordIds; // 被红冲的还款记录ID列表（逗号分隔）
    private Date createdAt; // 创建时间
    private Date updatedAt; // 更新时间
}