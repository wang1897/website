package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.*;
import com.aethercoder.core.entity.pay.Order;
import com.aethercoder.core.entity.wallet.CountryInformation;
import com.aethercoder.core.entity.wallet.Customer;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.CountryInformationService;
import com.aethercoder.core.service.CustomerService;
import com.aethercoder.core.service.SysConfigService;
import com.aethercoder.core.service.URLDecipheringService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.alipay.fc.csplatform.common.crypto.Base64Util;
import com.alipay.fc.csplatform.common.crypto.CustomerInfoCryptoUtil;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsBatchSend;
import com.yunpian.sdk.model.SmsSingleSend;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/26
 * @modified By:
 */
@Service
public class URLDecipheringServiceImpl implements URLDecipheringService {
    private static Logger logger = LoggerFactory.getLogger(URLDecipheringServiceImpl.class);

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private CountryInformationDao countryInformationDao;

    @Autowired
    private CountryInformationService countryInformationService;

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    public LocaleMessageService localeMessageUtil;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private SysConfigService sysConfigService;

    private static String publicKey = "fccsplatform_usercard__0VAOORk-pub-key_1292.pem";

    private final String apikey = "56cf9bab1f2434be90dc7292c11b95c7";

    @Override
    public String sealBuryingPointInfo(String accountNo, String version, String machineType) {
        try {
            JSONObject cinfo = new JSONObject();
            cinfo.put("userId", accountNo);
            cinfo.put("timestamp", System.currentTimeMillis());
//            cinfo.put("timestamp", "1111111");
            //用户ID
            cinfo.put("uname", accountNo);
            //qBao版本
            cinfo.put("appVersion", version);
            //机型ios/android
            cinfo.put("os", machineType);

            JSONObject extInfo = new JSONObject();
            extInfo.put("version", version);
            extInfo.put("machineType", machineType);
            cinfo.put("extInfo", extInfo);

            PublicKey publicKey = getPubKey();
            Map<String, String> map = CustomerInfoCryptoUtil.encryptByPublicKey(cinfo.toString(), publicKey);
            String uri = "?key=" + map.get("key") + "&cinfo=" + map.get("text");
            System.out.println(uri);
            return uri;
        } catch (Exception e) {
            System.out.println("sealBuryingPointInfo error:" + e.getStackTrace());
            throw new RuntimeException(e);
        }
//        return null;
    }

