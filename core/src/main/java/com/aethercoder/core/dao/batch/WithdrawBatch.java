package com.aethercoder.core.dao.batch;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.ExchangeLogDao;
import com.aethercoder.core.dao.SysWalletAddressDao;
import com.aethercoder.core.dao.WithdrawApplyDao;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.entity.wallet.SysWalletAddress;
import com.aethercoder.core.entity.wallet.WithdrawApply;
import com.aethercoder.core.service.*;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.exception.BlockChainException;
import com.aethercoder.foundation.service.BatchService;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.DateUtil;
import com.aethercoder.foundation.util.NumberUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static org.springframework.transaction.annotation.Propagation.*;

/**
 * Created by jiaweitao on 2018/01/23.
 */
@Component
@ConditionalOnProperty( name = "schedule.enabled", havingValue = "true" )
public class WithdrawBatch {
    private static Logger logger = LoggerFactory.getLogger(WithdrawBatch.class);
    @Autowired
    private SysWalletAddressDao sysWalletAddressDao;
    @Autowired
    private ContractService contractService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private QtumService qtumService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private LocaleMessageService localeMessageUtil;
    @Autowired
    private AccountBalanceService accountBalanceService;
    @Autowired
    private WithdrawApplyDao withdrawApplyDao;
    @Autowired
    private AccountService accountService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private BatchService batchService;
    @Autowired
    private ExchangeLogDao exchangeLogDao;
    @Autowired
    private ExchangeLogService exchangeLogService;
    @Autowired
    private URLDecipheringService urlDecipheringService;

    private Long qbtTokenId;
    private Long qtumTokenId;
    private BigDecimal moneyUnderLine = new BigDecimal(1000);
    private Integer failTime = 10;

