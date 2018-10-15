package com.aethercoder.foundation.security.encryption;

import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.basic.utils.AESUtil;
import com.google.common.io.ByteStreams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hepengfei on 25/12/2017.
 */
public class ParameterRequestWrapper extends HttpServletRequestWrapper {

    private Map<String , String[]> params = new HashMap<String, String[]>();
    public ParameterRequestWrapper(HttpServletRequest request) {
        super(request);

    }
    /**
     * 处理初始化参数
     * @param request
     */
    public Map<String , Object> handleRequestMap(HttpServletRequest request){
        Enumeration enu = request.getParameterNames();
        Map<String , Object> map = new HashMap<>();
        while(enu.hasMoreElements()){
            String paraName = (String)enu.nextElement();
            try {
                String parameter = AESUtil.decrypt(request.getParameter(paraName), CommonConstants.AES_KEY);
                map.put(paraName, parameter);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return map;
    }

    public void handleRequestBody(HttpServletRequest request) throws IOException {
        final InputStream in = request.getInputStream();
        byte[] bytes = ByteStreams.toByteArray(in);

    }

    public ParameterRequestWrapper(HttpServletRequest request , Map<String , Object> extendParams) {
        this(request);
        addAllParameters(extendParams);//这里将扩展参数写入参数表
    }

    @Override
    public String getParameter(String name) {//重写getParameter，代表参数从当前类中的map获取
        String[]values = params.get(name);
        if(values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    public String[] getParameterValues(String name) {//同上
        return params.get(name);
    }

    public void addAllParameters(Map<String , Object>otherParams) {//增加多个参数
        for(Map.Entry<String , Object>entry : otherParams.entrySet()) {
            addParameter(entry.getKey() , entry.getValue());
        }
    }

    public void addParameter(String name , Object value) {//增加参数
        if(value != null) {
            if(value instanceof String[]) {
                params.put(name , (String[])value);
            }else if(value instanceof String) {
                params.put(name , new String[] {(String)value});
            }else {
                params.put(name , new String[] {String.valueOf(value)});
            }
        }
    }
}
