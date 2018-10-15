package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.*;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.Event;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.AccountBalanceService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:42
 */
@Service
public class AccountBalanceServiceImpl implements AccountBalanceService {

    private static Logger logger = LoggerFactory.getLogger(AccountBalanceServiceImpl.class);

    @Autowired
    private AccountBalanceDao accountBalanceDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private EventDao eventDao;

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private ExchangeLogDao exchangeLogDao;

    @Override
    public List<AccountBalance> findByAccountNo(String accountNo) {
        return accountBalanceDao.findByAccountNo(accountNo);
    }

    @Override
    public AccountBalance findByAccountNoAndUnit(String accountNo, Long unit) {
        return accountBalanceDao.findByAccountNoAndUnit(accountNo, unit);
    }

    @Override
    public AccountBalance saveAccountBalance(AccountBalance accountBalance) {
        return accountBalanceDao.save(accountBalance);
    }

    @Override
    public AccountBalance saveAccountBalanceAmountAdd(AccountBalance accountBalance) {
        AccountBalance accountBalance1 = findAccountBalanceByAccountNoAndUnit(accountBalance.getAccountNo(), accountBalance.getUnit());
        //如果用户没有余额记录 即新添加QBAO余额
        if (null == accountBalance1) {
            accountBalance1 = new AccountBalance();
            accountBalance1.setAmount(accountBalance.getAmount());
            accountBalance1.setAccountNo(accountBalance.getAccountNo());
            accountBalance1.setUnit(accountBalance.getUnit());
        } else {
            //余额累加锁定金额
            accountBalance1.setAmount(accountBalance1.getAmount().add(accountBalance.getAmount()));
        }
        //更新数据
        return saveAccountBalance(accountBalance1);
    }

    private AccountBalance findAccountBalanceByAccountNoAndUnit(String accountNo, long unit) {
        return accountBalanceDao.findAccountBalanceByAccountNoAndUnit(accountNo, unit);
    }

    @Override
    public AccountBalance saveAccountBalanceAmountSubtract(AccountBalance accountBalance) {
        //添加Qbao余额
        AccountBalance accountBalance1 = findAccountBalanceByAccountNoAndUnit(accountBalance.getAccountNo(), accountBalance.getUnit());
        //如果用户没有余额记录 即红包余额为0
        if (null == accountBalance1 || accountBalance1.getAmount().compareTo(accountBalance.getAmount()) < 0) {
            throw new AppException(ErrorCode.QBAO_UNDERBALANCE);
        } else if (accountBalance1.getAmount().compareTo(accountBalance.getAmount()) >= 0) {
            //余额扣除提币金额
            accountBalance1.setAmount(accountBalance1.getAmount().subtract(accountBalance.getAmount()));
        }
        //更新数据
        return saveAccountBalance(accountBalance1);
    }

