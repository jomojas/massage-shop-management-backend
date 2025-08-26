package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.mapper.AccountMapper;
import com.jiade.massageshopmanagement.mapper.AuthMapper;
import com.jiade.massageshopmanagement.model.AdminAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    // 如果没用Spring Security，可以用自定义的加密工具

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
        boolean match = passwordEncoder.matches(oldPassword, user.getPassword());
        if (!match) {
            return false; // 原密码错误
        }
        // 3. 加密新密码并更新
        String encodedNewPwd = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPwd);
        accountMapper.updateById(user);
        return true;
    }
}