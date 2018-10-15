package com.aethercoder.foundation.config;

import com.aethercoder.foundation.security.APISignFilter;
import com.aethercoder.foundation.security.encryption.EncryptHttpFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Created by hepengfei on 26/12/2017.
 */
//@Configuration
public class FilterConfig {
//    @Autowired
    private EncryptHttpFilter encryptHttpFilter;

//    @Autowired
    private APISignFilter apiSignFilter;

//    @Bean
//    @Order(10)
    public FilterRegistrationBean ApiEncodeFilter() {
        System.out.println("ApiEncodeFilter");
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(encryptHttpFilter);
//        registration.setFilter(new EncryptHttpFilter());
// In case you want the filter to apply to specific URL patterns only
        registration.addUrlPatterns("*");
        return registration;
    }

//    @Bean
//    @Order(11)
    public FilterRegistrationBean ApiSignFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(apiSignFilter);
// In case you want the filter to apply to specific URL patterns only
        registration.addUrlPatterns("*");
        return registration;
    }

}
