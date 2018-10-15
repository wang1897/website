package com.aethercoder.core.dao;

import com.aethercoder.core.entity.pay.ContractCurrencyPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2018/4/4 下午1:22
 */
@Repository
public interface ContractCurrencyPriceDao extends JpaRepository<ContractCurrencyPrice,Long> {

    ContractCurrencyPrice getFirstByContractAndCurrencyOrderByIdDesc(Long contract, String currency);


    List<ContractCurrencyPrice> getByContractAndCurrencyAndCreateTimeAfter(Long contract, String currency,Date createTime);
}
