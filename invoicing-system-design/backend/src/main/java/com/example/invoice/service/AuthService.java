package com.example.invoice.service;

import com.example.invoice.dto.request.LoginRequest;
import com.example.invoice.dto.response.LoginResponse;
import com.example.invoice.dto.response.Result;
import com.example.invoice.entity.SysUser;
import com.example.invoice.mapper.SysUserMapper;
import com.example.invoice.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 认证服务类，处理用户登录、令牌签发和验证
 */
@Service
public class AuthService {
    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 用户登录，验证用户名和密码，成功后签发JWT令牌
     *
     * @param request 登录请求，包含用户名和密码
     * @return 登录响应，包含令牌、用户名、姓名和角色信息；认证失败返回401错误
     */
    public Result<LoginResponse> login(LoginRequest request) {
        SysUser user = sysUserMapper.selectByUsername(request.getUsername());
        if (user == null) {
            return Result.error(401, "用户名或密码错误");
        }
        // 演示环境使用明文密码校验
        if (!request.getPassword().equals(user.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }
        String token = JwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setRealName(user.getRealName());
        response.setRole(user.getRole());
        return Result.success(response);
    }

    /**
     * 用户退出登录
     *
     * @return 退出结果
     */
    public Result logout() {
        return Result.success("退出成功");
    }
}