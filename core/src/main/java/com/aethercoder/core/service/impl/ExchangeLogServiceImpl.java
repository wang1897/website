package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.*;
import com.aethercoder.core.dao.batch.ConfirmExchangeBatch;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.social.Group;
import com.aethercoder.core.entity.social.QbagMsg;
import com.aethercoder.core.entity.wallet.*;
import com.aethercoder.core.service.*;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.BatchService;
import com.aethercoder.foundation.util.DateUtil;
import freemarker.template.utility.StringUtil;
import io.rong.RongCloud;
import io.rong.messages.QbagMessage;
import io.rong.models.CodeSuccessResult;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.params.QtumMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;
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
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Guo Feiyan on 2017/9/13.
 */
@Service
public class ExchangeLogServiceImpl implements ExchangeLogService {

    private static Logger logger = LoggerFactory.getLogger(ExchangeLogServiceImpl.class);

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private ContractService contractService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private QtumService qtumService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private AccountBalanceDao accountBalanceDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private ExchangeLogDao exchangeLogDao;

    @Autowired
    private WithdrawApplyDao withdrawApplyDao;

    @Autowired
    private SysWalletAddressDao sysWalletAddressDao;

    @Autowired
    private EventApplyDao eventApplyDao;

    @Autowired
    private AccountSubsidiaryDao accountSubsidiaryDao;

    @Value( "${rongCloud.appKey}" )
    private String appKey;
    @Value( "${rongCloud.appSecret}" )
    private String appSecret;

    @Override
    public ExchangeLog saveExchangeLogFromFront(ExchangeLog exchangeLog) {

        // 判断Hex是否合法的
        String txId = exchangeLog.getTransactionHash();
        if (StringUtils.isEmpty(txId)) {
            throw new AppException(ErrorCode.INVALID_OPERATOR);
        }
        // 判断是否已经有同样的Transaction;
        ExchangeLog exchangeLogExist = exchangeLogDao.findByTransactionHash(txId);
        if (exchangeLogExist != null && exchangeLogExist.getId() != null) {
            throw new AppException(ErrorCode.REPETITIVE_OPERATION);
        }

        // 判断金额大于0
        if (!validateExchange(exchangeLog)) {
            throw new AppException(ErrorCode.INVALID_OPERATOR);
        }
        exchangeLog = exchangeLogDao.save(exchangeLog);

        if (WalletConstants.UNCONFIRMED.equals(exchangeLog.getStatus())) {

            //创建批处理
            Date endDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(endDate);
            cal.add(Calendar.MINUTE, 5);

            batchService.createBatchTask("ConfirmExchangeBatch", cal.getTime(), ConfirmExchangeBatch.class.getName(),
                    exchangeLog.getClass().getSimpleName(), exchangeLog.getId());
        }
        return exchangeLog;
    }

    @Override
    public boolean validateExchange(ExchangeLog exchangeLog) {

        boolean result = false;
        // 用户地址是在钱包地址中的
        Account account = accountService.findAccountByAccountNo(exchangeLog.getAccountNo());
        // 判断地址是否是这个用户的
        Set<Address> addresses = account.getAddresses();
        if (!StringUtils.isEmpty(exchangeLog.getFromddress()) && !accountService.checkAddress(exchangeLog.getAccountNo(), exchangeLog.getFromddress())) {
            return result;
        }
        // 判断充值币种是否是合法的
        Long unit = exchangeLog.getUnit();
        Contract contract = contractService.findContractById(unit);
        if (contract == null) {
            return result;
        }
        // 判断金额大于0
        if (exchangeLog.getAmount() == null || (exchangeLog.getAmount()).compareTo(new BigDecimal(0)) < 0) {
            return result;
        }
        result = true;
        return result;
    }

