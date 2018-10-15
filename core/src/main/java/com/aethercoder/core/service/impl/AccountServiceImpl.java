package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.basic.utils.MD5Util;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.*;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.member.MemberLevel;
import com.aethercoder.core.entity.social.GroupMember;
import com.aethercoder.core.entity.social.UserGroup;
import com.aethercoder.core.entity.social.UserInfo;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.AccountAssets;
import com.aethercoder.core.entity.wallet.Address;
import com.aethercoder.core.service.*;
import com.aethercoder.core.util.wallet.CurrentNetParams;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.*;
import io.rong.RongCloud;
import io.rong.models.CodeSuccessResult;
import io.rong.models.TokenResult;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by hepengfei on 2017/8/30.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Value( "${rongCloud.appKey}" )
    private String appKey;
    @Value( "${rongCloud.appSecret}" )
    private String appSecret;

    @Autowired
    public LocaleMessageService localeMessageUtil;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private RecordPasswordDao recordPasswordDao;
    @Autowired
    private UserGroupDao userGroupDao;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private GroupMemberDao groupMemberDao;

    @Autowired
    private UserContactDao userContactDao;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private ExchangeLogService exchangeLogService;

    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private AppTokenService appTokenService;
    @Autowired
    AccountService accountService;

    @Autowired
    MemberLevelDao memberLevelDao;

    @Autowired
    private AccountAssetsDao accountAssetsDao;

    @Override
    public Account createAccount(Account account) {


        Boolean captchaValid = captchaService.verifyCaptcha(account.getCaptchaCode(), account.getCaptchaToken(), account.getDeviceId());
        if (!captchaValid) {
            throw new AppException(ErrorCode.INVALID_CAPTCHA_CODE);
        }
        // 防止注册机
        if (!StringUtils.isEmpty(account.getInviteCode())) {
            account.setInviteCode(account.getInviteCode().toUpperCase());
            Account account2 = accountDao.findAccountByShareCode(account.getInviteCode());
            if (null == account2) {
                throw new AppException(ErrorCode.SHARE_CODE_NOT_EXIST);
            }
            Integer number = accountDao.countByReceiveNumber(DateUtil.getBeforeDay(new Date()), account.getInviteCode());
            if (number >= 30) {
                throw new AppException(ErrorCode.INCORRECT_PARAM);
            }
        }
        //校验accountName是否包含表情
        if (null != account.getAccountName() && StringUtil.containsEmoji(account.getAccountName())) {
            throw new AppException(ErrorCode.NAME_CANNOT_CONTAIN_EMOJI);
        }
        boolean isDefault = false;
        Set<Address> addresseSet = account.getAddresses();
        for (Iterator<Address> it = addresseSet.iterator(); it.hasNext(); ) {
            Address address = it.next();
            if (address.getIsDefault() != null && address.getIsDefault()) {
                isDefault = true;
            }
            String strAddress = address.getAddress();
            org.bitcoinj.core.Address bitcoinAddr = null;
            try {
                bitcoinAddr = org.bitcoinj.core.Address.fromBase58(CurrentNetParams.getNetParams(), strAddress);
            } catch (Exception e) {
                throw new AppException(ErrorCode.INVALID_QTUM_ADDRESS, new String[]{strAddress});
            }
            if (bitcoinAddr == null) {
                throw new AppException(ErrorCode.INVALID_QTUM_ADDRESS, new String[]{strAddress});
            }
            Address pAddress = addressDao.getByAddress(strAddress);
            if (pAddress != null) {
                throw new AppException(ErrorCode.USER_ADDRESS_EXIST, new String[]{address.getAddress()});
            }
        }
        // 如果没有默认，设置第一个是默认
        if (!isDefault) {
            Iterator<Address> it = addresseSet.iterator();
            Address address = it.next();
            address.setIsDefault(true);
        }

        account.setAccountNo(generateAccountNo());
        //md5加密
        if (account.getPassword() != null) {
            try {
                account.setPassword(MD5Util.encodeMD5(account.getPassword()));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        //head默认default
        if (account.getHeader() == null) {
            account.setHeader(WalletConstants.DEFAULT_HEADER);
        }
        //每日邀请次数 默认30
        if (account.getInvitedDaily() == null) {
            account.setInvitedDaily(WalletConstants.DEFAULT_INVIYED_DAILY);
        }
        //分享码
        account.setShareCode(NumberUtil.number_10_to_N(Long.parseLong(account.getAccountNo()), WalletConstants.HEXADECIMAL_THREE_TWO));
        account.setReceiveNumber(WalletConstants.DEFAULT_RECEVIE_NUMBER);
        if (account.getNewType() == null) {
            account.setNewType(false);
        }
        //默认这个人的邀请奖励
        if (account.getInviteQbe() == null) {
            account.setInviteQbe(WalletConstants.DEFAULT_INVITE_AMOUNT);
        }
        initRongToken(account);
        account.setLoginTime(new Date());
        account.setSysBlack(false);
        account.setLevel(0L);
        account.setLevelStatus(true);
        account.setIsDaka(false);
        account.setAuthority(false);
        Account account1 = accountDao.save(account);

        Set<Address> addressSet = account.getAddresses();
        addressSet.forEach(address -> {
            address.setAccountId(account1.getId());
            addressDao.save(address);
        });
        //新人赠送2000 qbao_energy
        String amountStr = sysConfigService.findSysConfigByName(WalletConstants.USER_REWARDS).getValue();

        if (!amountStr.isEmpty() && !amountStr.equals("0")) {
            BigDecimal amount = new BigDecimal(amountStr);
            // 填写邀请 奖励翻倍
            if (!StringUtils.isEmpty(account.getInviteCode())) {
                amount = amount.add(account.getInviteQbe());
            }
            ExchangeLog exchangeLog = new ExchangeLog();
            exchangeLog.setAccountNo(account.getAccountNo());
            exchangeLog.setAmount(amount);
            exchangeLog.setType(WalletConstants.USER_REWARDS_TYPE);
            exchangeLogService.saveTakeBonus(exchangeLog, account, 0);
        }

        //24h内 邀请够三十个人 邀请次数不更新

        if (null != account.getInviteCode() && !account.getInviteCode().isEmpty()) {
            Integer number = accountDao.countByReceiveNumber(DateUtil.getBeforeDay(account.getCreateTime()), account.getInviteCode());
            if (number <= account.getInvitedDaily()) {
                //更新分享码的用户抽奖次数
                updateAccountByReceiveNumber(account, 1);
            }
        }

        return account;
    }

    @Override
    public Account importAccount(Account account) {
        Boolean captchaValid = captchaService.verifyCaptchaWithoutClear(account.getCaptchaCode(), account.getCaptchaToken(), account.getDeviceId());
        if (!captchaValid) {
            throw new AppException(ErrorCode.INVALID_CAPTCHA_CODE);
        }

        //校验accountName是否包含表情
        if (null != account.getAccountName() && StringUtil.containsEmoji(account.getAccountName())) {
            throw new AppException(ErrorCode.NAME_CANNOT_CONTAIN_EMOJI);
        }

        Account persistAccount = null;
        Set<Address> addressSet = account.getAddresses();
        for (Address address : addressSet) {
            persistAccount = accountDao.findByAddress(address.getAddress());
            if (persistAccount == null) {
                break;
            }
        }

        if (persistAccount != null) {

            List<String> addressStrList = new ArrayList<>();
            for (Address address : addressSet) {
                addressStrList.add(address.getAddress());
            }

            List<String> pAddressStrList = new ArrayList<>();
            for (Address address : persistAccount.getAddresses()) {
                pAddressStrList.add(address.getAddress());
            }

            if (addressStrList.containsAll(pAddressStrList)) {
                if (account.getAccountName() != null) {
                    persistAccount.setAccountName(account.getAccountName());
                }
                initRongToken(persistAccount);
                //强制更新newType
                if (account.getNewType() == null) {
                    persistAccount.setNewType(false);
                } else {
                    persistAccount.setNewType(account.getNewType());
                }
                persistAccount.setLoginTime(new Date());
                accountDao.save(persistAccount);
                return persistAccount;
            }
        }

        throw new AppException(ErrorCode.WALLET_NOT_EXIST);
    }

    @Override
    public Account changeDefaultAddress(String accountNo, String addressStr) {
        Account account = accountDao.findByAccountNo(accountNo);
        if (account == null) {
            throw new RuntimeException("Account: " + accountNo + " does not exist");
        }
        Set<Address> addressSet = account.getAddresses();
        boolean found = false;
        for (Address address : addressSet) {
            if (address.getAddress().equals(addressStr)) {
                found = true;
                address.setIsDefault(true);
            } else {
                address.setIsDefault(false);
            }
            addressDao.save(address);
        }
        if (!found) {
            throw new RuntimeException("Address: " + addressStr + " does not exist");
        }
        accountDao.save(account);
        return account;
    }

    private String generateAccountNo() {
        Integer maxValue = WalletConstants.ACCOUNT_NO_MAX;
        Integer minValue = WalletConstants.ACCOUNT_NO_MIN;
        List<String> reservedRegList = WalletConstants.RESERVED_ACCOUNT_NO_REG_EXP;
        while (true) {
            boolean isReserved = false;
            Random rand = new Random();
            int randNo = rand.nextInt(maxValue - minValue + 1) + minValue;
            String formatRandNo = NumberUtil.formatNumberToString(randNo, "00000000");
            for (String reservedRegExp : reservedRegList) {
                isReserved = Pattern.matches(reservedRegExp, formatRandNo);

                if (!isReserved) {
                    Account account = accountDao.findByAccountNo(formatRandNo);
                    if (account == null) {
                        return formatRandNo;
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public Account updateAccount(Account account) {
        //校验accountName是否包含表情
        if (null != account.getAccountName() && StringUtil.containsEmoji(account.getAccountName())) {
            throw new AppException(ErrorCode.NAME_CANNOT_CONTAIN_EMOJI);
        }
        Account pAccount = accountDao.findByAccountNo(account.getAccountNo());
        logger.warn("aaaaaaaaaaa accountNo:" + pAccount.getVersionForLock());
        if (account.getAccountName() != null) {
            pAccount.setAccountName(account.getAccountName());
        }
        if (account.getEmail() != null) {
            pAccount.setEmail(account.getEmail());
        }
        if (account.getHeader() != null) {
            pAccount.setHeader(account.getHeader());
            String headerName = account.getHeader();
            String ext = headerName.substring(headerName.lastIndexOf('.'));
            String filename = headerName.substring(0, headerName.indexOf(ext));
            pAccount.setBiggerHeader(filename + "_B" + ext);
        }
        if (account.getNewType() != null) {
            pAccount.setNewType(account.getNewType());
        }
        Long oldLevel = pAccount.getLevel();
        Boolean oldLevelStatus = pAccount.getLevelStatus();
        // 资产上报 modified by wang ling hua start
//        if (account.getAssets() != null){
        if (account.getAssets() != null || (account.getEthAssets() != null) ) {

            // account.getAssets需要按类型更新到wallet_account_assets表
            // assetsType:0: QTUM  1:ETH
            BigDecimal qtumAssets = account.getAssets() == null ? new BigDecimal(0) : account.getAssets();

            BigDecimal ethAssets = account.getEthAssets() == null ? new BigDecimal(0) : account.getEthAssets();

            // Qtum资产更新
            AccountAssets accountAssets = accountAssetsDao.findByAccountNoAndType(account.getAccountNo(), WalletConstants.ASSETS_TYPE_QTUM);
            if (accountAssets == null && account.getAssets() != null) {
                accountAssets = new AccountAssets();
                accountAssets.setAccountNo(account.getAccountNo());
                accountAssets.setType(WalletConstants.ASSETS_TYPE_QTUM);
            }
            if (accountAssets != null) {
                accountAssets.setAssets(qtumAssets);
                accountAssetsDao.saveAndFlush(accountAssets);

            }

            // ETH资产更新
            accountAssets = accountAssetsDao.findByAccountNoAndType(account.getAccountNo(), WalletConstants.ASSETS_TYPE_ETH);
            if (accountAssets == null && account.getEthAssets() != null) {
                accountAssets = new AccountAssets();
                accountAssets.setAccountNo(account.getAccountNo());
                accountAssets.setType(WalletConstants.ASSETS_TYPE_ETH);
            }

            if (accountAssets != null) {
                accountAssets.setAssets(ethAssets);
                accountAssetsDao.saveAndFlush(accountAssets);

            }

            //  合计总资产更新到到wallet_account
            BigDecimal sumAssets = qtumAssets.add(ethAssets);
            pAccount.setAssets(sumAssets);

            List<MemberLevel> memberLevels = memberLevelDao.findAll();
            Integer level = 0;
            for (int i = (memberLevels.size() - 1); i >= 0; i--) {
                // 等级按照合计好的资产计算
//                if (account.getAssets().compareTo(new BigDecimal(memberLevels.get(i).getMoney())) == 1) {
                if (pAccount.getAssets().compareTo(new BigDecimal(memberLevels.get(i).getMoney())) == 1) {

                    level = memberLevels.get(i).getLevel();
                    break;
                }
            }

            pAccount.setLevel(new Long(level));
//            pAccount.setAssets(account.getAssets());
            // 资产上报 modified by wang ling hua end

        }
        if (account.getLevelStatus() != null) {
            pAccount.setLevelStatus(account.getLevelStatus());
        }

        if (!pAccount.getLevelStatus().equals(oldLevelStatus) || !pAccount.getLevel().equals(oldLevel)) {
            // 用户等级或用户开关有变化的时候， 更新所有该用户的群信息
            List<GroupMember> groupMembers = groupMemberDao.findAllByMemberNoAndIsDeletedIsFalse(pAccount.getAccountNo());
            for (GroupMember groupMember : groupMembers) {
                groupMember.setLevel(pAccount.getLevel().intValue());
                groupMember.setLevelStatus(pAccount.getLevelStatus());
            }
            groupMemberDao.save(groupMembers);
        }
        // 设置用户语言
        pAccount.setLanguage(account.getLanguage());

//        BeanUtils.copyProperties(account, pAccount, "addresses", "password");
        Account sAccount = accountDao.save(pAccount);
        //更新融云的用户信息
        if (!account.getAccountName().equals(pAccount.getAccountName()) || !account.getHeader().equals(pAccount.getHeader())) {
            RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
            try {
                CodeSuccessResult userGetTokenResult = rongCloud.user.refresh(account.getAccountNo(), account.getAccountName(), account.getHeader());
                logger.debug("---updateAccount---", "---用户:" + account.getAccountNo() + ": " + userGetTokenResult.toString());
            } catch (Exception e) {
                logger.debug("---updateAccount---", e);
                throw new AppException(ErrorCode.UPDATE_FAIL);
            }
        }
        //更新群成员信息
        List<UserGroup> userGroups = userGroupDao.findUserGroupsByAccountNoAndIsDeletedIsFalse(pAccount.getAccountNo());
        userGroups.forEach(userGroup -> {
            if ((null == userGroup.getDisplayName() || userGroup.getDisplayName().isEmpty()) && (null == userGroup.getHeaderUrl() || userGroup.getHeaderUrl().isEmpty())) {
                //获取群成员信息
                GroupMember groupMember = groupMemberDao.findGroupMemberByGroupNoAndMemberNoAndIsDeletedFalse(userGroup.getGroupNo(), userGroup.getAccountNo());
                //更新我的群 群昵称，群头像
                if (groupMember != null) {
                    groupMember.setDisplayName(pAccount.getAccountName() == null ? "" : pAccount.getAccountName());
                    groupMember.setHeaderUrl(pAccount.getHeader() == null ? WalletConstants.DEFAULT_HEADER : pAccount.getHeader());
                    groupMemberDao.save(groupMember);
                }
            }
        });
        // groupMemberDao.updateGroupMemeberByAccountNo(pAccount.getAccountNo(), pAccount.getHeader(), pAccount.getAccountName());
        MemberLevel memberLevel = memberLevelDao.findMemberLevelByLevel(sAccount.getLevel().intValue());
        sAccount.setMemberLevel(memberLevel);
        return sAccount;
    }

    @Override
    public Account updateAccountByInviteDaily(Account account) {
        Account updateAccount = accountDao.findByAccountNo(account.getAccountNo());
//        if (updateAccount.getAssets() != null) {
//            List<MemberLevel> memberLevels = memberLevelDao.findAll();
//            Integer level = 0;
//            for (int i = (memberLevels.size() - 1); i >= 0; i--) {
//                if (updateAccount.getAssets().compareTo(new BigDecimal(memberLevels.get(i).getMoney())) == 1) {
//                    level = memberLevels.get(i).getLevel();
//                    break;
//                }
//            }
//            updateAccount.setLevel(Long.va/lueOf(level));
//            updateAccount.setAssets(account.getAssets());
//        }

        BeanUtils.copyPropertiesWithoutNull(account, updateAccount);
//        if (account.getInvitedDaily() != null && updateAccount != null) {
//            updateAccount.setInvitedDaily(account.getInvitedDaily());
//        }
//        if (account.getInviteQbe() != null && account.getInviteQbe().compareTo(new BigDecimal(0)) >= 0) {
//            updateAccount.setInviteQbe(account.getInviteQbe());
//        }
//        if (account.getSysBlack() != null){
//            updateAccount.setSysBlack(account.getSysBlack());
//        }
        return accountDao.save(updateAccount);
    }


   /* @Override
    public Account saveUser(Account account) {
        //校验accountName是否包含表情
        if (null != account.getAccountName() && StringUtil.containsEmoji(account.getAccountName())) {
            throw new AppException(ErrorCode.NAME_CANNOT_CONTAIN_EMOJI);
        }
        Account u = null;
        try {
            //md5加密
            account.setPassword(MD5Util.encodeMD5(account.getPassword()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //head默认default
        if (account.getHeader() == null) {
            account.setHeader(WalletConstants.DEFAULT_HEADER);
        }
        //检验邮箱是否存在
        Account account1 = accountDao.findByEmail(account.getEmail());
        if (null != account1) {
            throw new AppException(ErrorCode.USER_EMAIL_CHECK);
        }
        //acountNo随机6位数字
        account.setAccountNo(generateAccountNo());
        Account emailList = accountDao.findByEmail(account.getEmail());
        if (emailList != null) {
            throw new AppException(ErrorCode.USER_EMAIL_CHECK);
        }
        u = accountDao.save(account);
        sendMailService.sendCheckEmail(account);

        return u;

    }


    @Override
    public Account activateUser(String accountNo) {
        try {
            Account u = accountDao.findAccountByAccountNo(accountNo);
            u.setAccountNo(accountNo);
            u.setActivateType(WalletConstants.ACCOUNT_INACTIVE);
            //            initRongToken(u);
            Account userresult = accountDao.save(u);
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
    public Account login(String noOrEmail, String password) {
        Account userRest;
        boolean status = noOrEmail.contains("@");
        if (status) {

            userRest = accountDao.findByEmail(noOrEmail);
        } else {

            userRest = accountDao.findByAccountNo(noOrEmail);
        }

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
    }*/


    @Override
    public Account findAccountByAccountNo(String accountNo) {
        Account account = accountDao.findByAccountNo(accountNo);
        if (account == null) {
            throw new AppException(ErrorCode.ACCOUNTNO_NOT_EXIST);
        }
        MemberLevel memberLevel = memberLevelDao.findMemberLevelByLevel(account.getLevel().intValue());
        account.setMemberLevel(memberLevel);
        return account;
    }

    @Override
    public Account findByEmail(String email) {
        Account account = accountDao.findByEmail(email);
        return account;
    }

    /* @Override
      public Account saveAccount(Account account) {
          //校验accountName是否包含表情
          if (null != account.getAccountName() && StringUtil.containsEmoji(account.getAccountName())) {
              throw new AppException(ErrorCode.NAME_CANNOT_CONTAIN_EMOJI);
          }
          Account emailList = accountDao.findByEmail(account.getEmail());
          if (emailList != null) {
              throw new AppException(ErrorCode.USER_EMAIL_CHECK);
          }

          Account a = accountDao.findByAccountNo(account.getAccountNo());
          if (WalletConstants.ACCOUNT_INACTIVE.equals(a.getActivateType())) {
              throw new AppException(ErrorCode.USER_REGISRTERED);
          }

          try {

              a.setPassword(account.getPassword());
              a.setEmail(account.getEmail());
              a.setActivateType(WalletConstants.ACCOUNT_ACTVATED);
              a.setAccountNo(account.getAccountNo());
              //md5加密
              a.setPassword(MD5Util.encodeMD5(account.getPassword()));
              //head默认default
              if (account.getHeader() == null) {
                  account.setHeader(WalletConstants.DEFAULT_HEADER);
              }

              accountDao.save(a);
              sendMailService.sendCheckEmail(a);
              return a;
          } catch (NoSuchAlgorithmException e) {
              e.printStackTrace();
          }
          return a;
      }

      @Override
      public Account passwordResetEamil(String email) {
          Account a = accountDao.findByEmail(email);
          if (a == null) {
              throw new AppException(ErrorCode.EMAIL_NOT_EXIST);
          } else if (WalletConstants.ACCOUNT_ACTVATED.equals(a.getActivateType())) {
              throw new AppException(ErrorCode.USER_NOT_ACTIVATE);
          }
          RecordPassword recordPassword = new RecordPassword();
          recordPassword.setAccountId(a.getId());
          recordPassword.setLapsetype("0");
          recordPassword = recordPasswordDao.save(recordPassword);
          sendMailService.sendFindPwdEmail(a, recordPassword.getCode());

          return a;
      }

      @Override
      public Account passwordReset(String accountNo, String password, String code) {
          Account a = accountDao.findByAccountNo(accountNo);
          try {
              a.setPassword(MD5Util.encodeMD5(password));
              RecordPassword recordPassword = recordPasswordDao.findByCode(code);
              recordPassword.setLapsetype("1");
              recordPasswordDao.save(recordPassword);
          } catch (NoSuchAlgorithmException e) {
              logger.error("passwordReset", e);
          }
          a = accountDao.save(a);
          return a;
      }
  */
    @Override
    public Page<Account> findAccountsByPage(Integer page, Integer size) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        return accountDao.findAll(pageable);
    }

    @Override
    public Page<Account> findAccountsByPage(Integer page, Integer size, String email, String accountName, String accountNo,
                                            String sourceType, String activateType, String type, Integer invitedDailyMin,
                                            Integer invitedDailyMax, BigDecimal maxInviteAmount, BigDecimal minInviteAmount,
                                            Integer level, Boolean authority,Boolean orderDesc) {

        Pageable pageable = new PageRequest(page, size, orderDesc == true ?Sort.Direction.DESC : Sort.Direction.ASC, "id");
        Page<Account> accounts = accountDao.findAll(new Specification<Account>() {
            @Override
            public Predicate toPredicate(Root<Account> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (null != email && !"".equals(email)) {
                    list.add(criteriaBuilder.like(root.get("email").as(String.class), email + "%"));
                }
                if (null != accountName && !"".equals(accountName)) {
                    list.add(criteriaBuilder.like(root.get("accountName").as(String.class), accountName + "%"));
                }
                if (null != accountNo && !"".equals(accountNo)) {
                    list.add(criteriaBuilder.like(root.get("accountNo").as(String.class), accountNo + "%"));
                }
                if (null != sourceType && !"".equals(sourceType)) {
                    list.add(criteriaBuilder.equal(root.get("sourceType").as(String.class), sourceType));
                }
                if (null != activateType && !"".equals(activateType)) {
                    list.add(criteriaBuilder.equal(root.get("activateType").as(String.class), activateType));
                }
                if (null != invitedDailyMin && !"".equals(invitedDailyMin)) {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("invitedDaily").as(Integer.class), invitedDailyMin));
                }
                if (null != invitedDailyMax && !"".equals(invitedDailyMax)) {
                    list.add(criteriaBuilder.lessThanOrEqualTo(root.get("invitedDaily").as(Integer.class), invitedDailyMax));
                }
                if (WalletConstants.QBAO_ADMIN.equals(type)) {
                    list.add(criteriaBuilder.isNotEmpty(root.get("addresses")));
                }
                if (minInviteAmount != null && minInviteAmount.compareTo(new BigDecimal(0)) >= 0) {
                    //大于或等于传入最大值
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("inviteQbe").as(BigDecimal.class), minInviteAmount));
                }
                if (maxInviteAmount != null && maxInviteAmount.compareTo(new BigDecimal(0)) >= 0) {
                    //小于或等于传入时间
                    list.add(criteriaBuilder.lessThanOrEqualTo(root.get("inviteQbe").as(BigDecimal.class), maxInviteAmount));
                }
                if (level != null) {
                    list.add(criteriaBuilder.equal(root.get("level").as(Integer.class), level));
                }
                if (authority != null) {
                    list.add(criteriaBuilder.equal(root.get("authority").as(Boolean.class), authority));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);

        accounts.forEach(account -> {
            if (account.getShareCode()==null || account.getShareCode().isEmpty()){
                account.setAllInviteCodeCount(0);
                account.setInviteCodeCount(0);
            }else {
                account.setAllInviteCodeCount(accountDao.countByInviteCode(account.getShareCode()));
                account.setInviteCodeCount(accountDao.countByReceiveNumber(DateUtil.stringToDate(DateFormatUtils.format(new Date(), "yyyy-MM-dd 00:00:00")), account.getShareCode()));
            }
        });
        return accounts;
    }


    @Override
    public String getNewRongToken(Account account) {
        account.setRongToken("");
        initRongToken(account);
        accountDao.save(account);
        return account.getRongToken();
    }

    @Override
    public UserInfo syncUserInfo(String accountNo, String version) {
        UserInfo userInfo = new UserInfo();
        if (StringUtils.isEmpty(version)) {
            version = WalletConstants.TIMESTAMP_DEFAULT;
        }
        Date updateTime = DateUtil.stringToDate(version);
        Account account = accountDao.findByAccountNo(accountNo);
        userInfo.setAccount(account);
        userInfo.setVersion(DateUtil.dateToString(new Date()));
        userInfo.setUserGroupsList(userGroupDao.findAllByAccountNoAndUpdateTimeAfter(accountNo, updateTime));
//        userInfo.setGroupMembersList(groupMemberDao.findAllByUpdateTimeAfterForSync(accountNo,updateTime));
        userInfo.setGroupList(groupDao.findAllByUpdateTimeAfterForSync(accountNo, updateTime));
        userInfo.setUserContactsList(userContactDao.findByAccountNoAndUpdateTimeAfter(accountNo, updateTime));
        return userInfo;
    }

    @Override
    public Account getFriendInfo(String accountNo) {
        Account account = accountDao.findByAccountNo(accountNo);
        if (account == null) {
            throw new AppException(ErrorCode.ACCOUNTNO_NOT_EXIST);
        } else {
            Address address = addressDao.findAddressByAccountIdAndIsDefault(account.getId(), true);
            Set<Address> addresses = new HashSet(0);
            addresses.add(address);
            account.setAddresses(addresses);
        }
        MemberLevel memberLevel = memberLevelDao.findMemberLevelByLevel(account.getLevel().intValue());
        account.setMemberLevel(memberLevel);
        return account;
    }

    @Override
    public String getFriendName(String accountNo) {
        Account account = accountDao.findByAccountNo(accountNo);
        String accountName = "";
        if (account == null) {
            throw new AppException(ErrorCode.ACCOUNTNO_NOT_EXIST);
        } else {
            accountName = account.getAccountName();
        }
        return accountName;
    }

    @Override
    public Boolean checkAddress(String accountNo, String toAddress) {

        //校验地址
        Account account = findAccountByAccountNo(accountNo);
        Set<Address> addresses = account.getAddresses();
        Set addressStr = new HashSet();
        addresses.forEach(address -> {
            addressStr.add(address.getAddress());
        });
        return addressStr.contains(toAddress);
    }

    @Override
    public String getMainAddress(String accountNo) {
        //4.获得用户的默认地址
        Account account = findAccountByAccountNo(accountNo);
        Set<Address> addresses = account.getAddresses();
        String toAddress = null;
        for (Iterator<Address> it = addresses.iterator(); it.hasNext(); ) {
            Address address = it.next();
            if (address.getIsDefault() != null && address.getIsDefault()) {
                toAddress = address.getAddress();
                break;
            }
        }
        // 如果没有默认，设置第一个是默认
        if (toAddress == null && addresses != null) {
            Iterator<Address> it = addresses.iterator();
            Address address = it.next();
            address.setIsDefault(true);
            toAddress = address.getAddress();
            addressDao.save(address);
            accountDao.save(account);
        }
        return toAddress;
    }

    @Override
    public List<Account> getInviteRankingList() {

        List<Account> accountList = new ArrayList<Account>();
        List accounts = accountDao.getInviteRankingList();
        for (int i = 0; i < accounts.size(); i++) {
            Object[] objs = (Object[]) accounts.get(i);
            for (int j = 0; j < objs.length; j++) {
                Account account = new Account();
                account.setInviteCodeCount(Integer.parseInt(objs[j].toString()));
                account.setAccountNo(objs[j += 1].toString());
                account.setShareCode(objs[j += 1].toString());
                account.setAccountName(objs[j += 1].toString());
                account.setHeader(objs[j += 1].toString());
                accountList.add(account);
            }

        }
        return accountList;
    }

    @Override
    public void updateAccountByReceiveNumber(Account account, Integer number) {
        if (null == account.getInviteCode() || account.getInviteCode().isEmpty()) {
            return;
        }
        Account account2 = accountDao.findAccountByShareCode(account.getInviteCode());
        if (null == account2) {
            throw new AppException(ErrorCode.SHARE_CODE_NOT_EXIST);
        }
        Integer receiveNumber = account2.getReceiveNumber() == null ? WalletConstants.DEFAULT_RECEVIE_NUMBER : account2.getReceiveNumber();
        //该邀请码的用户抽奖次数加一/减一
        if (receiveNumber + number < 0) {
            throw new AppException(ErrorCode.NO_LOTTERY);
        }
        account2.setReceiveNumber(receiveNumber + number);
        accountDao.save(account2);
    }

    @Override
    public String getShareCode(String accountNo) {
        Account account = accountDao.findByAccountNo(accountNo);
        String shareCodeStr = null;
        if (null == account.getShareCode() || account.getShareCode().isEmpty()) {
            shareCodeStr = NumberUtil.number_10_to_N(Long.parseLong(accountNo), WalletConstants.HEXADECIMAL_THREE_TWO);
            account.setShareCode(shareCodeStr);
            updateAccount(account);
        }

        return account.getShareCode();
    }

    @Override
    public Account getAccountsProfile() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Account account = new Account();
        //累计用户量
        Integer countAll = accountDao.countAll();
        logger.info("累计用户量  countAll " + countAll);
        account.setCountAccountsByAll(countAll);
        //今日新增
        Integer countByDay = accountDao.countAccountsByDay();
        account.setCountAccountsByDay(countByDay);
        logger.info("今日新增  countByDay " + countByDay);
        //本月新增
        Integer countByMouth = accountDao.countAccountsByMouth();
        account.setCountAccountsByMouth(countByMouth);
        logger.info("本月新增  countByMouth " + countByMouth);
        //本月环比新增
        Date endDate = DateUtil.getLastMouth(new Date());

        Date startDate = DateUtil.getLastMouthFirstDay(endDate);
        logger.info("本月环比新增  startDate " + startDate);
        logger.info("本月环比新增  endDate " + endDate);
        Integer countByLastMouth = accountDao.countAccounts(startDate, endDate);
        logger.info("本月环比新增  countByLastMouth " + countByLastMouth);
        Integer newMouth = 0;
        if (countByLastMouth != 0) {
            newMouth = countByMouth / countByLastMouth;
        }

        account.setCountAccountsByMouthOfGrowth(newMouth);
        logger.info("本月环比新增  newMouth " + newMouth);

        return account;
    }

    // 初始获取融云token
    // 如果要重新设定token 请清空原account.rongToken的值。
    private void initRongToken(Account account) {
        // 已有rongToken，不需要初始化
        if (account.getRongToken() != null && !account.getRongToken().equals("")) {
            return;
        }
        try {
            RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
            // 获取 融云的Token 方法
            TokenResult userGetTokenResult = rongCloud.user.getToken(account.getAccountNo(), account.getAccountName(), account.getHeader());
            System.out.println("getToken:  " + userGetTokenResult.toString());
//            if(userGetTokenResult.getCode()!=200 && userGetTokenResult.getCode()==2007){
//
//            }
            account.setRongToken(userGetTokenResult.getToken());
            if (account.getRongVersion() != null && account.getRongVersion() > 0) {

                account.setRongVersion(account.getRongVersion() + 1);
            } else {
                // 设置 初始版本号
                account.setRongVersion(0L);
            }
        } catch (IllegalArgumentException iex) {
            // 参数错误
            throw new AppException(iex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException("getRongToken error:" + ex.getMessage());
        }
    }

    @Override
    public String initToken(String accountNo) {
        String token = appTokenService.generateNewToken(accountNo);
        return token;
    }

    @Override
    public List<Account> queryAccountsByLevel(Long level) {
        return accountDao.findAccountByLevel(level);
    }

    @Override
    public List<Account> queryAccountsByAuthority(Boolean authority) {
        return accountDao.findAccountByLevel(authority);
    }

    @Override
    public void updateAccountsByAccountNoAndAuthority(String accountNo, Boolean authority) {
        Account account = accountDao.findByAccountNo(accountNo);
        account.setAuthority(authority);
        accountDao.save(account);
    }

    @Override
    public void updateAccountsLanguage(Account accountOrg) {
        Account account = accountDao.findByAccountNo(accountOrg.getAccountNo());
        account.setLanguage(accountOrg.getLanguage());
        accountDao.save(account);
    }

    @Override
    public Locale getLocaleByAccount(String accountNo) {
        Account account = accountService.findAccountByAccountNo(accountNo);
        String language = account.getLanguage();
        Locale locale = Locale.ENGLISH;
        if (language != null) {
            if (language.equalsIgnoreCase(WalletConstants.LANGUAGE_TYPE_EN)) {
                locale = Locale.ENGLISH;
            } else if (language.equalsIgnoreCase(WalletConstants.LANGUAGE_TYPE_KO)) {
                locale = Locale.KOREAN;
            } else if (language.equalsIgnoreCase(WalletConstants.LANGUAGE_TYPE_ZH)) {
                locale = Locale.SIMPLIFIED_CHINESE;
            } else if(language.equalsIgnoreCase(WalletConstants.LANGUAGE_TYPE_JA)){
                locale = Locale.JAPANESE;
            }
        }
        return locale;
    }

    @Override
    public Account getAccountByAccountNoFromCache(String accountNo) {
        Account account = accountDao.findAccountByAccountNo(accountNo);
        return account;
    }

    @Override
    public Account updateLoginTime(String accountNo, Date loginTime) {
        Account account = accountDao.findByAccountNo(accountNo);
        if (account == null) return null;

        account.setLoginTime(loginTime);
        return accountDao.save(account);
    }

}
