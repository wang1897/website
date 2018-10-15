package com.aethercoder.core.entity.security;

import java.io.Serializable;

/**
 * Created by hepengfei on 21/12/2017.
 */
public class CaptchaToken implements Serializable{
    private static final long serialVersionUID = 1L;
    private String token;
    private String verificationCode;
    private String deviceId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
