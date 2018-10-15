package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.AccountSubsidiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/22
 * @modified By:
 */
@Repository
public interface AccountSubsidiaryDao extends JpaRepository<AccountSubsidiary,Long> {
    AccountSubsidiary findByAccountNo(String accountNo);
}
