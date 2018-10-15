package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.*;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.result.AccountInvitingInfo;
import com.aethercoder.core.entity.result.InvitingRewardInfo;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.AccountSubsidiary;
import com.aethercoder.core.entity.wallet.InvitingReward;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.AccountService;
import com.aethercoder.core.service.InviteService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.util.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/8
 * @modified By:
 */
@Service
public class InviteServiceImpl implements InviteService {
    @Autowired
    private AccountDao accountDao;
    @Autowired
    private AccountBalanceDao accountBalanceDao;
    @Autowired
    private AccountSubsidiaryDao accountSubsidiaryDao;
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private InvitingRewardDao invitingRewardDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private SysConfigDao sysConfigDao;
    @Autowired
    private ExchangeLogDao exchangeLogDao;

    @Override
    public AccountInvitingInfo accountInvitingInfo(String accountNo){
        Account account = accountDao.findByAccountNo(accountNo);
        AccountInvitingInfo accountInvitingInfo = new AccountInvitingInfo();
        if (account != null){
            String shareCode = accountService.getShareCode(accountNo);
            accountInvitingInfo.setShareCode(shareCode);
            Integer invitingNumber = accountDao.countByInviteCode(shareCode);
            accountInvitingInfo.setInvitingNumber(invitingNumber);

            BigDecimal invitingReward = exchangeLogDao.sumByTypeAndAccountNo(WalletConstants.USER_REWARDS_TYPE,accountNo);
            accountInvitingInfo.setInvitingAward(invitingReward);

            List<Account> accountList = accountDao.findByInviteCode(shareCode);
            int gainedNumber = 0;
            if (accountList != null){
                for (Account invitedAccount : accountList) {
                    AccountSubsidiary accountSubsidiary = accountSubsidiaryDao.findByAccountNo(invitedAccount.getAccountNo());
                    if (accountSubsidiary != null){
                        char[] chars = accountSubsidiary.getIsReceive().toCharArray();
                        if (chars[2] == '0'){
                            gainedNumber++;
                        }
                    }
                }
            }
            accountInvitingInfo.setGainedNumber(gainedNumber);
        }
        return accountInvitingInfo;
    }
    @Override
    public Account accountInvitedInfo(String shareCode) {
        Account account = accountDao.findAccountByShareCode(shareCode);
        return account == null ? new Account() : account;
    }

    @Override
    public InvitingRewardInfo invitingRewardInfo(String accountNo) {
        Long qbeUnit = contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE).getId();

        AccountSubsidiary accountSubsidiary = accountSubsidiaryDao.findByAccountNo(accountNo);
        InvitingRewardInfo invitingRewardInfo = new InvitingRewardInfo();
        Account account = accountDao.findByAccountNo(accountNo);
        String isReceive = "11111";
        if (accountSubsidiary == null) {
            accountSubsidiary = new AccountSubsidiary();
            accountSubsidiary.setAccountNo(accountNo);
        } else {
            if (StringUtils.isNotBlank(accountSubsidiary.getIsReceive())) {
                isReceive = accountSubsidiary.getIsReceive();
            }
        }
        char[] chars = isReceive.toCharArray();
        Date createTime = account.getCreateTime();
        String startTime = sysConfigDao.findSysConfigByName(WalletConstants.INVITEING_REWARD_START).getValue();
        String endTime = sysConfigDao.findSysConfigByName(WalletConstants.INVITEING_REWARD_END).getValue();
        Date startDate = DateUtil.stringToDate(startTime);
        Date endDate = DateUtil.stringToDate(endTime);
        if (DateUtil.dateCompare(endDate,createTime)){
            InvitingRewardInfo invitingRewardInfo1 = new InvitingRewardInfo();
            invitingRewardInfo1.setOverdue(true);
            return invitingRewardInfo1;
        }
        //邀请奖励判断
        if (chars[0] == '1') {
            invitingRewardInfo.setInvitedType(0);
        } else {
            invitingRewardInfo.setInvitedType(1);
        }

        //上传头像奖励奖励判断
        if (chars[1] == '1') {
            invitingRewardInfo.setUploadHeaderType(0);
        } else {
            invitingRewardInfo.setUploadHeaderType(1);
        }
        //出师奖励判断
        if (chars[2] == '1') {
            invitingRewardInfo.setGainedType(0);
        } else {
            invitingRewardInfo.setGainedType(1);
        }

        //钱包使用奖励
        if (chars[3] == '1') {
            invitingRewardInfo.setWalletAchievedType(0);
        } else {
            invitingRewardInfo.setWalletAchievedType(1);
        }
        //幸运用户奖励
        if (chars[4] == '1') {
            invitingRewardInfo.setLuckyAccountType(0);
        } else {
            invitingRewardInfo.setLuckyAccountType(1);
        }

