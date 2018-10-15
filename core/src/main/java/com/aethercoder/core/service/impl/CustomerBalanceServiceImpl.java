package com.aethercoder.core.service.impl;

import com.aethercoder.core.dao.*;
import com.aethercoder.core.entity.pay.CustomerBalance;
import com.aethercoder.core.service.CustomerBalanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @auther Jiawei.Tao
 * @date 2017/12/7 下午5:42
 */
@Service
public class CustomerBalanceServiceImpl implements CustomerBalanceService {

    private static Logger logger = LoggerFactory.getLogger(CustomerBalanceServiceImpl.class);

    @Autowired
    private CustomerBalanceDao customerBalanceDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private ExchangeLogDao exchangeLogDao;

    @Override
    public List<CustomerBalance> findByCustomerId(String customerId) {
        return null;
    }

    @Override
    public CustomerBalance findByCustomerIdAndUnit(String customerId, Long unit) {
        return null;
    }

    @Override
    public CustomerBalance saveCustomerBalance(CustomerBalance customerBalance) {
        return null;
    }

    @Override
    public CustomerBalance saveCustomerBalanceAmountAdd(CustomerBalance customerBalance) {

        return null;
    }

    @Override
    public CustomerBalance saveCustomerBalanceAmountSubtract(CustomerBalance customerBalance) {
        return null;
    }

    @Override
    public Page<CustomerBalance> getAllCustomerBalance(Integer page, Integer size, String accountNo, Long unit, BigDecimal maxAmount, BigDecimal minAmount, String accountName) {
        return null;
    }

}
