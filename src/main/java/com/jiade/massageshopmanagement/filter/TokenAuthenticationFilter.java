package com.jiade.massageshopmanagement.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiade.massageshopmanagement.config.TokenConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (!StringUtils.hasText(header) || !header.startsWith(BEARER_PREFIX)) {
            writeJson(response, 450, "未登录或token失效");
            return;
        }

        String token = header.substring(BEARER_PREFIX.length()).trim();
        if (!StringUtils.hasText(token)) {
            writeJson(response, 450, "未登录或token失效");
            return;
        }

        String redisKey = "login:token:" + token;
        String userId = redisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.hasText(userId)) {
            writeJson(response, 450, "未登录或token失效");
            return;
        }

        // token有效，重置过期时间
        redisTemplate.expire(redisKey, tokenConfig.getExpireMinutes(), TimeUnit.MINUTES);

        // 设置到SecurityContextHolder
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        new User(userId, "", Collections.emptyList()), // 这里放userId
                        null,
                        Collections.emptyList()
                );
        SecurityContextHolder.getContext().setAuthentication(authentication);

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