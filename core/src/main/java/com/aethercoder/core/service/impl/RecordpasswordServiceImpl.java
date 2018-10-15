package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.RecordPasswordDao;
import com.aethercoder.core.entity.wallet.RecordPassword;
import com.aethercoder.core.service.RecordPasswordService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
@Service
public class RecordpasswordServiceImpl implements RecordPasswordService{

    private static Logger logger = LoggerFactory.getLogger(RecordpasswordServiceImpl.class);

    @Autowired
    private RecordPasswordDao recordPasswordDao;

    @Override
    public RecordPassword checkUrl(String code) {
        try {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        RecordPassword recordPassword = recordPasswordDao.findByCode(code);
            //判断是否在24小时之内
            if(recordPassword!=null){
                if(DateUtil.dateCompare(df.parse(df.format(new Date())),DateUtil.getNextDay(recordPassword.getCreateTime()))){
                    return recordPassword;
                }else if(WalletConstants.GETLAPSE_VALID.equals(recordPassword.getLapseType())){
                    throw  new AppException(ErrorCode.EMAIL_URL_EXIST);
                }else{
                throw  new AppException(ErrorCode.EMAIL_URL_EXIST);
                }
         }else{
                throw  new AppException(ErrorCode.EMAIL_URL_EXIST);
            }

        } catch (ParseException e) {
            logger.error("checkUrl",e);
            throw  new AppException(ErrorCode.EMAIL_URL_EXIST);
        }
    }
}
