package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.SysWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
@Repository
public interface SysWalletDao extends JpaRepository<SysWallet, Long> {

    SysWallet findSysWalletByName(String name);

}