    @Scheduled( cron = "${schedule.withdrawBatchCron}" )
    @Transactional
    public BatchResult run() throws Exception {
        logger.info("WithdrawBatch");

        BatchResult batchResult = new BatchResult();
        try {
            //获得qbtTokenId
            Contract qbtContract = contractService.findContractByName(WalletConstants.QBT_TOKEN_NAME, WalletConstants.CONTRACT_QTUM_TYPE);
            Contract qtumContract = contractService.findContractByName(WalletConstants.QTUM_TOKEN_NAME, WalletConstants.CONTRACT_QTUM_TYPE);
            qbtTokenId = qbtContract.getId();
            qtumTokenId = qtumContract.getId();
            SysConfig moneySysConfig = sysConfigService.findSysConfigByName(WalletConstants.WITHDRAW_POOL_LACK_LIMIT);
            if (moneySysConfig != null) {
                moneyUnderLine = new BigDecimal(moneySysConfig.getValue());
            }
            SysConfig failConfig = sysConfigService.findSysConfigByName(WalletConstants.WITHDRAW_POOL_LOTS_FAIL);
            if (failConfig != null) {
                failTime = new Integer(failConfig.getValue());
            }
            logger.info("获得 qbtTokenId：" + qbtTokenId);
            // 先取Withdraw表中等待取出确认
            List<WithdrawApply> waitingApplies = withdrawApplyDao.findByStatus(WalletConstants.WITHDRAW_STATUS_WAITING);

            // 根据确认结果修改address的服务状态
            for (WithdrawApply withdrawApply : waitingApplies) {
                // 确认交易是否成功
                confirmWithdraw(withdrawApply);

            }
            // 然后地址根据状态去取自己代币的值，如果没有取QBT的值
            List<SysWalletAddress> sysWalletAddressList = sysWalletAddressDao.findByKeepServiceIsTrue();
            for (SysWalletAddress sysWalletAddress : sysWalletAddressList) {

                updatePoolStatus(sysWalletAddress);

                if (!sysWalletAddress.getKeepService()) {
                    continue;
                }
                WithdrawApply applyApply = null;
                Long contractId = sysWalletAddress.getContractId();
                Contract contract = contractService.findContractById(contractId);
                // 如果此币种不能提币了，跳过
                if (!contract.getInService() && !contractId.equals(qbtTokenId)) {
                    contractId = qbtTokenId;
                }
                // QTUM 多次合并提币
                if (WalletConstants.QTUM_TOKEN_NAME.equalsIgnoreCase(contract.getName())) {
                    List<WithdrawApply> qtumList = withdrawApplyDao.findByUnitAndStatus(contractId, WalletConstants.WITHDRAW_STATUS_APPLIED);

                    if (qtumList != null && qtumList.size() > 0) {

                        logger.info("获得 QTUM 多次合并提币");
                        // 判断金额是否足够
                        BigDecimal total = new BigDecimal(0);
                        for (WithdrawApply qtumApply : qtumList) {

                            logger.info("获得 QTUM 提币 withdraw Id: " + qtumApply.getId());
                            // 执行提币的地址设定
                            qtumApply.setServicePool(sysWalletAddress.getId());
                            qtumApply.setFromAddress(sysWalletAddress.getAddress());
                            total = total.add(qtumApply.getAmount());
                        }
                        if (total.compareTo(sysWalletAddress.getLastLeftAmount()) > 0) {
                            // 如果金额小于提币的金额也跳过。并关闭服务。
                            warningAndClose(sysWalletAddress, 0);
                        } else {
                            try {
                                logger.info("QTUM 提币 开始 ");
                                walletService.mentionMoney(qtumList);
                            } catch (ObjectOptimisticLockingFailureException ex) {
                                // 乐观锁错误，忽略这次更新。
                                continue;
                            }

                            if (qtumList.get(0).getStatus().equals(WalletConstants.FAILED)) {
                                Date todayBegin = DateUtil.getTodayBegin();
                                // 失败次数超过10次，发送警告。
                                Integer failTimes = withdrawApplyDao.countByStatusAndExchangeTimeAfter(WalletConstants.FAILED, todayBegin);
                                if (failTimes != null && failTimes.compareTo(failTime) == 0) {
                                    warningAndClose(null, 3);
                                }
                            }
                        }
                        continue;
                    }
                } else {
                    // 取代币的提币
                    if (!contractId.equals(qbtTokenId)) {
                        applyApply = withdrawApplyDao.findFirst1ByUnitAndStatus(contractId, WalletConstants.WITHDRAW_STATUS_APPLIED);
                        if (applyApply != null && applyApply.getAmount().compareTo(sysWalletAddress.getLastLeftAmount()) > 0) {
                            // 如果金额小于提币的金额也跳过。并关闭服务。
                            warningAndClose(sysWalletAddress, 0);
                            continue;
                        }
                    }
                }

                // 如果没有对应的币种，就取QBT的
                if (applyApply == null && qbtContract.getInService()) {
                    applyApply = getQbtApply(sysWalletAddress);
                }

                // 然后执行非Qtum的代币提币
                if (applyApply != null) {
                    logger.info("获得 代币 提币 withdraw Id：" + applyApply.getId());
                    // 判断链上剩余资金和数据库存的信息是否一致，不一致报警，并且不能提币。

                    // 执行提币的地址设定
                    applyApply.setServicePool(sysWalletAddress.getId());
                    applyApply.setFromAddress(sysWalletAddress.getAddress());
                    //为避免并发先修改withdrawApply状态
                    try {
                        walletService.mentionMoney(applyApply);
                    } catch (ObjectOptimisticLockingFailureException ex) {
                        // 乐观锁错误，忽略这次更新。
                        continue;
                    }
                    if (applyApply.getStatus().equals(WalletConstants.FAILED)) {
                        Date todayBegin = DateUtil.getTodayBegin();
                        // 失败次数超过10次，发送警告。
                        Integer failTimes = withdrawApplyDao.countByStatusAndExchangeTimeAfter(WalletConstants.FAILED, todayBegin);
                        if (failTimes != null && failTimes.compareTo(failTime) == 0) {
                            warningAndClose(null, 3);
                        }
                    }
                }
            }

            batchResult.setTaskId(0L);
            batchResult.setStatus(CommonConstants.BATCH_RESULT_SUCCESS);
            batchResult.setResult("success");

            batchService.saveBatchResult(batchResult);
            logger.info("结束执行withdrawBatch");
        }catch(Exception ex){
            batchResult.setTaskId(0L);
            batchResult.setStatus(CommonConstants.BATCH_RESULT_FAIL);
            batchResult.setResult("failed"+ex.getStackTrace());

            logger.error("withdrawBatch异常结束"+ex.getStackTrace());
        }
        return batchResult;
    }

