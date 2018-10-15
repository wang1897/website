package com.aethercoder.core.service;

//import com.aethercoder.core.entity.wallet.InvitingReward;


import com.aethercoder.core.entity.result.AccountInvitingInfo;
import com.aethercoder.core.entity.result.InvitingRewardInfo;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.InvitingReward;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/8
 * @modified By:
 */
public interface InviteService {
    AccountInvitingInfo accountInvitingInfo(String accountNo);

    Account accountInvitedInfo(String shareCode);

    InvitingRewardInfo invitingRewardInfo(String accountNo);

    void invitingReward(String accountNo);

    void uploadHeaderReward(String accountNo);

    void gainedReward(String accountNo);

    void walletAchievingReward(String accountNo);

    Map luckyAccountReward(String accountNo);

    List<InvitingReward> invitingRegisteredInfo();

    void invitingRegistered(List<InvitingReward> invitingRewardList);
}
