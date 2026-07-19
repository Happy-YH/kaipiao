package com.example.invoice.entity;

import lombok.Data;

import java.util.Date;

/**
 * 客户信息实体类，记录贷款客户的完整信息
 */
@Data
public class Customer {
    private Long id; // 客户ID
    private String customerName; // 客户名称
    private String taxId; // 税号/统一社会信用代码
    private String address; // 地址
    private String phone; // 联系电话
    private String bankName; // 开户银行
    private String bankAccount; // 银行账号
    private String email; // 电子邮箱
    private String contactName; // 联系人姓名
    private String contactPhone; // 联系人手机
    private Integer status; // 状态（ACTIVE-启用/INACTIVE-停用）
    private Date createdAt; // 创建时间
    private Date updatedAt; // 更新时间
}