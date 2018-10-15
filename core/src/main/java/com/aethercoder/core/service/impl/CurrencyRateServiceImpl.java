package com.aethercoder.core.service.impl;

import com.aethercoder.core.dao.CurrencyRateDao;
import com.aethercoder.core.entity.wallet.CurrencyRate;
import com.aethercoder.core.service.CurrencyRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyRateServiceImpl implements CurrencyRateService {
    @Autowired
    private CurrencyRateDao currencyRateDao;

    @Override
    public List<CurrencyRate> getCurrencyRateList() {
        List<CurrencyRate> rates = currencyRateDao.findAll();
        return rates;
    }
}
