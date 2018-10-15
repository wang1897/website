package com.aethercoder.core.dao.batch;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.ExchangeLogDao;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.core.service.*;
import com.aethercoder.foundation.service.BatchService;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @auther jiawei.tao
 * @date 2017/12/23 下午1:38
 */
@Service
public class ConfirmExchangeBatch implements BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(ConfirmExchangeBatch.class);

    @Autowired
    private ExchangeLogService exchangeLogService;
    @Autowired
    private ExchangeLogDao exchangeLogDao;
    @Autowired
    private AccountBalanceService accountBalanceService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private BatchService batchService;
    @Autowired
    private ContractService contractService;
    @Autowired
    public LocaleMessageService localeMessageUtil;
    @Autowired
    private AccountService accountService;

    @Override
    @Transactional
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("ConfirmExchangeBatch");

        ExchangeLog exchangeLog = exchangeLogDao.findOne(task.getResourceId());
        if (exchangeLog == null) {

            BatchResult failResult = task.getFailedResult("no exchange log");
            return failResult;
        }
        boolean result = exchangeLogService.validateExchange(exchangeLog);
        Locale locale = accountService.getLocaleByAccount(exchangeLog.getAccountNo());
        Contract contract = contractService.findContractById(exchangeLog.getUnit());
        if (result) {
            try {
                result = exchangeLogService.confirmTransactionHash(exchangeLog);
            }catch(Exception ex){
                logger.error("ConfirmExchangeBatch error:"+ex.getMessage());
                result = false;
            }
            if (result) {
                // 成功确认了
                if (WalletConstants.CONFIRMED.equals(exchangeLog.getStatus())) {
                    exchangeLogDao.save(exchangeLog);

                    //添加交易记录 充值
                    AccountBalance accountBalance = new AccountBalance();
                    accountBalance.setAmount(exchangeLog.getAmount());
                    accountBalance.setAccountNo(exchangeLog.getAccountNo());
                    accountBalance.setUnit(exchangeLog.getUnit());
                    //充值-Qbao余额累加
                    accountBalanceService.saveAccountBalanceAmountAdd(accountBalance);

                    String content = localeMessageUtil.getLocalMessage("RECHARGE_SUCCESS",locale, new String[]{"\"" + NumberUtil.formatBigDecimalToCurrency(exchangeLog.getAmount()) +" "+ contract.getName() + "\""});
                    String pushContent = localeMessageUtil.getLocalMessage("RECHARGE_SUCCESS_TITLE",locale, null);
                    String message = exchangeLogService.combineMessage(content,exchangeLog);
                    exchangeLogService.sendQbagMessage(new String[]{exchangeLog.getAccountNo()}, message, pushContent);

                    BatchResult successResult = task.getSuccessResult("success");
                    return successResult;
                } else {
                    Date exchangeTime = exchangeLog.getExchangeTime();
                    Date now = new Date();
                    Calendar calExchangeTime = Calendar.getInstance();
                    calExchangeTime.setTime(exchangeTime);
                    calExchangeTime.add(Calendar.MINUTE, 60);
                    if (now.before(calExchangeTime.getTime())) {

                        //创建批处理再确认
                        Date endDate = new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(endDate);
                        cal.add(Calendar.MINUTE, 5);

                        batchService.createBatchTask("ConfirmExchangeBatch", cal.getTime(), ConfirmExchangeBatch.class.getName(),
                                exchangeLog.getClass().getSimpleName(), exchangeLog.getId());
                        BatchResult failResult = task.getSuccessResult("no confirm");

                        return failResult;
                    }

                }
            }
        }
        // 确认的数据信息不符合或者确认超时
        exchangeLog.setStatus(WalletConstants.FAILED);

        exchangeLogDao.save(exchangeLog);

        String content = localeMessageUtil.getLocalMessage("RECHARGE_FAILED",locale, new String[]{"\"" + NumberUtil.formatBigDecimalToCurrency(exchangeLog.getAmount()) +" "+ contract.getName()+ "\""});
        String pushContent = localeMessageUtil.getLocalMessage("RECHARGE_FAILED_TITLE",locale, null);

        String message = exchangeLogService.combineMessage(content,exchangeLog);
        exchangeLogService.sendQbagMessage(new String[]{exchangeLog.getAccountNo()}, message, pushContent);
        BatchResult failResult = task.getSuccessResult("failed");

        return failResult;
    }

}
