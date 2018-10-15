package com.aethercoder.foundation.security;

import com.aethercoder.basic.utils.BCryptUtil;
import com.aethercoder.foundation.contants.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by hepengfei on 26/12/2017.
 */
//@Component
public class APISignFilter extends OncePerRequestFilter {

    private String appHeader = CommonConstants.HEADER_APP_TOKEN_KEY;

    private String tokenHeader = CommonConstants.HEADER_WEB_TOKEN_KEY;

    private String timestampHeader = CommonConstants.HEADER_TIMESTAMP_KEY;

    private String signHeader = CommonConstants.HEADER_SIGN_KEY;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        PathMatcher matcher = new AntPathMatcher();
        Boolean noSecure = false;
        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            noSecure = true;
        } else {
            for (String path : CommonConstants.NO_SIGN_PATH) {
                if (matcher.match(path, request.getRequestURI())) {
                    noSecure = true;
                    break;
                }
            }
        }
//        logger.info("start do sign filter");

        if (!noSecure) {
//            logger.info("start do sign filter11");
            Long timestamp = Long.valueOf(request.getHeader(timestampHeader));
//            logger.info("start do sign filter22");
            Date apiDate = new Date(timestamp);
//            logger.info("start do sign filter33");
            Calendar calMin = Calendar.getInstance();
//            logger.info("start do sign filter44");
            Calendar calMax = Calendar.getInstance();
            calMin.add(Calendar.HOUR, -25);
            calMax.add(Calendar.HOUR, 25);

            if (apiDate.before(calMin.getTime()) || apiDate.after(calMax.getTime())) {
//                logger.info("start do sign filter55");
                throw new ServletException("illegal call");
            }

//            logger.info("start do sign filter66");
            String sign = request.getHeader(signHeader);
//            logger.info("start do sign filter77");
            String token = request.getHeader(tokenHeader);
//            logger.info("start do sign filter88");
            String randNum = request.getHeader(CommonConstants.HEADER_SIGN_RANDOM);
            if (token == null) {
                token = request.getHeader(appHeader);
            }
            if (token == null) {
                token = "";
            }
//            logger.info("start do sign filter99");
            String url = request.getRequestURL().toString();
            Enumeration<String> paraNames = request.getParameterNames();
            List<String> paraList = new ArrayList<>();
            while(paraNames.hasMoreElements()) {
                String paraName = paraNames.nextElement();
                String paraValue = request.getParameter(paraName);
                paraList.add(paraName + "=" + paraValue);
            }
            String queryString = StringUtils.collectionToDelimitedString(paraList, "&");

            if (!queryString.equals("")) {
                queryString = "?" + queryString;
            }
//            logger.info("start do sign filter1010");
            String plainSign ;
            if(request.getRequestURI().contains("/order/")) {
                plainSign = timestamp + CommonConstants.API_SIGN_SALT2_ORDER + url + queryString + CommonConstants.API_SIGN_SALT_ORDER + randNum + token;
            }else {
                plainSign = timestamp + CommonConstants.API_SIGN_SALT2 + url + queryString + CommonConstants.API_SIGN_SALT + randNum + token;
            }
            if (!BCryptUtil.checkMatch(plainSign, sign)) {
                logger.error("plainSign: " + plainSign + "==========md5Sign: md5Sign" + "==========md5parameter: " + sign);
                throw new ServletException("illegal call");
            }
            Object redisSign = redisTemplate.opsForValue().get(CommonConstants.REDIS_KEY_SIGN + sign);
            if (redisSign != null) {
                throw new ServletException("duplicate call");
            }
//            logger.info("start do sign filter1111");
            redisTemplate.opsForValue().set(CommonConstants.REDIS_KEY_SIGN + sign, sign);
//            logger.info("start do sign filter1212");
            redisTemplate.expire(CommonConstants.REDIS_KEY_SIGN + sign, 1, TimeUnit.HOURS);
//            logger.info("start do sign filter1313");
        }

        filterChain.doFilter(request, response);
    }
}
