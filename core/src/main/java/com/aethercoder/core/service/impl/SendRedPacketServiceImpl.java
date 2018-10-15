package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.ContractDao;
import com.aethercoder.core.dao.ExchangeLogDao;
import com.aethercoder.core.dao.GetRedPacketDao;
import com.aethercoder.core.dao.SendRedPacketDao;
import com.aethercoder.core.dao.batch.SendRedPacketBatch;
import com.aethercoder.core.entity.event.*;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.BatchService;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.core.service.*;
import com.aethercoder.foundation.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:42
 */
@Service
public class SendRedPacketServiceImpl implements SendRedPacketService {

    @Autowired
    private AccountBalanceService accountBalanceService;
    @Autowired
    private ExchangeLogDao exchangeLogDao;
    @Autowired
    private SendRedPacketDao sendRedPacketDao;
    @Autowired
    private GetRedPacketDao getRedPacketDao;
    @Autowired
    private AccountService accountService;

    @Autowired
    private ContractDao contractDao;
    @Autowired
    private BatchService batchService;

    @Autowired
    public LocaleMessageService localeMessageUtil;

    @Override
    public SendRedPacket createRedPacket(SendRedPacket redPacket) {

        if (validateCreate(redPacket)) {
            redPacket.setIsAvailable(Boolean.TRUE);
            redPacket.setBalance(redPacket.getAmount());
            if (StringUtils.isBlank(redPacket.getComment())) {
                redPacket.setComment(localeMessageUtil.getLocalMessage("SEND_RED_PACKET_COMMENT", null));
            }
            redPacket.setSendTime(new Date());

            sendRedPacketDao.save(redPacket);
            // 生成子红包。
            createGetRedPackets(redPacket);
            // 变更红包余额
            changeAccountBalance(redPacket);
            //保存红包交易记录
            createLogForRedPacket(redPacket);
        }
        //创建批处理
        Date endDate = redPacket.getSendTime();
        Calendar cal = Calendar.getInstance();
        cal.setTime(endDate);
        cal.add(Calendar.HOUR, 24);

        batchService.createBatchTask("SendRedPacket", cal.getTime(), SendRedPacketBatch.class.getName(), redPacket.getClass().getSimpleName(), redPacket.getId());

        return redPacket;
    }

    @Override
    public SendRedPacket findOne(long id) {
        return sendRedPacketDao.findById(id);
    }

    @Override
    public RedPacketInfo getRedPacketInfo(Long redPacketId) {
        RedPacketInfo redPacketInfo = new RedPacketInfo();
        SendRedPacket sendRedPacket = sendRedPacketDao.findById(redPacketId);

        // 获取发的头像
        Account accountSend = accountService.findAccountByAccountNo(sendRedPacket.getAccountNo());
        sendRedPacket.setAccountName(accountSend.getAccountName());
        sendRedPacket.setHeader(accountSend.getHeader());
        //计算时间
        List<GetRedPacket> getRedPackets = getRedPacketDao.getGetRedPacketByRedPacketIdAndAccountNoIsNotNullOrderByGetTimeDesc(redPacketId);

        // 获取收的头像
        for (GetRedPacket redPacket : getRedPackets) {
            Account account = accountService.findAccountByAccountNo(redPacket.getAccountNo());
            redPacket.setAccountName(account.getAccountName());
            redPacket.setHeader(account.getHeader());
        }
        redPacketInfo.setSendRedPacket(sendRedPacket);

        redPacketInfo.setRedPacketList(getRedPackets);
        return redPacketInfo;
    }

