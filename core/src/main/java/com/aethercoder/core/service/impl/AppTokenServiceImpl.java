package com.aethercoder.core.service.impl;

import com.aethercoder.basic.utils.RSAUtil;
import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.AccountDao;
import com.aethercoder.core.entity.security.DeviceKey;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.Address;
import com.aethercoder.core.security.JwtTokenUtil;
import com.aethercoder.core.service.AccountService;
import com.aethercoder.core.service.AppTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by hepengfei on 18/12/2017.
 */
@Service
public class AppTokenServiceImpl implements AppTokenService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountDao accountDao;

//    @Autowired
//    private AppTokenDao appTokenDao;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String generateNewToken(String accountNo) {
        Account account = accountService.findAccountByAccountNo(accountNo);
        if (account != null) {
            return generateToken(account, accountNo);
        }
        return null;
    }

    @Override
    public String generateNewToken(Account account) {
        Account persistAccount = accountService.findAccountByAccountNo(account.getAccountNo());
        Set<Address> addresses = account.getAddresses();
        if (persistAccount != null) {
            List<String> addressStrList = new ArrayList<>();
            for (Address address : addresses) {
                addressStrList.add(address.getAddress());
            }

            List<String> pAddressStrList = new ArrayList<>();
            for (Address address : persistAccount.getAddresses()) {
                pAddressStrList.add(address.getAddress());
            }

            if (addressStrList.containsAll(pAddressStrList)) {
                String accountNo = account.getAccountNo();
                return generateToken(persistAccount, accountNo);
            }
        }
        return null;
    }

    private String generateToken(Account account, String accountNo) {
        String token = jwtTokenUtil.generateToken(accountNo, WalletConstants.JWT_APP_EXPIRATION);
        redisTemplate.opsForValue().set(RedisConstants.REDIS_KEY_TOKEN + accountNo, token);
        redisTemplate.expire(RedisConstants.REDIS_KEY_TOKEN + accountNo, WalletConstants.JWT_APP_EXPIRATION + 1800000, TimeUnit.MILLISECONDS);
        account.setLoginTime(new Date());
        accountDao.save(account);
        return token;
    }


    @Override
    public DeviceKey generateDeviceKey(String deviceId) {
        try {
            KeyPair keyPair = RSAUtil.genKeyPair(1024);
            PrivateKey privateKey = keyPair.getPrivate();
            String privKeyStr = new String(Base64.getEncoder().encode(privateKey.getEncoded()));

            DeviceKey deviceKey = new DeviceKey();
            deviceKey.setDeviceId(deviceId);
            deviceKey.setAppKey(privKeyStr);
//            appTokenDao.save(appToken);
            redisTemplate.opsForValue().set(deviceId, deviceKey);
            return deviceKey;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
