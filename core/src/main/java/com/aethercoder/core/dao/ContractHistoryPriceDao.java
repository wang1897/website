package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.ContractHistoryPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractHistoryPriceDao extends JpaRepository<ContractHistoryPrice, Long> {
   ContractHistoryPrice findContractHistoryPriceById(Long id);

}
