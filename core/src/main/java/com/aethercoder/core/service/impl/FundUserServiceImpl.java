package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.MD5Util;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.FundUserAddressDao;
import com.aethercoder.core.dao.FundUserDao;
import com.aethercoder.core.dao.InvestigationDao;
import com.aethercoder.core.dao.RecordPasswordDao;
import com.aethercoder.core.entity.event.FundUser;
import com.aethercoder.core.entity.event.Investigation;
import com.aethercoder.core.entity.fundUser.FundUserAddress;
import com.aethercoder.core.entity.wallet.RecordPassword;
import com.aethercoder.core.security.JwtTokenUtil;
import com.aethercoder.core.service.FundUserService;
import com.aethercoder.core.service.SendMailService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hepengfei on 2017/8/30.
 */
@Service
public class FundUserServiceImpl implements FundUserService {

    private static Logger logger = LoggerFactory.getLogger(FundUserServiceImpl.class);
    @Autowired
    private FundUserDao fundUserDao;

    @Autowired
    private FundUserAddressDao fundUserAddressDao;

    @Autowired
    private InvestigationDao investigationDao;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private RecordPasswordDao recordPasswordDao;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public FundUser updateFundUser(FundUser fundUser) {
        //校验fundUserName是否包含表情
        if (null != fundUser.getUserName() && StringUtil.containsEmoji(fundUser.getUserName())) {
            throw new AppException(ErrorCode.NAME_CANNOT_CONTAIN_EMOJI);
        }
        FundUser pFundUser = fundUserDao.findOne(fundUser.getId());

        if (fundUser.getUserName() != null) {
            pFundUser.setUserName(fundUser.getUserName());
        }
        if (fundUser.getEmail() != null) {
            pFundUser.setEmail(fundUser.getEmail());
        }

        fundUserDao.save(pFundUser);
        return pFundUser;
    }