    private void warningAndClose(SysWalletAddress sysWalletAddress,Integer type) {

        SysConfig sysConfig = sysConfigService.findSysConfigByName(WalletConstants.WITHDRAW_WARNING_ACCOUNT_LIST);
        if(sysConfig!=null){
            // modified by wang ling hua App通知改发手机短信 start
//            String accountNoes = sysConfig.getValue();
//            String[] accountNoList = accountNoes.split(",");
            Locale locale = Locale.SIMPLIFIED_CHINESE;
            String message = null;
            String pushContent = null;
            if(type.equals(0)){
                // 金额不足 警告需要充值
                logger.info("金额不足 警告需要充值 ");
                 message = localeMessageUtil.getLocalMessage("WITHDRAW_ADDRESS_NO_MONEY", locale, new String[]{sysWalletAddress.getAddress()});
                 pushContent = localeMessageUtil.getLocalMessage("WITHDRAW_WARNING_TITLE", locale, null);

               // groupService.sendInVite(WalletConstants.GROUP_SYSTEM, accountNoList, message, pushContent, null);
                sysWalletAddress.setKeepService(false);
                sysWalletAddressDao.save(sysWalletAddress);

            }else if(type.equals(1)){
                // 链上资产异常
                logger.info("链上资产异常 ");
                 message = localeMessageUtil.getLocalMessage("WITHDRAW_ADDRESS_MONEY_WARNING", locale, new String[]{sysWalletAddress.getAddress()});
                 pushContent = localeMessageUtil.getLocalMessage("WITHDRAW_WARNING_TITLE", locale, null);
               // groupService.sendInVite(WalletConstants.GROUP_SYSTEM, accountNoList, message, pushContent, null);
                sysWalletAddress.setKeepService(false);
                sysWalletAddressDao.save(sysWalletAddress);

            }else if(type.equals(2)) {
                // 链上资产短缺
                logger.info("链上资产短缺 ");
                 message = localeMessageUtil.getLocalMessage("WITHDRAW_ADDRESS_LESS_MONEY", locale, new String[]{sysWalletAddress.getAddress()});
                 pushContent = localeMessageUtil.getLocalMessage("WITHDRAW_WARNING_TITLE", locale, null);
               // groupService.sendInVite(WalletConstants.GROUP_SYSTEM, accountNoList, message, pushContent, null);

            }else if(type.equals(3)) {
                // 失败次数超过警戒线
                logger.info("失败次数超过警戒线 ");
                 message = localeMessageUtil.getLocalMessage("WITHDRAW_ADDRESS_LOTS_FAIL", locale);
                 pushContent = localeMessageUtil.getLocalMessage("WITHDRAW_WARNING_TITLE", locale, null);
                //groupService.sendInVite(WalletConstants.GROUP_SYSTEM, accountNoList, message, pushContent, null);

            }


            // 发送短信
            String mobileStr = sysConfig.getValue();

            if (pushContent != null && message != null) {
                String templateCode = WalletConstants.QBAO_PAY_STR + pushContent + ":" + message;
                urlDecipheringService.sendMultiSms(mobileStr, templateCode);
            }
            // modified by wang ling hua App通知改发手机短信 end
        }
    }