    @Override
    public Page<AccountBalance> getAllAccountBalance(Integer page, Integer size, String accountNo, Long unit, BigDecimal maxAmount, BigDecimal minAmount, String accountName) {

        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "amount");
        Page<AccountBalance> accountBalances = accountBalanceDao.findAll(new Specification<AccountBalance>() {
            @Override
            public Predicate toPredicate(Root<AccountBalance> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                List<Predicate> listOr = new ArrayList<Predicate>();
                if (null != accountNo && !"".equals(accountNo)) {
                    list.add(criteriaBuilder.equal(root.get("accountNo").as(String.class), accountNo));
                }
                if (null != unit && !"".equals(unit)) {
                    list.add(criteriaBuilder.equal(root.get("unit").as(Long.class), unit));
                }
                if (null != accountName && !"".equals(accountName)) {
                    List<Account> accounts = accountDao.findAccountsByAccountNameLike("%" + accountName + "%");
                    accounts.forEach(account -> {
                        listOr.add(criteriaBuilder.equal(root.get("accountNo").as(String.class), account.getAccountNo()));
                    });
                }
                if (minAmount != null && minAmount.compareTo(new BigDecimal(0)) >= 0) {
                    //大于或等于传入最大值
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("amount").as(BigDecimal.class), minAmount));
                }
                if (maxAmount != null && maxAmount.compareTo(new BigDecimal(0)) >= 0) {
                    //小于或等于传入时间
                    list.add(criteriaBuilder.lessThanOrEqualTo(root.get("amount").as(BigDecimal.class), maxAmount));
                }

                Predicate[] p = new Predicate[list.size()];
                Predicate[] pOr = new Predicate[listOr.size()];

                if (listOr.size() > 0) {
                    return criteriaBuilder.and(criteriaBuilder.and(list.toArray(p)), criteriaBuilder.or(listOr.toArray(pOr)));
                } else {
                    return criteriaBuilder.and(list.toArray(p));
                }

            }
        }, pageable);
        accountBalances.forEach(exchangeLog -> {
            Account account = accountDao.findByAccountNo(exchangeLog.getAccountNo());
            if (account != null) {
                exchangeLog.setAccountName(account.getAccountName());
            }

        });
        return accountBalances;
    }

    @Override
    public Map getRechargeMoneyProfile() {
        Map map = new HashMap();

        List<Contract> contracts = contractDao.findContractsByIsDeleteIsFalseOrderBySequenceAsc();
        contracts.forEach(contract -> {
            BigDecimal bigDecimal = exchangeLogDao.findExchangeLogsByUnitAndType(contract.getId(), WalletConstants.RECHANGER_TYPE);
            map.put(contract.getId(), bigDecimal == null ? new BigDecimal(0) : bigDecimal);
        });

        return map;
    }

    @Override
    public Map getDefaultExchangeQBE(String accountNo, Long unit, Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map map = new HashMap();
        //check_QBT兑换过期时间
        Event event = eventDao.findEventByIdAndEventAvailableIsTrue(id);
        if (event == null) {
            throw new AppException(ErrorCode.EVENT_NOT_EXIST);
        }
        Boolean result = false;

        if (DateUtil.dateCompare(event.getEndDate(), new Date())) {
            result = true;
        }

        //默认兑换QBE金额 当前 2w QBE = 1 QBT
        SysConfig sysConfig = sysConfigDao.findSysConfigByName(WalletConstants.SYS_DEFAULT_EXCHANGE_QBE);
        if (sysConfig == null) {
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }
        BigDecimal defaultExchange = new BigDecimal(sysConfig.getValue());

        AccountBalance accountBalance = accountBalanceDao.findByAccountNoAndUnit(accountNo, unit);

        BigDecimal amountQBE = accountBalance == null ? new BigDecimal(0) : accountBalance.getAmount();

        Double exchangeQBE = Math.floor(amountQBE.divide(defaultExchange, 2, RoundingMode.HALF_UP).doubleValue());

        map.put("amountQBE", amountQBE);
        map.put("exchangeQBE", exchangeQBE);
        map.put("result", result);
        return map;
    }

    @Override
    @Transactional
    public Map getExchangeQBE(String accountNo, Long unit, Long id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //check_QBT兑换过期时间
        Event event = eventDao.findEventByIdAndEventAvailableIsTrue(id);
        if (event == null) {
            throw new AppException(ErrorCode.EVENT_NOT_EXIST);
        }

        if (DateUtil.dateCompare(event.getEndDate(), new Date())) {
            throw new AppException(ErrorCode.EXCHANGE_QBT_OVER);
        }
        //获取发放QBT总额
        SysConfig sysConfigBySumQBT = sysConfigDao.findSysConfigByName(WalletConstants.SYS_SUM_EXCHANGE_QBT_AMOUNT);
        if (sysConfigBySumQBT == null) {
            throw new AppException(ErrorCode.EXCHANGE_QBT_OVER);
        }
        BigDecimal sumQBT = new BigDecimal(sysConfigBySumQBT.getValue());
        logger.info(" 发放QBT总额:" + sumQBT);
        //qbt代币
        Contract contract = contractDao.findContractByNameAndType(WalletConstants.QBT_TOKEN_NAME,WalletConstants.CONTRACT_QTUM_TYPE);
        //check交易QB金额
        BigDecimal decimalExchangeQBT = exchangeLogDao.findExchangeLogsByUnitAndType(contract.getId(), WalletConstants.QBE_EXCHANGE_QBT);
        if(decimalExchangeQBT==null){
            decimalExchangeQBT = new BigDecimal(0);
        }
        logger.info(" 已经发放QBT金额:" + decimalExchangeQBT);
        //check是否发放完毕
        if (decimalExchangeQBT==null){
            decimalExchangeQBT = new BigDecimal(0);
        }
        if (sumQBT.compareTo(decimalExchangeQBT) <= 0) {
            throw new AppException(ErrorCode.EXCHANGE_QBT_OVER);
        }
        Map map = new HashMap();
        //默认兑换QBE金额 当前 1w QBE = 1 QBT
        SysConfig sysConfig = sysConfigDao.findSysConfigByName(WalletConstants.SYS_DEFAULT_EXCHANGE_QBE);
        if (sysConfig == null) {
            throw new AppException(ErrorCode.EXCHANGE_QBT_OVER);
        }
        //默认兑换QBE金额
        BigDecimal defaultExchange = new BigDecimal(sysConfig.getValue());

        AccountBalance accountBalance = accountBalanceDao.findByAccountNoAndUnit(accountNo, unit);

        //该用户中心的余额
        BigDecimal amountQBE = accountBalance == null ? new BigDecimal(0) : accountBalance.getAmount();

        //check该用户余额QBE不足
        if (amountQBE.compareTo(defaultExchange) < 0) {
            throw new AppException(ErrorCode.EXCHANGE_QBT_OVER);
        }
        //剩余发放QBT个数
        BigDecimal amount = sumQBT.subtract(decimalExchangeQBT);

        Double exchangeQBE = Math.floor(amountQBE.divide(defaultExchange, 2, RoundingMode.HALF_UP).doubleValue());
        //扣除QBE
        BigDecimal decimalMutiply = defaultExchange.multiply(new BigDecimal(exchangeQBE));//*
        BigDecimal decimal = amountQBE.subtract(decimalMutiply);
        //兑换QBT个数
        BigDecimal decimalQBT = new BigDecimal(exchangeQBE);

        //check扣除QBE时对该用户是否足够发放
        if (amount.compareTo(decimalQBT) < 0) {
            //不足够发放
            throw new AppException(ErrorCode.EXCHANGE_QBT_OVER);
        }
        accountBalance.setAmount(decimal);
        accountBalanceDao.save(accountBalance);
        //兑换QBE交易记录
        ExchangeLog exchangeLog = new ExchangeLog();
        exchangeLog.setAmount(decimalMutiply);
        exchangeLog.setAccountNo(accountNo);
        exchangeLog.setUnit(unit);
        exchangeLog.setStatus(WalletConstants.CONFIRMED);
        exchangeLog.setType(WalletConstants.EXCHANGE_QBE_MUTIPLY);
        exchangeLog.setExchangeTime(new Date());
        exchangeLog.setIsDeleted(false);
        exchangeLogDao.save(exchangeLog);


        //兑换QBT

        AccountBalance accountBalanceQBT = new AccountBalance();

        accountBalanceQBT.setAmount(decimalQBT);
        accountBalanceQBT.setUnit(contract.getId());
        accountBalanceQBT.setAccountNo(accountNo);
        saveAccountBalanceAmountAdd(accountBalanceQBT);

        //兑换QBT交易记录
        ExchangeLog exchangeLogQBT = new ExchangeLog();
        exchangeLogQBT.setAmount(decimalQBT);
        exchangeLogQBT.setAccountNo(accountNo);
        exchangeLogQBT.setUnit(contract.getId());
        exchangeLogQBT.setStatus(WalletConstants.CONFIRMED);
        exchangeLogQBT.setType(WalletConstants.QBE_EXCHANGE_QBT);
        exchangeLogQBT.setExchangeTime(new Date());
        exchangeLogQBT.setIsDeleted(false);
        exchangeLogDao.save(exchangeLogQBT);

        map.put("result", decimal);

        return map;
    }

    //用户奖励
    @Override
    public void accountReward(String accountNo,Long unit,BigDecimal winAmount,Integer type){
        AccountBalance accountBalance = accountBalanceDao.findByAccountNoAndUnit(accountNo,unit);
        //奖励记录
        ExchangeLog exchangeLog = new ExchangeLog();
        exchangeLog.setType(type);
        exchangeLog.setStatus(WalletConstants.CONFIRMED);
        exchangeLog.setUnit(unit);
        exchangeLog.setAmount(winAmount);
        exchangeLog.setAccountNo(accountNo);
        exchangeLogDao.save(exchangeLog);

        //添加对应奖励
        if (null == accountBalance){
            accountBalance = new AccountBalance();
            accountBalance.setAmount(winAmount);
            accountBalance.setAccountNo(accountNo);
            accountBalance.setUnit(unit);

        }else {
            accountBalance.setAmount(accountBalance.getAmount().add(winAmount));
        }
        accountBalanceDao.save(accountBalance);
    }

    @Override
    @Transactional
    public void accountDeduct(String accountNo, Long unit, BigDecimal loseAmount, Integer type) {
        AccountBalance accountBalance = accountBalanceDao.findByAccountNoAndUnit(accountNo,unit);
        //扣除金额记录
        ExchangeLog exchangeLog = new ExchangeLog();
        exchangeLog.setType(type);
        exchangeLog.setStatus(WalletConstants.CONFIRMED);
        exchangeLog.setUnit(unit);
        exchangeLog.setAmount(loseAmount);
        exchangeLog.setAccountNo(accountNo);
        exchangeLogDao.save(exchangeLog);
        //扣除对应金额
        accountBalance.setAmount(accountBalance.getAmount().subtract(loseAmount));
        accountBalanceDao.save(accountBalance);
    }
}
