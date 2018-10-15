package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.FundUserDao;
import com.aethercoder.core.dao.InvestigationDao;
import com.aethercoder.core.entity.event.FundUser;
import com.aethercoder.core.entity.event.Investigation;
import com.aethercoder.core.service.FundUserService;
import com.aethercoder.core.service.InvestigationService;
import com.aethercoder.core.service.SendMailService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import io.rong.RongCloud;
import io.rong.models.SMSSendCodeResult;
import io.rong.models.SMSVerifyCodeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiawei.tao on 2017/10/24.
 */
@Service
public class InvestigatonServiceImpl implements InvestigationService {

    private static Logger logger = LoggerFactory.getLogger(InvestigatonServiceImpl.class);
    @Autowired
    private InvestigationDao investigationDao;
    @Autowired
    private FundUserService fundUserService;
    @Autowired
    private FundUserDao fundUserDao;
    @Autowired
    private SendMailService sendMailService;
    @Value( "${rongCloud.appKey}" )
    private String appKey;
    @Value( "${rongCloud.appSecret}" )
    private String appSecret;

    @Override
    public Investigation save(Investigation investigaton) {

        if (validationEvent(investigaton)) {
            if (WalletConstants.INVESTIGATION_SOURCE == investigaton.getInvestigationNo()) {
                Investigation investigation = investigationDao.findInverstigationByEmail(investigaton.getEmail());
                if (null != investigation) {
                    throw new AppException(ErrorCode.USER_EMAIL_CHECK);
                }
            }
            investigaton.setStatus("0");
            investigationDao.save(investigaton);
        }
        return investigaton;
    }

    private boolean validationEvent(Investigation investigaton) {
        boolean result = true;
        // 判断名字中是否含有非法字符。
        Pattern pattern = Pattern.compile("[&<>\"'/]");
        Matcher matcher = pattern.matcher(investigaton.getUserName());
        if (matcher.find()) {
            throw new AppException(ErrorCode.FUNDUSER_NAME_INVALID);
        }

        // 判断证件格式是否正确 只能英数字加下划线
        if (null != investigaton.getCardNo()) {
            pattern = Pattern.compile("\\W");
            matcher = pattern.matcher(investigaton.getCardNo());
            if (matcher.find()) {
                throw new AppException(ErrorCode.FUNDUSER_CARD_INVALID);
            }
        }

        // 判断电话格式是否正确
        pattern = Pattern.compile("[^0-9 \\(\\)+-]");
        matcher = pattern.matcher(investigaton.getPhone());
        if (matcher.find()) {
            throw new AppException(ErrorCode.FUNDUSER_PHONE_INVALID);
        }

        // 判断邮件格式是否正确
        pattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        matcher = pattern.matcher(investigaton.getEmail());
        if (!matcher.matches()) {
            throw new AppException(ErrorCode.FUNDUSER_EMAIL_INVALID);
        }

        // 判断是否重复提交问卷
        if (investigaton.getId() == null) {
            Investigation investigation = investigationDao.findByAccountNo(investigaton.getAccountNo());
            if(investigation!=null){
                throw new AppException(ErrorCode.INVESTIGATION_EXIST);
            }
        }
        return result;
    }

    @Override
    public Investigation update(Investigation investigation) {
        Investigation investigation1 = investigationDao.findOne(investigation.getId());
        BeanUtils.copyProperties(investigation, investigation1);
        return investigationDao.save(investigation1);
    }

    @Override
    public Investigation findById(long id) {
        return investigationDao.findOne(id);
    }

    @Override
    public Investigation findByUserId(String accountNo) {
        return investigationDao.findByAccountNo(accountNo);
    }

