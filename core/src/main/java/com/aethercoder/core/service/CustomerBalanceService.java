package com.aethercoder.core.service;

import com.aethercoder.core.entity.pay.CustomerBalance;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:41
 */
public interface CustomerBalanceService {

    List<CustomerBalance> findByCustomerId(String customerId);

    CustomerBalance findByCustomerIdAndUnit(String customerId, Long unit);

    CustomerBalance saveCustomerBalance(CustomerBalance customerBalance);

    CustomerBalance saveCustomerBalanceAmountAdd(CustomerBalance customerBalance);

    CustomerBalance saveCustomerBalanceAmountSubtract(CustomerBalance customerBalance);

    Page<CustomerBalance> getAllCustomerBalance(Integer page, Integer size, String accountNo, Long unit, BigDecimal maxAmount, BigDecimal minAmount, String accountName);

}
