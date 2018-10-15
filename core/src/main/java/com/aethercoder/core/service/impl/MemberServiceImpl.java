package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.dao.AccountDao;
import com.aethercoder.core.dao.MemberLevelDao;
import com.aethercoder.core.entity.member.MemberLevel;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.MemberService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.LocaleMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/1/23
 * @modified By:
 */
@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberLevelDao memberLevelDao;

    @Autowired
    public LocaleMessageService localeMessageUtil;

    @Autowired
    public AccountDao accountDao;

    @Override
    public List getMemberInformation() {
        List<MemberLevel> memberLevels = memberLevelDao.findAll();
        List list = new ArrayList();
        for (int i = 0; i < memberLevels.size(); i++) {
            Map<String, String> map = new TreeMap();
            String icon = memberLevels.get(i).getIcon();
            Integer level = memberLevels.get(i).getLevel();
            String strLevel = level.toString() + localeMessageUtil.getLocalMessage("MEMBER_LEVEL", null);
            Integer money = memberLevels.get(i).getMoney();
            String description = "";
            if (i == (memberLevels.size() - 1)) {
                description = money.toString() + "<" + localeMessageUtil.getLocalMessage("PURSE_ASSETS", null);
            } else {
                Integer maxMoney = memberLevels.get(i + 1).getMoney();
                description = money.toString() + "<" + localeMessageUtil.getLocalMessage("PURSE_ASSETS", null) + "≤" + maxMoney.toString();
            }
            map.put("icon", icon);
            map.put("strLevel", strLevel);
            map.put("description", description);
            list.add(map);
        }
        return list;
    }

    @Override
    public List getMember() {
        List<MemberLevel> memberLevels = memberLevelDao.findAll();
        for (int i = 0; i < memberLevels.size(); i++) {
            MemberLevel memberLevel = memberLevels.get(i);
            if (i == (memberLevels.size() - 1)) {
                memberLevel.setMoneyMax(null);
            } else {
                MemberLevel memberLevel1 = memberLevels.get(i + 1);
                memberLevel.setMoneyMax(memberLevel1.getMoney());
            }
        }
        return memberLevels;
    }

    @Override
    public Map saveMemberLevelByMoney(MemberLevel memberLevel) {
        Map map = new HashMap();
        //check level
        MemberLevel memberLevel1 = memberLevelDao.findMemberLevelByLevel(memberLevel.getLevel());
        if (memberLevel1 != null) {
            throw new AppException(ErrorCode.CHECK_MEMBER_LEVEL_MONEY_EXIST);
        }
        //check money
        MemberLevel memberLevel2 = memberLevelDao.findFirstByMoneyIsNotNullOrderByMoneyDesc();
        if (memberLevel2!=null && memberLevel.getMoney().compareTo(memberLevel2.getMoney())<=0){
            throw new AppException(ErrorCode.CHECK_MEMBER_LEVEL_MONEY);
        }
        memberLevelDao.save(memberLevel);
        map.put("result", "success");
        return map;

    }

    @Override
    public Map updateMemberLevelByMoney(MemberLevel memberLevel) {
        if (memberLevel == null) {
            throw new AppException(ErrorCode.CHECK_MEMBER_LEVEL_NOT_NULL);
        }
        Map map = new HashMap();
        memberLevelDao.save(memberLevel);
//        List<MemberLevel> memberLevels = memberLevelDao.findAll();
//        List<Account> accounts = accountDao.findAll();
//        for (Account account : accounts) {
//            if (account.getAssets() != null) {
//                Integer level = 0;
//                for (int i = (memberLevels.size() - 1); i >= 0; i--) {
//                    if (account.getAssets().compareTo(new BigDecimal(memberLevels.get(i).getMoney())) == 1){
//                        level = memberLevels.get(i).getLevel();
//                        break;
//                    }
//                }
//                account.setLevel(Long.valueOf(level));
//                accountDao.save(account);
//            }
//        }

        map.put("result","success");
        return map;
    }

    @Override
    public Map deleteMemberInformationByLevel(Long id) {
       /* List<MemberLevel> memberLevels = memberLevelDao.findAll();
        for (MemberLevel memberLevel : memberLevels) {
            if (null != level && level.equals(memberLevel.getLevel())) {
                memberLevelDao.delete(memberLevel);
            }
        }*/
        Map map = new HashMap();
        memberLevelDao.delete(id);
        map.put("result","success");
        return map;
    }

    @Override
    public MemberLevel getMemberLevelByAccountNo(String accountNo) {
        Account account = accountDao.findByAccountNo(accountNo);
        Integer level = account.getLevel().intValue();
        MemberLevel memberLevel = memberLevelDao.findMemberLevelByLevel(level);
        return memberLevel;
    }
}