    @Override
    public Page<SendRedPacket> findSendPacketsByAccountNo(Integer page, Integer size, String accountNo, Long unit) {

        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "sendTime");
        Page<SendRedPacket> sendRedPackets = sendRedPacketDao.findAll(new Specification<SendRedPacket>() {
            @Override
            public Predicate toPredicate(Root<SendRedPacket> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (accountNo != null) {
                    list.add(criteriaBuilder.equal(root.get("accountNo").as(String.class), accountNo));
                }
                if (unit != null) {
                    list.add(criteriaBuilder.equal(root.get("unit").as(Long.class), unit));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return sendRedPackets;
    }

    private void changeAccountBalance(SendRedPacket redPacket) {
        AccountBalance accountBalance = accountBalanceService.findByAccountNoAndUnit(redPacket.getAccountNo(), redPacket.getUnit());
        accountBalance.setAmount(accountBalance.getAmount().subtract(redPacket.getAmount()));
        accountBalanceService.saveAccountBalance(accountBalance);
    }

    private void createLogForRedPacket(SendRedPacket redPacket) {
        ExchangeLog exchangeLog = new ExchangeLog();
        exchangeLog.setType(WalletConstants.SEND_RED_BAG);
        exchangeLog.setAccountNo(redPacket.getAccountNo());
        exchangeLog.setAmount(redPacket.getAmount());
        exchangeLog.setExchangeTime(new Date());
        exchangeLog.setStatus(WalletConstants.CONFIRMED);
        exchangeLog.setUnit(redPacket.getUnit());
        exchangeLogDao.save(exchangeLog);
    }

    private void createGetRedPackets(SendRedPacket redPacket) {
        Integer type = redPacket.getType();
        BigDecimal amount = redPacket.getAmount();
        Integer num = redPacket.getNumber();

        List<GetRedPacket> getGetRedPacketList = new ArrayList<>();
        List<BigDecimal> getPieceAmountList = new ArrayList<>();
        BigDecimal pieceAmount = amount.divide(new BigDecimal(num), 6, RoundingMode.HALF_UP);
        if (WalletConstants.RED_PACKET_TYPE_COMPETE.equals(type)) {
            getPieceAmountList = getRedPacKetLists(redPacket);
        } else {
            for (int i = 1; i < num; i++) {
                getPieceAmountList.add(pieceAmount);
            }
            getPieceAmountList.add(amount.subtract(pieceAmount.multiply(new BigDecimal(num - 1))));
        }

        BigDecimal maxAmount = Collections.max(getPieceAmountList);
        Boolean bestLuck = false;
        for (int i = 0; i < num; i++) {
            GetRedPacket getRedPacket = new GetRedPacket();
            if (WalletConstants.RED_PACKET_TYPE_COMPETE.equals(type)) {
                // 第一个手气最佳
                if (!bestLuck && maxAmount.equals(getPieceAmountList.get(i))) {
                    // 设置手气最佳
                    bestLuck = true;
                    getRedPacket.setBestLuck(true);
                }
            }
            getRedPacket.setAmount(getPieceAmountList.get(i));
            getRedPacket.setRedPacketId(redPacket.getId());
            getRedPacket.setSequence(i);
            getRedPacket.setUnit(redPacket.getUnit());
            getGetRedPacketList.add(getRedPacket);
        }
        getRedPacketDao.save(getGetRedPacketList);
    }

    private List<BigDecimal> getRedPacKetLists(SendRedPacket redPacket) {
        List<BigDecimal> redPackets = new ArrayList<>();
        BigDecimal amount = redPacket.getAmount();
        Integer num = redPacket.getNumber();
        BigDecimal minimum = new BigDecimal(0.000001);
        BigDecimal safeTotal = amount;
        Random random = new Random();
        for (int i = 1; i < num; i++) {
            BigDecimal leftNum = new BigDecimal(num - i);
            safeTotal = (amount.subtract(leftNum.multiply(minimum))).divide(leftNum, 6, RoundingMode.HALF_UP);
            BigDecimal piecePacket = new BigDecimal(random.nextDouble()).multiply(safeTotal);
            if(piecePacket.compareTo(minimum)<=0){
                piecePacket = minimum;
            }
            piecePacket = piecePacket.setScale(6, RoundingMode.HALF_UP);
            amount = amount.subtract(piecePacket);
            redPackets.add(piecePacket);
        }
        redPackets.add(amount);
        // 乱序
        Collections.shuffle(redPackets);
        return redPackets;
    }

    private Boolean validateCreate(SendRedPacket redPacket) {
        Boolean result = true;
        if (redPacket == null) {
            return false;
        }
        String account = redPacket.getAccountNo();
        Long unit = redPacket.getUnit();
        // 金额
        BigDecimal amount = redPacket.getAmount() == null ? new BigDecimal(0) : redPacket.getAmount();
        Integer num = redPacket.getNumber();
        if (num == null || num <= 0 || num >= 1000) {
            throw new AppException(ErrorCode.INCORRECT_PARAM);
        }
        Integer type = redPacket.getType() == null ? WalletConstants.RED_PACKET_TYPE_NORMAL : redPacket.getType();

        if (WalletConstants.RED_PACKET_TYPE_NORMAL.equals(type)) {
            amount = amount.multiply(new BigDecimal(num));
            redPacket.setAmount(amount);
        }
        // 获取用户余额
        AccountBalance accountBalance = accountBalanceService.findByAccountNoAndUnit(account, unit);
        if (accountBalance == null || accountBalance.getAmount().compareTo(amount) < 0) {
            throw new AppException(ErrorCode.UNDER_BALANCE);
        }
        // 如果是Qbao Energy 则必须是有100000以上的Energy的
        Contract contract = contractDao.findOne(unit);

        if (WalletConstants.QBAO_ENERGY_ADRESS.equals(contract.getAddress())) {
            throw new AppException(ErrorCode.UNDER_BALANCE_QBAO_ENERGY_STOP);
        }
        if (WalletConstants.QBAO_ENERGY_ADRESS.equals(contract.getAddress()) && accountBalance.getAmount().compareTo(new BigDecimal(10000)) < 0) {
            throw new AppException(ErrorCode.UNDER_BALANCE_QBAO_ENERGY);
        }
        BigDecimal minimum = new BigDecimal(0.000001);
        if (amount.compareTo(minimum.multiply(BigDecimal.valueOf(num))) < 0) {
            throw new AppException(ErrorCode.RED_PACKET_TOO_SMALL);
        }
        if (redPacket.getComment() == null) {
            redPacket.setComment(localeMessageUtil.getLocalMessage("SEND_RED_PACKET_COMMENT", null));
        } else if (StringUtil.containsEmoji(redPacket.getComment())) {
            throw new AppException(ErrorCode.CANNOT_CONTAIN_EMOJI);
        } else if (redPacket.getComment().length() > 50) {
            redPacket.setComment(redPacket.getComment().substring(0, 50));
        }
        return result;
    }
//    private long xRandom(long min, long max) {
//        return sqrt(nextLong(sqr(max - min)));
//    }
//
//    private long sqrt(long n) {
//        // 改进为查表？
//        return (long) Math.sqrt(n);
//    }
//
//    private long sqr(long n) {
//        // 查表快，还是直接算快？
//        return n * n;
//    }
//
//    private long nextLong(long n) {
//        return random.nextInt((int) n);
//    }
//
//    private long nextLong(long min, long max) {
//        return random.nextInt((int) (max - min + 1)) + min;
//    }

}
