package com.aethercoder.cache;

import com.aethercoder.TestApplication;
import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.AccountDao;
import com.aethercoder.core.dao.AddressDao;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.Address;
import com.aethercoder.core.service.AccountService;
import com.aethercoder.core.service.CaptchaService;
import com.aethercoder.core.service.UserContactService;
import com.aethercoder.foundation.contants.CommonConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by hepengfei on 05/01/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class AccountCacheTest {
    @Autowired
    private AccountDao accountDao;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserContactService userContactService;

    @Mock
    CaptchaService captchaService;

    public static String accountNo = "8888";

    private String address1 = "aaa";
    private String address2 = "bbb";
    private String address3 = "ccc";

    private Account _account;

    @Before
    public void setupAccount() {
        MockitoAnnotations.initMocks(this);
        when(captchaService.verifyCaptcha(any(String.class), any(String.class), any(String.class))).thenReturn(true);
        redisTemplate.delete(CommonConstants.REDIS_CACHE_NAME_ACCOUNT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_WALLET_ACCOUNT + accountNo);
        _account = accountDao.findByAccountNo(AccountCacheTest.accountNo);
        if (_account != null) {
            return;
        }

        _account = new Account();
        _account.setAccountName("ttt");
        _account.setAccountNo(accountNo);
        _account.setInviteQbe(new BigDecimal(0));
        _account.setHeader(WalletConstants.DEFAULT_HEADER);
        _account.setReceiveNumber(10);
        _account.setInvitedDaily(10);
        _account.setSysBlack(false);
        _account.setIsDaka(true);
        _account.setLevelStatus(true);
        _account.setLevel(1L);
        Account a = accountDao.save(_account);

        Address addressObj = new Address();
        addressObj.setIsDefault(true);
        addressObj.setAddress(address1);
        addressObj.setAccountId(a.getId());
        addressDao.save(addressObj);
//        addressObj.setAccount(_account);

        Address address2Obj = new Address();
        address2Obj.setAddress(address2);
        address2Obj.setAccountId(a.getId());
        addressDao.save(address2Obj);
//        address2Obj.setAccount(account);

        Address address3Obj = new Address();
        address3Obj.setAddress(address3);
        address3Obj.setAccountId(a.getId());
        addressDao.save(address3Obj);
//        address3Obj.setAccount(account);

        Set<Address> set = new HashSet<>();
        set.add(addressObj);
        set.add(address2Obj);
        set.add(address3Obj);

        _account.setAddresses(set);

//        accountDao.save(account);
        AccountCacheTest.accountNo = a.getAccountNo();
    }

    @Test
    public void testUpdateMulti() {
        String accountNo = "586860";
        redisTemplate.delete(CommonConstants.REDIS_CACHE_NAME_ACCOUNT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_WALLET_ACCOUNT + accountNo);

        Account account = accountDao.findByAccountNo(accountNo);
        Assert.assertNull(redisTemplate.opsForValue().get(CommonConstants.REDIS_CACHE_NAME_ACCOUNT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_WALLET_ACCOUNT + accountNo));

        Account a = accountDao.findAccountByAccountNo(accountNo);
        Assert.assertNotNull(redisTemplate.opsForValue().get(CommonConstants.REDIS_CACHE_NAME_ACCOUNT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_WALLET_ACCOUNT + accountNo));

        List<Account> list = new ArrayList<>();
        list.add(account);

        accountDao.save(list);
        Assert.assertNull(redisTemplate.opsForValue().get(CommonConstants.REDIS_CACHE_NAME_ACCOUNT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_WALLET_ACCOUNT + accountNo));
        System.out.println("1111");
    }

   @Test
    public void testCachePut() {
        redisTemplate.delete(CommonConstants.REDIS_CACHE_NAME_ACCOUNT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_WALLET_ACCOUNT + accountNo);

        Account account = accountDao.findAccountByAccountNo(accountNo);
        Account rAccount = (Account)redisTemplate.opsForValue().get(CommonConstants.REDIS_CACHE_NAME_ACCOUNT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_WALLET_ACCOUNT + accountNo);
        System.out.println(account.getVersionForLock() + ":" + rAccount.getVersionForLock());

        Account pAccount = accountDao.findByAccountNo(accountNo);
        pAccount.setAccountName("abcdddd");
        Account tAccount = accountDao.save(pAccount);
        Assert.assertNull(redisTemplate.opsForValue().get(CommonConstants.REDIS_CACHE_NAME_ACCOUNT + CommonConstants.REDIS_CACHE_PREFIX + RedisConstants.REDIS_KEY_WALLET_ACCOUNT + accountNo));
       System.out.println("1111");
    }

    @Test
    public void testChangeDefaultAddress(){
        System.out.println("1111");
        Account account = accountDao.findByAccountNo(accountNo);
        Assert.assertEquals(account.getAddresses().size(), 3);

        accountService.changeDefaultAddress(accountNo, address1);

        Account account1 = accountDao.findByAccountNo(accountNo);
        Set<Address> set = account1.getAddresses();
        set.forEach(address -> {
            if (address.getAddress().equals(address1)) {
                Assert.assertTrue(address.getIsDefault());
            } else {
                Assert.assertFalse(address.getIsDefault());
            }
        });

        accountService.changeDefaultAddress(accountNo, address2);

        account1 = accountDao.findByAccountNo(accountNo);
        set = account1.getAddresses();
        set.forEach(address -> {
            if (address.getAddress().equals(address2)) {
                Assert.assertTrue(address.getIsDefault());
            } else {
                Assert.assertFalse(address.getIsDefault());
            }
        });


        account1 = accountDao.findByAccountNo(accountNo);
        set = account1.getAddresses();
        set.forEach(address -> {
            if (address.getAddress().equals(address2)) {
                Assert.assertTrue(address.getIsDefault());
            } else {
                Assert.assertFalse(address.getIsDefault());
            }
        });


    }
}
