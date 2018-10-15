package com.aethercoder.core.service;

import com.aethercoder.core.entity.event.FundUser;
import com.aethercoder.core.entity.wallet.Account;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
public interface SendMailService {


    void sendCheckEmail(Account account);

    void sendFindPwdEmail(Account account, String code);

    void sendFundUser(FundUser fundUser, String code);

    void sendFundUserICOSuccess(FundUser fundUser, String code);

    void sendFundUserICOFail(FundUser fundUser, String code);

    void sendFundPwdEmail(FundUser fundUser, String code);


}
