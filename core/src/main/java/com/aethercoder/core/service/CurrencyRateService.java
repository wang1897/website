package com.aethercoder.core.service;

import com.aethercoder.core.entity.wallet.CurrencyRate;

import java.util.List;

/**
 * @auther jiawei.tao
 * @date 2017/12/12 下午5:41
 */
public interface CurrencyRateService {
    List<CurrencyRate> getCurrencyRateList();
}
