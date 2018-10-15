package com.aethercoder.core.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/26
 * @modified By:
 */
public interface URLDecipheringService {
    String sealBuryingPointInfo(String accountNo, String version, String machineType);

    String sealBuryingPointInfoForAndroid(String accountNo, String version, String machineType,String language);

    Map getCustomerInfo(HttpServletRequest request);

    void sendSms(String orderId);

    void sendSmsPwd(String customerNo,String pwd);

    void sendSmsResetPwd(String customerNo,String pwd);

    void sendSmsCode(String code, Integer time,String customerNo);

    void sendSMSNotice(String accountNo,String unitAmount,String tokenUnitAmount);

    void sendMultiSms(String mobile , String templateCode);


}