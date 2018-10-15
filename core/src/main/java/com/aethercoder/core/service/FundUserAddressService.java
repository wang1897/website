package com.aethercoder.core.service;

import com.aethercoder.core.entity.fundUser.FundUserAddress;

/**
 * @auther Guo Feiyan
 * @date 2017/11/8 下午6:16
 */
public interface FundUserAddressService {

    FundUserAddress partIn(long userId);

    FundUserAddress save(FundUserAddress fundUserAddress);

}