    @Override
    public FundUser saveUser(FundUser fundUser) {
        //校验fundUserName是否包含表情
        if (null != fundUser.getUserName() && StringUtil.containsEmoji(fundUser.getUserName())) {
            throw new AppException(ErrorCode.NAME_CANNOT_CONTAIN_EMOJI);
        }
        FundUser u = null;
        try {
            //md5加密
            fundUser.setPassword(MD5Util.encodeMD5(fundUser.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // 判断名字中是否含有非法字符。
        Pattern pattern = Pattern.compile("[&<>\"'/]");
        Matcher matcher = pattern.matcher(fundUser.getUserName());
        if (matcher.find()) {
            throw new AppException(ErrorCode.FUNDUSER_NAME_INVALID);
        }
        // 判断邮件格式是否正确
        pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        matcher = pattern.matcher(fundUser.getEmail());
        if (!matcher.matches()) {
            throw new AppException(ErrorCode.FUNDUSER_EMAIL_INVALID);
        }
        FundUser emailList = fundUserDao.findByEmail(fundUser.getEmail());
        if (emailList != null) {
            throw new AppException(ErrorCode.USER_EMAIL_CHECK);
        }
        fundUser.setActivateType(WalletConstants.ACCOUNT_ACTVATED);
        u = fundUserDao.save(fundUser);
        sendMailService.sendFundUser(u, null);

        return u;

    }


    @Override
    public FundUser activateUser(String uniqueId ) {
        try {
            FundUser u = fundUserDao.findByUniqueId(uniqueId);
            if (u == null) {
                u = fundUserDao.findOne(Long.valueOf(uniqueId));
            }
            //若该用户已经激活
            if(WalletConstants.ACCOUNT_INACTIVE.equals(u.getActivateType())){
                throw new AppException(ErrorCode.ACTIVATED_STATE);
            }
            u.setActivateType(WalletConstants.ACCOUNT_INACTIVE);
            //            initRongToken(u);
            FundUser userresult = fundUserDao.save(u);
            if (!"".equals(userresult)) {
                return userresult;
            } else {
                throw new AppException(ErrorCode.USER_ACTICVATE);
            }
        } catch (Exception e) {
            logger.error("activateUser", e);
            throw new AppException(ErrorCode.USER_ACTICVATE);
        }
    }

    @Override
    public FundUser login(String nameOrEmail, String password) {
        FundUser userRest = fundUserDao.findByEmail(nameOrEmail);
        try {
            if (userRest == null) {
                throw new AppException(ErrorCode.USER_PASSWORD_ERROR);
            } else if (!MD5Util.encodeMD5(password).equals(userRest.getPassword())) {
                throw new AppException(ErrorCode.USER_PASSWORD_ERROR);
            } else if (WalletConstants.GETLAPSE_NOT_VALID.equals(userRest.getActivateType())) {
                throw new AppException(ErrorCode.USER_NOT_ACTIVATE);
            } else {
                return userRest;
            }
        } catch (NoSuchAlgorithmException e) {
            logger.error("login", e);
            throw new AppException(ErrorCode.USER_PASSWORD_ERROR);
        }
    }

    @Override
    public FundUser findFundUserByUserName(String fundUserName) {
        FundUser fundUser = fundUserDao.findByUsername(fundUserName);
        if (fundUser == null) {
            throw new AppException(ErrorCode.FUNDUSER_NOT_EXIST);
        }
        return fundUser;
    }

    @Override
    public FundUser findFundUserById(Long id) {
        FundUser fundUser = fundUserDao.findOne(id);
        if (fundUser == null) {
            throw new AppException(ErrorCode.FUNDUSER_NOT_EXIST);
        }
        return fundUser;
    }

    @Override
    public FundUser findByEmail(String email) {
        FundUser fundUser = fundUserDao.findByEmail(email);
        return fundUser;
    }

    @Override
    public FundUser saveFundUser(FundUser fundUser) {
        //校验fundUserName是否包含表情
        if (null != fundUser.getUserName() && StringUtil.containsEmoji(fundUser.getUserName())) {
            throw new AppException(ErrorCode.NAME_CANNOT_CONTAIN_EMOJI);
        }
        // 判断名字中是否含有非法字符。
        Pattern pattern = Pattern.compile("[&<>\"'/]");
        Matcher matcher = pattern.matcher(fundUser.getUserName());
        if (matcher.find()) {
            throw new AppException(ErrorCode.FUNDUSER_NAME_INVALID);
        }
        // 判断邮件格式是否正确
        pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        matcher = pattern.matcher(fundUser.getEmail());
        if (!matcher.matches()) {
            throw new AppException(ErrorCode.FUNDUSER_EMAIL_INVALID);
        }
        FundUser emailList = fundUserDao.findByEmail(fundUser.getEmail());
        if (emailList != null) {
            throw new AppException(ErrorCode.USER_EMAIL_CHECK);
        }

        FundUser a = fundUserDao.findOne(fundUser.getId());
        if (WalletConstants.ACCOUNT_INACTIVE.equals(a.getActivateType())) {
            throw new AppException(ErrorCode.USER_REGISRTERED);
        }

        try {

            a.setPassword(fundUser.getPassword());
            a.setEmail(fundUser.getEmail());
            a.setActivateType(WalletConstants.ACCOUNT_ACTVATED);
            //md5加密
            a.setPassword(MD5Util.encodeMD5(fundUser.getPassword()));
            fundUserDao.save(a);
            sendMailService.sendFundUser(a, null);
            return a;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return a;
    }

    @Override
    public FundUser passwordResetEamil(String email) {
        FundUser a = fundUserDao.findByEmail(email);
        if (a == null) {
            throw new AppException(ErrorCode.EMAIL_NOT_EXIST);
        } else if (WalletConstants.ACCOUNT_ACTVATED.equals(a.getActivateType())) {
            throw new AppException(ErrorCode.USER_NOT_ACTIVATE);
        }
        RecordPassword recordPassword = new RecordPassword();
        recordPassword.setId(a.getId());
        recordPassword.setLapsetype("0");
        recordPassword = recordPasswordDao.save(recordPassword);
        sendMailService.sendFundPwdEmail(a, recordPassword.getCode());

        return a;
    }

    @Override
    public FundUser passwordReset(String uniqueId, String password, String code) {
        FundUser a = fundUserDao.findByUniqueId(uniqueId);
        try {
            a.setPassword(MD5Util.encodeMD5(password));
            RecordPassword recordPassword = recordPasswordDao.findByCode(code);
            recordPassword.setLapsetype("1");
            recordPasswordDao.save(recordPassword);
        } catch (NoSuchAlgorithmException e) {
            logger.error("passwordReset", e);
        }
        a = fundUserDao.save(a);
        return a;
    }

    @Override
    public Page<FundUser> findFundUsersByPage(Integer page, Integer size) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        return fundUserDao.findAll(pageable);
    }

    @Override
    public Page<FundUser> findFundUsersByPage(Integer page, Integer size, String email, String fundUserName, String activateType, String icoType) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<FundUser> bookPage = fundUserDao.findAll(new Specification<FundUser>() {
            @Override
            public Predicate toPredicate(Root<FundUser> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (null != email && !"".equals(email)) {
                    list.add(criteriaBuilder.like(root.get("email").as(String.class), "%" + email + "%"));
                }
                if (null != fundUserName && !"".equals(fundUserName)) {
                    list.add(criteriaBuilder.like(root.get("username").as(String.class), "%" + fundUserName + "%"));
                }
                if (null != activateType && !"".equals(activateType)) {
                    list.add(criteriaBuilder.equal(root.get("activateType").as(String.class), activateType));
                }
                if (null != icoType && !"".equals(icoType)) {
                    list.add(criteriaBuilder.equal(root.get("icoQualification").as(String.class), icoType));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);

        bookPage.forEach(fundUser -> {
            if(!StringUtils.isEmpty(fundUser.getIcoQualification())&&
                    !WalletConstants.ICO_QUALIFICATION_NO_APPLY.equals(fundUser.getIcoQualification())) {
                FundUserAddress userAddress = fundUserAddressDao.findFundUserAddressByUserid(fundUser.getId());
                if(userAddress!=null) {
                    fundUser.setAddress(userAddress.getUserAddress());
                }
                Investigation investigation = investigationDao.findByAccountNo(fundUser.getId().toString());
                if(investigation!=null){
                    fundUser.setInvestigationId(investigation.getId());
                }
            }
        });
        return bookPage;
    }

    @Override
    public FundUser getFundUserByToken(String token) {
        String email = jwtTokenUtil.getUsernameFromToken(token);
        Assert.notNull(email,"email is null");
        return fundUserDao.findByEmail(email);
    }

}
