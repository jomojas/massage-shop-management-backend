package com.jiade.massageshopmanagement.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiade.massageshopmanagement.config.TokenConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 使用 Bearer Token 的认证过滤器
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenConfig tokenConfig;
    private final StringRedisTemplate redisTemplate;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public TokenAuthenticationFilter(StringRedisTemplate redisTemplate, TokenConfig tokenConfig) {
        this.redisTemplate = redisTemplate;
        this.tokenConfig = tokenConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/api/login")) {
            filterChain.doFilter(request, response); // 直接放行，不做token校验
            return;
        }

        String token = null;

        // 1. 先從 Header 取
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            token = header.substring(BEARER_PREFIX.length()).trim();
        }

        // 2. 如果 Header 沒有，從 Cookie 取
        if (!StringUtils.hasText(token)) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        System.out.println("Extracted Token: " + token);
        // 3. token判斷
        if (!StringUtils.hasText(token)) {
            writeJson(response, 450, "未登录或token失效");
            return;
        }

        // 區分登錄方式延期token過期時間
        String userId = redisTemplate.opsForValue().get("login:token:" + token);
        String loginKey = null;
        String tokenKey = null;
        String phone = null;
        long ttl = -2;

        if (StringUtils.hasText(userId)) { // 賬號密碼登錄
            loginKey = "login:user:" + userId;
            tokenKey = "login:token:" + token;
            ttl = redisTemplate.getExpire(tokenKey, TimeUnit.SECONDS);
        } else {
            phone = redisTemplate.opsForValue().get("login:token:employee:" + token);
            if (StringUtils.hasText(phone)) { // 手機號登錄
                loginKey = "login:employee:" + phone;
                tokenKey = "login:token:employee:" + token;
                ttl = redisTemplate.getExpire(tokenKey, TimeUnit.SECONDS);
            } else {
                writeJson(response, 450, "未登录或token失效");
                return;
            }
        }

        System.out.println("Token TTL (seconds): " + ttl);
        // 只在剩餘時間 <= 一半時才重置過期
        long expireMinutes = tokenConfig.getExpireMinutes();
        long halfExpireSeconds = expireMinutes * 60 / 2;

        System.out.println("開始檢查並可能延期token過期時間");
        if (ttl > 0 && ttl <= halfExpireSeconds) {
            redisTemplate.expire(tokenKey, expireMinutes, TimeUnit.MINUTES);
            redisTemplate.expire(loginKey, expireMinutes, TimeUnit.MINUTES);
        }

        // 设置到SecurityContextHolder
//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(
//                        new User(userId, "", Collections.emptyList()), // 这里放userId
//                        null,
//                        Collections.emptyList()
//                );
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        String principal = StringUtils.hasText(userId) ? userId : phone;
        if (!StringUtils.hasText(principal)) {
            writeJson(response, 450, "未登录或token失效");
            return;
        }
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal, // 只要你不用UserDetails邏輯，直接用字符串即可
                        null,
                        Collections.emptyList()
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        System.out.println("Token驗證通過，準備前往controller");
        filterChain.doFilter(request, response);
    }

    private void writeJson(HttpServletResponse response, int code, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", code);
        resp.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(resp));
    }
}