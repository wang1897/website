package com.aethercoder.core.service;

import com.aethercoder.core.entity.wallet.Address;
import com.aethercoder.core.entity.wallet.QtumAddress;
import com.aethercoder.core.entity.wallet.SysWalletAddress;
import com.aethercoder.core.entity.wallet.WithdrawApply;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by hepengfei on 2017/9/14.
 */
public interface WalletService {
    List<QtumAddress> getUnspentAddress(Set<Address> addressList);

    BigDecimal getUnspentAmount(Set<Address> addressList);

    BigDecimal getUnspentAmount(List<String> address);

    BigDecimal getUnspentAmountByAccountNo(String AccountNo,Long unit);

    /**
     * 提币
     * @param withdrawApply
     */
    void mentionMoney(WithdrawApply withdrawApply);
    void mentionMoney(List<WithdrawApply> withdrawApply);

    String getAddress(Integer index);

    BigDecimal getTokenAmountByAccountNo(String accountNo, String contractAddress);

    BigDecimal getTokenAmount(List<String> address, String contractAddress);

    void startServiceByAddress(List<SysWalletAddress> sysWalletAddressList);

    BigDecimal getChainQtumAmount(String address);

    BigDecimal getChainTokenAmount(String address, Long contractId);

    void startWithdrawBatch();
}
