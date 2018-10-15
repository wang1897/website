package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.CurrencyRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:39
 */
@Repository
public interface CurrencyRateDao extends JpaRepository<CurrencyRate, Long> {

    CurrencyRate findByCurrency(String currency);
}
