package com.jiade.massageshopmanagement.config;

import com.jiade.massageshopmanagement.filter.TokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           StringRedisTemplate redisTemplate) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new TokenAuthenticationFilter(redisTemplate),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 关键，覆盖 Spring Boot 的自动 UserDetailsService
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> null;
    }
}