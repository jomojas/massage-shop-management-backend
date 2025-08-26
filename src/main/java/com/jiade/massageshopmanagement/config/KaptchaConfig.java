package com.jiade.massageshopmanagement.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public Producer captchaProducer() {
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "no");
        properties.setProperty("kaptcha.textproducer.font.color", "black");
        properties.setProperty("kaptcha.textproducer.char.space", "4");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.textproducer.char.string", "abcde2345678gfynmnpwx");

        Config config = new Config(properties);
        com.google.code.kaptcha.impl.DefaultKaptcha kaptcha = new com.google.code.kaptcha.impl.DefaultKaptcha();
        kaptcha.setConfig(config);
        return kaptcha;
    }
}