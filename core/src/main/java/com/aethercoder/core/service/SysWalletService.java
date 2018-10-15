package com.aethercoder.core.service;

import com.aethercoder.core.entity.wallet.SysWallet;

import java.util.List;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
public interface SysWalletService {
    SysWallet findByName(String name);

    List<SysWallet> findSysWallets();

    SysWallet updateSysWallet(SysWallet sysWallet);

    SysWallet saveSysWallet(SysWallet sysWallet);

    String getWalletSeed();

    String getWalletAddress();

    String getFee();

    String getGasLimit();

    String getGasPrice();

}
