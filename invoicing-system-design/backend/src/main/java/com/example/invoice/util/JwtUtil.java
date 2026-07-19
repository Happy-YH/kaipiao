package com.example.invoice.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final String SECRET = "invoice_system_secret_key_2026";
    private static final long EXPIRATION_TIME = 86400000L;

    public static String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public static Long getUserId(String token) {
        try {
            Claims claims = parseToken(token);
            Object val = claims.get("userId");
            if (val instanceof Number) {
                return ((Number) val).longValue();
            }
            if (val instanceof String) {
                return Long.parseLong((String) val);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUsername(String token) {
        try {
            Claims claims = parseToken(token);
            Object val = claims.get("username");
            return val != null ? val.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public static String getRole(String token) {
        try {
            Claims claims = parseToken(token);
            Object val = claims.get("role");
            return val != null ? val.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
}