    @Override
    public SMSSendCodeResult sendCode(String mobile, String templateId) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        SMSSendCodeResult sMSSendCodeResult = null;
        try {
            sMSSendCodeResult = rongCloud.sms.sendCode(mobile, templateId, "86", null, null);
        } catch (Exception e) {
            logger.error("---sendCode error----", e);
            throw new RuntimeException("sendCode error:" + e.getMessage());
        }
        return sMSSendCodeResult;
    }

    @Override
    public SMSVerifyCodeResult verifyCode(String sessionId, String code) {
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        SMSVerifyCodeResult sMSVerifyCodeResult = null;
        try {
            sMSVerifyCodeResult = rongCloud.sms.verifyCode(sessionId, code);
        } catch (Exception e) {
            logger.error("---verifyCode error----", e);
            throw new RuntimeException("verifyCode error:" + e.getMessage());
        }
        return sMSVerifyCodeResult;
    }

    @Override
    public Page<Investigation> findInvestigationByPage(Integer page, Integer size, String userName, String phone, String email, Boolean isEmailed, String investigationNo) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<Investigation> investigations = investigationDao.findAll(new Specification<Investigation>() {
            @Override
            public Predicate toPredicate(Root<Investigation> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (null != email && !"".equals(email)) {
                    list.add(criteriaBuilder.like(root.get("email").as(String.class), "%" + email + "%"));
                }
                if (null != userName && !"".equals(userName)) {
                    list.add(criteriaBuilder.like(root.get("userName").as(String.class), "%" + userName + "%"));
                }
                if (null != phone && !"".equals(phone)) {
                    list.add(criteriaBuilder.like(root.get("phone").as(String.class), "%" + phone + "%"));
                }
                if (null != isEmailed && !"".equals(isEmailed)) {
                    list.add(criteriaBuilder.equal(root.get("isEmailed").as(Boolean.class), isEmailed));
                }
                if (null != investigationNo && !"".equals(investigationNo)) {
                    list.add(criteriaBuilder.like(root.get("investigationNo").as(String.class), "%" + investigationNo + "%"));
                }

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return investigations;
    }

    @Override
    public Page<Investigation> findInvestigationForICOByPage(Integer page, Integer size, String userName, String phone, String email, String status) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.ASC, "id");
        Page<Investigation> investigations = investigationDao.findAll(new Specification<Investigation>() {
            @Override
            public Predicate toPredicate(Root<Investigation> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();

                // 群No
                Predicate pf = cb.equal(root.get("investigationNo").as(String.class), "1");

                if (null != email && !"".equals(email)) {
                    list.add(cb.like(root.get("email").as(String.class), "%" + email + "%"));
                }
                if (null != userName && !"".equals(userName)) {
                    list.add(cb.like(root.get("userName").as(String.class), "%" + userName + "%"));
                }
                if (null != phone && !"".equals(phone)) {
                    list.add(cb.like(root.get("phone").as(String.class), "%" + phone + "%"));
                }
                if (null != status && !"".equals(status)) {
                    list.add(cb.equal(root.get("status").as(String.class), status));
                }

                Predicate[] p = new Predicate[list.size()];
                return cb.and(cb.and(list.toArray(p)), pf);
            }
        }, pageable);
        return investigations;
    }

    @Override
    public void updateSuccessByList(List<Long> idList) {
        List<Investigation> investigationList = new ArrayList<Investigation>();
        List<FundUser> fundUserList = new ArrayList<FundUser>();
        idList.forEach(id -> {
            Investigation investigation = investigationDao.findOne(id);
            if (investigation != null && !WalletConstants.STATUS_DECLINED.equals(investigation.getStatus())) {
                investigation.setStatus(WalletConstants.STATUS_APPROVED);
                FundUser fundUser = fundUserService.findFundUserById(Long.parseLong(investigation.getAccountNo()));
                fundUser.setIcoQualification(WalletConstants.ICO_QUALIFICATION_APPROVED);
                investigationList.add(investigation);
                fundUserList.add(fundUser);
            }
        });
        investigationDao.save(investigationList);
        fundUserDao.save(fundUserList);
        // 发送邮件
        fundUserList.forEach(fundUser -> {
            sendMailService.sendFundUserICOSuccess(fundUser, null);
        });
    }


}