    private WithdrawApply getQbtApply(SysWalletAddress sysWalletAddress) {

        WithdrawApply applyApply = null;
        // 此地址还有QBT
        if (sysWalletAddress.getQbtLeftAmount().compareTo(new BigDecimal(0)) > 0) {

            applyApply = withdrawApplyDao.findFirst1ByUnitAndStatusAndAmountIsLessThanEqual(qbtTokenId,
                    WalletConstants.WITHDRAW_STATUS_APPLIED, sysWalletAddress.getQbtLeftAmount());
        }
        return applyApply;
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void updatePoolStatus(SysWalletAddress sysWalletAddress) {

        Long contractId = sysWalletAddress.getContractId();
        Contract contract = contractService.findContractById(contractId);
        // 判断keepService状态，更新资金池剩余
        BigDecimal leftAmount = sysWalletAddress.getLastLeftAmount();
        BigDecimal qbtLeftAmount = sysWalletAddress.getQbtLeftAmount();
        BigDecimal qtumLeftAmount = sysWalletAddress.getQtumLeftAmount();

        BigDecimal leftAmountChain = new BigDecimal(0);
        // 取链上的数据
        String address = sysWalletAddress.getAddress();
        BigDecimal qtumLeftAmountChain = walletService.getChainQtumAmount(address);
        if (!WalletConstants.QTUM_TOKEN_NAME.equalsIgnoreCase(contract.getName())) {
            leftAmountChain = walletService.getChainTokenAmount(address, sysWalletAddress.getContractId());
        } else {
            leftAmountChain = qtumLeftAmountChain;
        }
        BigDecimal qbtLeftAmountChain = walletService.getChainTokenAmount(address, qbtTokenId);
        // 链上资金少于数据库存储的, 将这条address关闭。
        if (leftAmount.compareTo(leftAmountChain) > 0
                || qbtLeftAmount.compareTo(qbtLeftAmountChain) > 0
                || qtumLeftAmount.compareTo(qtumLeftAmountChain) > 0) {

            // 报警
            warningAndClose(sysWalletAddress,1);
            return;
        }
        // 链上资金比较少的时候，警告
        if(moneyUnderLine.compareTo(leftAmountChain)>=0 && moneyUnderLine.compareTo(leftAmount)<0){
            warningAndClose(sysWalletAddress,2);
            return;
        }

        // sysWalletAddress 更新最新状态
        sysWalletAddress.setLastLeftAmount(leftAmountChain);
        sysWalletAddress.setQbtLeftAmount(qbtLeftAmountChain);
        sysWalletAddress.setQtumLeftAmount(qtumLeftAmountChain);

        if (leftAmountChain.compareTo(new BigDecimal(1)) <= 0 || qtumLeftAmountChain.compareTo(new BigDecimal(0.1)) <= 0) {
            // 地址上面资金不足(包括手续费)，关闭这个地址的提币，并报错
            warningAndClose(sysWalletAddress,0);
            if (!contractId.equals(qbtTokenId)) {
                // 非QBT，需要把代币的提币状态也关闭
                contract.setInService(false);
                contractService.saveContract(contract);
            }
        } else {
            sysWalletAddressDao.save(sysWalletAddress);
        }
        logger.info("sysWalletAddress 更新最新状态");
    }

    @Transactional(propagation = REQUIRES_NEW)
    public void confirmWithdraw(WithdrawApply withdrawApply) {
        SysWalletAddress sysWalletAddress = sysWalletAddressDao.findOne(withdrawApply.getServicePool());
        Long unit = withdrawApply.getUnit();
        Contract contract = contractService.findContractById(unit);
        List<String> addressList = new ArrayList<>();
        addressList.add(contract.getAddress());
        Integer result = confirmWithdrawTxId(withdrawApply.getTransactionHash(), addressList, withdrawApply.getAmount(), withdrawApply.getUnit());
        if (WalletConstants.CONFIRMED.equals(result)) {

            BigDecimal amount = withdrawApply.getAmount();
            // 更新状态
            withdrawApply.setStatus(WalletConstants.WITHDRAW_STATUS_EXCHANGED);
            withdrawApply.setConfirmTime(new Date());
            withdrawApplyDao.save(withdrawApply);
            //更新划账记录信息
//                ExchangeLog exchangeLog = exchangeLogDao.findFirst1ByAccountNoAndUnitAndTypeAndStatusAndAddress(
//                        withdrawApply.getAccountNo(), withdrawApply.getUnit(), WalletConstants.MENTION_TYPE,
//                        WalletConstants.UNCONFIRMED, withdrawApply.getToAddress());
            Long exchangeLogId = new Long(0);
            try {
                exchangeLogId = new Long(withdrawApply.getExchangeNo());
            } catch (NumberFormatException ex) {
            }
            ExchangeLog exchangeLog = exchangeLogDao.findOne(exchangeLogId);
            exchangeLog.setFromddress(withdrawApply.getFromAddress());
            exchangeLog.setTransactionHash(withdrawApply.getTransactionHash());
            //已确认
            exchangeLog.setStatus(WalletConstants.CONFIRMED);
            ExchangeLog exchangeLog1 = exchangeLogDao.save(exchangeLog);
            logger.info(" 用户 " + withdrawApply.getAccountNo() + " 转账成功之后 提币划账记录信息： " + exchangeLog1.toString());
            //存储划账记录信息
            if (exchangeLog1.getFee().compareTo(new BigDecimal(0)) > 0) {
                ExchangeLog exchangeFee = new ExchangeLog();

                exchangeFee.setType(WalletConstants.FEE_TYPE);
                exchangeFee.setAccountNo(withdrawApply.getAccountNo());
                exchangeFee.setAmount(exchangeLog1.getFee());
                exchangeFee.setUnit(withdrawApply.getFeeUnit());
                exchangeFee.setAddress(exchangeLog1.getAddress());
                exchangeFee.setFromddress(exchangeLog1.getFromddress());
                exchangeFee.setStatus(WalletConstants.CONFIRMED);
                ExchangeLog exchangeLog2 = exchangeLogDao.save(exchangeFee);
                logger.info(" 用户 " + withdrawApply.getAccountNo() + " 转账成功之后 手续费划账记录信息： " + exchangeLog2.toString());
                //存储划账记录信息
                ExchangeLog exchangeEarn = new ExchangeLog();
                //手续费
                exchangeEarn.setType(WalletConstants.FEE_TYPE_ERAN);
                exchangeEarn.setAccountNo(WalletConstants.FEE_ACCOUNTNO);
                exchangeEarn.setUnit(exchangeFee.getUnit());
                exchangeEarn.setAmount(exchangeFee.getAmount());
                exchangeEarn.setFromddress(exchangeLog.getAddress());
                exchangeEarn.setAddress(withdrawApply.getFromAddress());
                exchangeEarn.setStatus(WalletConstants.CONFIRMED);
                ExchangeLog exchangeLog3 = exchangeLogDao.save(exchangeEarn);
                logger.info(" 用户 " + withdrawApply.getAccountNo() + " 转账成功之后 手续费进账记录信息： " + exchangeLog3.toString());

                // 红包中心
                //添加交易记录 手续费
                AccountBalance accountBalanceFee = new AccountBalance();
                accountBalanceFee.setAmount(exchangeEarn.getAmount());
                accountBalanceFee.setAccountNo(exchangeEarn.getAccountNo());
                accountBalanceFee.setUnit(exchangeEarn.getUnit());
                //充值手续费-Qbao累加
                accountBalanceService.saveAccountBalanceAmountAdd(accountBalanceFee);
            }
            // 复式对账用转账记录
            ExchangeLog exchangeCheck = new ExchangeLog();

            exchangeCheck.setType(WalletConstants.MENTION_ERAN);
            exchangeCheck.setStatus(WalletConstants.CONFIRMED);
            exchangeCheck.setAmount(amount);
            exchangeCheck.setAccountNo(WalletConstants.OUT_ACCOUNTNO);
            exchangeCheck.setAddress(withdrawApply.getToAddress());
            exchangeCheck.setUnit(unit);
            exchangeCheck.setFee(withdrawApply.getFee());
            exchangeCheck.setExchangeTime(withdrawApply.getApplyTime());
            exchangeCheck.setIsDeleted(false);
            exchangeLogDao.save(exchangeCheck);

            // 红包中心
            //添加交易记录
            AccountBalance accountBalance = new AccountBalance();
            accountBalance.setAmount(amount);
            accountBalance.setAccountNo(WalletConstants.OUT_ACCOUNTNO);
            accountBalance.setUnit(unit);
            //充值手续费-Qbao累加
            accountBalanceService.saveAccountBalanceAmountAdd(accountBalance);

            // 扣除此次的支付费用，计算余额。
            Contract addressUnit = contractService.findContractById(sysWalletAddress.getContractId());
            // 扣除手续费
            sysWalletAddress.setQtumLeftAmount(sysWalletAddress.getQtumLeftAmount().subtract(withdrawApply.getFee()));
            // 如果此次是QBT交易扣除qbt余额
            if (WalletConstants.QBT_TOKEN_NAME.equalsIgnoreCase(contract.getName())) {
                sysWalletAddress.setQbtLeftAmount(sysWalletAddress.getQbtLeftAmount().subtract(amount));
                if (WalletConstants.QBT_TOKEN_NAME.equalsIgnoreCase(addressUnit.getName())) {
                    sysWalletAddress.setLastLeftAmount(sysWalletAddress.getQbtLeftAmount());
                }
            } else if (WalletConstants.QTUM_TOKEN_NAME.equalsIgnoreCase(contract.getName())) {
                sysWalletAddress.setQtumLeftAmount(sysWalletAddress.getQtumLeftAmount().subtract(amount));
                sysWalletAddress.setLastLeftAmount(sysWalletAddress.getQtumLeftAmount());
            } else {
                sysWalletAddress.setLastLeftAmount(sysWalletAddress.getLastLeftAmount().subtract(amount));
            }

            sysWalletAddress.setKeepService(true);
            sysWalletAddress = sysWalletAddressDao.saveAndFlush(sysWalletAddress);

            Locale locale = accountService.getLocaleByAccount(withdrawApply.getAccountNo());

            String content = localeMessageUtil.getLocalMessage("WITHDRAW_SUCCESS", locale, new String[]{"\"" + NumberUtil.formatBigDecimalToCurrency(exchangeLog.getAmount()) + " " + contract.getName() + "\""});
            String pushContent = localeMessageUtil.getLocalMessage("WITHDRAW_SUCCESS_TITLE", locale, null);

            String message = exchangeLogService.combineMessage(content,exchangeLog);
            exchangeLogService.sendQbagMessage(new String[]{exchangeLog.getAccountNo()}, message, pushContent);

        } else if (WalletConstants.FAILED.equals(result)) {
            withdrawApply.setStatus(WalletConstants.WITHDRAW_STATUS_FAILED);
            withdrawApplyDao.save(withdrawApply);
            // 修改 ExchangeLog的状态
            Long exchangeLogId = new Long(0);
            try {
                exchangeLogId = new Long(withdrawApply.getExchangeNo());
            } catch (NumberFormatException ex) {
            }
            ExchangeLog exchangeLog = exchangeLogDao.findOne(exchangeLogId);

            exchangeLog.setFromddress(withdrawApply.getFromAddress());
            exchangeLog.setTransactionHash(withdrawApply.getTransactionHash());
            //已确认
            exchangeLog.setStatus(WalletConstants.FAILED);
            ExchangeLog exchangeLog1 = exchangeLogDao.save(exchangeLog);
            logger.info(" 用户 " + withdrawApply.getAccountNo() + " 转账失败之后 提币划账记录信息： " + exchangeLog1.toString());

            Locale locale = accountService.getLocaleByAccount(withdrawApply.getAccountNo());

            String content = localeMessageUtil.getLocalMessage("WITHDRAW_FAILED", locale, new String[]{"\"" + NumberUtil.formatBigDecimalToCurrency(exchangeLog.getAmount()) + " " + contract.getName() + "\""});
            String pushContent = localeMessageUtil.getLocalMessage("WITHDRAW_FAILED_TITLE", locale, null);
            String message = exchangeLogService.combineMessage(content,exchangeLog);
            exchangeLogService.sendQbagMessage(new String[]{exchangeLog.getAccountNo()}, message, pushContent);

            // 扣除手续费
            sysWalletAddress.setQtumLeftAmount(sysWalletAddress.getQtumLeftAmount().subtract(withdrawApply.getFee()));
            sysWalletAddress.setKeepService(true);
            sysWalletAddress = sysWalletAddressDao.saveAndFlush(sysWalletAddress);
        }
    }

    private Integer confirmWithdrawTxId(String txId, List<String> addressList, BigDecimal amount, Long unit) {
        Integer result = WalletConstants.UNCONFIRMED;

        Integer confirms = 6;
        if (amount.compareTo(new BigDecimal(100)) < 0) {
            confirms = 4;
        }
        try {
            HashMap map = qtumService.getTransaction(txId);
            if (map == null) {
                return result;
            } else {
                if (map.containsKey("confirmations")) {
                    Integer confirmations = (Integer) map.get("confirmations");
                    Integer height = (Integer) map.get("height");
                    if (confirmations != null && confirmations.compareTo(confirms) >= 0) {
                        if (qtumTokenId.equals(unit)) {
                            result = WalletConstants.CONFIRMED;
                        } else {
                            height = (Integer) map.get("height");
                            List<LinkedHashMap> logs = (List<LinkedHashMap>) qtumService.getEventLog(new Long(height.toString()), new Long(height.toString()), addressList);
                            result = WalletConstants.FAILED;
                            for (LinkedHashMap log : logs) {
                                String transactionHash = (String) log.get("transactionHash");
                                if (txId.equals(transactionHash)) {
                                    System.out.println("Withdraw Success");
                                    result = WalletConstants.CONFIRMED;
                                    break;
                                }
                            }
                        }
                    } else if (height != null && height.compareTo(new Integer(0)) < 0) {
                        logger.error("confirmWithdrawTxId failed:height 没有");
                        result = WalletConstants.FAILED;
                    }
                }
            }
        }catch(BlockChainException bcEx){
            logger.error("confirmWithdrawTxId failed:"+ bcEx.toString());
            result = WalletConstants.FAILED;
        }
        return result;
    }
}
