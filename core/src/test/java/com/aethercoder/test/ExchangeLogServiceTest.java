package com.aethercoder.test;

import com.aethercoder.TestApplication;
import com.aethercoder.core.service.URLDecipheringService;
import com.aethercoder.core.util.OverlapImageUtil;
import com.aethercoder.core.util.PushUtil;
import com.alipay.fc.csplatform.common.crypto.Base64Util;
import com.alipay.fc.csplatform.common.crypto.CustomerInfoCryptoUtil;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hepengfei on 08/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class ExchangeLogServiceTest {
    @Autowired
    private URLDecipheringService urlDecipheringService;

    @Test
    public void test() throws Exception {
        System.out.println("1212");
//        String uri = urlDecipheringService.sealBuryingPointInfo("123456", "2.88", "ios");
//        System.out.println(uri);

//        String qrCodePath = "./upload/images/icon/M000000018qrCode.png";
//        String qrCodeBgPath = "qrcode_bg_zh.png";
//        mergeImage(qrCodeBgPath,qrCodePath);
    }
//    private static ResponseEntity<InputStreamResource> mergeImage(String bgPath, String fgPath) {
////        logger.info("getfile");
//
//        File file = null;
//
//        HttpHeaders respHeaders = new HttpHeaders();
//        String ext = bgPath.substring(bgPath.lastIndexOf('.') + 1);
//        if (ext.toLowerCase().equals("png")) {
//            respHeaders.setContentType(MediaType.IMAGE_PNG);
//        } else if (ext.toLowerCase().equals("gif")) {
//            respHeaders.setContentType(MediaType.IMAGE_GIF);
//        } else if (ext.toLowerCase().equals("jpg")) {
//            respHeaders.setContentType(MediaType.IMAGE_JPEG);
//        }
//        respHeaders.setContentLength(file.length());

//        try {
//            FileInputStream fis = new FileInputStream(file);
//        ByteArrayOutputStream os = OverlapImageUtil.overlapImage(bgPath,
//                fgPath, ext);
//
//        InputStream is = new ByteArrayInputStream(os.toByteArray());
//        InputStreamResource isr = new InputStreamResource(is);
//        return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
//        } catch (IOException ioe) {
//            throw new RuntimeException(ioe);
//        }
//    }
}

