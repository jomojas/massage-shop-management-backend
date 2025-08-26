package com.jiade.massageshopmanagement.sms;

import java.util.Map;

public interface SmsService {
    /**
     * 发送短信
     * @param phone      手机号
     * @param templateId 模板ID
     * @param params     模板参数
     */
    void send(String phone, String templateId, Map<String, String> params);
}