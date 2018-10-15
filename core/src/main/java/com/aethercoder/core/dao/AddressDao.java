package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by hepengfei on 2017/8/30.
 */
@Repository
public interface AddressDao extends JpaRepository<Address, Long> {
    Address getByAddress(String address);

    Address findAddressByAccountIdAndIsDefault(Long accountId, Boolean idDefault);
}
