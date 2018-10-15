package com.aethercoder.core.config.email;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @auther Guo Feiyan
 * @date 2017/11/6 下午1:35
 */
@Configuration
@ConfigurationProperties(prefix = "fund_mail")
public class FundServerConfig extends ServerConfig{
}
