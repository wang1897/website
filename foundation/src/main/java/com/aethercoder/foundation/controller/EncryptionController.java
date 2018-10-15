package com.aethercoder.foundation.controller;

import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.basic.utils.AESUtil;
import com.aethercoder.basic.utils.BCryptUtil;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hepengfei on 28/12/2017.
 */
@RestController
@Profile("local")
@RequestMapping(value = "encryption")
public class EncryptionController {

    @RequestMapping( value = "/encrypt", method = RequestMethod.GET)
    @Transactional
    public String encrypt(HttpServletRequest request) {
        String str = request.getParameter("str");
        return AESUtil.encrypt(str, CommonConstants.AES_KEY);
    }

    @RequestMapping( value = "/decrypt", method = RequestMethod.GET)
    @Transactional
    public String decrypt(HttpServletRequest request) {
        String str = request.getParameter("str");
        return AESUtil.decrypt(str, CommonConstants.AES_KEY);
    }

    @RequestMapping( value = "/getSign", method = RequestMethod.GET)
    @Transactional
    public Map<String, String> getSign(HttpServletRequest request) throws Exception {
        String token = request.getParameter("token");
        if (token == null) token = "";
        String url = request.getParameter("url");
        Date date = new Date();
        String timestamp = date.getTime() + "";
        System.out.println(timestamp + CommonConstants.API_SIGN_SALT2 + url + CommonConstants.API_SIGN_SALT + "0000" + token);
        String sign = BCryptUtil.encodeBCrypt(timestamp + CommonConstants.API_SIGN_SALT2 + url + CommonConstants.API_SIGN_SALT + "0000" + token, null);
        Map<String, String> map = new HashMap<>();
        map.put("timestamp", timestamp);
        map.put("sign", sign);
        map.put("r", "0000");
        return map;
    }
}
