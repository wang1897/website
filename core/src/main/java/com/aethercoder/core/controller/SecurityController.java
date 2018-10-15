package com.aethercoder.core.controller;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.entity.security.CaptchaToken;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.AppTokenService;
import com.aethercoder.core.service.CaptchaService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import io.jsonwebtoken.lang.Assert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import nl.captcha.Captcha;
import nl.captcha.servlet.CaptchaServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hepengfei on 18/12/2017.
 */

@RestController
@RequestMapping( "s" )
@Api( tags = "security", description = "APP安全接口" , produces = "application/json")
public class SecurityController {

    private static Logger logger = LoggerFactory.getLogger(SecurityController.class);

    @Autowired
    private AppTokenService appTokenService;

    @Autowired
    private CaptchaService captchaService;

    @ApiOperation( value = "Qbao 生成Token", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Map.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/gt", method = RequestMethod.POST, consumes = "application/json")
    public Map<String, String> generateToken(@RequestBody Map<String, String> accountMap) {
        String accountNo = accountMap.get("accountNo");
        Assert.notNull(accountNo, "accountNo cannot be null");
        String token = appTokenService.generateNewToken(accountNo);
        if (token == null) {
            throw new AppException(ErrorCode.ACCOUNTNO_NOT_EXIST);
        }
        Map<String, String> map = new HashMap<>();
        map.put("result", token);
        return map;
    }

    @RequestMapping( value = "/pay/gt", method = RequestMethod.POST, consumes = "application/json")
    public Map<String, String> generateToken(@RequestBody Account account) {
        String token = appTokenService.generateNewToken(account);
        if (token == null) {
            throw new AppException(ErrorCode.ACCOUNTNO_NOT_EXIST);
        }
        Map<String, String> map = new HashMap<>();
        map.put("result", token);
        return map;
    }


    @ApiOperation( value = "Qbao 生成验证码图片", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Map.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "/gc", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> generateCaptcha(@RequestParam String deviceId) {
        try {

            CaptchaToken captchaToken = captchaService.generateCaptchaToken(deviceId);
            Captcha captcha = captchaService.generateCaptcha(captchaToken, deviceId);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            CaptchaServletUtil.writeImage(os, captcha.getImage());
//            ImageIO.write(image, "jpg", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            InputStreamResource isr = new InputStreamResource(is);

            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.add("CAPTCHA", captchaToken.getToken());
            respHeaders.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);



            /*int width = 75;
            int height = 25;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = image.createGraphics();
            graphics2D.setBackground(Color.RED);
            graphics2D.fillRect(0, 0, width, height);
            Color c = new Color(0.662f, 0.469f, 0.232f);
            GradientPaint gp = new GradientPaint(30, 30, c, 15, 25, Color.black, true);
            graphics2D.setPaint(gp);
            Font font = new Font("Verdana", Font.CENTER_BASELINE, 20);
            graphics2D.setFont(font);
            graphics2D.drawString(captchaToken.getVerificationCode(), 5, 20);
            graphics2D.dispose();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            InputStreamResource isr = new InputStreamResource(is);

            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.add("CAPTCHA", captchaToken.getToken());
            respHeaders.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);*/

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
