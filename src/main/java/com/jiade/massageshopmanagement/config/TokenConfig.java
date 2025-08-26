package com.jiade.massageshopmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenConfig {
    @Value("${token.expire-minutes:60}") // 默认60分钟
    private int expireMinutes;

    public int getExpireMinutes() {
        return expireMinutes;
    }
}