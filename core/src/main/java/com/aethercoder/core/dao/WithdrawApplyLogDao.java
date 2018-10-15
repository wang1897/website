package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.WithdrawApplyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawApplyLogDao extends JpaRepository<WithdrawApplyLog,Long> {

}
