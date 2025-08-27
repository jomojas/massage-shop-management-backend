package com.jiade.massageshopmanagement.controller;

import com.jiade.massageshopmanagement.config.TokenConfig;
import com.jiade.massageshopmanagement.dto.ApiResponse;
import com.jiade.massageshopmanagement.dto.OperationResultDTO;
import com.jiade.massageshopmanagement.dto.LoginDto.LoginRequest;
import com.jiade.massageshopmanagement.dto.LoginDto.PhoneLoginRequest;
import com.jiade.massageshopmanagement.dto.LoginDto.SendCodeRequest;
import com.jiade.massageshopmanagement.dto.LoginDto.TokenResponse;
import com.jiade.massageshopmanagement.service.AuthService;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/login")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private TokenConfig tokenConfig;

    // 账号密码登录
    @PostMapping("/account")
    public ApiResponse<TokenResponse> loginByAccount(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            String token = authService.loginByAccount(request.getUsername(), request.getPassword());

            // 設置 cookie (假設 expireMinutes 單位是分鐘)
            int expireMinutes = tokenConfig.getExpireMinutes();
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true); // 防止XSS
            cookie.setPath("/");      // 全站有效
            cookie.setMaxAge(expireMinutes * 60); // 轉為秒
            // 如有跨域需求可加 SameSite、Domain 屬性

            response.addCookie(cookie);

            return ApiResponse.success(new TokenResponse(token));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(401, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "登录失败，请稍后重试");
        }
    }

    @GetMapping("/captcha")
    public ApiResponse<String> getCaptcha(@RequestParam("phone") String phone) {
        try {
            BufferedImage captchaImage = authService.generateCaptcha(phone);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(captchaImage, "png", baos);
            String base64Img = Base64.getEncoder().encodeToString(baos.toByteArray());

            // 成功返回，data 为 base64 图片字符串
            return ApiResponse.success(base64Img);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "验证码生成失败，请稍后重试");
        }
    }


    // 发送验证码
    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody SendCodeRequest request) {
        try {
            authService.sendCode(request.getPhone(), request.getCaptcha());
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, e.getMessage()));
        }
    }

    // 验证码登录
    @PostMapping("/phone")
    public ApiResponse<TokenResponse> loginByPhone(@RequestBody PhoneLoginRequest request, HttpServletResponse response) {
        try {
            String token = authService.loginByPhone(request.getPhone(), request.getCode());

            // 設置 cookie
            int expireMinutes = tokenConfig.getExpireMinutes();
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(expireMinutes * 60); // 單位:秒
            // cookie.setSecure(true); // 若僅限 HTTPS 可打開這行

            response.addCookie(cookie);

            return ApiResponse.success(new TokenResponse(token));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(401, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "登录失败，请稍后重试");
        }
    }
}