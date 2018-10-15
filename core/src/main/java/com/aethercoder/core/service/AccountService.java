package com.aethercoder.core.service;

import com.aethercoder.core.entity.social.UserInfo;
import com.aethercoder.core.entity.wallet.Account;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by hepengfei on 2017/8/30.
 */
public interface AccountService {
    Account createAccount(Account account);

    Account importAccount(Account account);

    Account changeDefaultAddress(String accountNo, String address);

    Account updateAccount(Account account);

    Account updateAccountByInviteDaily(Account account);

  /*  Account saveUser(Account account);

    Account activateUser(String id);

    Account login(String noOrEmail,String password);*/

    Account findAccountByAccountNo(String accountNo);

    Account findByEmail(String email);

   /*  Account saveAccount(Account account);

    Account passwordResetEamil(String email);

    Account passwordReset(String accountNo,String password,String code);*/

    Page<Account> findAccountsByPage(Integer page, Integer size);

    Page<Account> findAccountsByPage(Integer page, Integer size, String email, String accountName,
                                     String accountNo, String sourceType, String activateType, String type,
                                     Integer inviteDailyMin, Integer inviteDailyMax, BigDecimal maxInviteAmount,
                                     BigDecimal minInviteAmount,Integer level,Boolean authority,Boolean orderDesc);

    String getNewRongToken(Account account);

    UserInfo syncUserInfo(String accountNo, String version);

    Account getFriendInfo(String accountNo);

    String getFriendName(String accountNo);

    Boolean checkAddress(String accountNo, String toAddress);

    //获得主地址
    String getMainAddress(String accountNo);

    //获得所有邀请排行榜
    List<Account> getInviteRankingList();

    void updateAccountByReceiveNumber(Account account, Integer number);

    String getShareCode(String accountNo);

    String initToken(String accountNo);

    Account getAccountsProfile();

    List<Account> queryAccountsByLevel(Long level);

    List<Account> queryAccountsByAuthority(Boolean authority);

    void updateAccountsByAccountNoAndAuthority(String accountNo,Boolean authority);

    void updateAccountsLanguage(Account accountOrg);

    Locale getLocaleByAccount(String accountNo);

    Account getAccountByAccountNoFromCache(String accountNo);

    Account updateLoginTime(String accountNo, Date loginTime);
}
