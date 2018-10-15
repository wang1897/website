package com.aethercoder.core.service.impl;

import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.security.CaptchaToken;
import com.aethercoder.core.service.CaptchaService;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.FlatColorBackgroundProducer;
import nl.captcha.gimpy.FishEyeGimpyRenderer;
import nl.captcha.gimpy.RippleGimpyRenderer;
import nl.captcha.noise.CurvedLineNoiseProducer;
import nl.captcha.text.producer.DefaultTextProducer;
import nl.captcha.text.renderer.ColoredEdgesWordRenderer;
import nl.captcha.text.renderer.WordRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by hepengfei on 21/12/2017.
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Defining Character Array you can change accordingly
    private static final char[] chars = { '1', 'A', 'a', 'B', 'b', 'C',
            'c', '2', 'D', 'd', 'E', 'e', 'F', 'f', '3', 'G', 'g', 'H', 'h',
            'I', 'i', 'J', 'j', 'K', 'k', 'L', 'l', '4', 'M', 'm', 'N', 'n',
            'O', 'o', '5', 'P', 'p', 'Q', 'q', 'R', 'r', 'S', 's', 'T', 't',
            '6', '7', 'U', 'u', 'V', 'v', 'W', 'w', '8', 'X', 'x', '9',
            'Y', 'y', 'Z', 'z', '0' };

    //验证码数量
    private int _CodeCount = 4;
    //验证码宽度
    private int _width = 125;
    //验证码高度
    private int _height = 40;
    //验证码颜色
    private Color _CodeColor = Color.BLACK;
    //使用字体名字
    private String _FontName = "Verdana";
    //使用字体类型
    private int _FontType = Font.CENTER_BASELINE;
    //使用字体大小
    private int _FontSize = 30;
    //干扰线颜色
    private Color _NoiseColor = Color.BLACK;
    //干扰线大小
    private int _NoiseSize = 1;
    //干扰线条数
    private int _NoiseCount = 1;

    private static final Color[] colors = {Color.red, Color.black, Color.blue};

    // Method for generating the Captcha Code
    private String generateCaptchaText() {
        String randomStrValue = "";
        final int LENGTH = 4; // Character Length
        StringBuffer sb = new StringBuffer();
        int index = 0;
        for (int i = 0; i < LENGTH; i++) {
            // Getting Random Number with in range(ie: 60 total character present)
            index = (int) (Math.random() * (chars.length - 1));
            sb.append(chars[index]); // Appending the character using StringBuffer
        }
        randomStrValue = String.valueOf(sb); // Assigning the Generated Password to String variable
        return randomStrValue;
    }

    @Override
    public CaptchaToken generateCaptchaToken(String deviceId) {
        String verificationCode = generateCaptchaText();
        Random rand = new Random();
        int randNo = rand.nextInt(WalletConstants.APPID_MAX_VALUE - WalletConstants.APPID_MIN_VALUE+ 1) + WalletConstants.APPID_MIN_VALUE;
        String token = randNo + "";

        CaptchaToken captchaToken = new CaptchaToken();
        captchaToken.setDeviceId(deviceId);
        captchaToken.setVerificationCode(verificationCode);
        captchaToken.setToken(token);

        return captchaToken;
    }

    @Override
    public Captcha generateCaptcha(CaptchaToken captchaToken, String deviceId) {

        Captcha.Builder captcha = new Captcha.Builder(_width, _height);
        java.util.List<Font> fontList = new ArrayList<>();
        java.util.List<Color> colorList = new ArrayList<>();
        colorList.add(_CodeColor);
        fontList.add(new Font(_FontName, _FontType, _FontSize));
        WordRenderer dwr = new ColoredEdgesWordRenderer(colorList, fontList, 1);
        captcha.addText(new DefaultTextProducer(_CodeCount, captchaToken.getVerificationCode().toCharArray()), dwr);
        for (int i = 0; i < _NoiseCount; i++) {
            captcha.addNoise(new CurvedLineNoiseProducer(_NoiseColor, _NoiseSize));
        }
        captcha.gimp(new FishEyeGimpyRenderer(new Color(0, 0, 0, 0), new Color(0, 0, 0, 0)));
        captcha.gimp(new RippleGimpyRenderer());
        captcha.addBackground(new FlatColorBackgroundProducer(Color.white));
        captcha.build();
        Captcha captchas = captcha.build();

        captchaToken.setVerificationCode(captchas.getAnswer());
        redisTemplate.opsForValue().set(RedisConstants.REDIS_KEY_CAPTCHA + deviceId, captchaToken);
        redisTemplate.expire(RedisConstants.REDIS_KEY_CAPTCHA + deviceId, WalletConstants.CAPTCHA_EXPIRE_MINUTE, TimeUnit.MINUTES);

        return captchas;
//        CaptchaServletUtil.writeImage(resp, captchas.getImage());
//        CodeMemory = captchas.getAnswer();
    }

    @Override
    public Boolean verifyCaptcha(String verificationCode, String token, String deviceId) {
        CaptchaToken captchaToken = (CaptchaToken)redisTemplate.opsForValue().get(RedisConstants.REDIS_KEY_CAPTCHA + deviceId);
        if (captchaToken == null) {
            return false;
        }
        Boolean result = false;
        if (captchaToken.getToken().equals(token) && captchaToken.getVerificationCode().equalsIgnoreCase(verificationCode)) {
            result = true;
        }
        redisTemplate.delete(RedisConstants.REDIS_KEY_CAPTCHA + deviceId);
        return result;
    }

    @Override
    public Boolean verifyCaptchaWithoutClear(String verificationCode, String token, String deviceId) {
        CaptchaToken captchaToken = (CaptchaToken)redisTemplate.opsForValue().get(RedisConstants.REDIS_KEY_CAPTCHA + deviceId);
        if (captchaToken == null) {
            return false;
        }
        Boolean result = false;
        if (captchaToken.getToken().equals(token) && captchaToken.getVerificationCode().equalsIgnoreCase(verificationCode)) {
            result = true;
        }
        return result;
    }

    @Override
    public void clearCaptcha(String deviceId) {
        redisTemplate.delete(RedisConstants.REDIS_KEY_CAPTCHA + deviceId);
    }
}
