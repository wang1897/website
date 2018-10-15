package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.ExchangeLogType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/12
 * @modified By:
 */
@Repository
public interface ExchangeLogTypeDao extends JpaRepository<ExchangeLogType, Long> {

}
