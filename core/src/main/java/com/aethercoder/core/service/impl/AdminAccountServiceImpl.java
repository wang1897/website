package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.basic.utils.MD5Util;
import com.aethercoder.core.dao.AdminAccountDao;
import com.aethercoder.core.entity.admin.AdminAccount;
import com.aethercoder.core.security.JwtTokenUtil;
import com.aethercoder.core.service.AdminAcountService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
@Service
public class AdminAccountServiceImpl implements AdminAcountService {

    private static Logger logger = LoggerFactory.getLogger(AdminAccountServiceImpl.class);

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AdminAccountDao adminAccountDao;

    @Override
    public AdminAccount loginAdminAccount(String name, String password) {
        try {
            String pwd= MD5Util.encodeMD5(password);
            AdminAccount adminAccount = adminAccountDao.findAdminAccountByNameAndPassword(name, pwd);
            if(adminAccount!=null){
                return adminAccount;
            } else{
                throw new AppException(ErrorCode.USER_PASSWORD_ERROR);
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("loginAdminAccount",e);
            throw new AppException(ErrorCode.USER_PASSWORD_ERROR);
        }
    }

    @Override
    public AdminAccount findByName(String name) {
        AdminAccount adminAccount = adminAccountDao.findAdminAccountByName(name);
        if(adminAccount==null){
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }
        return adminAccount;
    }

    @Override
    public AdminAccount findById(long id) {
        return adminAccountDao.findOne(id);
    }

    @Override
    public AdminAccount saveAdminAccount(AdminAccount adminAccount) {
        //md5加密
        try {
            adminAccount.setPassword(MD5Util.encodeMD5(adminAccount.getPassword()));
            if(null != adminAccountDao.findAdminAccountByName(adminAccount.getName())){
                throw new AppException(ErrorCode.USER_NAME_CHECK);
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("saveAdminAccount",e);
            throw new AppException(ErrorCode.ADD_FAIL);
        }
        return adminAccountDao.save(adminAccount);
    }

    @Override
    public AdminAccount updateAdminAccount(AdminAccount adminAccount) {
        try {
            adminAccount.setPassword(MD5Util.encodeMD5(adminAccount.getPassword()));
            AdminAccount adminAccount1 = adminAccountDao.findOne(adminAccount.getId());
            BeanUtils.copyPropertiesWithoutNull(adminAccount, adminAccount1);

        } catch (NoSuchAlgorithmException e) {
            logger.error("updateAdminAccount",e);
            throw new AppException(ErrorCode.UPDATE_FAIL);
        }
        return adminAccountDao.save(adminAccount);
    }

    @Override
    public AdminAccount getAdminAccountByToken(String token) {
        String name = jwtTokenUtil.getUsernameFromToken(token);
        return adminAccountDao.findAdminAccountByName(name);
    }

    @Override
    public List<AdminAccount> findAllAdminAccount() {
        return adminAccountDao.findAll();
    }

    @Override
    public void deleteAdminAccount(Long id) {
        adminAccountDao.delete(id);
    }

    @Override
    public void checkPasswordIsTrue(String password, Long id) {
        try {
            String pwd = MD5Util.encodeMD5(password);
            AdminAccount adminAccount = adminAccountDao.findAdminAccountByPasswordAndId(pwd,id);
            if(adminAccount==null){
                throw new AppException(ErrorCode.PASSWORD_ERROR);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
