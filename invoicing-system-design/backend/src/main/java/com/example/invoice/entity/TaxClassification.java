package com.example.invoice.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 税目分类实体类，记录可用的税目编码和税率
 */
@Data
public class TaxClassification {
    private Long id; // 税目ID
    private String taxCode; // 税目编码
    private String taxName; // 税目名称
    private String feeType; // 关联费用类型
    private BigDecimal taxRate; // 税率(%)
    private Integer status; // 状态（ACTIVE-启用/INACTIVE-停用）
    private Date createdAt; // 创建时间
}