package com.thinking.backendmall.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // 功能：获取signingkey
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 功能：处理generate令牌
    public String generateToken(String username, Long userId, String roleKey) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .claim("roleKey", roleKey)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 功能：获取claimsfrom令牌
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 功能：获取用户名from令牌
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // 功能：获取用户idfrom令牌
    public Long getUserIdFromToken(String token) {
        return getClaimsFromToken(token).get("userId", Long.class);
    }

    // 功能：获取角色keyfrom令牌
    public String getRoleKeyFromToken(String token) {
        return getClaimsFromToken(token).get("roleKey", String.class);
    }

    // 功能：判断令牌expired
    public boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

    // 功能：校验令牌
    public boolean validateToken(String token) {
        try {
            // 功能：获取claimsfrom令牌
            getClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}