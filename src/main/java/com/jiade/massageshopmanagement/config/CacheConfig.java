package com.jiade.massageshopmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheConfig {
    @Value("${cache.default-expire-minutes:1}")
    private int defaultExpireMinutes;

//    @Value("${cache.refresh-interval-minutes:1}")
//    private int refreshIntervalMinutes;

    public int getDefaultExpireMinutes() {
//        System.out.println("Default cache expiration time: " + defaultExpireMinutes + " minutes");
        return defaultExpireMinutes;
    }

//    public int getRefreshIntervalMinutes() {
//        return refreshIntervalMinutes;
//    }
}