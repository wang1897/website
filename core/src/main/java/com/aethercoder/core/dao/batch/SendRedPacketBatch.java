package com.aethercoder.core.dao.batch;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.ExchangeLogDao;
import com.aethercoder.core.dao.SendRedPacketDao;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.event.SendRedPacket;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.core.service.AccountBalanceService;
import com.aethercoder.core.service.ExchangeLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @auther Guo Feiyan
 * @date 2017/12/10 下午1:38
 */
@Service
public class SendRedPacketBatch implements  BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(SendRedPacketBatch.class);

    @Autowired
    private SendRedPacketDao sendRedPacketDao;
    @Autowired
    private ExchangeLogDao exchangeLogDao;

    @Autowired
    private ExchangeLogService exchangeLogService;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Override
    @Transactional
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("SendRedPacketBatch");
        logger.info("红包退款 ："+task);
        SendRedPacket sendRedPacket = sendRedPacketDao.findOne(task.getResourceId());
        logger.info("红包详情 ："+sendRedPacket);
        // 红包是否还有效

        if (sendRedPacket == null){
            throw new AppException(ErrorCode.INCORRECT_PARAM);
        }
        logger.info("红包余额 ："+sendRedPacket.getBalance());
        if (sendRedPacket.getBalance().compareTo(new BigDecimal(0))>0) {
            //进行转账
            AccountBalance accountBalance1 = new AccountBalance();
            accountBalance1.setAmount(sendRedPacket.getBalance());
            accountBalance1.setAccountNo(sendRedPacket.getAccountNo());
            accountBalance1.setUnit(sendRedPacket.getUnit());
            AccountBalance accountBalance =accountBalanceService.saveAccountBalanceAmountAdd(accountBalance1);
            logger.info("accountBalance"+accountBalance.toString());
            //转账记录
            ExchangeLog exchangeLog = new ExchangeLog();
            exchangeLog.setType(WalletConstants.REFUNDS_RED_PACKET);
            exchangeLog.setAccountNo(sendRedPacket.getAccountNo());
            exchangeLog.setAmount(sendRedPacket.getBalance());
            exchangeLog.setExchangeTime(new Date());
            exchangeLog.setUnit(sendRedPacket.getUnit());
            exchangeLog.setStatus(WalletConstants.CONFIRMED);

            ExchangeLog exchangeLog1 =exchangeLogDao.save(exchangeLog);
            logger.info("exchangeLog1"+exchangeLog1.toString());

        }
        sendRedPacket.setIsAvailable(false);
        SendRedPacket sendRedPacket1 =sendRedPacketDao.save(sendRedPacket);
        logger.info("sendRedPacket1"+sendRedPacket1.toString());
        BatchResult successResult = task.getSuccessResult("success");

        return successResult;
    }


}
