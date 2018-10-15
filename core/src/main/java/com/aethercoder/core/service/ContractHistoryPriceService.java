package com.aethercoder.core.service;

import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.entity.wallet.ContractHistoryPrice;


/**
 *@author lilangfeng
 * @date 2018/01/12
 */

public interface ContractHistoryPriceService {
    ContractHistoryPrice insertContractHistoryPrice(Contract contract);
}
