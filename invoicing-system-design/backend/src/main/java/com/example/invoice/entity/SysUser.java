package com.example.invoice.entity;

import lombok.Data;

import java.util.Date;

/**
 * 系统用户实体类，记录登录用户信息
 */
@Data
public class SysUser {
    private Long id; // 用户ID
    private String username; // 用户名
    private String password; // 密码（加密存储）
    private String realName; // 真实姓名
    private String role; // 角色（ADMIN-管理员/OPERATOR-开票员/REVIEWER-审核员）
    private Integer status; // 状态（ACTIVE-启用/INACTIVE-停用）
    private Date createdAt; // 创建时间
    private Date updatedAt; // 更新时间
}