    @Override
    public boolean confirmTransactionHash(ExchangeLog exchangeLog) {
        boolean result = false;
        Long unit = exchangeLog.getUnit();
        Contract contract = contractService.findContractById(unit);

        HashMap map = qtumService.getTransaction(exchangeLog.getTransactionHash());
        if (map == null) {
            return result;
        } else {
            if (map.containsKey("vout")) {
                Object vout = map.get("vout");
                if (vout != null) {
                    ArrayList<HashMap> voutMap = (ArrayList<HashMap>) vout;
                    // Qtum 的充值
                    if (WalletConstants.QTUM_TOKEN_NAME.equals(contract.getAddress())) {
                        for (HashMap voutValue : voutMap) {
                            BigDecimal vValue = BigDecimal.valueOf((Double) voutValue.get("value"));
                            Integer nValue = (Integer) voutValue.get("n");
                            ArrayList<String> addressValue = (ArrayList<String>) (((HashMap) voutValue.get("scriptPubKey")).get("addresses"));
                            String address = addressValue != null ? addressValue.get(0) : "";
                            // 收款地址等同公司地址
                            if (exchangeLog.getAddress().equals(address)) {
                                // 判断金额是否正确
                                if (vValue.compareTo(exchangeLog.getAmount()) == 0) {

                                    // 确认转账是否已经被确认。
                                    if (map.containsKey("confirmations")) {
                                        Integer confirmations = (Integer) map.get("confirmations");
                                        exchangeLog.setStatus(confirmStatus(vValue, confirmations));
                                    }
                                    result = true;
                                    break;
                                }
                            }
                        }
                    } else {
                        org.bitcoinj.core.Address address = new org.bitcoinj.core.Address(QtumMainNetParams.get(), exchangeLog.getAddress());
                        String ourAddress = StringUtils.leftPad(Hex.toHexString(address.getHash160()), 64, '0');
                        // 代币的充值
                        for (HashMap voutValue : voutMap) {
                            BigDecimal vValue = BigDecimal.valueOf((Double) voutValue.get("value"));
                            Integer nValue = (Integer) voutValue.get("n");
                            if (vValue.compareTo(new BigDecimal(0)) == 0) {
                                ArrayList<String> addressValue = (ArrayList<String>) (((HashMap) voutValue.get("scriptPubKey")).get("addresses"));
                                String asm = (String) (((HashMap) voutValue.get("scriptPubKey")).get("asm"));
                                String[] asms = StringUtil.split(asm, ' ');
                                if (asms.length > 3) {
                                    String toAddress = asms[3].substring(8, 72);
                                    String moneyStr = asms[3].substring(72, 136);
                                    if (ourAddress.equals(toAddress)) {

                                        Long moneyLong = Long.parseLong(moneyStr, 16);
                                        Integer decimal = Integer.parseInt(contract.getContractDecimal(), 16);
                                        BigDecimal amount = new BigDecimal(moneyLong);
                                        for (int i = 0; i < decimal; i++) {
                                            amount = amount.divide(new BigDecimal(10));
                                        }
                                        if (amount.compareTo(exchangeLog.getAmount()) == 0) {

                                            if (addressValue.size() > 0) {
                                                org.bitcoinj.core.Address addressUnit = new org.bitcoinj.core.Address(QtumMainNetParams.get(), addressValue.get(0));
                                                String addressUnitStr = StringUtils.leftPad(Hex.toHexString(addressUnit.getHash160()), 64, '0');
                                                if (StringUtils.leftPad(contract.getAddress(), 64, '0').equals(addressUnitStr)) {
                                                    // 判断币种是否一致
                                                    // 确认转账是否已经被确认。
                                                    if (map.containsKey("confirmations")) {
                                                        Integer confirmations = (Integer) map.get("confirmations");
                                                        exchangeLog.setStatus(confirmStatus(vValue, confirmations));
                                                    }
                                                    result = true;
                                                    break;

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public int count() {
        int a = accountBalanceDao.countAll();
        System.out.println("bbbbbb: " + a);
        return a;
    }

    @Override
    public Map getExchangeLogProfile() {
        Map map = new HashMap();
        DecimalFormat df = new DecimalFormat("#");
        Contract contract = contractService.findContractByName(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE);
        if (contract == null) {
            throw new AppException(ErrorCode.CONTRACT_NAME_NOT_EXIST);
        }
        //QBE总发放额度
        BigDecimal allAmount = accountBalanceDao.sumAmountByUnit(contract.getId());
        logger.info("QBE总发放额度  allAmount " + allAmount);
        map.put("allAmount", allAmount);
        //QBE>0用户数
        Integer QBEUsers = accountBalanceDao.countAllByUnit(contract.getId());
        logger.info("QBE>0用户数  QBEUsers " + QBEUsers);
        map.put("QBEUsers", QBEUsers);
        //总用户数
        Integer countAll = accountBalanceDao.countAll();
        logger.info("累计用户量  countAll " + countAll);
        //QBE>0的用户数（占比） QBE不等于0的人数/累计用户量
        Double userQBE = Double.valueOf(QBEUsers) / Double.valueOf(countAll) * 100;
        logger.info("QBE>0的用户数（占比）  userQBE " + df.format(userQBE));
        map.put("userQBE", df.format(userQBE));
        //QBE>1W用户数的QBE总量
        BigDecimal QBEAmountLess = accountBalanceDao.sumAmountByQBELessThan(contract.getId(), WalletConstants.QBE_LESS_THAN);
        logger.info("QBE>1W用户数的QBE总量  QBEAmountLess " + QBEAmountLess);
        map.put("QBEAmountLess", QBEAmountLess);
        //Qbao Energy>10,000.00总量（占比)  QBE>10,000的人的QBE总量/QBE总量
        BigDecimal amountQBE = QBEAmountLess.divide(allAmount, 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
        logger.info(" QBE>10,000的人的QBE总量/QBE总量占比  amountQBE " + amountQBE);
        map.put("amountQBE", amountQBE);
        //QBE>10,000.00人数（占比）QBE>10,000的人数
        Integer QBEUsersLessThousand = accountBalanceDao.countAllByUnitAndAmountGreaterThanEqual(contract.getId(), WalletConstants.QBE_LESS_THAN);
        logger.info("QBE>10,000人数（占比）QBE>10,000的人数  QBEUsersLessThousand " + QBEUsersLessThousand);
        map.put("QBEUsersLessThousand", QBEUsersLessThousand);
        //QBE>10,000.00人数（占比）QBE>10,000的人数/累计用户量
        Double userQBELessThousand = Double.valueOf(QBEUsersLessThousand) / Double.valueOf(countAll) * 100;
        logger.info("QBE>10,000的人数/累计用户量占比 userQBELessThousand " + df.format(userQBELessThousand));
        map.put("userQBELessThousand", df.format(userQBELessThousand));
        //人均Energy量：QBE总量/累计用户数（四舍五入，不保留小数）
        BigDecimal QBEAvg = accountBalanceDao.avgQBE(contract.getId()).setScale(0, BigDecimal.ROUND_HALF_UP);
        logger.info("人均Energy量 QBEAvg " + QBEAvg);
        map.put("QBEAvg", QBEAvg);

        return map;

    }

    private Integer confirmStatus(BigDecimal vValue, Integer confirmations) {
        Integer result = WalletConstants.UNCONFIRMED;
        if (vValue.compareTo(new BigDecimal(10000)) >= 0 && confirmations >= 8) {
            result = WalletConstants.CONFIRMED;
        } else if (vValue.compareTo(new BigDecimal(1000)) >= 0 && confirmations >= 6) {
            result = WalletConstants.CONFIRMED;
        } else if (confirmations >= 3) {
            result = WalletConstants.CONFIRMED;
        } else {
            result = WalletConstants.UNCONFIRMED;
        }
        return result;
    }

    @Override
    @Transactional
    public ExchangeLog updateExchangeLog(ExchangeLog exchangeLog) {

        ExchangeLog exchangeLog1 = exchangeLogDao.findOne(exchangeLog.getId());
        if (exchangeLog.getStatus().equals(WalletConstants.CONFIRMED)) {
//            if(validateExchange(exchangeLog)) {
            //添加交易记录 充值
            AccountBalance accountBalance = new AccountBalance();
            accountBalance.setAmount(exchangeLog1.getAmount());
            accountBalance.setAccountNo(exchangeLog1.getAccountNo());
            accountBalance.setUnit(exchangeLog1.getUnit());
            //充值-Qbao余额累加
            AccountBalance accountBalance1 = accountBalanceService.saveAccountBalanceAmountAdd(accountBalance);
//            }
        }
        //只更新转账状态
        exchangeLog1.setStatus(exchangeLog.getStatus());
        return exchangeLogDao.save(exchangeLog1);
    }

    @Override
    public ExchangeLog saveTakeBonus(ExchangeLog exchangeLog) {

        Random random = new Random();
        BigDecimal amount = new BigDecimal(0);

        //获取1-100中随机数
        Integer i = random.nextInt(100) + 1;
        if (i > 0 && i <= 15) {
            amount = new BigDecimal(500);
        } else if (i > 15 && i <= 30) {
            amount = new BigDecimal(700);
        } else if (i > 30 && i <= 60) {
            amount = new BigDecimal(1000);
        } else if (i > 60 && i <= 90) {
            amount = new BigDecimal(1500);
        } else if (i > 90 && i <= 95) {
            amount = new BigDecimal(2000);
        } else if (i > 95 && i <= 100) {
            amount = new BigDecimal(5000);
        }

        exchangeLog.setAmount(amount);
        return saveTakeBonus(exchangeLog, null, WalletConstants.RECEVIE_NUMBER);
    }

    @Override
    @Transactional
    public ExchangeLog saveTakeBonus(ExchangeLog exchangeLog, Account account, Integer number) {
        if (exchangeLog.getAmount().compareTo(WalletConstants.TAKE_BOUNS_MAX) <= 0 && exchangeLog.getAmount().compareTo(new BigDecimal(0)) > 0) {
            //更新用户抽奖次数
            if (null == account) {
                Account account1 = accountService.findAccountByAccountNo(exchangeLog.getAccountNo());
                //该邀请码的用户抽奖次数加一/减一
                if ((account1.getReceiveNumber() + number) < 0) {
                    throw new AppException(ErrorCode.NO_LOTTERY);
                }
                account1.setReceiveNumber((account1.getReceiveNumber() == null ? 0 : account1.getReceiveNumber()) + number);
                accountDao.saveAndFlush(account1);
            } else {
                account.setReceiveNumber((account.getReceiveNumber() == null ? 0 : account.getReceiveNumber()) + number);
                accountDao.saveAndFlush(account);
            }

            exchangeLog.setUnit(contractService.findContractByName(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE).getId());
            //更新该用户对应代币余额
            AccountBalance accountBalance = new AccountBalance();
            accountBalance.setAmount(exchangeLog.getAmount());
            accountBalance.setAccountNo(exchangeLog.getAccountNo());
            accountBalance.setUnit(exchangeLog.getUnit());
            AccountBalance accountBalance1 = accountBalanceService.saveAccountBalanceAmountAdd(accountBalance);
            //并添加充值表记录
            //已确认状态
            exchangeLog.setStatus(WalletConstants.CONFIRMED);
            exchangeLogDao.save(exchangeLog);

        }
        return exchangeLog;
    }

    @Override
    public Page<ExchangeLog> findExchangeLogsByPage(Integer page, Integer size, String accountNo, Long unit, Integer type, Integer status, String beginDate, String endDate, String accountName, Boolean isDeleted) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<ExchangeLog> exchangeLogs = exchangeLogDao.findAll(new Specification<ExchangeLog>() {
            @Override
            public Predicate toPredicate(Root<ExchangeLog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                List<Predicate> listOr = new ArrayList<Predicate>();
                if (StringUtils.isNotBlank(accountNo)) {
                    list.add(criteriaBuilder.equal(root.get("accountNo").as(String.class), accountNo));
                }
                if (null != unit && !"".equals(unit)) {
                    list.add(criteriaBuilder.equal(root.get("unit").as(Long.class), unit));
                }
                if (null != type && !"".equals(type)) {
                    list.add(criteriaBuilder.equal(root.get("type").as(Integer.class), type));
                }
                if (null != status && !"".equals(status)) {
                    list.add(criteriaBuilder.equal(root.get("status").as(Integer.class), status));
                }
                if (StringUtils.isNotBlank(beginDate)) {
                    //大于或等于传入时间
                    list.add(criteriaBuilder.greaterThanOrEqualTo(root.get("exchangeTime").as(String.class), beginDate));
                }
                if (StringUtils.isNotBlank(endDate)) {
                    //小于或等于传入时间
                    list.add(criteriaBuilder.lessThanOrEqualTo(root.get("exchangeTime").as(String.class), endDate));
                }
                if (null != accountName && !"".equals(accountName)) {
                    List<Account> accounts = accountDao.findAccountsByAccountNameLike("%" + accountName + "%");
                    accounts.forEach(account -> {
                        listOr.add(criteriaBuilder.equal(root.get("accountNo").as(String.class), account.getAccountNo()));
                    });
                }
                if (null != isDeleted && !"".equals(isDeleted)) {
                    list.add(criteriaBuilder.equal(root.get("isDeleted").as(Boolean.class), isDeleted));
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
        exchangeLogs.forEach(exchangeLog -> {
            if (exchangeLog.getAccountNo() != null) {
                Account account = accountDao.findByAccountNo(exchangeLog.getAccountNo());
                if (account != null) {
                    exchangeLog.setAccountName(account.getAccountName());
                }
            }
        });
        return exchangeLogs;
    }

    @Override
    public List<ExchangeLog> takeBonusRankingList() {
        List<ExchangeLog> exchangeLogArrayList = new ArrayList<ExchangeLog>();
        List exchangeLogs = exchangeLogDao.getTakeBonusRankingList();
        List<String> accountNoList = new ArrayList<>();
        BigDecimal oldAmount = new BigDecimal(0);
        Integer oldNumber = 0;
        Long rank = new Long(0);
        for (int i = 0; i < exchangeLogs.size(); i++) {

            Object[] objs = (Object[]) exchangeLogs.get(i);
            BigDecimal newAmount = new BigDecimal(objs[1].toString());
            Integer newNumber = Integer.parseInt(objs[2].toString());
            if (!oldAmount.equals(newAmount) || !(oldNumber.equals(newNumber))) {
                rank = rank + 1;
                oldAmount = newAmount;
                oldNumber = newNumber;
            }
            ExchangeLog exchangeLog = new ExchangeLog();
            exchangeLog.setAccountNo(objs[0].toString());
//            exchangeLog.setHeader(objs[1].toString());
//            exchangeLog.setAccountName(objs[2].toString());
            exchangeLog.setSumAmount(newAmount);
            exchangeLog.setId(rank);
            exchangeLogArrayList.add(exchangeLog);
            accountNoList.add(objs[0].toString());
        }

//        List<Account> accountList =accountDao.findAccountsByAccountNoIn(accountNoList);
        List<Account> accountList = new ArrayList<>();
        for (int i = 0; i < accountNoList.size(); i++) {
            Account account = new Account();
            account = accountDao.findByAccountNo(accountNoList.get(i));
            accountList.add(account);
        }
        Map<String, Account> accountMap = new HashMap<>();
        accountList.forEach(account -> {
            if (account != null) {
                accountMap.put(account.getAccountNo(), account);
            }
        });
        exchangeLogArrayList.forEach(exchangeLog -> {
            Account account = accountMap.get(exchangeLog.getAccountNo());
            if (account != null) {
                exchangeLog.setHeader(account.getHeader() == null ? WalletConstants.DEFAULT_HEADER : account.getHeader());
                exchangeLog.setAccountName(account.getAccountName() == null ? "" : account.getAccountName());
                exchangeLog.setNumber(DateUtil.differentDays(account.getCreateTime(), new Date()));
            } else {
                exchangeLog.setHeader(WalletConstants.DEFAULT_HEADER);
                exchangeLog.setAccountName("");
                exchangeLog.setNumber(0);
            }
        });

        return exchangeLogArrayList;
    }

    @Override
    public Map<String, Object> getTakeBonusRankingListByCount(String accountNo) {
        Map<String, Object> map = new HashMap<>();
        BigDecimal oldAmount = new BigDecimal(0);
        Integer oldNumber = 0;
        Long rank = new Long(0);
        List list = exchangeLogDao.getTakeBonusRankingListByCountAndAccountNo();
        for (int i = 0; i < list.size(); i++) {
            Object[] objs = (Object[]) list.get(i);
            BigDecimal newAmount = new BigDecimal(objs[1].toString());
            Integer newNumber = Integer.parseInt(objs[2].toString());
            if (!oldAmount.equals(newAmount) || !(oldNumber.equals(newNumber))) {
                rank = rank + 1;
                oldAmount = newAmount;
                oldNumber = newNumber;
            }
            ExchangeLog exchangeLog = new ExchangeLog();
            exchangeLog.setAccountNo(objs[0].toString());
//            exchangeLog.setHeader(objs[1].toString());
//            exchangeLog.setAccountName(objs[2].toString());
            exchangeLog.setSumAmount(newAmount);
            exchangeLog.setNumber(newNumber);
            exchangeLog.setId(rank);
            if (exchangeLog.getAccountNo().equals(accountNo)) {
                Account account = accountDao.findByAccountNo(exchangeLog.getAccountNo());
                if (account != null) {
                    exchangeLog.setHeader(account.getHeader() == null ? WalletConstants.DEFAULT_HEADER : account.getHeader());
                    exchangeLog.setAccountName(account.getAccountName() == null ? "" : account.getAccountName());
                } else {
                    exchangeLog.setHeader(WalletConstants.DEFAULT_HEADER);
                    exchangeLog.setAccountName("");
                }
                map.put("count", exchangeLog.getId());
                map.put("sumAmount", exchangeLog.getSumAmount());
                break;
            }
        }
        if (map.size() == 0) {
            map.put("count", list.size() + 1);
            map.put("sumAmount", "0");
        }
        return map;
    }

    @Override
    public Map getEverydayAmount() {
        //获取type为1的ExchangeLog对象
        List<ExchangeLog> exchangeLogs = exchangeLogDao.findExchangeLogsByType(WalletConstants.RECHANGER_TYPE);
        //获取Contract
        List<Contract> contracts = contractDao.findContractsByIsDeleteIsFalseOrderBySequenceAsc();
        Map<Long, Map<String, BigDecimal>> map1 = new HashMap<>();
        for (Contract contract : contracts) {
            if (contract.getName().equals(WalletConstants.QBAO_ENERGY)) {
                continue;
            }
            Map<String, BigDecimal> map = new TreeMap<>();
            for (ExchangeLog exchangeLog : exchangeLogs) {
                Date exchangeTime = exchangeLog.getExchangeTime();
                String exchangeDay = DateUtil.dateToStringYYYYMMDD(exchangeTime);
                BigDecimal amount = exchangeLog.getAmount();

                if (exchangeLog.getUnit() == contract.getId()) {
                    if (map.containsKey(exchangeDay)) {
                        BigDecimal amountUnit = map.get(exchangeDay);
                        map.put(exchangeDay, amountUnit.add(amount));
                    } else {
                        map.put(exchangeDay, amount);
                    }
                }
            }
            map1.put(contract.getId(), map);
        }

        ExchangeLog exchangeLog1 = exchangeLogs.get(0);
        Date firstTime = exchangeLog1.getExchangeTime();
        int days = DateUtil.differentDays(firstTime, new Date());

        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map1.entrySet()) {
            Map<String, BigDecimal> dates = new TreeMap<>();
            Date before = DateUtil.getBeforeDay(firstTime);
            for (int i = 0; i <= days; i++) {
                Date nextDay = DateUtil.getNextDay(before);
                String date = DateUtil.dateToStringYYYYMMDD(nextDay);
                dates.put(date, new BigDecimal(0));
                before = DateUtil.getNextDay(before);
            }
            dates.putAll(entry.getValue());
            map1.put(entry.getKey(), dates);
        }

        Map<Long, List> map2 = new TreeMap();
        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map1.entrySet()) {
            List list = new ArrayList();
            for (Map.Entry<String, BigDecimal> entry1 : entry.getValue().entrySet()) {
                List list1 = new ArrayList();
                list1.add(entry1.getKey());
                list1.add(entry1.getValue());
                list.add(list1);
            }
            map2.put(entry.getKey(), list);
        }
        return map2;
    }

    @Override
    public Map getEverydayDrawAmount() {
        List<ExchangeLog> exchangeLogs = exchangeLogDao.findExchangeLogsByType(WalletConstants.MENTION_TYPE);
        //获取Contract
        List<Contract> contracts = contractDao.findContractsByIsDeleteIsFalseOrderBySequenceAsc();
        Map<Long, Map<String, BigDecimal>> map1 = new HashMap<>();
        for (Contract contract : contracts) {
            if (contract.getName().equals(WalletConstants.QBAO_ENERGY)) {
                continue;
            }
            Map<String, BigDecimal> map = new TreeMap<>();
            for (ExchangeLog exchangeLog : exchangeLogs) {
                Date exchangeTime = exchangeLog.getExchangeTime();
                String exchangeDay = DateUtil.dateToStringYYYYMMDD(exchangeTime);
                BigDecimal amount = exchangeLog.getAmount();

                if (exchangeLog.getUnit() == contract.getId()) {
                    if (map.containsKey(exchangeDay)) {
                        BigDecimal amountUnit = map.get(exchangeDay);
                        map.put(exchangeDay, amountUnit.add(amount));
                    } else {
                        map.put(exchangeDay, amount);
                    }
                }
            }
            map1.put(contract.getId(), map);
        }

        ExchangeLog exchangeLog1 = exchangeLogs.get(0);
        Date firstTime = exchangeLog1.getExchangeTime();
        int days = DateUtil.differentDays(firstTime, new Date());

        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map1.entrySet()) {
            Map<String, BigDecimal> dates = new TreeMap<>();
            Date before = DateUtil.getBeforeDay(firstTime);
            for (int i = 0; i <= days; i++) {
                Date nextDay = DateUtil.getNextDay(before);
                String date = DateUtil.dateToStringYYYYMMDD(nextDay);
                dates.put(date, new BigDecimal(0));
                before = DateUtil.getNextDay(before);
            }
            dates.putAll(entry.getValue());
            map1.put(entry.getKey(), dates);
        }

        Map<Long, List> map2 = new TreeMap();
        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map1.entrySet()) {
            List list = new ArrayList();
            for (Map.Entry<String, BigDecimal> entry1 : entry.getValue().entrySet()) {
                List list1 = new ArrayList();
                list1.add(entry1.getKey());
                list1.add(entry1.getValue());
                list.add(list1);
            }
            map2.put(entry.getKey(), list);
        }
        return map2;
    }

    @Override
    public Map getEveryweekAmount() {
        //获取type为1的ExchangeLog对象
        List<ExchangeLog> exchangeLogs = exchangeLogDao.findExchangeLogsByType(WalletConstants.RECHANGER_TYPE);
        //获取Contract
        List<Contract> contracts = contractDao.findContractsByIsDeleteIsFalseOrderBySequenceAsc();
        Map<Long, Map<String, BigDecimal>> map1 = new HashMap<>();
        for (Contract contract : contracts) {
            if (contract.getName().equals(WalletConstants.QBAO_ENERGY)) {
                continue;
            }
            Map<String, BigDecimal> map = new TreeMap<>();
            for (ExchangeLog exchangeLog : exchangeLogs) {
                Date exchangeTime = exchangeLog.getExchangeTime();
                String exchangeDay = DateUtil.dateToStringYYYYMMDD(exchangeTime);
                BigDecimal amount = exchangeLog.getAmount();

                if (exchangeLog.getUnit() == contract.getId()) {
                    if (map.containsKey(exchangeDay)) {
                        BigDecimal amountUnit = map.get(exchangeDay);
                        map.put(exchangeDay, amountUnit.add(amount));
                    } else {
                        map.put(exchangeDay, amount);
                    }
                }
            }
            map1.put(contract.getId(), map);
        }

        ExchangeLog exchangeLog1 = exchangeLogs.get(0);
        Date firstTime = exchangeLog1.getExchangeTime();
        int days = DateUtil.differentDays(firstTime, new Date());

        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map1.entrySet()) {
            Map<String, BigDecimal> dates = new TreeMap<>();
            Date before = DateUtil.getBeforeDay(firstTime);
            for (int i = 0; i <= days; i++) {
                Date nextDay = DateUtil.getNextDay(before);
                String date = DateUtil.dateToStringYYYYMMDD(nextDay);
                dates.put(date, new BigDecimal(0));
                before = DateUtil.getNextDay(before);
            }
            dates.putAll(entry.getValue());
            map1.put(entry.getKey(), dates);
        }

        Map<Long, Map<String, BigDecimal>> map3 = new HashMap<>();
        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map1.entrySet()) {
            Map<String, BigDecimal> map2 = new TreeMap<>();
            List<String> list = new ArrayList<>();
            Map<String, BigDecimal> value = entry.getValue();
            for (Map.Entry<String, BigDecimal> entry1 : value.entrySet()) {
                String str = "";
                Date date = DateUtil.stringToDateFormat(entry1.getKey());
                Long yearOfDate = DateUtil.getYearOfDate(date);
                Long weekth = DateUtil.getWeekthByDate(date);
                str = yearOfDate.toString() + "-" + weekth.toString();
                if (map2.containsKey(str)) {
                    BigDecimal value1 = entry1.getValue();
                    map2.put(str, map2.get(str).add(value1));
                } else {
                    map2.put(str, entry1.getValue());
                }
                map2.remove("2017-1");
            }
            map3.put(entry.getKey(), map2);
        }
        Map<Long, List> map = new TreeMap<>();
        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map3.entrySet()) {

            List list = new ArrayList();
            for (Map.Entry<String, BigDecimal> entry1 : entry.getValue().entrySet()) {
                List list1 = new ArrayList();
                list1.add(entry1.getKey());
                list1.add(entry1.getValue());
                list.add(list1);
            }
            map.put(entry.getKey(), list);
        }
        return map;
    }

    @Override
    public Map getEveryweekDrawAmount() {
        //获取type为1的ExchangeLog对象
        List<ExchangeLog> exchangeLogs = exchangeLogDao.findExchangeLogsByType(WalletConstants.MENTION_TYPE);
        //获取Contract
        List<Contract> contracts = contractDao.findContractsByIsDeleteIsFalseOrderBySequenceAsc();
        Map<Long, Map<String, BigDecimal>> map1 = new HashMap<>();
        for (Contract contract : contracts) {
            if (contract.getName().equals(WalletConstants.QBAO_ENERGY)) {
                continue;
            }
            Map<String, BigDecimal> map = new TreeMap<>();
            for (ExchangeLog exchangeLog : exchangeLogs) {
                Date exchangeTime = exchangeLog.getExchangeTime();
                String exchangeDay = DateUtil.dateToStringYYYYMMDD(exchangeTime);
                BigDecimal amount = exchangeLog.getAmount();

                if (exchangeLog.getUnit() == contract.getId()) {
                    if (map.containsKey(exchangeDay)) {
                        BigDecimal amountUnit = map.get(exchangeDay);
                        map.put(exchangeDay, amountUnit.add(amount));
                    } else {
                        map.put(exchangeDay, amount);
                    }
                }
            }
            map1.put(contract.getId(), map);
        }

        ExchangeLog exchangeLog1 = exchangeLogs.get(0);
        Date firstTime = exchangeLog1.getExchangeTime();
        int days = DateUtil.differentDays(firstTime, new Date());

        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map1.entrySet()) {
            Map<String, BigDecimal> dates = new TreeMap<>();
            Date before = DateUtil.getBeforeDay(firstTime);
            for (int i = 0; i <= days; i++) {
                Date nextDay = DateUtil.getNextDay(before);
                String date = DateUtil.dateToStringYYYYMMDD(nextDay);
                dates.put(date, new BigDecimal(0));
                before = DateUtil.getNextDay(before);
            }
            dates.putAll(entry.getValue());
            map1.put(entry.getKey(), dates);
        }

        Map<Long, Map<String, BigDecimal>> map3 = new HashMap<>();
        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map1.entrySet()) {
            Map<String, BigDecimal> map2 = new TreeMap<>();
            List<String> list = new ArrayList<>();
            Map<String, BigDecimal> value = entry.getValue();
            for (Map.Entry<String, BigDecimal> entry1 : value.entrySet()) {
                String str = "";
                Date date = DateUtil.stringToDateFormat(entry1.getKey());
                Long yearOfDate = DateUtil.getYearOfDate(date);
                Long weekth = DateUtil.getWeekthByDate(date);
                str = yearOfDate.toString() + "-" + weekth.toString();
                if (map2.containsKey(str)) {
                    BigDecimal value1 = entry1.getValue();
                    map2.put(str, map2.get(str).add(value1));
                } else {
                    map2.put(str, entry1.getValue());
                }
                map2.remove("2017-1");
            }
            map3.put(entry.getKey(), map2);
        }
        Map<Long, List> map = new TreeMap<>();
        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map3.entrySet()) {

            List list = new ArrayList();
            for (Map.Entry<String, BigDecimal> entry1 : entry.getValue().entrySet()) {
                List list1 = new ArrayList();
                list1.add(entry1.getKey());
                list1.add(entry1.getValue());
                list.add(list1);
            }
            map.put(entry.getKey(), list);
        }
        return map;
    }

    @Override
    public Map getEverymonthAmount() {
        //获取type为1的ExchangeLog对象
        List<ExchangeLog> exchangeLogs = exchangeLogDao.findExchangeLogsByType(WalletConstants.RECHANGER_TYPE);
        //获取Contract
        List<Contract> contracts = contractDao.findContractsByIsDeleteIsFalseOrderBySequenceAsc();
        Map<Long, Map<String, BigDecimal>> map1 = new HashMap<>();
        for (Contract contract : contracts) {
            if (contract.getName().equals(WalletConstants.QBAO_ENERGY)) {
                continue;
            }
            Map<String, BigDecimal> map = new TreeMap<>();
            for (ExchangeLog exchangeLog : exchangeLogs) {
                Date exchangeTime = exchangeLog.getExchangeTime();
                String exchangeDay = DateUtil.dateToStringYYYYMMDD(exchangeTime);
                BigDecimal amount = exchangeLog.getAmount();

                if (exchangeLog.getUnit() == contract.getId()) {
                    if (map.containsKey(exchangeDay)) {
                        BigDecimal amountUnit = map.get(exchangeDay);
                        map.put(exchangeDay, amountUnit.add(amount));
                    } else {
                        map.put(exchangeDay, amount);
                    }
                }
            }
            map1.put(contract.getId(), map);
        }

        ExchangeLog exchangeLog1 = exchangeLogs.get(0);
        Date firstTime = exchangeLog1.getExchangeTime();
        int days = DateUtil.differentDays(firstTime, new Date());

        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map1.entrySet()) {
            Map<String, BigDecimal> dates = new TreeMap<>();
            Date before = DateUtil.getBeforeDay(firstTime);
            for (int i = 0; i <= days; i++) {
                Date nextDay = DateUtil.getNextDay(before);
                String date = DateUtil.dateToStringYYYYMMDD(nextDay);
                dates.put(date, new BigDecimal(0));
                before = DateUtil.getNextDay(before);
            }
            dates.putAll(entry.getValue());
            map1.put(entry.getKey(), dates);
        }

        Map<Long, Map<String, BigDecimal>> map3 = new TreeMap<>();
        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map1.entrySet()) {
            Map<String, BigDecimal> map2 = new TreeMap<>();
            List<String> list = new ArrayList<>();
            Map<String, BigDecimal> value = entry.getValue();
            for (Map.Entry<String, BigDecimal> entry1 : value.entrySet()) {
                String str = "";
                Date date = DateUtil.stringToDateFormat(entry1.getKey());
                Long yearOfDate = DateUtil.getYearOfDate(date);
                Long month = DateUtil.getMonthByDate(date);
                month = month + 1;
                str = yearOfDate.toString() + "-" + month.toString();
                if (map2.containsKey(str)) {
                    BigDecimal value1 = entry1.getValue();
                    map2.put(str, map2.get(str).add(value1));
                } else {
                    map2.put(str, entry1.getValue());
                }
            }
            map3.put(entry.getKey(), map2);
        }
        Map<Long, List> map = new TreeMap<>();
        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map3.entrySet()) {
            List list = new ArrayList();
            for (Map.Entry<String, BigDecimal> entry1 : entry.getValue().entrySet()) {
                List list1 = new ArrayList();
                list1.add(entry1.getKey());
                list1.add(entry1.getValue());
                list.add(list1);
            }
            map.put(entry.getKey(), list);
        }
        return map;
    }

    @Override
    public Map getEverymonthDrawAmount() {
        //获取type为1的ExchangeLog对象
        List<ExchangeLog> exchangeLogs = exchangeLogDao.findExchangeLogsByType(WalletConstants.MENTION_TYPE);
        //获取Contract
        List<Contract> contracts = contractDao.findContractsByIsDeleteIsFalseOrderBySequenceAsc();
        Map<Long, Map<String, BigDecimal>> map1 = new HashMap<>();
        for (Contract contract : contracts) {
            if (contract.getName().equals(WalletConstants.QBAO_ENERGY)) {
                continue;
            }
            Map<String, BigDecimal> map = new TreeMap<>();
            for (ExchangeLog exchangeLog : exchangeLogs) {
                Date exchangeTime = exchangeLog.getExchangeTime();
                String exchangeDay = DateUtil.dateToStringYYYYMMDD(exchangeTime);
                BigDecimal amount = exchangeLog.getAmount();

                if (exchangeLog.getUnit() == contract.getId()) {
                    if (map.containsKey(exchangeDay)) {
                        BigDecimal amountUnit = map.get(exchangeDay);
                        map.put(exchangeDay, amountUnit.add(amount));
                    } else {
                        map.put(exchangeDay, amount);
                    }
                }
            }
            map1.put(contract.getId(), map);
        }

        ExchangeLog exchangeLog1 = exchangeLogs.get(0);
        Date firstTime = exchangeLog1.getExchangeTime();
        int days = DateUtil.differentDays(firstTime, new Date());

        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map1.entrySet()) {
            Map<String, BigDecimal> dates = new TreeMap<>();
            Date before = DateUtil.getBeforeDay(firstTime);
            for (int i = 0; i <= days; i++) {
                Date nextDay = DateUtil.getNextDay(before);
                String date = DateUtil.dateToStringYYYYMMDD(nextDay);
                dates.put(date, new BigDecimal(0));
                before = DateUtil.getNextDay(before);
            }
            dates.putAll(entry.getValue());
            map1.put(entry.getKey(), dates);
        }

        Map<Long, Map<String, BigDecimal>> map3 = new TreeMap<>();
        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map1.entrySet()) {
            Map<String, BigDecimal> map2 = new TreeMap<>();
            List<String> list = new ArrayList<>();
            Map<String, BigDecimal> value = entry.getValue();
            for (Map.Entry<String, BigDecimal> entry1 : value.entrySet()) {
                String str = "";
                Date date = DateUtil.stringToDateFormat(entry1.getKey());
                Long yearOfDate = DateUtil.getYearOfDate(date);
                Long month = DateUtil.getMonthByDate(date);
                month = month + 1;
                str = yearOfDate.toString() + "-" + month.toString();
                if (map2.containsKey(str)) {
                    BigDecimal value1 = entry1.getValue();
                    map2.put(str, map2.get(str).add(value1));
                } else {
                    map2.put(str, entry1.getValue());
                }
            }
            map3.put(entry.getKey(), map2);
        }
        Map<Long, List> map = new TreeMap<>();
        for (Map.Entry<Long, Map<String, BigDecimal>> entry : map3.entrySet()) {
            List list = new ArrayList();
            for (Map.Entry<String, BigDecimal> entry1 : entry.getValue().entrySet()) {
                List list1 = new ArrayList();
                list1.add(entry1.getKey());
                list1.add(entry1.getValue());
                list.add(list1);
            }
            map.put(entry.getKey(), list);
        }
        return map;
    }

    @Override
    public List<ExchangeLog> findUnclearExchangeLogs(Long startId, Long endId, Integer type) {
        if (startId.equals(new Long(0)) && endId.equals(new Long(0))) {
            return exchangeLogDao.findExchangeLogByTypeAndStatusOrderByIdDesc(type, new Integer(WalletConstants.STATUS_APPROVED));
        } else if (startId.equals(new Long(0))) {
            return exchangeLogDao.findExchangeLogByIdBeforeAndTypeAndStatusOrderByIdDesc(endId, type, new Integer(WalletConstants.STATUS_APPROVED));
        } else if (endId.equals(new Long(0))) {
            return exchangeLogDao.findExchangeLogByIdAfterAndTypeAndStatusOrderByIdDesc(startId, type, new Integer(WalletConstants.STATUS_APPROVED));
        } else {
            return exchangeLogDao.findExchangeLogByIdBetweenAndTypeAndStatusOrderByIdDesc(startId, endId, type, new Integer(WalletConstants.STATUS_APPROVED));
        }

    }

    @Override
    public WithdrawApply checkBeforeWithdraw(String accountNo, Long unit, BigDecimal withdrawAmount) {
        WithdrawApply withdrawApply = new WithdrawApply();

        // 获取该代币的提笔手续费，当日提币单次上限和总上限。（smart_contract表中的withdraw开头的3字段）Exchange
        Contract contract = contractDao.findOne(unit);
        // 如果此币不允许提币，报错 不允许提此类代币。
        if (!contract.getInService()) {
            throw new AppException(ErrorCode.FEE_PERMISSION_MSG);
        }
        BigDecimal withdrawFee = contract.getWithdrawFee();
        // 如果首次提币免手续费
        AccountSubsidiary accountSubsidiary = accountSubsidiaryDao.findByAccountNo(accountNo);
        if (accountSubsidiary != null && !accountSubsidiary.getHadWithdraw() || accountSubsidiary==null) {
            withdrawFee = new BigDecimal(0);
        }

        // 单次提币上限
        BigDecimal withdrawOneLimit = contract.getWithdrawOneLimit();
        // 每个人单日提币上限
        BigDecimal withdrawDayLimit = contract.getWithdrawDayLimit();
        // 每个币种单日总上限
        BigDecimal withdrawLimit = contract.getWithdrawLimit();
        if (withdrawOneLimit.compareTo(withdrawAmount) < 0) {
            String[] param = {contract.getName(), withdrawOneLimit.toString()};
            throw new AppException(ErrorCode.FEE_ACCOUNT_ONY_LIMIT_MSG, param);
        }

        // 获取该代币的现在资金池余额（sys_wallet_address表中的left_amount - withdraw表中再排队的提币金额总值）
        List<SysWalletAddress> sysWalletAddressList;
        SysWalletAddress useWalletAddress = new SysWalletAddress();
        if (WalletConstants.QBT_TOKEN_NAME.equals(contract.getName())) {
            // QBT的取所有地址
            sysWalletAddressList = sysWalletAddressDao.findAll();
        } else {
            sysWalletAddressList = sysWalletAddressDao.findByContractId(unit);
        }
        BigDecimal lastLeftAmount = new BigDecimal(0);
        for (SysWalletAddress sysAddress : sysWalletAddressList) {

            BigDecimal addressAmount = sysAddress.getLastLeftAmount();
            if (WalletConstants.QBT_TOKEN_NAME.equals(contract.getName())) {
                addressAmount = sysAddress.getQbtLeftAmount();
            }
            if (addressAmount.compareTo(lastLeftAmount) > 0) {
                lastLeftAmount = addressAmount;
                useWalletAddress = sysAddress;
            }
        }
        if (lastLeftAmount.compareTo(new BigDecimal(0)) <= 0) {
            String[] param = {contract.getName()};
            throw new AppException(ErrorCode.FEE_UNIT_LIMIT_MSG, param);
        }
        Date startTime = DateUtil.getTodayBegin();
        Date endTime = DateUtil.getTodayEnd();

        BigDecimal sumWaitingAmount = withdrawApplyDao.sumAmountByUnitAndStatusIsLessThanEqual(unit,
                WalletConstants.FEE_TRADING_TREMS_WATING, startTime, endTime);
        BigDecimal leftAmount = lastLeftAmount;
        if (sumWaitingAmount != null) {
            leftAmount = lastLeftAmount.subtract(sumWaitingAmount);
        }
        // 如果代币资金池金额已经用完，则报错告知今日该币种 额度用完无法提币。
        if (leftAmount.compareTo(new BigDecimal(0)) <= 0) {
            String[] param = {contract.getName()};
            throw new AppException(ErrorCode.FEE_UNIT_LIMIT_MSG, param);
        } else if (leftAmount.compareTo(withdrawAmount) <= 0) {
            String[] param = {contract.getName(), leftAmount.toString()};
            throw new AppException(ErrorCode.FEE_UNIT_LIMIT_LEFT, param);
        }

        Date todayBegin = DateUtil.getTodayBegin();
        Date todayEnd = DateUtil.getTodayEnd();
        BigDecimal sumUnitAmount = exchangeLogDao.sumByTypeAndUnitAndStatusAndExchangeTimeIsBetween(WalletConstants.MENTION_TYPE, unit, WalletConstants.CONFIRMED, todayBegin, todayEnd);
        if (sumUnitAmount == null) {
            sumUnitAmount = new BigDecimal(0);
        }
        // 如果提币总额已经用完，则报错告知此币提币金额已经用完。
        if (sumUnitAmount.compareTo(withdrawLimit) >= 0) {
            String[] param = {contract.getName()};
            throw new AppException(ErrorCode.FEE_UNIT_LIMIT_MSG, param);
        } else if ((sumUnitAmount.add(withdrawAmount)).compareTo(withdrawLimit) > 0) {
            String[] param = {contract.getName(), withdrawLimit.subtract(sumUnitAmount).toString()};
            throw new AppException(ErrorCode.FEE_UNIT_LIMIT_LEFT, param);
        }
        // 获取该用户本日提币量，exchangeLog表中exchange_time是今日的提币状态不是失败的资金总额。\
        BigDecimal sumAmount = exchangeLogDao.sumByTypeAndAccountNoAndUnitAndStatusAndExchangeTimeIsBetween(WalletConstants.MENTION_TYPE, accountNo, unit, WalletConstants.CONFIRMED, todayBegin, todayEnd);
        if (sumAmount == null) {
            sumAmount = new BigDecimal(0);
        }
        // 如果提币总额已经用完，则报错告知此人本日金额已经用完。
        if (sumAmount.compareTo(withdrawDayLimit) >= 0) {
            String[] param = {contract.getName(), withdrawDayLimit.toString()};
            throw new AppException(ErrorCode.FEE_ACCOUNT_DAY_LIMIT_MSG, param);
            // 如果剩余提币额度小于本次提币额度， 则将本次提币额度修改成剩余提币额度。
        } else if (withdrawAmount.compareTo(withdrawDayLimit.subtract(sumAmount)) > 0) {
            String[] param = {contract.getName(), withdrawDayLimit.subtract(sumAmount).toString()};
            throw new AppException(ErrorCode.FEE_LIMIT_MSG, param);
        }

        // 获取该用户的财务中心的代币余额
        AccountBalance accountBalance = accountBalanceDao.findByAccountNoAndUnit(accountNo, unit);
        BigDecimal amount = accountBalance.getAmount();
        if (amount == null) {
            amount = new BigDecimal(0);
        }

        // 如果此人没有余额，报错告知他账号中心里没有这个代币的钱，无法提币。
        if (amount.compareTo(new BigDecimal(0)) <= 0) {
            throw new AppException(ErrorCode.ACCOUNT_CENTER_MSG);
        }
        // 如果（用户余额-所需手续费）小于本次提币金额，则返回金额不足。
        if (amount.subtract(withdrawFee).compareTo(withdrawAmount) < 0) {
            // 超过提币最大值
            throw new AppException(ErrorCode.QBAO_UNDERBALANCE);
        }
        withdrawApply.setAmount(withdrawAmount);
        withdrawApply.setFee(withdrawFee);
        withdrawApply.setServicePool(useWalletAddress.getId());
        return withdrawApply;
    }

    @Override
    @Transactional
    public WithdrawApply withdrawApply(WithdrawApply withdrawApply) {
        if (validateApply(withdrawApply)) {
            String accountNo = withdrawApply.getAccountNo();
            // 手续费币种即转账币种
            withdrawApply.setFeeUnit(withdrawApply.getUnit());

            ExchangeLog exchangeLog = new ExchangeLog();
            exchangeLog.setType(WalletConstants.MENTION_TYPE);
            exchangeLog.setStatus(WalletConstants.UNCONFIRMED);
            exchangeLog.setAmount(withdrawApply.getAmount());
            exchangeLog.setAccountNo(accountNo);
            exchangeLog.setAddress(withdrawApply.getToAddress());
            exchangeLog.setFee(withdrawApply.getFee());
            exchangeLog.setFeeUnit(withdrawApply.getFeeUnit());
            exchangeLog.setUnit(withdrawApply.getUnit());
            exchangeLog.setExchangeTime(withdrawApply.getApplyTime());
            exchangeLog.setIsDeleted(false);

            // 扣除余额
            AccountBalance accountBalance = new AccountBalance();
            accountBalance.setAccountNo(accountNo);
            accountBalance.setUnit(withdrawApply.getUnit());
            accountBalance.setAmount(withdrawApply.getAmount().add(withdrawApply.getFee()));
            accountBalanceService.saveAccountBalanceAmountSubtract(accountBalance);
            // 添加提币记录
            exchangeLogDao.save(exchangeLog);
            // 添加提币排队记录
            withdrawApply.setStatus(WalletConstants.WITHDRAW_STATUS_APPLIED);
            withdrawApply.setApplyTime(new Date());
            withdrawApply.setExchangeNo(exchangeLog.getId().toString());
            withdrawApplyDao.save(withdrawApply);

            // 更新已经提币的状态，给免手续费用
            AccountSubsidiary accountSubsidiary = accountSubsidiaryDao.findByAccountNo(accountNo);
            if (accountSubsidiary != null) {
                accountSubsidiary.setHadWithdraw(true);
            } else {
                accountSubsidiary = new AccountSubsidiary();
                accountSubsidiary.setAccountNo(accountNo);
                accountSubsidiary.setHadWithdraw(true);
                accountSubsidiary.setIsReceive("11111");
            }
            accountSubsidiaryDao.save(accountSubsidiary);
        }
        return withdrawApply;
    }

    /**
     * 检验提币的合法性
     */
    private boolean validateApply(WithdrawApply withdrawApply) {
        boolean result = true;
        if (withdrawApply == null) {
            throw new AppException(ErrorCode.INVALID_OPERATOR);
        }
        // 用户地址是在钱包地址中的
        // 判断地址是否是这个用户的
        if (!StringUtils.isEmpty(withdrawApply.getToAddress())
                && !accountService.checkAddress(withdrawApply.getAccountNo(), withdrawApply.getToAddress())) {
            throw new AppException(ErrorCode.INVALID_ADDRESS);
        }
        // 判断充值币种是否是合法的
        Long unit = withdrawApply.getUnit();
        Contract contract = contractService.findContractById(unit);
        if (contract == null) {
            throw new AppException(ErrorCode.CONTRACT_NAME_NOT_EXIST);
        }
        // 判断金额大于0
        if (withdrawApply.getAmount() == null || (withdrawApply.getAmount()).compareTo(new BigDecimal(0)) < 0) {
            throw new AppException(ErrorCode.CHECK_GUESS_NUMBER_AMOUNT);
        }
        // 判断金额是否超过可提取的单笔金额
        WithdrawApply maxAmount = checkBeforeWithdraw(withdrawApply.getAccountNo(), withdrawApply.getUnit(), withdrawApply.getAmount());

        if (maxAmount != null && withdrawApply.getAmount().compareTo(maxAmount.getAmount()) == 0) {
            withdrawApply.setFee(maxAmount.getFee());
            withdrawApply.setServicePool(maxAmount.getServicePool());
        }

        return result;
    }

    @Override
    public String combineMessage(String content, ExchangeLog exchangeLog) {

        QbagMsg message = new QbagMsg();
        message.setContent(content);
        message.setUnit(exchangeLog.getUnit());
        message.setAmount(exchangeLog.getAmount());
        message.setTime(exchangeLog.getExchangeTime());
        message.setStatus(exchangeLog.getStatus().toString());
        message.setType(exchangeLog.getType().toString());
        return message.toString();
    }
    /**
     * 发送Q包相关消息
     */
    @Override
    public void sendQbagMessage(String[] toUserNoList, String message, String pushContent) {
        logger.info("sendQbagMessage content:"+message);

        QbagMessage txtMessage = new QbagMessage(message);
        //发送好友请求--toUser
        RongCloud rongCloud = RongCloud.getInstance(appKey, appSecret);
        CodeSuccessResult userGetTokenResult;
        try {
            userGetTokenResult = rongCloud.message.publishPrivate(WalletConstants.PAY_ASSISTANT,
                    toUserNoList,
                    txtMessage,
                    pushContent,
                    null,
                    null,
                    null, null, null, null);
            logger.info("----sendQbagMessage :" + userGetTokenResult.toString());
        } catch (Exception e) {
            logger.error("---sendQbagMessage error----", e);
            throw new RuntimeException("sendQbagMessage error:" + e.getMessage());
        }
    }

}
