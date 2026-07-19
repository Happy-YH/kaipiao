package com.example.invoice.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 批量开票任务实体类，记录批量开票任务的状态和进度
 */
@Data
public class BatchInvoiceTask {
    private Long id; // 任务ID
    private String taskNo; // 任务编号
    private String taskName; // 任务名称
    private String taskType; // 任务类型（AUTO-自动/MANUAL-手动/IMPORT-导入）
    private String status; // 任务状态（PENDING-待执行/RUNNING-执行中/PAUSED-已暂停/COMPLETED-已完成/FAILED-失败）
    private Integer totalCount; // 总记录数
    private Integer successCount; // 成功数
    private Integer failCount; // 失败数
    private BigDecimal progress; // 进度百分比(0-100)
    private Long createdBy; // 创建人ID
    private Date createdAt; // 创建时间
    private Date updatedAt; // 更新时间
    private Date finishedAt; // 完成时间
}