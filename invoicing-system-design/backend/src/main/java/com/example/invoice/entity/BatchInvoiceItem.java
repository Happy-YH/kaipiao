package com.example.invoice.entity;

import lombok.Data;

/**
 * 批量开票明细项实体类，记录批量任务中每条开票明细的处理结果
 */
@Data
public class BatchInvoiceItem {
    private Long id; // 明细项ID
    private Long taskId; // 关联批量任务ID
    private Long invoiceId; // 生成的发票ID
    private Long customerId; // 客户ID
    private Long contractId; // 合同ID
    private Long repaymentRecordId; // 还款记录ID
    private String itemData; // 开票项数据（JSON格式）
    private String status; // 处理状态（PENDING-待处理/SUCCESS-成功/FAILED-失败/RETRYING-重试中）
    private String errorMessage; // 错误信息
}