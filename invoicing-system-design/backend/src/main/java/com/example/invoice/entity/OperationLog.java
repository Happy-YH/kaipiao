package com.example.invoice.entity;

import lombok.Data;
import java.util.Date;

/**
 * 操作日志实体类，记录用户的关键操作审计信息
 */
@Data
public class OperationLog {
    private Long id; // 日志ID
    private Long userId; // 操作人ID
    private String username; // 操作人用户名
    private String operationType; // 操作类型（CREATE/UPDATE/DELETE/APPROVE/REJECT/ISSUE/RED_CANCEL/DELIVER）
    private String module; // 操作模块（INVOICE/CUSTOMER/CONTRACT/REPAYMENT/RED_CONFIRMATION/BATCH/TAX_CONFIG/SYSTEM）
    private Long targetId; // 操作目标ID
    private String targetNo; // 操作目标编号
    private String content; // 操作内容描述
    private String oldValue; // 修改前值（JSON格式）
    private String newValue; // 修改后值（JSON格式）
    private String clientIp; // 客户端IP地址
    private String userAgent; // 客户端设备信息
    private Date createdAt; // 操作时间
}
