package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.RecordPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
@Repository
public interface RecordPasswordDao extends JpaRepository<RecordPassword, Long> {

    RecordPassword findByCode(String code);


}
