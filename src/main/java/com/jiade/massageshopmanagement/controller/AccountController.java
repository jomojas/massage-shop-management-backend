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
import jakarta.servlet.http.Cookie;

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
//        System.out.println("進入logout接口");
        String token = getTokenFromHeaderOrCookie(request);
        if (!StringUtils.hasText(token)) {
            return ApiResponse.error(401, "未登录或token无效");
        }
        boolean success = accountService.logout(token);
        return ApiResponse.success(success ? "退出登录成功" : "token不存在或已失效");
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
        if (authentication == null || !(authentication.getPrincipal() instanceof String)) {
            return ApiResponse.error(401, "未登录或token无效");
        }
        String userId = (String) authentication.getPrincipal();

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
     * 從請求頭Authorization（Bearer）或Cookie中獲取token
     */
    private String getTokenFromHeaderOrCookie(HttpServletRequest request) {
//        System.out.println("從請求中獲取token");
        // 1. 先從Header獲取
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
//            System.out.println("header中獲取到token");
            return auth.substring(7).trim();
        }

//        System.out.println("header中未獲取到token，嘗試從cookie中獲取");
        // 2. 再從Cookie獲取
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                // 這裡假設你的cookie名叫"token"，如有不同請自行調整
                if ("token".equals(cookie.getName())) {
                    String value = cookie.getValue();
                    if (value != null && !value.isEmpty()) {
                        return value.trim();
                    }
                }
            }
        }
//        System.out.println("cookie中也未獲取到token");

        // 都沒有
        return null;
    }
}