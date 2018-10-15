package com.aethercoder.core.service.impl;

import com.aethercoder.basic.utils.AESUtil;
import com.aethercoder.core.contants.QtumConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.*;
import com.aethercoder.core.dao.batch.WithdrawBatch;
import com.aethercoder.core.entity.WalletTransaction.*;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.entity.batch.BatchDefinition;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.Event;
import com.aethercoder.core.entity.event.EventApply;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.wallet.*;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.core.service.*;
import com.aethercoder.core.util.wallet.ContractBuilder;
import com.aethercoder.core.util.wallet.ContractManagementHelper;
import com.aethercoder.core.util.wallet.datastorage.KeyStorage;
import com.aethercoder.core.util.wallet.sha3.sha.Keccak;
import com.aethercoder.core.util.wallet.sha3.sha.Parameters;
import com.aethercoder.foundation.service.BatchService;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.NumberUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

/**
 * Created by hepengfei on 2017/9/14.
 */
@Service
public class WalletServiceImpl implements WalletService, BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private ContractService contractService;

    @Autowired
    private SysWalletService sysWalletService;

    @Autowired
    private EventApplyService eventApplyService;
    @Autowired
    private SysWalletAddressDao sysWalletAddressDao;

    @Autowired
    private BatchService batchService;

    @Autowired
    private EventService eventService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private WithdrawApplyDao withdrawApplyDao;

    @Autowired
    private WithdrawApplyLogDao withdrawApplyLogDao;

    @Autowired
    private EventApplyDao eventApplyDao;

    @Autowired
    private ExchangeLogDao exchangeLogDao;
    @Autowired
    private ExchangeLogService exchangeLogService;

    @Autowired
    private LocaleMessageService localeMessageUtil;

    private String qbtAddress;

    private String decimal;

    private String contractAddress;

    @Autowired
    QtumService qtumService;

    @Override
    public List<QtumAddress> getUnspentAddress(Set<Address> addressSet) {
        List<String> addressList = new ArrayList<>();
        Address[] addressArray = new Address[addressSet.size()];
        addressArray = addressSet.toArray(addressArray); ////////*******
        for (int i = 0; i < addressArray.length; i++) {
            addressList.add(addressArray[i].getAddress());
        }
        List<QtumAddress> qtumAddressList = getUnspentAddress(addressList);
        return qtumAddressList;
    }

    private List<QtumAddress> getUnspentAddress(List<String> addressList) {
        List unspentList = qtumService.getUnspentByAddresses(addressList);
        List<QtumAddress> qtumAddressList = new ArrayList<>();
        for (int i = 0; i < unspentList.size(); i++) {
            Map map = (Map) unspentList.get(i);
            QtumAddress qtumAddress = new QtumAddress();
            qtumAddress.setAddress((String) map.get("address"));
            qtumAddress.setTxHash((String) map.get("txid"));
            qtumAddress.setVout((Integer) map.get("outputIndex"));
            qtumAddress.setTxoutScriptPubKey((String) map.get("script"));
            BigDecimal satoshis = new BigDecimal(map.get("satoshis").toString());
            qtumAddress.setAmount(qtumService.convertQtumAmount(satoshis));
            qtumAddress.setBlockHeight(Long.valueOf(map.get("height").toString()));
            qtumAddress.setStake((Boolean) map.get("isStake"));
            qtumAddressList.add(qtumAddress);
        }
        return qtumAddressList;
    }

    //获取余额
    @Override
    public BigDecimal getUnspentAmount(Set<Address> addressList) {
        List<QtumAddress> unspentAddrList = getUnspentAddress(addressList);
        BigDecimal amount = new BigDecimal(0);
        for (QtumAddress unspentAddr : unspentAddrList) {
//            if (unspentAddr.getBlockHeight() > 0) {
            amount = amount.add(unspentAddr.getAmount());
//            }
        }
        return amount;
    }

    //获取余额
    @Override
    public BigDecimal getUnspentAmount(List<String> addressList) {
        List<QtumAddress> unspentAddrList = getUnspentAddress(addressList);
        BigDecimal amount = new BigDecimal(0);
        for (QtumAddress unspentAddr : unspentAddrList) {
//            if (unspentAddr.getBlockHeight() > 0) {
            amount = amount.add(unspentAddr.getAmount());
//            }
        }
        return amount;
    }

    @Override
    public BigDecimal getUnspentAmountByAccountNo(String accountNo, Long unit) {
        Account account = accountDao.findByAccountNo(accountNo);
        Contract contract = contractDao.findOne(unit);
        BigDecimal amount = new BigDecimal(0);
        if (WalletConstants.QTUM_TOKEN_NAME.equals(contract.getAddress().toUpperCase())) {
            amount = getUnspentAmount(account.getAddresses());
        } else {
            List<String> addressList = new ArrayList<>();
            for (Address address : account.getAddresses()) {
                addressList.add(address.getAddress());
            }
            amount = getTokenAmount(addressList, contract.getAddress());
        }
        return amount;
    }

    @Override
    public BigDecimal getTokenAmountByAccountNo(String accountNo, String contractAddress) {
        Account account = accountDao.findByAccountNo(accountNo);
        List<String> addressList = new ArrayList<>();
        for (Address address : account.getAddresses()) {
            addressList.add(address.getAddress());
        }
        return getTokenAmount(addressList, contractAddress);
    }

    @Override
    public BigDecimal getTokenAmount(List<String> addresses, String contractAddress) {
        //余额
        BigDecimal tokenValue = null;

        String balanceHashStr = generateTokenHash(addresses);
        String[] balanceHash = balanceHashStr.split(",");

//        String decimalHash = generateDecimalHash(QtumConstants.decimals);

        Contract contract = contractService.findContractByAddress(contractAddress);
        Integer decimal = Integer.parseInt(contract.getContractDecimal(), 16);

        String[] hashes = new String[balanceHash.length];
        for (int i = 0; i < balanceHash.length; i++) {
            hashes[i] = balanceHash[i];
        }
//        hashes[hashes.length - 1] = decimalHash;
//        CallSmartContractRequest callSmartContractRequest = new CallSmartContractRequest(hashes);
        CallSmartContractResponse callSmartContractResponse = callSmartContract(contractAddress, Arrays.asList(hashes));
        ContractMethodParameter contractMethodParameter = new ContractMethodParameter("", QtumConstants.uint256);
        ContractMethod contractMethod = new ContractMethod();
        contractMethod.outputParams = new ArrayList<>();
        contractMethod.outputParams.add(contractMethodParameter);
//        String decimalString = decimal;
        BigDecimal balance = new BigDecimal(0);
        if (callSmartContractResponse.getItems().size() > 0) {
            for (Item item : callSmartContractResponse.getItems()) {
                if (ArrayUtils.contains(balanceHash, item.getHash())) {
                    balance = balance.add(new BigDecimal(ContractManagementHelper.processResponse(contractMethod.outputParams, item.getOutput())));
                }
            }
        }
        if (balance != null && decimal != null) {
            //余额>0
            if (balance.doubleValue() > 0) {
                tokenValue = qtumService.covertTokenAmount(balance, decimal);
//                BigDecimal decimal = new BigDecimal(Math.pow(10, decimalInt));
                //除以Decimal 后的余额
//                tokenValue = balance.divide(decimal, decimalInt, RoundingMode.HALF_UP);
            } else {
                tokenValue = new BigDecimal(0);
            }
        }
        return tokenValue;
    }

    private String generateDecimalHash(String name) {
        Keccak keccak = new Keccak();
        String hashMethod = keccak.getHash(Hex.toHexString(("getExpectedRate(ERC20,ERC20,uint256)").getBytes()), Parameters.KECCAK_256).substring(0, 8);
        return hashMethod;
    }

    private String generateTokenHash(List<String> toAddress) {
        ContractBuilder contractBuilder = new ContractBuilder();
        List<ContractMethodParameter> contractMethodParameters = new ArrayList<>();
        for (String address : toAddress) {
            ContractMethodParameter contractMethodParameterAddress = new ContractMethodParameter(QtumConstants.to, QtumConstants.address, address);
            contractMethodParameters.add(contractMethodParameterAddress);
        }
        return contractBuilder.createAbiMethodParamsByList(QtumConstants.balanceOf, contractMethodParameters);
    }

    @Override
    @Transactional
    public BatchResult run(BatchTask task) throws Exception {
        BatchResult successResult = task.getSuccessResult("success");
        //获得已结束未关闭的活动  返回所有报名用户信息
        // 1.获得报名用户eventApply报名时所有信息
        List<EventApply> eventApplyList = eventApplyService.findUnpaidEventAppliesUntilNow();
        logger.info("该活动报名用户eventApply报名时所有信息：" + eventApplyList.toString());
//        if (eventApplyList.isEmpty()) {
//            logger.info(" 活动结束了 ");
//            successResult.setResult("no apply");
//            return successResult;
//        }
        //实际转账金额
        BigDecimal playAmount = new BigDecimal(0);
        //获得助记词
        //String seed = sysWalletService.getWalletSeed();
        //获得转账地址
        String fromAddress = sysWalletService.getWalletAddress();
        logger.info(" 转账地址：" + fromAddress);
        Event event = null;
        //实际转账
        BigDecimal actualIncome = null;
        //获得合约地址
        //String contractAddress = null;
        Map<Long, BigDecimal> eventSurplus = new HashMap<>();

        for (EventApply eventApply : eventApplyList) {
            if (event == null || !event.getId().equals(eventApply.getEventId())) {
                event = eventService.findEventByEventID(eventApply.getEventId());
                if (!eventSurplus.containsKey(event.getId())) {
                    eventSurplus.put(event.getId(), new BigDecimal(event.getEventTotalAmount()));
                }
                logger.info(" 本次活动(  " + event.getEventName() + " )所有信息:" + event);
            }
            //获取合约id
            if (WalletConstants.QBT_TOKEN_NAME.equals(event.getOriginalCurrency().toUpperCase())) {
                Contract contract = contractService.findContractByName(WalletConstants.QBT_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);
                event.setOriginalCurrency(String.valueOf(contract.getId()));
            } else if (WalletConstants.QTUM_TOKEN_NAME.equals(event.getOriginalCurrency().toUpperCase())) {
                Contract contract = contractService.findContractByName(WalletConstants.QTUM_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);
                event.setOriginalCurrency(String.valueOf(contract.getId()));
            }
            if (WalletConstants.QBT_TOKEN_NAME.equals(event.getDestCurrency().toUpperCase())) {
                Contract contract = contractService.findContractByName(WalletConstants.QBT_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);
                event.setDestCurrency(String.valueOf(contract.getId()));
            } else if (WalletConstants.QTUM_TOKEN_NAME.equals(event.getDestCurrency().toUpperCase())) {
                Contract contract = contractService.findContractByName(WalletConstants.QTUM_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);
                event.setDestCurrency(String.valueOf(contract.getId()));
            }
            logger.info(" 用户 " + eventApply.getAccountNo() + " 报名时所有信息:" + eventApply.toString());
/*
            if (contractAddress == null) {
                contractAddress = contractService.getByContractAddress(event.getDestCurrency());
            }*/

            // 获得当前余额amount
            BigDecimal amountNow = getUnspentAmountByAccountNo(eventApply.getAccountNo(), Long.valueOf(event.getOriginalCurrency()));
            logger.info(" 用户 " + eventApply.getAccountNo() + " 当前余额:" + amountNow);

            //报名时的金额
            BigDecimal eventAccount = eventApply.getApplyAmount();

            // 3.若apply_amount>=amount 实际转账是amount
            if (eventAccount.compareTo(amountNow) >= 0) {
                playAmount = amountNow;
            } else {
                //   若apply_amount<amount 实际转账是apply_amount
                playAmount = eventAccount;
            }
            //如果余额<活动最下限 实际余额为0
            if (event.getLowerLimit() != null && playAmount.longValue() < event.getLowerLimit()) {
                continue;
            }
            Double amount = playAmount.doubleValue();

            //实际转账
            actualIncome = eventApplyService.parseEventExpression(amount, event.getExpression()).setScale(6, BigDecimal.ROUND_FLOOR);

            //预约时锁定金额
            BigDecimal expectedIncome = eventApply.getExpectedIncome();
            logger.info(" 用户 " + eventApply.getAccountNo() + " 预约时锁定金额: " + expectedIncome);
            //如果实际转账金额>=预约时锁定金额 最终转账金额为预约时锁定金额
            if (actualIncome.compareTo(expectedIncome) >= 0) {
                actualIncome = expectedIncome;
            }
            // 如果剩余金额已经不足，则按照剩余金额
            BigDecimal surplus = eventSurplus.get(eventApply.getEventId());
            if (actualIncome.compareTo(surplus) > 0) {
                actualIncome = surplus;
                eventSurplus.replace(eventApply.getEventId(), new BigDecimal(0));
            } else {
                eventSurplus.replace(eventApply.getEventId(), surplus.subtract(actualIncome));
            }

            logger.info(" 用户 " + eventApply.getAccountNo() + " 实际转账代币金额: " + actualIncome);
            logger.info(" 用户 " + eventApply.getAccountNo() + " 实际转账代币原金额: " + playAmount);
            eventApply.setActualIncome(actualIncome);
            eventApply.setActualAmount(playAmount);
            eventApply.setApplyStatus(WalletConstants.APPLY_STATUS_DONE);
            EventApply PeventApply = eventApplyService.updateEventApply(eventApply);
            logger.info(" 用户 " + eventApply.getAccountNo() + " 转账成功之后 修改eventApply当前余额actual_amount和实际划账代币金额actual_income： " + PeventApply.toString());
        }
        List<EventApply> eventApplies = eventApplyDao.findEventApplyByApplyStatus(WalletConstants.APPLY_STATUS_DONE);
        event = null;
        long unit = 0;
        for (EventApply eventApply : eventApplies) {
            if (event == null || !event.getId().equals(eventApply.getEventId())) {
                event = eventService.findByEventID(eventApply.getEventId());

                if (WalletConstants.QBT_TOKEN_NAME.equals(event.getDestCurrency().toUpperCase())) {
                    Contract contract = contractService.findContractByName(WalletConstants.QBT_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);
                    event.setDestCurrency(String.valueOf(contract.getId()));
                } else if (WalletConstants.QTUM_TOKEN_NAME.equals(event.getDestCurrency().toUpperCase())) {
                    Contract contract = contractService.findContractByName(WalletConstants.QTUM_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);
                    event.setDestCurrency(String.valueOf(contract.getId()));
                }
                //获取合约id
                unit = Long.parseLong(event.getDestCurrency());
                logger.info(" 交易代币 " + event.getDestCurrency() + ",获得该代币id:" + unit);

                logger.info(" 本次活动(  " + event.getEventName() + " )所有信息:" + event);
            }
            if (!event.getEventAvailable()) {
                continue;
            }
//            if(eventApply.getActualIncome().compareTo(new BigDecimal(0))<=0) {
            //添加Qbao余额
            AccountBalance accountBalance = new AccountBalance();
            accountBalance.setAmount(eventApply.getActualIncome());
            accountBalance.setAccountNo(eventApply.getAccountNo());
            accountBalance.setUnit(unit);
            //更新数据
            AccountBalance accountBalance1 = accountBalanceService.saveAccountBalanceAmountAdd(accountBalance);
            logger.info(" 用户 " + eventApply.getAccountNo() + " 添加锁定金额到Qbao余额该交易数据详情: " + accountBalance1.toString());

            //存储划账记录信息
            ExchangeLog exchangeLog = new ExchangeLog();
            exchangeLog.setType(WalletConstants.EVENT_TYPE);
            exchangeLog.setAccountNo(eventApply.getAccountNo());
            exchangeLog.setEventApplyId(eventApply.getId());
            exchangeLog.setFromddress(fromAddress);
            exchangeLog.setAmount(eventApply.getActualIncome());
            exchangeLog.setUnit(unit);
            exchangeLog.setStatus(WalletConstants.CONFIRMED);
            ExchangeLog exchangeLog1 = exchangeLogDao.save(exchangeLog);
            logger.info(" 用户 " + eventApply.getAccountNo() + " 转账成功之后 存储划账记录信息： " + exchangeLog1.toString());
//        }
            //修改状态apply_status=4：以兑换完毕
//            eventApplyService.updateEventStatus(eventApply.getAccountNo(), WalletConstants.EXCHANGE_OVER);
            eventApply.setApplyStatus(WalletConstants.APPLY_STATUS_EXCHAGED);
            eventApplyService.updateEventApply(eventApply);

            logger.info(" 用户 " + eventApply.getAccountNo() + " 转账成功，转账金额：" + eventApply.getActualIncome());
        }
        return successResult;
        //关闭活动
        // eventApplyService.setEventAppliesPaid(eventApplyList);
    }

    //提币-所有金额
    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public void mentionMoney(WithdrawApply withdrawApply) {

        //为避免并发先修改withdrawApply状态
        //withdrawApply.setStatus(WalletConstants.WITHDRAW_STATUS_PENDING);
        withdrawApply = withdrawApplyDao.save(withdrawApply);

        Long unit = withdrawApply.getUnit();
        BigDecimal amount = withdrawApply.getAmount();
        String accountNo = withdrawApply.getAccountNo();
        String toAddress = withdrawApply.getToAddress();
        BigDecimal decimalFeePerKb = withdrawApply.getFeePerKb();
        Contract contract = contractService.findContractById(unit);
        logger.info("代币详情： " + contract.toString());
        logger.info("交易总额： " + amount);
        //获得手续费QBT
//        String deductFeeStr = sysConfigService.findSysConfigByName(WalletConstants.DEDUCT_FEE).getValue();
//        BigDecimal deductFee = new BigDecimal(deductFeeStr);
        BigDecimal deductFee = withdrawApply.getFee();
        logger.info("获得手续费 deductFee： " + deductFee);
        //获得fee
        String fee = sysWalletService.getFee();
        fee = "0.001";
        logger.info("获得 fee：" + fee);

        SysWalletAddress sysWalletAddress = sysWalletAddressDao.findOne(withdrawApply.getServicePool());
        //获得助记词
        String seed = sysWalletAddress.getKey();
        seed = AESUtil.decrypt(seed, CommonConstants.DB_KEY);
        //获得gas_price
        String gasPriceStr = sysWalletService.getGasPrice();
        Integer gasPrice = Integer.parseInt(gasPriceStr);
        logger.info("获得 gasPrice：" + gasPrice);
        //获得gas_limit
        Integer gasLimit = Integer.valueOf(sysConfigService.findSysConfigByName(WalletConstants.SYS_CONFIG_GAS_LIMIT).getValue());
        logger.info("获得 gas_limit：" + gasLimit);
        //获得转账地址
        String fromAddress = sysWalletAddress.getAddress();
        logger.info("获得转账地址 fromAddress：" + fromAddress);
        //获得用户的地址
        logger.info("用户 " + accountNo + " 转账地址toAddress: " + toAddress);

        String bigDecimal = contract.getContractDecimal();
        logger.info("contract: " + contract.getName() + "  , bigDecimal: " + bigDecimal);

        String txId = "";
        try {
            //获得合约地址
            contractAddress = contractService.getByContractAddress(contract.getName());
            logger.info("获得合约地址 contractAddress：" + contractAddress);
            //进行转账
            SendRawTransactionResponse sendRawTransactionResponse = createAbiMethod(seed, fromAddress, toAddress, contractAddress, amount.toString(), gasLimit, bigDecimal, gasPrice, decimalFeePerKb, withdrawApply);
            logger.info("用户 " + accountNo + " 提币成功后返回 sendRawTransactionResponse： " + sendRawTransactionResponse.toString());
            txId = sendRawTransactionResponse.getTxid();

        } catch (Exception ex) {
            logger.info(" 用户 " + withdrawApply.getAccountNo() + " 转账失败： " + ex.toString());

            // 修改 ExchangeLog的状态
            Long exchangeLogId = new Long(0);
            try {
                exchangeLogId = new Long(withdrawApply.getExchangeNo());
            } catch (NumberFormatException e) {
            }
            ExchangeLog exchangeLog = exchangeLogDao.findOne(exchangeLogId);
            exchangeLog.setFromddress(withdrawApply.getFromAddress());
            exchangeLog.setTransactionHash(withdrawApply.getTransactionHash());
            //已确认
            exchangeLog.setStatus(WalletConstants.FAILED);
            ExchangeLog exchangeLog1 = exchangeLogDao.save(exchangeLog);
            logger.info(" 用户 " + withdrawApply.getAccountNo() + " 转账失败之后 提币划账记录信息： " + exchangeLog1.toString());

            String content = localeMessageUtil.getLocalMessage("WITHDRAW_FAILED", new String[]{"\"" + NumberUtil.formatBigDecimalToCurrency(exchangeLog.getAmount()) + " " + contract.getName() + "\""});
            String pushContent = localeMessageUtil.getLocalMessage("WITHDRAW_FAILED_TITLE", null);
            String message = exchangeLogService.combineMessage(content,exchangeLog);
            exchangeLogService.sendQbagMessage(new String[]{exchangeLog.getAccountNo()}, message, pushContent);

        }
        if (StringUtils.isNotBlank(txId)) {
            // 设置转账完成
            withdrawApply.setExchangeTime(new Date());
            withdrawApply.setTransactionHash(txId);
            withdrawApply.setStatus(WalletConstants.WITHDRAW_STATUS_WAITING);
            withdrawApplyDao.save(withdrawApply);
            // 设置此地址状态
            sysWalletAddress.setKeepService(false);
            sysWalletAddressDao.save(sysWalletAddress);

            // 追加withdrawLog表记录
            saveWithdrawApplyLog(withdrawApply);

        } else {
            // 没有hash则报转账失败
            // 设置转账完成
            withdrawApply.setExchangeTime(new Date());
//            withdrawApply.setTransactionHash(txId);
            withdrawApply.setStatus(WalletConstants.WITHDRAW_STATUS_FAILED);
            withdrawApplyDao.save(withdrawApply);

        }
    }

    private void saveWithdrawApplyLog(WithdrawApply withdrawApply) {
        WithdrawApplyLog withdrawApplyLog = new WithdrawApplyLog();
        withdrawApplyLog.setWithdrawId(withdrawApply.getId());
        withdrawApplyLog.setAccountNo(withdrawApply.getAccountNo());
        withdrawApplyLog.setAmount(withdrawApply.getAmount());
        withdrawApplyLog.setUnit(withdrawApply.getUnit());
        withdrawApplyLog.setApplyTime(withdrawApply.getApplyTime());
        withdrawApplyLog.setExchangeTime(withdrawApply.getExchangeTime());
        withdrawApplyLog.setExchangeNo(withdrawApply.getExchangeNo());
        withdrawApplyLog.setConfirmTime(withdrawApply.getConfirmTime());
        withdrawApplyLog.setFromAddress(withdrawApply.getFromAddress());
        withdrawApplyLog.setFee(withdrawApply.getFee());
        withdrawApplyLog.setFeePerKb(withdrawApply.getFeePerKb());
        withdrawApplyLog.setFeeUnit(withdrawApply.getFeeUnit());
        withdrawApplyLog.setServicePool(withdrawApply.getServicePool());
        withdrawApplyLog.setToAddress(withdrawApply.getToAddress());
        withdrawApplyLog.setStatus(withdrawApply.getStatus());
        withdrawApplyLog.setTransactionHash(withdrawApply.getTransactionHash());
        withdrawApplyLogDao.save(withdrawApplyLog);

    }

    @Override
    @Transactional(propagation = REQUIRES_NEW)
    public void mentionMoney(List<WithdrawApply> withdrawApplyList) {

        //为避免并发先修改withdrawApply状态
        for(WithdrawApply apply:withdrawApplyList) {
            //apply.setStatus(WalletConstants.WITHDRAW_STATUS_PENDING);
        }
        withdrawApplyList = withdrawApplyDao.save(withdrawApplyList);

        WithdrawApply withdrawApply = new WithdrawApply();
        if (withdrawApplyList.size() > 0) {
            withdrawApply = withdrawApplyList.get(0);
        }
        Long unit = withdrawApply.getUnit();
        BigDecimal decimalFeePerKb = withdrawApply.getFeePerKb();
        Contract contract = contractService.findContractById(unit);
        logger.info("代币详情： " + contract.toString());

        SysWalletAddress sysWalletAddress = sysWalletAddressDao.findOne(withdrawApply.getServicePool());
        //获得助记词
        String seed = sysWalletAddress.getKey();
        seed = AESUtil.decrypt(seed, CommonConstants.DB_KEY);
        //获得gas_price
        String gasPriceStr = sysWalletService.getGasPrice();
        Integer gasPrice = Integer.parseInt(gasPriceStr);
        logger.info("获得 gasPrice：" + gasPrice);
        //获得gas_limit
        Integer gasLimit = Integer.valueOf(sysConfigService.findSysConfigByName(WalletConstants.SYS_CONFIG_GAS_LIMIT).getValue());
        logger.info("获得 gas_limit：" + gasLimit);
        //获得转账地址
        String fromAddress = sysWalletAddress.getAddress();
        logger.info("获得转账地址 fromAddress：" + fromAddress);

        String bigDecimal = contract.getContractDecimal();
        logger.info("contract: " + contract.getName() + "  , bigDecimal: " + bigDecimal);

        String txId = "";
        try {
            //QTUM转账
            SendRawTransactionResponse sendRawTransactionResponse = withdrawQtum(seed, fromAddress, bigDecimal, decimalFeePerKb, withdrawApplyList);
            logger.info(" 提币成功后返回 sendRawTransactionResponse： " + sendRawTransactionResponse.toString());
            txId = sendRawTransactionResponse.getTxid();

        } catch (Exception ex) {

            logger.info(" Qtum转账失败之后 提币划账记录信息");

        }
        if (StringUtils.isNotBlank(txId)) {
            for(WithdrawApply apply:withdrawApplyList) {
                // 设置转账完成
                apply.setExchangeTime(new Date());
                apply.setTransactionHash(txId);
                apply.setStatus(WalletConstants.WITHDRAW_STATUS_WAITING);
                // 追加withdrawLog表记录
                saveWithdrawApplyLog(apply);
            }
            withdrawApplyDao.save(withdrawApplyList);
            // 设置此地址状态
            sysWalletAddress.setKeepService(false);
            sysWalletAddressDao.save(sysWalletAddress);
        } else {
            for(WithdrawApply apply:withdrawApplyList) {

                // 修改 ExchangeLog的状态
                Long exchangeLogId = new Long(0);
                try {
                    exchangeLogId = new Long(apply.getExchangeNo());
                } catch (NumberFormatException e) {
                }
                ExchangeLog exchangeLog = exchangeLogDao.findOne(exchangeLogId);
                exchangeLog.setFromddress(apply.getFromAddress());
                //已确认
                exchangeLog.setStatus(WalletConstants.FAILED);
                ExchangeLog exchangeLog1 = exchangeLogDao.save(exchangeLog);

                String content = localeMessageUtil.getLocalMessage("WITHDRAW_FAILED", new String[]{"\"" + NumberUtil.formatBigDecimalToCurrency(exchangeLog.getAmount()) + " " + contract.getName() + "\""});
                String pushContent = localeMessageUtil.getLocalMessage("WITHDRAW_FAILED_TITLE", null);

                String message = exchangeLogService.combineMessage(content,exchangeLog);
                exchangeLogService.sendQbagMessage(new String[]{exchangeLog.getAccountNo()}, message, pushContent);
                // 没有hash则报转账失败
                // 设置转账完成
                apply.setExchangeTime(new Date());
                apply.setStatus(WalletConstants.WITHDRAW_STATUS_FAILED);
            }
            withdrawApplyDao.save(withdrawApplyList);
        }
    }


    @Override
    public String getAddress(Integer index) {
        String seedString = sysWalletService.getWalletSeed();
        KeyStorage keyStorage = KeyStorage.getInstance(seedString);
        keyStorage.importWallet();
        return keyStorage.getAddress(index);
    }

    private SendRawTransactionResponse withdrawQtum(String seed, String fromAddress,
                                                    String bigDecimal, BigDecimal decimalFeePerKb, List<WithdrawApply> applies) {

        KeyStorage keyStorage = KeyStorage.getInstance(seed);
        keyStorage.setAddressCount(10);
        keyStorage.importWallet();
        logger.info("bigDecimal:" + bigDecimal);
        //}
        ContractBuilder contractBuilder = new ContractBuilder();

        List<UnspentOutput> unspentOutputs = getUnspentOutputs(fromAddress);
        logger.info("decimalFeePerKb:" + decimalFeePerKb);

        SendRawTransactionResponse sendRawTransactionResponse = sendTx(contractBuilder.createQtumTransactionHash(keyStorage,
                decimalFeePerKb, unspentOutputs, applies));
        return sendRawTransactionResponse;
    }

    private SendRawTransactionResponse createAbiMethod(String seed, String fromAddress, String toAddress, String contractAddress, String amount,
                                                       int gasLimitInt, String bigDecimal, Integer gasPrice, BigDecimal decimalFeePerKb, WithdrawApply withdrawApply) {
        KeyStorage keyStorage = KeyStorage.getInstance(seed);
        keyStorage.setAddressCount(10);
        keyStorage.importWallet();
       /* String resultAmount = amount;
        if (Integer.valueOf(getTokenDecimal(contractAddress)) != 0) {
            BigDecimal bigDecimal = new BigDecimal(getTokenDecimal(contractAddress));*/
        Integer decimal = Integer.parseInt(bigDecimal, 16);
        double decimalDouble = Math.pow(10, decimal.intValue());
        BigDecimal amountBigDecimal = new BigDecimal(amount);
        BigDecimal amountDecimal = amountBigDecimal.multiply(new BigDecimal(decimalDouble));
        String resultAmount = amountDecimal.toBigInteger().toString();
        logger.info("decimal:" + decimal);
        ContractBuilder contractBuilder = new ContractBuilder();
        List<ContractMethodParameter> contractMethodParameterList = new ArrayList<>();
        ContractMethodParameter contractMethodParameterAddress = new ContractMethodParameter("_to", "address", toAddress);
        ContractMethodParameter contractMethodParameterAmount = new ContractMethodParameter("_value", "uint256", resultAmount);
        contractMethodParameterList.add(contractMethodParameterAddress);
        contractMethodParameterList.add(contractMethodParameterAmount);
        String abiParams = contractBuilder.createAbiMethodParams("transfer", contractMethodParameterList);

        Script script = createMethodScript(abiParams, contractAddress, gasLimitInt, gasPrice);

        List<UnspentOutput> unspentOutputs = getUnspentOutputs(fromAddress);
        logger.info("gasPrice:" + gasPrice + ",decimalFeePerKb:" + decimalFeePerKb);
        SendRawTransactionResponse sendRawTransactionResponse = sendTx(contractBuilder.createTransactionHash(keyStorage, null, script, gasLimitInt, gasPrice,
                decimalFeePerKb, unspentOutputs, withdrawApply));
        return sendRawTransactionResponse;
    }

    private Script createMethodScript(final String abiParams, final String contractAddress, int gasLimitInt, int gasPrice) {
        ContractBuilder contractBuilder = new ContractBuilder();
        Script script = contractBuilder.createMethodScript(abiParams, gasLimitInt, gasPrice, contractAddress);
        return script;
    }

    private DGPInfo getDgpInfo() {
        HashMap hashMap = qtumService.getDGPInfo();

        DGPInfo dgpInfo = new DGPInfo();
        dgpInfo.setBlockgaslimit((Integer) hashMap.get("maxblocksize"));
        dgpInfo.setMaxblocksize((Integer) hashMap.get("mingasprice"));
        dgpInfo.setMingasprice((Integer) hashMap.get("blockgaslimit"));
        return dgpInfo;
    }

    private FeePerKb getEstimateFeePerKb() {
        Double d = qtumService.estimateFee(25);

        FeePerKb feePerKb = new FeePerKb();
        feePerKb.setFeePerKb(new BigDecimal(d));
        return feePerKb;
    }

    private SendRawTransactionResponse sendTx(String txHex) {
        logger.info("txid:" + txHex);
        String txhash = qtumService.sendRawTransaction(txHex);
        SendRawTransactionResponse sendRawTransactionResponse = new SendRawTransactionResponse();
//       sendRawTransactionResponse.setResult();
        sendRawTransactionResponse.setTxid(txhash);
        return sendRawTransactionResponse;
    }

    //lilangfeng4
    public List<UnspentOutput> getUnspentOutputs(String address) {
        List<String> addressList = new ArrayList<>();
        addressList.add(address);
        List<Map> list = qtumService.getConfirmUnspentByAddresses(addressList);
        List<UnspentOutput> unspentOutputList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Map map = (Map) list.get(i);
            UnspentOutput unspentOutput = new UnspentOutput();
            unspentOutput.setAddress((String) map.get("address"));
            unspentOutput.setTxHash((String) map.get("txid"));
            unspentOutput.setVout((Integer) map.get("outputIndex"));
            unspentOutput.setTxoutScriptPubKey((String) map.get("script"));
            unspentOutput.setBlockHeight(Long.valueOf(map.get("height").toString()));
            unspentOutput.setStake((Boolean) map.get("isStake"));
            BigDecimal satoshis = new BigDecimal(map.get("satoshis").toString());
            unspentOutput.setAmount(qtumService.convertQtumAmount(satoshis));
            unspentOutput.setConfirmations((Integer) map.get("confirmations"));
            unspentOutputList.add(unspentOutput);
        }

        for (Iterator<UnspentOutput> iterator = unspentOutputList.iterator(); iterator.hasNext(); ) {
            UnspentOutput unspentOutput = iterator.next();
            //remove confirmations<500的数据
            if (!unspentOutput.isOutputAvailableToPay()) {
                iterator.remove();
            }
        }
        Collections.sort(unspentOutputList, (unspentOutput, t1) ->
                unspentOutput.getAmount().doubleValue() < t1.getAmount().doubleValue() ? 1 : unspentOutput.getAmount().doubleValue() > t1.getAmount().doubleValue() ? -1 : 0);

        return unspentOutputList;
    }

    private String getTokenDecimal(String contractAddress) {
        if (decimal == null) {
            String date = "";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            date = formatter.format(new Date());
            Token token = new Token(contractAddress, UUID.randomUUID().toString(), true, date, "Stub!", "name");
            ContractMethod contractMethod = new ContractMethod();
            String[] hashes = getHash(WalletConstants.DECIMALS);
            CallSmartContractResponse callSmartContractResponse = callSmartContract(contractAddress, Arrays.asList(hashes));
            ContractMethodParameter contractMethodParameter = new ContractMethodParameter("", WalletConstants.UINT256);
            contractMethod.outputParams = new ArrayList<>();
            contractMethod.outputParams.add(contractMethodParameter);
            decimal = ContractManagementHelper.processResponse(contractMethod.outputParams, callSmartContractResponse.getItems().get(0).getOutput());
            return decimal;
        }
        return decimal;
    }

    private String[] getHash(String name) {
        Keccak keccak = new Keccak();
        String hashMethod = keccak.getHash(Hex.toHexString((name + "()").getBytes()), Parameters.KECCAK_256).substring(0, 8);
        return new String[]{hashMethod};
    }

    private CallSmartContractResponse callSmartContract(String contractAddress, List<String> hashes) {

        List<HashMap> mapList = qtumService.callContract(contractAddress, hashes);
        List<Item> itemList = new ArrayList<>();

        for (HashMap map : mapList) {
            Map executionResult = (Map) map.get("executionResult");
            Item item = new Item();
            item.setExcepted((String) executionResult.get("excepted"));
            item.setGas_used(executionResult.get("gasUsed").toString());
            String output = (String) executionResult.get("output");

//            item.setOutput(new BigInteger(output, 16).toString());
            item.setOutput(output);
            itemList.add(item);
            item.setHash((String) map.get("hash"));
        }

        CallSmartContractResponse callSmartContractResponse = new CallSmartContractResponse();
        callSmartContractResponse.setItems(itemList);
        return callSmartContractResponse;
    }

    private String getQBTAddress() {
        if (qbtAddress == null) {
            com.aethercoder.core.entity.wallet.Contract contract = contractDao.findContractByNameAndType(WalletConstants.QBT_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);
            qbtAddress = contract.getAddress();
            return qbtAddress;
        }
        return qbtAddress;
    }

    private String formatString(Object... object) {
        StringBuilder builder = new StringBuilder();
        for (Object o : object) {
            if (o != null) {
                builder.append(o);
            }
        }
        return builder.toString();
    }

    @Override
    public void startServiceByAddress(List<SysWalletAddress> sysWalletAddressList) {

        // 取链上的数据
        Contract qbtContract = contractService.findContractByName(WalletConstants.QBT_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);
        Long qbtTokenId = qbtContract.getId();
        for (SysWalletAddress sysWalletAddress : sysWalletAddressList) {
            String address = sysWalletAddress.getAddress();
            BigDecimal leftAmountChain = new BigDecimal(0);

            Long contractId = sysWalletAddress.getContractId();
            Contract contract = contractService.findContractById(contractId);
            BigDecimal qtumLeftAmountChain = getChainQtumAmount(address);
            if (!WalletConstants.QTUM_TOKEN_NAME.equalsIgnoreCase(contract.getName())) {
                leftAmountChain = getChainTokenAmount(address, sysWalletAddress.getContractId());
            } else {
                leftAmountChain = qtumLeftAmountChain;
            }
            BigDecimal qbtLeftAmountChain = getChainTokenAmount(address, qbtTokenId);
            // keepService恢复
            sysWalletAddress.setKeepService(true);
            sysWalletAddress.setLastLeftAmount(leftAmountChain);
            sysWalletAddress.setQbtLeftAmount(qbtLeftAmountChain);
            sysWalletAddress.setQtumLeftAmount(qtumLeftAmountChain);
            sysWalletAddressDao.saveAndFlush(sysWalletAddress);
        }
    }

    @Override
    public BigDecimal getChainQtumAmount(String address) {
        BigDecimal amount = new BigDecimal(0);
        List<String> addressList = new ArrayList<>();
        addressList.add(address);
        amount = getUnspentAmount(addressList).setScale(6,BigDecimal.ROUND_FLOOR);
        return amount;
    }

    @Override
    public BigDecimal getChainTokenAmount(String address, Long contractId) {
        BigDecimal amount = new BigDecimal(0);

        Contract contract = contractService.findContractById(contractId);
        List<String> addressList = new ArrayList<>();
        addressList.add(address);
        amount = getTokenAmount(addressList, contract.getAddress());

        return amount;
    }

    @Override
    public void startWithdrawBatch() {

        System.out.println("testStartWithdrawBatch");

        String batchName = "WithdrawBatch";
        BatchDefinition batchDefinition = batchService.findDefinitionByName(batchName);
        if (batchDefinition == null) {
            batchDefinition = new BatchDefinition();
            batchDefinition.setName(batchName);
            batchDefinition.setFrequency(CommonConstants.BATCH_FREQUENCY_MINUTELY);
            batchDefinition.setStartTime(new Date());
            batchDefinition.setIsActive(false);
            batchDefinition.setClassName(WithdrawBatch.class.getName());
            batchDefinition.setTimeSlot(WalletConstants.BATCH_TIMESLOT);
            batchService.saveBatchDefinition(batchDefinition);
        }
        // 初始化可提币数值
        // contractService.setDefaultWithdraw();

        // 初始化提币地址值
        List<SysWalletAddress> addressList = sysWalletAddressDao.findByKeepServiceIsTrue();
        startServiceByAddress(addressList);
    }
}