    @Override
    public String sealBuryingPointInfoForAndroid(String accountNo, String version, String machineType,String language) {
        String url = null;
        if (WalletConstants.LANGUAGE_TYPE_ZH.equals(language)){
            url = sysConfigService.findSysConfigByName(WalletConstants.CUSTOMER_SERVICE_URL_ZN).getValue();
        }else if (WalletConstants.LANGUAGE_TYPE_KO.equals(language)){
            url = sysConfigService.findSysConfigByName(WalletConstants.CUSTOMER_SERVICE_URL_KO).getValue();
        }else {
            url = sysConfigService.findSysConfigByName(WalletConstants.CUSTOMER_SERVICE_URL_EN).getValue();
        }
        try {
            JSONObject cinfo = new JSONObject();
            cinfo.put("userId", accountNo);
            cinfo.put("timestamp", System.currentTimeMillis());
            //用户ID
            cinfo.put("uname", accountNo);
            //qBao版本
            cinfo.put("appVersion", version);
            //机型ios/android
            cinfo.put("os", machineType);

            JSONObject extInfo = new JSONObject();
            extInfo.put("version", version);
            extInfo.put("machineType", machineType);
            cinfo.put("extInfo", extInfo);

            PublicKey publicKey = getPubKey();
            Map<String, String> map = CustomerInfoCryptoUtil.encryptByPublicKey(cinfo.toString(), publicKey);
            url = url + "?key=" + map.get("key") + "&cinfo=" + map.get("text");
            System.out.println(url);
            return url;
        } catch (Exception e) {
            System.out.println("sealBuryingPointInfo error:" + e.getStackTrace());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map getCustomerInfo(HttpServletRequest request) {
        Map resultList = new HashMap();
        try {
            String params = request.getParameter("cinfo");
            String key = request.getParameter("key");

            PublicKey publicKey = getPublicKey();
            String cinfo = CustomerInfoCryptoUtil.decryptByPublicKey(URLEncoder.encode(params, "UTF-8"), URLEncoder.encode(key, "UTF-8"), publicKey);
            JSONObject dataJsonObject = BeanUtils.jsonToObject(cinfo, JSONObject.class);

            resultList.put("message", "查询成功");
            resultList.put("success", true);

            Map result = new HashMap();
            result.put("userId", dataJsonObject.getString("userId"));
            result.put("telePhone", dataJsonObject.getString("version"));

            Map schema = new HashMap();
            Map properties = new HashMap();

//            Map account = new HashMap();
//            account.put("name", dataJsonObject.getString("accountNo"));
//            account.put("type", "text");

            //qbao版本
            Map version = new HashMap();
            version.put("name", dataJsonObject.getString("version"));
            version.put("type", "text");

//            properties.put("account", account);
            properties.put("version", version);
            schema.put("properties", properties);
            result.put("schema", schema);
            //加验时间戳
            result.put("timestamp", dataJsonObject.getString("timestamp"));
            resultList.put("result", result);

            System.out.println(resultList);
            logger.info(resultList.toString());
        } catch (Exception e) {

        }
        return resultList;
    }

    @Override
    @Transactional
    public void sendSms(String orderId) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#");
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        //获取订单信息
        Order order = orderDao.findByOrderId(orderId);
        //获取商户信息
        Customer customer = customerDao.findByCustomerNo(order.getCustomerId());

        String orderTime = sdf.format(order.getOrderTime()).toString();

        String amount = df.format(order.getAmount());
        String unitAmount = order.getUnit() + " " + amount;
        String orderID = order.getOrderId();
        String orderNo = orderID.substring(orderID.length() - 10);
        String accountNo = order.getAccountNo();
        String accountNoStr = "*****" + accountNo.substring(accountNo.length() - 3);

        //设置多语言
        //定义语言地区信息
        String templateCode = null;

        countryInformationService.setLocale(customer.getCountryInfoId());
        CountryInformation countryInformation = countryInformationDao.findOne(customer.getCountryInfoId());

        String sign = WalletConstants.QBAO_PAY_STR;
        //FORMAT UNITAMOUNT
        if (countryInformation.getCountry().equals(WalletConstants.LANGUAGE_TYPE_ZH)) {
            if (order.getUnit().equals(WalletConstants.CURRENCY_KRW)){
                unitAmount = amount + " 韩元";
                sign = WalletConstants.QBAO_PAY_STR;
            }else if (order.getUnit().equals(WalletConstants.CURRENCY_CNY)){
                unitAmount = amount + " 元";
                sign = WalletConstants.QBAO_PAY_STR_ZH;
            }else {
                unitAmount = amount + " 美元";
                sign = WalletConstants.QBAO_PAY_STR;
            }

        }
        if (order != null && order.getStatus().equals(WalletConstants.CONFIRMED)) {
            //支付成功
            templateCode = localeMessageUtil.getLocalMessage("PAY_SMS_SUCCESS", new String[]{orderTime, accountNoStr, unitAmount});
        } else if (order != null && order.getStatus().equals(WalletConstants.FAILED)) {
            //支付失败
            templateCode = localeMessageUtil.getLocalMessage("PAY_SMS_FAIL", new String[]{orderTime, accountNoStr, orderNo});
        }
        templateCode = sign + templateCode;
        logger.info("pay-sendSms  templateCode" + templateCode);
        logger.info("pay-sendSms  orderId:" + orderId + " accountNo：" + order.getAccountNo() + " phone：" + customer.getPhoneNumber() + " amount：" + order.getAmount()+" countryName:"+countryInformation.getCountryName());
        sendYunPianSms(templateCode,countryInformation.getTelNumber() + customer.getPhoneNumber());

       /* //初始化clnt,使用单例方式
        YunpianClient clnt = null;
        try {
            clnt = new YunpianClient("apikey").init();
            //发送短信API
            Map<String, String> param = clnt.newParam(2);
            param.put("apikey", apikey);
            param.put("text", templateCode);
            param.put("mobile", countryInformation.getTelNumber() + customer.getPhoneNumber());
            Result<SmsSingleSend> r = clnt.sms().single_send(param);
            //获取返回结果，返回码:r.getCode(),返回码描述:r.getMsg(),API结果:r.getData(),其他说明:r.getDetail(),调用异常:r.getThrowable()
            logger.info("pay-sendSms response: code " + r.getCode() + " message:" + r.getMsg() + " orderId:" + orderId + " accountNo：" + order.getAccountNo() + " phone：" + customer.getPhoneNumber() + " amount：" + order.getAmount());
            //账户:clnt.user().* 签名:clnt.sign().* 模版:clnt.tpl().* 短信:clnt.sms().* 语音:clnt.voice().* 流量:clnt.flow().* 隐私通话:clnt.call().*
            logger.info("pay-sendSms clny: mode " + clnt.tpl() + " message:" + clnt.sms());
        } catch (Exception ex) {
            throw ex;
        } finally {
            //释放clnt
            if (clnt != null) {
                clnt.close();
            }
        }*/
    }

    @Override
    @Transactional
    public void sendSMSNotice(String accountNo,String unitAmount,String tokenUnitAmount){
        //PAY_SMS_NOTICE
        SysConfig sysConfig = sysConfigDao.findSysConfigByName(WalletConstants.QBAO_PAY_NOTICE_PHONE);
        String phone = null;
        if (sysConfig != null) {
            phone = sysConfig.getValue();
        }
        //设置多语言
        //定义语言地区信息
        Locale currentLocale = new Locale(WalletConstants.LANGUAGE_TYPE_KO);
        LocaleContextHolder.setLocale(currentLocale);
        String templateCode = WalletConstants.QBAO_PAY_STR + localeMessageUtil.getLocalMessage("PAY_SMS_NOTICE", new String[]{accountNo, unitAmount,tokenUnitAmount});
        sendYunPianSms(templateCode,phone);
        logger.info("pay-sendSMSNotice :  accountNo：" + accountNo + " phone:" + phone+" unitAmount:"+unitAmount+" tokenUnitAmount:"+tokenUnitAmount+" templateCode:"+templateCode);
       /* //初始化clnt,使用单例方式
        YunpianClient clnt = null;
        try {
            clnt = new YunpianClient("apikey").init();
            //发送短信API
            Map<String, String> param = clnt.newParam(2);
            param.put("apikey", apikey);
            param.put("text", templateCode);
            param.put("mobile", phone);
            Result<SmsSingleSend> r = clnt.sms().single_send(param);
            //获取返回结果，返回码:r.getCode(),返回码描述:r.getMsg(),API结果:r.getData(),其他说明:r.getDetail(),调用异常:r.getThrowable()
            logger.info("pay-sendSmsNotice response: code " + r.getCode() + " message:" + r.getMsg() + " phone:" + phone+" templateCode:"+templateCode+" accountNo:"+accountNo+" unitAmount:"+unitAmount+" getThrowable:"+r.getThrowable());
            //账户:clnt.user().* 签名:clnt.sign().* 模版:clnt.tpl().* 短信:clnt.sms().* 语音:clnt.voice().* 流量:clnt.flow().* 隐私通话:clnt.call().*
            logger.info("pay-sendSmsNotice clny: mode " + clnt.tpl() + " message:" + clnt.sms());
        } catch (Exception ex) {
            throw ex;
        } finally {
            //释放clnt
            if (clnt != null) {
                clnt.close();
            }
        }*/
    }



    @Override
    @Transactional
    public void sendSmsPwd(String customerNo, String pwd) {
        SysConfig sysConfig = sysConfigDao.findSysConfigByName(WalletConstants.QBAO_PAY_PHONE);
        String phone = null;
        if (sysConfig != null) {
            phone = sysConfig.getValue();
        }
        //设置多语言
        //定义语言地区信息
        Locale currentLocale = new Locale(WalletConstants.LANGUAGE_TYPE_ZH);
        LocaleContextHolder.setLocale(currentLocale);
        String templateCode = WalletConstants.QBAO_PAY_STR + localeMessageUtil.getLocalMessage("QBAO_PAY_PWD_SMS", new String[]{customerNo, pwd});
        sendYunPianSms(templateCode,phone);
        logger.info("pay-sendSmsPwd :  customerNo：" + customerNo + " phone:" + phone);
       /* //初始化clnt,使用单例方式
        YunpianClient clnt = null;
        try {
            clnt = new YunpianClient("apikey").init();
            //发送短信API
            Map<String, String> param = clnt.newParam(2);
            param.put("apikey", apikey);
            param.put("text", templateCode);
            param.put("mobile", phone);
            Result<SmsSingleSend> r = clnt.sms().single_send(param);
            //获取返回结果，返回码:r.getCode(),返回码描述:r.getMsg(),API结果:r.getData(),其他说明:r.getDetail(),调用异常:r.getThrowable()
            logger.info("pay-sendSms response: code " + r.getCode() + " message:" + r.getMsg() + " customerNo：" + customerNo + " phone:" + phone);
            //账户:clnt.user().* 签名:clnt.sign().* 模版:clnt.tpl().* 短信:clnt.sms().* 语音:clnt.voice().* 流量:clnt.flow().* 隐私通话:clnt.call().*
            logger.info("pay-sendSms clny: mode " + clnt.tpl() + " message:" + clnt.sms());
        } catch (Exception ex) {
            throw ex;
        } finally {
            //释放clnt
            if (clnt != null) {
                clnt.close();
            }
        }*/

    }

    @Override
    @Transactional
    public void sendSmsResetPwd(String customerNo, String pwd) {
        Customer customer = customerService.findCustomerByCustomerNo(customerNo);
        if (customer.getStatus().equals(WalletConstants.CUSTOMER_STATUS_UNACTIVE)){
            throw new AppException(ErrorCode.CUSTOMER_NOT_EXIT);
        }
        //定义语言地区信息
        CountryInformation countryInformation = countryInformationDao.findOne(customer.getCountryInfoId());
        //设置多语言
        countryInformationService.setLocale(customer.getCountryInfoId());

        String templateCode =  localeMessageUtil.getLocalMessage("CUSTOMER_RESET_PWD", new String[]{pwd});
        String sign = WalletConstants.QBAO_STR;
        templateCode = sign + templateCode;
        sendYunPianSms(templateCode,countryInformation.getTelNumber() + customer.getPhoneNumber());
        logger.info("pay-sendSmsResetPwd  templateCode" + templateCode +" phone：" + customer.getPhoneNumber() +" customerNo:"+customerNo +" countryName:"+countryInformation.getCountryName());

    }

    @Override
    public void sendSmsCode(String code, Integer time, String customerNo) {
        Customer customer = customerService.findCustomerByCustomerNo(customerNo);
        if (customer.getStatus().equals(WalletConstants.CUSTOMER_STATUS_UNACTIVE)){
            throw new AppException(ErrorCode.CUSTOMER_NOT_EXIT);
        }
        //定义语言地区信息
        CountryInformation countryInformation = countryInformationDao.findOne(customer.getCountryInfoId());
        String templateCode =  localeMessageUtil.getLocalMessage("CUSTOMER_CODE_SMS", new String[]{code,time+""});
        String sign = WalletConstants.QBAO_STR;
        templateCode = sign + templateCode;
        sendYunPianSms(templateCode,countryInformation.getTelNumber() + customer.getPhoneNumber());
        logger.info("pay-sendSmsCode  templateCode" + templateCode +" phone：" + customer.getPhoneNumber() +" customerNo:"+customerNo +" countryName:"+countryInformation.getCountryName());

    }

    private void sendYunPianSms(String templateCode,String phone){
        //初始化clnt,使用单例方式
        YunpianClient clnt = null;
        try {
            clnt = new YunpianClient("apikey").init();
            //发送短信API
            Map<String, String> param = clnt.newParam(2);
            param.put("apikey", apikey);
            param.put("text", templateCode);
            param.put("mobile", phone);
            Result<SmsSingleSend> r = clnt.sms().single_send(param);
            //获取返回结果，返回码:r.getCode(),返回码描述:r.getMsg(),API结果:r.getData(),其他说明:r.getDetail(),调用异常:r.getThrowable()
            logger.info("pay-sendSms response: code " + r.getCode() + " message:" + r.getMsg() + " phone:" + phone+" 其他说明："+r.getDetail()+" 调用异常："+r.getThrowable()+" API结果："+r.getData());
            //账户:clnt.user().* 签名:clnt.sign().* 模版:clnt.tpl().* 短信:clnt.sms().* 语音:clnt.voice().* 流量:clnt.flow().* 隐私通话:clnt.call().*
            logger.info("pay-sendSms clny: mode " + clnt.tpl() + " message:" + clnt.sms());
        } catch (Exception ex) {
            throw ex;
        } finally {
            //释放clnt
            if (clnt != null) {
                clnt.close();
            }
        }

    }

    //还原出RSA公钥对象
    private PublicKey getPubKey() throws Exception {
//        File file = new ClassPathResource(publicKey).getFile();
//
//        FileInputStream fis = new FileInputStream(file);
        Resource resource = new ClassPathResource(publicKey);

        BufferedReader bis = new BufferedReader(new InputStreamReader(resource.getInputStream()));
//        InputStreamReader isr = new InputStreamReader(fis);
//        BufferedReader bis = new BufferedReader(isr);
        String lineStr = null;
        StringBuffer sb = new StringBuffer();
        while ((lineStr = bis.readLine()) != null) {
            sb.append(lineStr);
        }

        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64Util.decode(sb.toString()));
        KeyFactory keyFactory;
        keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        return key;
    }

