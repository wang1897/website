package com.aethercoder.core.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "activate")
public class MailConfig {

    private String title;
    private String validationUrl;
    private String pwdUrl;
    private String fundUserValidationUrl;
    private String fundUserPwdUrl;

    private String qbaoFundUrl;

    public String getFundUserPwdUrl() {
        return fundUserPwdUrl;
    }

    public void setFundUserPwdUrl(String fundUserPwdUrl) {
        this.fundUserPwdUrl = fundUserPwdUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValidationUrl() {
        return validationUrl;
    }

    public void setValidationUrl(String validationUrl) {
        this.validationUrl = validationUrl;
    }

    public String getPwdUrl() {
        return pwdUrl;
    }

    public void setPwdUrl(String pwdUrl) {
        this.pwdUrl = pwdUrl;
    }

    public String getFundUserValidationUrl() {
        return fundUserValidationUrl;
    }

    public void setFundUserValidationUrl(String fundUserValidationUrl) {
        System.out.println("setFundUserValidationUrl");
        this.fundUserValidationUrl = fundUserValidationUrl;
    }

    public String getQbaoFundUrl() {
        return qbaoFundUrl;
    }

    public void setQbaoFundUrl(String qbaoFundUrl) {
        this.qbaoFundUrl = qbaoFundUrl;
    }

}