        accountSubsidiary.setIsReceive(new String(chars));
        accountSubsidiaryDao.save(accountSubsidiary);

        InvitingReward invitingReward1 = invitingRewardDao.findByRewardName(WalletConstants.INVITED_REWARD_NAME);
        invitingRewardInfo.setInvitedReward(invitingReward1.getRewardAmount());
        InvitingReward invitingReward2 = invitingRewardDao.findByRewardName(WalletConstants.INVITING_REWARD_NAME);
        invitingRewardInfo.setInvitingReward(invitingReward2.getRewardAmount());
        InvitingReward invitingReward5 = invitingRewardDao.findByRewardName(WalletConstants.UPLOAD_REWARD_NAME);
        invitingRewardInfo.setUploadHeaderReward(invitingReward5.getRewardAmount());
        InvitingReward invitingReward3 = invitingRewardDao.findByRewardName(WalletConstants.GAINED_REWARD_NAME);
        invitingRewardInfo.setGainedReward(invitingReward3.getRewardAmount());
        InvitingReward invitingReward4 = invitingRewardDao.findByRewardName(WalletConstants.GAINING_REWARD_NAME);
        invitingRewardInfo.setGainingReward(invitingReward4.getRewardAmount());
        InvitingReward invitingReward6 = invitingRewardDao.findByRewardName(WalletConstants.INVITED_WALLET_REWARD_NAME);
        invitingRewardInfo.setWalletAchievedReward(invitingReward6.getRewardAmount());
        InvitingReward invitingReward7 = invitingRewardDao.findByRewardName(WalletConstants.INVITING_WALLET_REWARD_NAME);
        invitingRewardInfo.setWalletAchievingReward(invitingReward7.getRewardAmount());
        InvitingReward invitingReward8 = invitingRewardDao.findByRewardName(WalletConstants.INVITED_FOUR_TO_THREE);
        invitingRewardInfo.setInvitedFourToThree(invitingReward8.getRewardAmount());
        invitingRewardInfo.setInvitingFourToThree(invitingReward8.getRewardAmount());
        InvitingReward invitingReward9 = invitingRewardDao.findByRewardName(WalletConstants.INVITED_SIX_TO_FIVE);
        InvitingReward invitingReward10 = invitingRewardDao.findByRewardName(WalletConstants.INVITED_SIX_TO_FOUR);
        InvitingReward invitingReward11 = invitingRewardDao.findByRewardName(WalletConstants.INVITED_SIX_TO_THREE);
        invitingRewardInfo.setInvitedSixToFive(invitingReward9.getRewardAmount());
        invitingRewardInfo.setInvitingSixToFive(invitingReward9.getRewardAmount());
        invitingRewardInfo.setInvitedSixToFour(invitingReward10.getRewardAmount());
        invitingRewardInfo.setInvitingSixToFour(invitingReward10.getRewardAmount());
        invitingRewardInfo.setInvitedSixToThree(invitingReward11.getRewardAmount());
        invitingRewardInfo.setInvitingSixToThree(invitingReward11.getRewardAmount());
        return invitingRewardInfo;
    }

    @Override
    public void invitingReward(String accountNo) {
        //日期判断
        Account account = accountDao.findByAccountNo(accountNo);

        String value = sysConfigDao.findSysConfigByName(WalletConstants.INVITEING_REWARD_START).getValue();
        Date date = DateUtil.stringToDate(value);

        if (DateUtil.dateCompare(date, account.getCreateTime())) {
            // 发送奖励
            int index = 0;
            setReward(account, index,null,null);
        } else {
            throw new AppException(ErrorCode.NOT_NEW_ACCOUNT);
        }
    }

    @Override
    public void uploadHeaderReward(String accountNo) {

        // 头像判断
        Account account = accountDao.findByAccountNo(accountNo);

        if (account.getHeader()!=null && !account.getHeader().equals(WalletConstants.DEFAULT_HEADER)) {
            // 发送奖励
            int index = 1;
            setReward(account, index,null,null);
        } else {
            throw new AppException(ErrorCode.NOT_AVAILABLE_USER);
        }
    }

    @Override
    public void gainedReward(String accountNo) {
        // 出师判断
        Account account = accountDao.findByAccountNo(accountNo);

        String value = sysConfigDao.findSysConfigByName(WalletConstants.INVITEING_REWARD_START).getValue();
        Date date = DateUtil.stringToDate(value);

        if (DateUtil.dateCompare(date, account.getCreateTime())) {
            Long qbeUnit =contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE).getId();
            AccountBalance accountBalance = accountBalanceDao.findByAccountNoAndUnit(accountNo, qbeUnit);
            SysConfig sysConfig = sysConfigDao.findSysConfigByName(WalletConstants.SUM_QBAO_ENERGY);
            if(accountBalance!=null&&accountBalance.getAmount().compareTo(BigDecimal.valueOf(Long.valueOf(sysConfig.getValue())))>0) {
                // 发送奖励
                int index = 2;
                setReward(account, index,null,null);
            }else{
                throw new AppException(ErrorCode.NOT_AVAILABLE_USER);
            }
        } else {
            throw new AppException(ErrorCode.NOT_NEW_ACCOUNT);
        }
    }

    @Override
    public void walletAchievingReward(String accountNo) {
        // 钱包有没有
        Account account = accountDao.findByAccountNo(accountNo);
        if (account.getAssets()!=null&&account.getAssets().compareTo(new BigDecimal(0))>0) {
            // 发送奖励
            int index = 3;
            setReward(account, index,null,null);
        } else {
            throw new AppException(ErrorCode.NOT_AVAILABLE_USER);
        }
    }

    @Override
    public Map luckyAccountReward(String accountNo) {
        BigDecimal result = new BigDecimal(0);
        Account account = accountDao.findByAccountNo(accountNo);
        Long qbeUnit = contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE).getId();
        AccountBalance accountBalance = accountBalanceDao.findByAccountNoAndUnit(accountNo, qbeUnit);
        if (accountBalance != null) {
        } else {
            accountBalance = new AccountBalance();
            accountBalance.setAmount(new BigDecimal(0));
        }
        String shareCode = accountService.getShareCode(accountNo);
        int count1 = intersection(shareCode, WalletConstants.LUCK_STRING_QBAO);
        int count2 = intersection(shareCode, WalletConstants.LUCK_STRING_NT);
        InvitingReward reward4to3 = invitingRewardDao.findByRewardName(WalletConstants.INVITED_FOUR_TO_THREE);
        InvitingReward reward6to5 = invitingRewardDao.findByRewardName(WalletConstants.INVITED_SIX_TO_FIVE);
        InvitingReward reward6to4 = invitingRewardDao.findByRewardName(WalletConstants.INVITED_SIX_TO_FOUR);
        InvitingReward reward6to3 = invitingRewardDao.findByRewardName(WalletConstants.INVITED_SIX_TO_THREE);
        if (shareCode.length() == 4) {
            if (count1 == 3) {
                result = reward4to3!=null?reward4to3.getRewardAmount():WalletConstants.FOUR_LUCK_REWARD_THREE;
            }
        } else {
            if (count1 == 3 && count2 == 0) {
                result = reward6to3!=null?reward6to3.getRewardAmount():WalletConstants.LUCK_REWARD_THREE;
            } else if (count1 == 3 && count2 == 1) {
                result = reward6to4!=null?reward6to4.getRewardAmount():WalletConstants.LUCK_REWARD_FOUR;
            } else if (count1 == 3 && count2 == 2) {
                result = reward6to5!=null?reward6to5.getRewardAmount():WalletConstants.LUCK_REWARD_FIVE;
            }
        }
        if(result.compareTo(new BigDecimal(0))>0) {
            // 发送奖励
            int index = 4;
            setReward(account, index,result,result);
        }else {
            throw new AppException(ErrorCode.NOT_AVAILABLE_USER);
        }
        Map<String,String> resultMap = new HashMap();
        resultMap.put("result",result.toString());
        return resultMap;
    }

    @Override
    public List<InvitingReward> invitingRegisteredInfo() {
        return invitingRewardDao.findAll();
    }

    @Override
    @Transactional
    public void invitingRegistered(List<InvitingReward> invitingRewardList) {
        for (InvitingReward invitingReward : invitingRewardList) {
            InvitingReward target = invitingRewardDao.findOne(invitingReward.getId());
            BeanUtils.copyPropertiesWithoutNull(invitingReward, target);
            invitingRewardDao.save(target);
        }
    }

    public int intersection(String str1, String str2) {
        List list1 = Arrays.asList(str1.split(""));
        List list2 = Arrays.asList(str2.split(""));
        java.util.Collection<Object> collection = CollectionUtils.intersection(list1, list2);
        int count = collection.size();
        return count;
    }

    @Transactional
    public void setReward(Account account, int index,BigDecimal reward,BigDecimal rewardInviter) {
        //是否领取了
        String accountNo = account.getAccountNo();
        //将受邀请用户领取状态修改为 已领取
        AccountSubsidiary accountSubsidiary = accountSubsidiaryDao.findByAccountNo(accountNo);
        if (accountSubsidiary==null) {
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }
        String isReceive = accountSubsidiary.getIsReceive() == null ? "11111" : accountSubsidiary.getIsReceive();
        char[] chars = isReceive.toCharArray();
        if (chars[index] == '0') {
            throw new AppException(ErrorCode.EVENT_HAD_APPLIED);
        }

        Long qbeUnit =contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE).getId();
        // 获取奖励
        if (index == 0) {
            InvitingReward invitingReward = invitingRewardDao.findByRewardName(WalletConstants.INVITED_REWARD_NAME);
            reward = invitingReward.getRewardAmount();

            InvitingReward invitedReward = invitingRewardDao.findByRewardName(WalletConstants.INVITING_REWARD_NAME);
            rewardInviter = invitedReward.getRewardAmount();
        } else if (index == 1) {
            InvitingReward invitingReward = invitingRewardDao.findByRewardName(WalletConstants.UPLOAD_REWARD_NAME);
            reward = invitingReward.getRewardAmount();

            //rewardInviter = invitingReward.getRewardAmount();
        } else if (index == 2) {
            InvitingReward invitingReward = invitingRewardDao.findByRewardName(WalletConstants.GAINED_REWARD_NAME);
            reward = invitingReward.getRewardAmount();

            InvitingReward invitedReward = invitingRewardDao.findByRewardName(WalletConstants.GAINING_REWARD_NAME);
            rewardInviter = invitedReward.getRewardAmount();
        } else if (index == 3) {
            InvitingReward invitingReward = invitingRewardDao.findByRewardName(WalletConstants.INVITED_WALLET_REWARD_NAME);
            reward = invitingReward.getRewardAmount();

            InvitingReward invitedReward = invitingRewardDao.findByRewardName(WalletConstants.INVITING_WALLET_REWARD_NAME);
            rewardInviter = invitedReward.getRewardAmount();
        }else if(index ==4){
            // 就用传进来的值
        }

        if (reward != null && reward.compareTo(new BigDecimal(0)) > 0) {
            AccountBalance accountBalance = accountBalanceDao.findByAccountNoAndUnit(accountNo, qbeUnit);
            if (accountBalance != null) {
                accountBalance.setAmount(accountBalance.getAmount().add(reward));
            } else {
                accountBalance = new AccountBalance();
                accountBalance.setAccountNo(accountNo);
                accountBalance.setUnit(qbeUnit);
                accountBalance.setAmount(reward);
            }
            accountBalanceDao.save(accountBalance);
            // Exchange_log 追加记录
            ExchangeLog exchangeLog = new ExchangeLog();
            exchangeLog.setAccountNo(account.getAccountNo());
            exchangeLog.setAmount(reward);
            exchangeLog.setUnit(qbeUnit);
            exchangeLog.setStatus(WalletConstants.CONFIRMED);
            exchangeLog.setExchangeTime(new Date());
            exchangeLog.setType(WalletConstants.TAKE_BONUS);
            exchangeLogDao.save(exchangeLog);
        }
        //被邀请用户获得奖励
        if (rewardInviter != null && rewardInviter.compareTo(new BigDecimal(0)) > 0) {

            String inviteCode = account.getInviteCode();
            if (inviteCode != null) {
                Account oldAccount = accountDao.findAccountByShareCode(inviteCode);
                if (oldAccount != null) {
                    AccountBalance oldAccountBalance = accountBalanceDao.findByAccountNoAndUnit(oldAccount.getAccountNo(), qbeUnit);

                    if (oldAccountBalance != null) {
                        oldAccountBalance.setAmount(oldAccountBalance.getAmount().add(rewardInviter));
                    } else {
                        oldAccountBalance = new AccountBalance();
                        oldAccountBalance.setAccountNo(oldAccount.getAccountNo());
                        oldAccountBalance.setUnit(qbeUnit);
                        oldAccountBalance.setAmount(rewardInviter);
                    }
                    accountBalanceDao.save(oldAccountBalance);

                    // Exchange_log 追加记录
                    ExchangeLog exchangeLogInviter = new ExchangeLog();
                    exchangeLogInviter.setAccountNo(oldAccount.getAccountNo());
                    exchangeLogInviter.setAmount(rewardInviter);
                    exchangeLogInviter.setUnit(qbeUnit);
                    exchangeLogInviter.setStatus(WalletConstants.CONFIRMED);
                    exchangeLogInviter.setExchangeTime(new Date());
                    exchangeLogInviter.setType(WalletConstants.USER_REWARDS_TYPE);
                    exchangeLogDao.save(exchangeLogInviter);
                }
            }
        }
        chars[index] = '0';
        accountSubsidiary.setIsReceive(new String(chars));
        accountSubsidiaryDao.save(accountSubsidiary);
    }

}
