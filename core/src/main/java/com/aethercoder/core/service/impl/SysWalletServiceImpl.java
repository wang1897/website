package com.aethercoder.core.service.impl;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.SysWalletDao;
import com.aethercoder.core.entity.wallet.SysWallet;
import com.aethercoder.core.service.SysWalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by GUOFEIYAN on 2017/9/18.
 */
@Service
public class SysWalletServiceImpl implements SysWalletService {
    private static Logger logger = LoggerFactory.getLogger(SysWalletServiceImpl.class);

    @Autowired
    private SysWalletDao sysWalletDao;

    @Override
    public SysWallet findByName(String name) {
        return sysWalletDao.findSysWalletByName(name);
    }

    @Override
    public List<SysWallet> findSysWallets() {
        return sysWalletDao.findAll();
    }

    @Override
    public SysWallet updateSysWallet(SysWallet sysWallet) {
        SysWallet uSysWallet = sysWalletDao.findOne(sysWallet.getId());
        BeanUtils.copyProperties(sysWallet, uSysWallet);
        return sysWalletDao.save(uSysWallet);
    }

    @Override
    public SysWallet saveSysWallet(SysWallet sysWallet) {
        return sysWalletDao.save(sysWallet);
    }

    @Override
    public String getWalletSeed() {
        return sysWalletDao.findSysWalletByName(WalletConstants.SYS_CONFIG_WALLET_SEED).getValue();
    }

    @Override
    public String getWalletAddress() {
        return sysWalletDao.findSysWalletByName(WalletConstants.SYS_CONFIG_WALLET_ADDRESS).getValue();
    }

    @Override
    public String getFee() {
        return sysWalletDao.findSysWalletByName(WalletConstants.SYS_CONFIG_FEE).getValue();
    }

    @Override
    public String getGasLimit() {
        return sysWalletDao.findSysWalletByName(WalletConstants.SYS_CONFIG_GAS_LIMIT).getValue();
    }

    @Override
    public String getGasPrice() {
        return sysWalletDao.findSysWalletByName(WalletConstants.SYS_CONFIG_GAS_PRICE).getValue();
    }
}
