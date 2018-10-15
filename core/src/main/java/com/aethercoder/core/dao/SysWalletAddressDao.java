package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.SysWalletAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
@Repository
public interface SysWalletAddressDao extends JpaRepository<SysWalletAddress, Long> {

    List<SysWalletAddress> findByContractId(Long contractId);

    List<SysWalletAddress> findByKeepServiceIsTrue();

}
