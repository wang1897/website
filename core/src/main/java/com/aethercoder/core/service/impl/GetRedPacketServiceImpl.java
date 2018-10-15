package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.ExchangeLogDao;
import com.aethercoder.core.dao.GetRedPacketDao;
import com.aethercoder.core.dao.SendRedPacketDao;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.event.GetRedPacket;
import com.aethercoder.core.entity.event.SendRedPacket;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.service.*;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.BatchService;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:43
 */
@Service
public class GetRedPacketServiceImpl implements GetRedPacketService {
    @Autowired
    private ExchangeLogDao exchangeLogDao;
    @Autowired
    private AccountBalanceService accountBalanceService;
    @Autowired
    private SendRedPacketDao sendRedPacketDao;
    @Autowired
    private GetRedPacketDao getRedPacketDao;
    @Autowired
    private ContractService contractService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private BatchService batchService;

    @Override
    @Transactional
    public GetRedPacket getRedPacket(GetRedPacket redPacket) {
        // 红包是否还有效
        SendRedPacket fatherPacket = sendRedPacketDao.getOne(redPacket.getRedPacketId());
        if (fatherPacket == null) {
            throw new AppException(ErrorCode.INCORRECT_PARAM);
        }
        if (!fatherPacket.getIsAvailable()) {
            if (fatherPacket.getBalance().compareTo(new BigDecimal(0)) > 0) {
                throw new AppException(ErrorCode.RED_PACKET_EXPIRED);
            }
            throw new AppException(ErrorCode.RED_PACKET_OVER);
        }
        // 自己是否已经抢过
        GetRedPacket sonPacket = getRedPacketDao.getGetRedPacketByRedPacketIdAndAccountNo(redPacket.getRedPacketId(), redPacket.getAccountNo());

        if (sonPacket != null && sonPacket.getId() > 0) {
            // 币种名称
            Contract contract = contractService.findContractById(sonPacket.getUnit());
            throw new AppException(ErrorCode.RED_PACKET_HAD_GOTTEN, new String[]{sonPacket.getAmount().toString(), contract.getName()});
        }
        Long fatherPacketID = redPacket.getRedPacketId();
        // 抢子红包
        Integer isDone = getRedPacketDao.updateRedPacket(fatherPacketID, redPacket.getAccountNo());
        if (isDone != null && isDone == 1) {
            sonPacket = getRedPacketDao.getGetRedPacketByRedPacketIdAndAccountNo(fatherPacketID, redPacket.getAccountNo());
            if (sonPacket != null) {
                // 变更红包余额
                changeAccountBalance(sonPacket);
                // 更新发红包表，如果已经领完状态修改成无效
                updateSendRedPacket(sonPacket);
                // 保存红包记录
                createLogForRedPacket(sonPacket);
                redPacket = sonPacket;
            } else {
                throw new AppException(ErrorCode.RED_PACKET_OVER);
            }
        } else {
            throw new AppException(ErrorCode.RED_PACKET_OVER);
        }

        return redPacket;
    }

    @Override
    public Page<GetRedPacket> findGetPacketsByAccountNo(Integer page, Integer size, String accountNo, Long unit) {

        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "getTime");
        Page<GetRedPacket> getRedPackets = getRedPacketDao.findAll(new Specification<GetRedPacket>() {
            @Override
            public Predicate toPredicate(Root<GetRedPacket> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                //AccountNo
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

        // 获取头像
        for (GetRedPacket redPacket : getRedPackets) {
            SendRedPacket father = sendRedPacketDao.findById(redPacket.getRedPacketId());
            Account account = accountService.findAccountByAccountNo(father.getAccountNo());
            redPacket.setAccountName(account.getAccountName());
            redPacket.setHeader(account.getHeader());
        }
        return getRedPackets;
    }

    private void updateSendRedPacket(GetRedPacket sonPacket) {
        SendRedPacket fatherPacket = sendRedPacketDao.findById(sonPacket.getRedPacketId());
        BigDecimal amount = sonPacket.getAmount();
        BigDecimal balance = fatherPacket.getBalance().subtract(amount);
        if (balance.compareTo(new BigDecimal(0)) <= 0) {
            fatherPacket.setIsAvailable(false);
            // 删除最后修改状态的批处理红包。
            //删除批处理
            batchService.deleteBatchTask("SendRedPacket", fatherPacket.getClass().getSimpleName(), sonPacket.getRedPacketId());
        }
        fatherPacket.setBalance(balance);
        sendRedPacketDao.save(fatherPacket);
    }

    private void changeAccountBalance(GetRedPacket redPacket) {
        AccountBalance accountBalance = accountBalanceService.findByAccountNoAndUnit(redPacket.getAccountNo(), redPacket.getUnit());
        if (accountBalance == null) {
            accountBalance = new AccountBalance();
            accountBalance.setAccountNo(redPacket.getAccountNo());
            accountBalance.setUnit(redPacket.getUnit());
            accountBalance.setAmount(redPacket.getAmount());
        } else {
            BigDecimal amount = accountBalance.getAmount() == null ? new BigDecimal(0) : accountBalance.getAmount();
            accountBalance.setAmount(amount.add(redPacket.getAmount()));
        }
        accountBalanceService.saveAccountBalance(accountBalance);
    }

    private void createLogForRedPacket(GetRedPacket redPacket) {
        ExchangeLog exchangeLog = new ExchangeLog();
        exchangeLog.setType(WalletConstants.RECEIVE_RED_BAG);
        exchangeLog.setAccountNo(redPacket.getAccountNo());
        exchangeLog.setAmount(redPacket.getAmount());
        exchangeLog.setExchangeTime(new Date());
        exchangeLog.setStatus(WalletConstants.CONFIRMED);
        exchangeLog.setUnit(redPacket.getUnit());
        exchangeLogDao.save(exchangeLog);
    }
}
