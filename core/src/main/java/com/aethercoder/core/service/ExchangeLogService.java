package com.aethercoder.core.service;

import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.WithdrawApply;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
public interface ExchangeLogService {
    ExchangeLog saveExchangeLogFromFront(ExchangeLog exchangeLog);

    ExchangeLog updateExchangeLog(ExchangeLog exchangeLog);

    ExchangeLog saveTakeBonus(ExchangeLog exchangeLog);

    List<ExchangeLog> takeBonusRankingList();

    Map<String, Object> getTakeBonusRankingListByCount(String accountNo);

    Page<ExchangeLog> findExchangeLogsByPage(Integer page, Integer size, String accountNo,Long unit, Integer type, Integer status, String beginDate, String endDate, String accountName,Boolean isDelete);

    ExchangeLog saveTakeBonus(ExchangeLog exchangeLog,Account account,Integer number);

    boolean validateExchange(ExchangeLog exchangeLog);

    boolean confirmTransactionHash(ExchangeLog exchangeLog);

    Map getExchangeLogProfile();

    int count();

    Map getEverydayAmount();

    Map getEveryweekAmount();

    Map getEverymonthAmount();

    Map getEverydayDrawAmount();

    Map getEveryweekDrawAmount();

    Map getEverymonthDrawAmount();

    List<ExchangeLog> findUnclearExchangeLogs(Long id, Long endId,Integer type);

    WithdrawApply checkBeforeWithdraw(String accountNo, Long unit, BigDecimal withdrawAmount);

    WithdrawApply withdrawApply(WithdrawApply withdrawApply);

    String combineMessage(String content, ExchangeLog exchangeLog);

    void sendQbagMessage(String[] toUserNoList, String message, String pushContent);
}
