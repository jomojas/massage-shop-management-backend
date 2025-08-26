package com.jiade.massageshopmanagement.dto.LoginDto;

public class SendCodeRequest {
    private String phone;
    private String captcha;

    // Getter 和 Setter
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCaptcha() { return captcha; }
    public void setCaptcha(String captcha) { this.captcha = captcha; }
}