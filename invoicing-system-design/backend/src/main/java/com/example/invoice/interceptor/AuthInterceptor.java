package com.example.invoice.interceptor;

import com.example.invoice.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证拦截器，校验 JWT token 并将 userId/username/role 注入请求上下文
 */
public class AuthInterceptor implements HandlerInterceptor {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (token == null || token.isEmpty()) {
            return writeUnauthorized(response, "未登录或登录已过期");
        }

        try {
            if (JwtUtil.isTokenExpired(token)) {
                return writeUnauthorized(response, "登录已过期，请重新登录");
            }
            Long userId = JwtUtil.getUserId(token);
            if (userId == null) {
                return writeUnauthorized(response, "无效的token");
            }
            request.setAttribute("userId", userId);
            request.setAttribute("username", JwtUtil.getUsername(token));
            request.setAttribute("role", JwtUtil.getRole(token));
            return true;
        } catch (Exception e) {
            return writeUnauthorized(response, "无效的token");
        }
    }

    private boolean writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        out.write(OBJECT_MAPPER.writeValueAsString(result));
        out.flush();
        out.close();
        return false;
    }
}
