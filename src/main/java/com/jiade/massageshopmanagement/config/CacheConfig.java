package com.jiade.massageshopmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheConfig {
    @Value("${cache.default-expire-minutes:1}")
    private int defaultExpireMinutes;

    public int getDefaultExpireMinutes() {
//        System.out.println("Default cache expiration time: " + defaultExpireMinutes + " minutes");
        return defaultExpireMinutes;
    }
}