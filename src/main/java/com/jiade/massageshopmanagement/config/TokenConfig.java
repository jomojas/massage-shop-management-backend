package com.jiade.massageshopmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenConfig {
    @Value("${token.expire-minutes:60}") // 默认60分钟
    private int expireMinutes;

    @Value("${token.max-expire-minutes:4320}") // 默认最大3天
    private int maxExpireMinutes;

    public int getExpireMinutes() {
        return expireMinutes;
    }

    public int getMaxExpireMinutes() {
        return maxExpireMinutes;
    }
}