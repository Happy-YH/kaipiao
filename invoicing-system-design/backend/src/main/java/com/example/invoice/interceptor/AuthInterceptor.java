package com.example.invoice.interceptor;

import com.example.invoice.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }

        if (token == null || token.isEmpty()) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("message", "未登录或登录已过期");
            out.write(new ObjectMapper().writeValueAsString(result));
            out.flush();
            out.close();
            return false;
        }

        try {
            if (JwtUtil.isTokenExpired(token)) {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                Map<String, Object> result = new HashMap<>();
                result.put("code", 401);
                result.put("message", "登录已过期，请重新登录");
                out.write(new ObjectMapper().writeValueAsString(result));
                out.flush();
                out.close();
                return false;
            }
            request.setAttribute("userId", JwtUtil.getUserId(token));
            return true;
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("message", "无效的token");
            out.write(new ObjectMapper().writeValueAsString(result));
            out.flush();
            out.close();
            return false;
        }
    }
}