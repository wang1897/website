package com.aethercoder.foundation.advice;

import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.AESUtil;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.foundation.contants.CommonConstants;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hepengfei on 02/01/2018.
 */
//@Component
//@ControllerAdvice
public class EncryptionAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (o instanceof ReqExceptionEntity) {
            return o;
        }
        PathMatcher matcher = new AntPathMatcher();
        Boolean noEnc = false;
        for (String path : CommonConstants.NO_ENCRYPTION_RESPONSE_PATH) {
            if (matcher.match(path, serverHttpRequest.getURI().getPath())) {
                noEnc = true;
                break;
            }
        }
        if (noEnc) {
            return o;
        }

        String json = BeanUtils.objectToJson(o);
        String encrypted = AESUtil.encrypt(json, CommonConstants.AES_KEY);
        Map<String, String> result = new HashMap<>();
        result.put("result", encrypted);
        return result;
    }
}
