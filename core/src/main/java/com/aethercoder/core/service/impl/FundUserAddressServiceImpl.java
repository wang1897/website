package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.FundUserAddressDao;
import com.aethercoder.core.entity.fundUser.FundUserAddress;
import com.aethercoder.core.service.FundUserAddressService;
import com.aethercoder.core.service.SysConfigService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.util.DateUtil;
import org.bitcoinj.core.Address;
import org.bitcoinj.params.QtumMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @auther Guo Feiyan
 * @date 2017/11/8 下午6:17
 */
@Service
public class FundUserAddressServiceImpl implements FundUserAddressService {

    private static Logger logger = LoggerFactory.getLogger(FundUserAddressServiceImpl.class);

    @Autowired
    private FundUserAddressDao fundUserAddressDao;

    @Autowired
    private SysConfigService sysConfigService;

//    private String icoAddress;

//    public String getIcoAddress() {
//        return icoAddress;
//    }

//    public void setIcoAddress(String icoAddress) {
//        this.icoAddress = icoAddress;
//    }

    @Override
    public FundUserAddress partIn(long userId) {
        FundUserAddress fundUserAddress = fundUserAddressDao.findFundUserAddressByUserid(userId);
        if (null == fundUserAddress){
            fundUserAddress = new FundUserAddress();
            //int id = new Long(userId).intValue();
            fundUserAddress.setUserid(userId);
            //fundUserAddress.setQtumAddress(walletService.getAddress(id));
//            fundUserAddress.setQtumAddress(sysConfigService.findSysConfigByName(WalletConstants.ICO_ADDRESS).getValue());
        }

        //获取ico开始时间
        String time = sysConfigService.findSysConfigByName(WalletConstants.ICO_START_TIME).getValue();
        Date date = DateUtil.stringToDate(time);
        //当前时间
        Date nowDate = new Date();
        //获取ico开关
        String isFinishedStr = sysConfigService.findSysConfigByName(WalletConstants.ICO_IS_FINISHED).getValue();
        boolean isFinished = Boolean.valueOf(isFinishedStr);
        String icoAddress = sysConfigService.findSysConfigByName(WalletConstants.ICO_ADDRESS).getValue();
//        if (null == icoAddress || "".equals(icoAddress)) {
//            setIcoAddress(sysConfigService.findSysConfigByName(WalletConstants.ICO_ADDRESS).getValue());
//        }

        //未开始或者已结束
        if (nowDate.before(date) || isFinished) {
            fundUserAddress.setQtumAddress("");
        } else {
            //已开始
            fundUserAddress.setQtumAddress(icoAddress);
        }

        return fundUserAddress;
    }

    @Override
    public FundUserAddress save(FundUserAddress fundUserAddress) {
        if (isValidAddress(fundUserAddress.getUserAddress())) {
            FundUserAddress fundUserAddress1 = fundUserAddressDao.findFundUserAddressByUserid(fundUserAddress.getUserid());
            if (null != fundUserAddress1) {
                BeanUtils.copyPropertiesWithoutNull(fundUserAddress, fundUserAddress1);
                return fundUserAddressDao.save(fundUserAddress1);
            }
        }
        return fundUserAddressDao.save(fundUserAddress);
    }

    private boolean isValidAddress(String address) {
        Address address1;
        try {
            address1 = new Address(new QtumMainNetParams(), address);
            System.out.println(address1);
        } catch (Exception ex) {
            throw new AppException(ErrorCode.INVALID_ADDRESS);
        }
        return true;
    }
}
