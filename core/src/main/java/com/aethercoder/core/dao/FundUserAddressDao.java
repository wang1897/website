package com.aethercoder.core.dao;

import com.aethercoder.core.entity.fundUser.FundUserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @auther Guo Feiyan
 * @date 2017/11/8 下午6:04
 */
public interface FundUserAddressDao extends JpaRepository<FundUserAddress, Long> {

    FundUserAddress findFundUserAddressByUserid(long userId);

    FundUserAddress findFundUserAddressByAmountIsNullAndUserid(long userId);

}
