package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.AccountAssets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * @Author: wang ling hua
 * @Description:
 * @Date: Created in 2018/4/18
 * @modified By:
 */
@Repository
public interface AccountAssetsDao extends JpaRepository<AccountAssets,Long> {

    AccountAssets findByAccountNoAndType(String accountNo,Integer type);
}
