package com.jiade.massageshopmanagement.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        String rawPassword = "123456";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(rawPassword);
        System.out.println("加密后的hash值：" + hash);
    }
}
