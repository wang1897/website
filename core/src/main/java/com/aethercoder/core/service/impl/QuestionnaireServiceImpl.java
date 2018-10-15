package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.*;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.wallet.AccountSubsidiary;
import com.aethercoder.core.entity.wallet.Questionnaire;
import com.aethercoder.core.service.QuestionnaireService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/2/28
 * @modified By:
 */
@Service
public class QuestionnaireServiceImpl implements QuestionnaireService{

    @Autowired
    private AccountSubsidiaryDao accountSubsidiaryDao;

    @Autowired
    private QuestionnaireDao questionnaireDao;
    @Autowired
    private AccountBalanceDao accountBalanceDao;
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private ExchangeLogDao exchangeLogDao;

    @Override
    public Boolean questionnaireInfo(String accountNo){
        //根据用户查询 AccountSubsidiary
        AccountSubsidiary accountSubsidiary = accountSubsidiaryDao.findByAccountNo(accountNo);
        //如果 AccountSubsidiary 为空 创建一条记录到数据库 并返回true可以参加
        if (accountSubsidiary == null){
            accountSubsidiary = new AccountSubsidiary();
            accountSubsidiary.setAccountNo(accountNo);
            accountSubsidiaryDao.save(accountSubsidiary);
        }
        //如果 AccountSubsidiary 不为空 判断是否参加过问卷调查
        return !accountSubsidiary.getIsJoined();
    }

    @Override
    @Transactional
    public void submitQuestionnaire(Questionnaire questionnaire,String accountNo){
        //判断答案中是否有null；
        if (questionnaire.getQuestion1() == null || questionnaire.getQuestion2() == null || questionnaire.getQuestion3() == null ||
                questionnaire.getQuestion4() == null || questionnaire.getQuestion5() == null){
            throw new AppException(ErrorCode.CANNOT_SUBMIT_WITH_NULL);
        }
        //根据用户查询 AccountSubsidiary
        AccountSubsidiary accountSubsidiary = accountSubsidiaryDao.findByAccountNo(accountNo);
        //如果已参加过问卷调查
        if (accountSubsidiary != null && accountSubsidiary.getIsJoined()){
            //报错已参加，不能提交
            throw new AppException(ErrorCode.HAD_JOINED_QUESTIONNAIRE);
        }
        if (accountSubsidiary == null){
            accountSubsidiary = new AccountSubsidiary();
            accountSubsidiary.setAccountNo(accountNo);
            accountSubsidiary.setIsReceive("11111");
        }
        accountSubsidiary.setIsJoined(true);
        accountSubsidiaryDao.save(accountSubsidiary);
        questionnaire.setAccountNo(accountNo);
        questionnaireDao.save(questionnaire);

        Long qbeUnit =contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE).getId();
        AccountBalance accountBalance = accountBalanceDao.findByAccountNoAndUnit(accountNo, qbeUnit);
        if (accountBalance == null){
            accountBalance = new AccountBalance();
            accountBalance.setAccountNo(accountNo);
            accountBalance.setUnit(qbeUnit);
            accountBalance.setAmount(WalletConstants.QUESTIONNAIRE_REWARD);
        }else {
            BigDecimal amount = accountBalance.getAmount()==null? BigDecimal.valueOf(0):accountBalance.getAmount();
            accountBalance.setAmount(WalletConstants.QUESTIONNAIRE_REWARD.add(amount));
        }
        accountBalanceDao.save(accountBalance);
        // Exchange_log 追加记录
        ExchangeLog exchangeLog = new ExchangeLog();
        exchangeLog.setAccountNo(accountNo);
        exchangeLog.setAmount(WalletConstants.QUESTIONNAIRE_REWARD);
        exchangeLog.setUnit(qbeUnit);
        exchangeLog.setStatus(WalletConstants.CONFIRMED);
        exchangeLog.setExchangeTime(new Date());
        exchangeLog.setType(WalletConstants.TAKE_BONUS);
        exchangeLogDao.save(exchangeLog);
    }
}
