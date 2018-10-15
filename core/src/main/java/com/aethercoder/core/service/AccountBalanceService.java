package com.aethercoder.core.service;

import com.aethercoder.core.entity.event.AccountBalance;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:41
 */
public interface AccountBalanceService {

    List<AccountBalance> findByAccountNo(String accountNo);

    AccountBalance findByAccountNoAndUnit(String accountNo, Long unit);

    AccountBalance saveAccountBalance(AccountBalance accountBalance);

    AccountBalance saveAccountBalanceAmountAdd(AccountBalance accountBalance);

    AccountBalance saveAccountBalanceAmountSubtract(AccountBalance accountBalance);

    Page<AccountBalance> getAllAccountBalance(Integer page, Integer size, String accountNo, Long unit, BigDecimal maxAmount,BigDecimal minAmount,String accountName);

    Map getRechargeMoneyProfile();

    Map getDefaultExchangeQBE(String accountNo, Long unit,Long id);

    Map getExchangeQBE(String accountNo, Long unit,Long id);

    void accountReward(String accountNo,Long unit,BigDecimal winAmount,Integer type);

    void accountDeduct(String accountNo,Long unit,BigDecimal winAmount,Integer type);
}
