package com.example.invoice.controller;

import com.example.invoice.dto.request.LoginRequest;
import com.example.invoice.dto.response.Result;
import com.example.invoice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器，提供用户登录和令牌验证接口
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     * @param request 登录请求体（含用户名和密码）
     * @return 登录结果（含令牌）
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /**
     * 用户登出
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result logout() {
        return authService.logout();
    }
}
