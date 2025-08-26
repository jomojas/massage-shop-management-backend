package com.jiade.massageshopmanagement.sms;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@Profile("dev")
public class MockSmsService implements SmsService {
    @Override
    public void send(String phone, String templateId, Map<String, String> params) {
        System.out.println("【模拟短信发送】手机号: " + phone
                + "，模板ID: " + templateId
                + "，参数: " + params);
    }
}