    private PublicKey getPublicKey() throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64Util.decode(publicKey));
        KeyFactory keyFactory;
        keyFactory = KeyFactory.getInstance("RSA");
        PublicKey key = keyFactory.generatePublic(keySpec);
        return key;
    }

    @Override
    public void sendMultiSms(String mobile, String templateCode) {
        //设置多语言
        //定义语言地区信息
        Locale currentLocale = new Locale(WalletConstants.LANGUAGE_TYPE_ZH);
        LocaleContextHolder.setLocale(currentLocale);

        //初始化clnt
        YunpianClient clnt = null;
        try {
            clnt = new YunpianClient("apikey").init();
            //发送短信API
            Map<String, String> param = clnt.newParam(2);
            param.put("apikey", apikey);
            param.put("text", templateCode);
            param.put("mobile", mobile);
            if (!mobile.contains(",")) {
                Result<SmsSingleSend> r = clnt.sms().single_send(param);

                logger.info("pay-sendSms response: code " + r.getCode() + " message:" + r.getMsg() + " phone:" + mobile );
                logger.info("pay-sendSms clny: mode " + clnt.tpl() + " message:" + clnt.sms());
            } else {

                Result<SmsBatchSend> r = clnt.sms().batch_send(param);
                logger.info("pay-sendSms response: code " + r.getCode() + " message:" + r.getMsg() + " phone:" + mobile );
                logger.info("pay-sendSms clny: mode " + clnt.tpl() + " message:" + clnt.sms());
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            //释放clnt
            if (clnt != null) {
                clnt.close();
            }

        }


    }

}