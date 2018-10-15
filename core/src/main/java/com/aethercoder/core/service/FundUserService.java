package com.aethercoder.core.service;

import com.aethercoder.core.entity.event.FundUser;
import org.springframework.data.domain.Page;

/**
 * Created by hepengfei on 2017/8/30.
 */
public interface FundUserService {

    FundUser updateFundUser(FundUser fundUser);

    FundUser saveUser(FundUser fundUser);

    FundUser activateUser(String uniqueId);

    FundUser login(String noOrEmail, String password);

    FundUser findFundUserByUserName(String fundUserNo);

    FundUser findFundUserById(Long id);

    FundUser findByEmail(String email);

    FundUser saveFundUser(FundUser fundUser);

    FundUser passwordResetEamil(String email);

    FundUser passwordReset(String fundUserNo, String password, String code);

    Page<FundUser> findFundUsersByPage(Integer page, Integer size);

    Page<FundUser> findFundUsersByPage(Integer page, Integer size, String email, String fundUserName, String activateType, String icoType);

    FundUser getFundUserByToken(String token);

}
