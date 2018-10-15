package com.aethercoder.core.service;

import com.aethercoder.core.entity.security.DeviceKey;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.Address;

import java.util.Set;

/**
 * Created by hepengfei on 18/12/2017.
 */
public interface AppTokenService {
    String generateNewToken(String accountNo);

    String generateNewToken(Account account);

    DeviceKey generateDeviceKey(String deviceId);
}
