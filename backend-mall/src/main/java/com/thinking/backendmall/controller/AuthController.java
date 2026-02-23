package com.thinking.backendmall.controller;

import com.thinking.backendmall.common.Result;
import com.thinking.backendmall.config.security.AuthContext;
import com.thinking.backendmall.dto.AuthLoginRequest;
import com.thinking.backendmall.dto.AuthRegisterRequest;
import com.thinking.backendmall.service.AuthService;
import com.thinking.backendmall.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MenuService menuService;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@Valid @RequestBody AuthRegisterRequest body) {
        Map<String, Object> result = authService.register(
                body.getUsername(),
                body.getPhone(),
                body.getPassword(),
                body.getConfirmPassword());
        return Result.success(result);
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody AuthLoginRequest body) {
        Map<String, Object> result = authService.login(body.getUsername(), body.getPassword());
        return Result.success(result);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            authService.logout(header.substring(7));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return Result.success();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public Result<Map<String, Object>> me() {
        Long userId = AuthContext.getUserId();
        String username = AuthContext.getUsername();
        String roleKey = AuthContext.getRoleKey();
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("userId", userId);
        result.put("username", username);
        result.put("roleKey", roleKey);
        result.put("menus", menuService.listMyMenus(roleKey));
        result.put("perms", menuService.listMyPerms(roleKey));
        return Result.success(result);
    }
}
