package com.jiade.massageshopmanagement.controller;

import com.jiade.massageshopmanagement.dto.ApiResponse;
import com.jiade.massageshopmanagement.dto.LoginDto.ChangePasswordRequest;
import com.jiade.massageshopmanagement.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 账号相关操作：退出登录、修改密码
 */
@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AccountService accountService;

    /**
     * 退出登录（删除Redis中的token）
     */
    @PostMapping("/logout")
    public ApiResponse<?> logout(HttpServletRequest request) {
        String token = getTokenFromHeader(request);
        if (!StringUtils.hasText(token)) {
            return ApiResponse.error(401, "未登录或token无效");
        }
        String redisKey = "login:token:" + token;
        Boolean deleted = redisTemplate.delete(redisKey);
        return ApiResponse.success(deleted != null && deleted ? "退出登录成功" : "token不存在或已失效");
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@RequestBody ChangePasswordRequest request) {
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();
        // 1. 从SecurityContextHolder获取当前登录用户（principal为userId）
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User)) {
            return ApiResponse.error(401, "未登录或token无效");
        }
        User user = (User) authentication.getPrincipal();
        String userId = user.getUsername(); // 这里存的是userId

        // 2. 校验旧密码，修改为新密码
        try {
            boolean ok = accountService.changePassword(userId, oldPassword, newPassword);
            if (ok) {
                return ApiResponse.success("密码修改成功");
            } else {
                return ApiResponse.error(400, "原密码错误");
            }
        } catch (Exception e) {
            return ApiResponse.error(500, "密码修改失败：" + e.getMessage());
        }
    }

    /**
     * 从请求头Authorization中获取token，支持Bearer schema
     */
    private String getTokenFromHeader(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return auth;
    }
}