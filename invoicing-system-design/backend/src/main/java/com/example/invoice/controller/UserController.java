package com.example.invoice.controller;

import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.SysUser;
import com.example.invoice.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 用户管理控制器，提供用户的增删改查和角色管理接口
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询所有用户列表
     *
     * @return 用户列表
     */
    @GetMapping
    public Result<List<SysUser>> list() {
        List<SysUser> users = sysUserMapper.selectAll();
        // 清空密码字段，不返回给前端
        for (SysUser user : users) {
            user.setPassword(null);
        }
        return Result.success(users);
    }

    /**
     * 根据ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /**
     * 新增用户
     *
     * @param user 用户信息
     * @return 新增结果
     */
    @PostMapping
    public Result<SysUser> create(@RequestBody SysUser user) {
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        sysUserMapper.insert(user);
        user.setPassword(null);
        return Result.success("创建成功", user);
    }

    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<SysUser> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        user.setUpdatedAt(new Date());
        sysUserMapper.update(user);
        SysUser updated = sysUserMapper.selectById(id);
        updated.setPassword(null);
        return Result.success("更新成功", updated);
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        sysUserMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 重置用户密码
     *
     * @param id 用户ID
     * @param newPassword 新密码
     * @return 重置结果
     */
    @PostMapping("/{id}/reset-password")
    public Result resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        SysUser user = sysUserMapper.selectById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(newPassword);
        user.setUpdatedAt(new Date());
        sysUserMapper.update(user);
        return Result.success("密码重置成功");
    }
}
