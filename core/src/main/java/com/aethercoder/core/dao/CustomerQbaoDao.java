package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.CustomerQbao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/8
 * @modified By:
 */
@Repository
public interface CustomerQbaoDao extends JpaRepository<CustomerQbao,Long> {

    List<CustomerQbao> findByCustomerNo(String customerNo);
}
