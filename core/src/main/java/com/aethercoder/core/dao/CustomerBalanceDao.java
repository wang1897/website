package com.aethercoder.core.dao;

import com.aethercoder.core.entity.pay.CustomerBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @auther Jiawei.Tao
 * @date 2018/03/16 下午5:39
 */
@Repository
public interface CustomerBalanceDao extends JpaRepository<CustomerBalance, Long> {

    List<CustomerBalance> findByCustomerId(String customerId);

    CustomerBalance findByCustomerIdAndUnit(String customerId, Long unit);

    Integer countAllByUnit(Long unit);

    Page<CustomerBalance> findAll(Specification<CustomerBalance> specification, Pageable pageable);
}
