package com.aethercoder.core.service.impl;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.controller.FileController;
import com.aethercoder.core.dao.AndroidDao;
import com.aethercoder.core.dao.SysConfigDao;
import com.aethercoder.core.entity.android.Android;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.VerifyIOSService;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.net.InetAddress;

/**
 * @author jiawei.Tao on 2017/10/30.
 */
@Service
public class VerifyIOSSerivceImpl implements VerifyIOSService {

    private static Logger logger = LoggerFactory.getLogger(VerifyIOSSerivceImpl.class);

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private AndroidDao androidDao;

    @Override
    public boolean isVerifyForIOS(HttpServletRequest request) {
        boolean result = false;
        String iosVersion = request.getHeader(WalletConstants.IOS_VERSION);
        // 是否处于IOS审核期间
        SysConfig isVerifyForIOS = sysConfigDao.findSysConfigByName(WalletConstants.IS_IOS_VERIFY);
        if(isVerifyForIOS==null){
            return true;
        }
        String iosValue = isVerifyForIOS.getValue();
        //判断版本是否一致
        Android android = androidDao.findFirstBySourceIsFalseOrderByIdDesc();
        if(null == android){return false;}
        //处于IOS审核期间
        Boolean isVerify = Boolean.valueOf(iosValue);

        if(isVerify && null != iosVersion && iosVersion.equals(android.getVersionCode())){
            result = true;
        }
        return result;
//        // 设备是否是国外的
//        String strAbroad = request.getHeader(WalletConstants.IS_ABROAD).toString();
//        Boolean isAbroad = strAbroad != null && "1".equals(strAbroad) ? true : false;
//        if (!isAbroad) {
//            // 非国外设备 直接返回false;
//            return result;
//        }
//        String ipAddress;
//        try {
//            ipAddress = NetworkUtil.getIpAddress(request);
//        } catch (IOException ex) {
//            throw new RuntimeException(ex.getMessage());
//        }
//        if (!StringUtils.isEmpty(ipAddress)) {
//            // 取得IP所在国家
//            String country = getIsoCode(ipAddress);
//            // 审核期间在美国的国外设备不能参加活动
//            if (!StringUtils.isEmpty(country) && !"CN".equals(country)) {
//                result = true;
//            }
//
//        } else {
//            // 无法取得IP
//            result = true;
//        }
//        return result;
    }


    public String getIsoCode(String ip) {

        String result = null;
        InputStream is = FileController.class.getClassLoader().getResourceAsStream(WalletConstants.GET_DATA_CITY_IP);
        // This creates the DatabaseReader object. To improve performance, reuse
        // the object across lookups. The object is thread-safe.
        DatabaseReader reader = null;
        try {
            reader = new DatabaseReader.Builder(is).build();
            InetAddress ipAddress = InetAddress.getByName(ip);
            // Replace "city" with the appropriate method for your database, e.g.,
            // "country".
            CityResponse response = reader.city(ipAddress);
            Country country = response.getCountry();
            result = country.getIsoCode();            // 'US'
        } catch (Exception e) {
            logger.error("没有查到此ip:" + ip);
        }
        return result;
    }
}
