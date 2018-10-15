package com.aethercoder.core.service;

import com.aethercoder.core.entity.security.CaptchaToken;
import nl.captcha.Captcha;

/**
 * Created by hepengfei on 21/12/2017.
 */
public interface CaptchaService {
    CaptchaToken generateCaptchaToken(String deviceId);
    Boolean verifyCaptcha(String verificationCode, String token, String deviceId);
    Boolean verifyCaptchaWithoutClear(String verificationCode, String token, String deviceId);
    void clearCaptcha(String deviceId);
    Captcha generateCaptcha(CaptchaToken captchaToken, String deviceId);
}
