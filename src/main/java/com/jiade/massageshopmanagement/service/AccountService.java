package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.mapper.AccountMapper;
import com.jiade.massageshopmanagement.mapper.AuthMapper;
import com.jiade.massageshopmanagement.model.AdminAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public boolean logout(String token) {
//        System.out.println("進入logout方法，token: " + token);
        boolean deleted = false;
        // 賬號密碼登錄
        String userId = redisTemplate.opsForValue().get("login:token:" + token);
//        System.out.println("userId from redis: " + userId);
        if (StringUtils.hasText(userId)) {
            deleted |= redisTemplate.delete("login:token:" + token);
            deleted |= redisTemplate.delete("login:user:" + userId);
            deleted |= redisTemplate.delete("login:token:max:" + token);
        }
        // 手機號登錄
        String phone = redisTemplate.opsForValue().get("login:token:employee:" + token);
//        System.out.println("phone from redis: " + phone);
        if (StringUtils.hasText(phone)) {
            deleted |= redisTemplate.delete("login:token:employee:" + token);
            deleted |= redisTemplate.delete("login:employee:" + phone);
            deleted |= redisTemplate.delete("login:token:employee:max:" + token);
        }
        return deleted;
    }

    /**
     * 修改密码
     * @param userId 当前用户id
     * @param oldPassword 旧密码（明文）
     * @param newPassword 新密码（明文）
     * @return true: 修改成功  false: 旧密码错误
     */
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        // 1. 查找当前用户
        AdminAccount user = accountMapper.selectById(userId); // 用userId查找
        if (user == null) {
            return false; // 用户不存在
        }
        // 2. 校验原密码（假设数据库存的是加密后的密码）
        boolean match = passwordEncoder.matches(oldPassword, user.getPasswordHash());
        if (!match) {
            return false; // 原密码错误
        }
        // 3. 加密新密码并更新
        String encodedNewPwd = passwordEncoder.encode(newPassword);
        user.setPasswordHash(encodedNewPwd);
        accountMapper.updateById(user);
        return true;
    }
}