package com.thinking.backendmall.config.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.thinking.backendmall.config.JwtUtil;
import com.thinking.backendmall.entity.Role;
import com.thinking.backendmall.repository.MenuRepository;
import com.thinking.backendmall.repository.RoleRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                if (!isBlacklisted(token)
                        && jwtUtil.validateToken(token)
                        && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Claims claims = jwtUtil.getClaimsFromToken(token);
                    String username = claims.getSubject();
                    String roleKey = claims.get("roleKey", String.class);
                    Long userId = claims.get("userId", Long.class);
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    if (roleKey != null) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + roleKey));
                        Role role = roleRepository.selectOne(
                                new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, roleKey));
                        if (role != null) {
                            List<String> perms = menuRepository.selectPermCodesByRoleId(role.getId());
                            if (perms != null) {
                                for (String perm : perms) {
                                    if (perm != null && !perm.isBlank()) {
                                        authorities.add(new SimpleGrantedAuthority(perm));
                                    }
                                }
                            }
                        }
                    }

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    AuthContext.set(new AuthContext.AuthUser(userId, username, roleKey));
                }
            }
            filterChain.doFilter(request, response);
        } finally {
            AuthContext.clear();
        }
    }

    private boolean isBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey("jwt:blacklist:" + token));
        } catch (Exception ex) {
            return false;
        }
    }
}
