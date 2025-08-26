package com.jiade.massageshopmanagement.service;

import com.google.code.kaptcha.Producer;
import com.jiade.massageshopmanagement.config.TokenConfig;
import com.jiade.massageshopmanagement.mapper.AuthMapper;
import com.jiade.massageshopmanagement.mapper.StaffMapper;
import com.jiade.massageshopmanagement.model.AdminAccount;
import com.jiade.massageshopmanagement.model.Staff;
import com.jiade.massageshopmanagement.sms.SmsService;
import com.jiade.massageshopmanagement.sms.SmsTemplateId;
import com.jiade.massageshopmanagement.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class AuthService {

    @Autowired
    private Producer captchaProducer; // Kaptcha生成器，需配置Bean

    @Autowired
    private SmsService smsService;

    @Autowired
    private AuthMapper authMapper;

    @Autowired
    private StaffMapper staffMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private TokenConfig tokenConfig;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final int CODE_EXPIRE_MINUTES = 1; // 1分钟

    public static String generateCode() {
        int code = (int)((Math.random() * 9 + 1) * 1000); // 保证首位不为0
        return String.valueOf(code);
    }

    // 账号密码登录，登录成功返回token，失败抛出异常
    public String loginByAccount(String username, String password) {
        try {
            // 1. 查询数据库
            AdminAccount account = authMapper.selectByUsername(username);
            if (account == null) {
                throw new IllegalArgumentException("用户名不存在");
            }

            // 2. 校验密码（数据库中存储的是加密后的密码，这里用BCrypt比对）
            if (!passwordEncoder.matches(password, account.getPasswordHash())) {
                throw new IllegalArgumentException("密码错误");
            }

            // 3. 生成随机token
            String token = TokenUtil.generateToken();

            // 4. 存入Redis，设置过期时间
            String redisKey = "login:token:" + token;
            redisTemplate.opsForValue().set(redisKey, String.valueOf(account.getId()), tokenConfig.getExpireMinutes(), TimeUnit.MINUTES);

            // 5. 返回token
            return token;
        } catch (IllegalArgumentException e) {
            throw e; // 业务异常继续抛出
        } catch (Exception e) {
            // 其他异常统一包装成运行时异常
            throw new RuntimeException("登录失败，请稍后重试", e);
        }
    }

    // 生成图片验证码
    public BufferedImage generateCaptcha(String phone) {
        try {
            // 1. 校验phone资格（与sendCode一致）
            if (phone == null || phone.trim().isEmpty()) {
                throw new IllegalArgumentException("手机号不能为空");
            }
            List<Staff> staffList = staffMapper.selectStaffByPhone(phone);
            if (staffList == null || staffList.isEmpty()) {
                throw new IllegalArgumentException("您没有权限使用本系统");
            }
            boolean isAllowed = staffList.stream().anyMatch(
                    staff -> staff.getCommission() != null && staff.getCommission().compareTo(BigDecimal.ONE) == 0
            );
            if (!isAllowed) {
                throw new IllegalArgumentException("您的权限不足，无法获取验证码");
            }

            // 2. 生成图片验证码
            String captchaText = captchaProducer.createText();
            redisTemplate.opsForValue().set("captcha:login:" + phone, captchaText, 3, TimeUnit.MINUTES);
            BufferedImage image = captchaProducer.createImage(captchaText);
            return image;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("验证码生成失败，请稍后重试", e);
        }
    }

    // 发送验证码
    public void sendCode(String phone, String captcha) {
        try {
            // 1. 参数校验
            if (phone == null || phone.trim().isEmpty()) {
                throw new IllegalArgumentException("手机号不能为空");
            }
            if (captcha == null || captcha.trim().isEmpty()) {
                throw new IllegalArgumentException("验证码不能为空");
            }
            // 2. 校验图片验证码
            String captchaKey = "captcha:login:" + phone;
            String cacheCaptcha = redisTemplate.opsForValue().get(captchaKey);
            if (cacheCaptcha == null || !cacheCaptcha.equalsIgnoreCase(captcha.trim())) {
                throw new IllegalArgumentException("图形验证码错误或已过期");
            }
            // 校验通过后，删除验证码防止重复提交
            redisTemplate.delete(captchaKey);

            // 3. 生成4位数字验证码
            int codeInt = (int) ((Math.random() * 9 + 1) * 1000); // 1000-9999
            String code = String.valueOf(codeInt);

            // 4. 存入Redis
            String redisKey = "login:sms:code:" + phone;
            redisTemplate.opsForValue().set(redisKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

            // 5. 发送短信
            Map<String, String> smsParams = new HashMap<>();
            smsParams.put("code", code);
            smsService.send(phone, SmsTemplateId.VERIFICATION_CODE, smsParams);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("发送验证码失败，请稍后重试", e);
        }
    }

    // 验证码登录，成功返回token，失败抛出异常
    public String loginByPhone(String phone, String code) {
        try {
            if (phone == null || phone.trim().isEmpty()) {
                throw new IllegalArgumentException("手机号不能为空");
            }
            if (code == null || code.trim().isEmpty()) {
                throw new IllegalArgumentException("验证码不能为空");
            }

            // 1. 从Redis获取验证码
            String redisKey = "login:sms:code:" + phone;
            String cachedCode = redisTemplate.opsForValue().get(redisKey);
            if (cachedCode == null) {
                throw new IllegalArgumentException("验证码已过期，请重新获取");
            }
            if (!cachedCode.equals(code)) {
                throw new IllegalArgumentException("验证码错误");
            }

            // 2. 通过校验，删除验证码（避免重复使用）
            redisTemplate.delete(redisKey);

            // 3. 生成token
            String token = TokenUtil.generateToken();

            // 4. 存入Redis，设置token过期时间
            String tokenKey = "login:token:" + token;
            // 假设手机号就是用户唯一标识
            redisTemplate.opsForValue().set(tokenKey, phone, tokenConfig.getExpireMinutes(), TimeUnit.MINUTES);

            // 5. 返回token
            return token;
        } catch (IllegalArgumentException e) {
            throw e; // 业务异常继续抛出
        } catch (Exception e) {
            // 其他异常统一包装成运行时异常
            throw new RuntimeException("登录失败，请稍后重试", e);
        }
    }
}