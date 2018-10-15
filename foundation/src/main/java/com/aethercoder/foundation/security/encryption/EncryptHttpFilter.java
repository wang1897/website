package com.aethercoder.foundation.security.encryption;

import com.aethercoder.foundation.contants.CommonConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by hepengfei on 25/12/2017.
 */
//@Component
public class EncryptHttpFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        System.out.println("aaaaaaaaaaaa");
        Enumeration<String> headersNames = httpServletRequest.getHeaderNames();
        while(headersNames.hasMoreElements()) {
            String headerName = headersNames.nextElement();
            System.out.println(headerName);
            if (headerName.equals("admin")) {
                System.out.println(httpServletRequest.getHeader("admin"));
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        /*PathMatcher matcher = new AntPathMatcher();
        Boolean noEncryption = false;
        for(String path: CommonConstants.NO_ENCRYPTION_PATH) {
            if (matcher.match(path, httpServletRequest.getRequestURI())) {
                noEncryption = true;
                break;
            }
        }

        if (!noEncryption) {
            ParameterRequestWrapper requestWrapper = new ParameterRequestWrapper(httpServletRequest);
            Map<String, Object> map = requestWrapper.handleRequestMap(httpServletRequest);
            requestWrapper.addAllParameters(map);
            filterChain.doFilter(requestWrapper, httpServletResponse);
        } else {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        }*/

    }
}