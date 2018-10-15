package com.aethercoder.core.service.impl;

import com.aethercoder.core.dao.ContractHistoryPriceDao;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.entity.wallet.ContractHistoryPrice;
import com.aethercoder.core.service.ContractHistoryPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lilangfeng
 * @date 2018/01/12
 */
@Service
public class ContractHistoryPriceServiceImpl implements ContractHistoryPriceService {
    @Autowired
    ContractHistoryPriceDao contractHistoryPriceDao;

    @Override
    public ContractHistoryPrice insertContractHistoryPrice(Contract contract) {
        ContractHistoryPrice contractHistoryPrice = new ContractHistoryPrice();
        contractHistoryPrice.setContractId(contract.getId());
        contractHistoryPrice.setPrice(contract.getValue());
        return contractHistoryPriceDao.save(contractHistoryPrice);
    }
}
