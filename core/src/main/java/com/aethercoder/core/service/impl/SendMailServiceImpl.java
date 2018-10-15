package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.config.MailConfig;
import com.aethercoder.core.config.email.FundServerConfig;
import com.aethercoder.core.config.email.ServerConfig;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.BaseEntity;
import com.aethercoder.core.entity.event.FundUser;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.SendMailService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.util.UrlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
@Service
public class SendMailServiceImpl implements SendMailService{

    private static Logger logger = LoggerFactory.getLogger(SendMailServiceImpl.class);

    @Autowired
    private MailConfig mailConfig;

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private FundServerConfig fundServerConfig;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Override
    public void sendCheckEmail(Account account) {
        sendEmail(account, WalletConstants.EMAIL_VALIDATION,mailConfig.getValidationUrl(),null, WalletConstants.IO_TEMPLATE);
    }

    @Override
    public void sendFindPwdEmail(Account account,String code) {
        sendEmail(account, WalletConstants.PASSWORD_RESET,mailConfig.getPwdUrl(), code, WalletConstants.IO_TEMPLATE);
    }

    @Override
    public void sendFundUser(FundUser fundUser , String code){
        sendEmail(fundUser, WalletConstants.EMAIL_VALIDATION, mailConfig.getFundUserValidationUrl(),null, WalletConstants.FUND_TEMPLATE);
    }

    @Override
    public void sendFundUserICOSuccess(FundUser fundUser , String code){
        sendEmail(fundUser, WalletConstants.FUND_ICO_SUCCESS_TYPE, mailConfig.getQbaoFundUrl(),null, WalletConstants.FUND_ICO_SUCCESS_TEMPLATRE);
    }
    @Override
    public void sendFundUserICOFail(FundUser fundUser , String code){
        sendEmail(fundUser, WalletConstants.QBAO_FUND_HOMEPAGE, mailConfig.getQbaoFundUrl(),null, WalletConstants.FUND_ICO_FAIL_TEMPLATRE);
    }
    @Override
    public void sendFundPwdEmail(FundUser fundUser, String code) {
        sendEmail(fundUser, WalletConstants.PASSWORD_RESET, mailConfig.getFundUserPwdUrl(),code, WalletConstants.FUND_TEMPLATE);
    }


    private void sendEmail(BaseEntity user, String type, String emailUrl, String code, String htmlTemplate) {
        try {
            Account account = null;
            FundUser fundUser = null;
            String email;
            String userName;
            ServerConfig serverConfig1 = null;
            byte[] textByte = null;
            if (user instanceof Account) {
                serverConfig1 = serverConfig;
                account = (Account) user;
                email = account.getEmail();
                userName = account.getUserName();
                textByte = account.getAccountNo().toString().getBytes("UTF-8");
            } else {
                serverConfig1 = fundServerConfig;
                fundUser = (FundUser) user;
                email = fundUser.getEmail();
                userName = fundUser.getUserName();
                textByte = fundUser.getUniqueId().toString().getBytes("UTF-8");
            }
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(serverConfig1.getHost());
            sender.setPassword(serverConfig1.getPassword());
            sender.setUsername(serverConfig1.getUsername());
            sender.setPort(serverConfig1.getPort());
            sender.setDefaultEncoding(serverConfig1.getEncode());

            Properties properties = new Properties();
            properties.setProperty("mail.smtp.auth", "true");//开启认证
//            properties.setProperty("mail.debug", "true");//启用调试
            properties.setProperty("mail.smtp.timeout", "1000");//设置链接超时
            properties.setProperty("mail.smtp.port", Integer.toString(serverConfig1.getPort()));//设置端口
            properties.setProperty("mail.smtp.socketFactory.port", Integer.toString(serverConfig1.getPort()));//设置ssl端口
            properties.setProperty("mail.smtp.socketFactory.fallback", "false");
            properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            sender.setJavaMailProperties(properties);

            MimeMessage message = sender.createMimeMessage();

            //message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(serverConfig1.getUsername());
            helper.setTo(email);
            helper.setSubject(mailConfig.getTitle());

            String encodedText = UrlUtil.encodeBufferBase64(textByte);

            Map<String, Object> model = new HashMap<>();
            model.put("username", userName);
            String emailStrUrl = emailUrl + encodedText;
            model.put("type", type);
            if (code != null) {
                emailStrUrl += "&code=" + code;
                model.put("code", code);
            }
            model.put("url", emailStrUrl);
            //读取 html 模板
            Locale locale = LocaleContextHolder.getLocale();
            freemarker.template.Template template = freeMarkerConfigurer.getConfiguration().getTemplate(htmlTemplate, locale);//"sendEmailToFundUser.html");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            helper.setText(html, true);
            sender.send(message);
        } catch (Exception e) {
            logger.error("sendEmail",e);
            throw  new AppException(ErrorCode.EMAIL_SEND_FAIL);
        }
    }
}
