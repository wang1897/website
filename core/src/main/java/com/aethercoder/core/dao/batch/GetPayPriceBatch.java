package com.aethercoder.core.dao.batch;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.ContractCurrencyPriceDao;
import com.aethercoder.core.dao.ContractDao;
import com.aethercoder.core.dao.SysConfigDao;
import com.aethercoder.core.entity.pay.ContractCurrencyPrice;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.foundation.util.NetworkUtil;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @auther Guo Feiyan
 * @date 2017/12/10 下午1:38
 */
@Service
public class GetPayPriceBatch implements BatchInterface {
    private static Logger logger = LoggerFactory.getLogger(GetPayPriceBatch.class);

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private ContractCurrencyPriceDao contractCurrencyPriceDao;

    @Override
    @Transactional
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("GetPayPriceBatch");
        String apiUrl = null;
        try {
            Gson gson = new Gson();
            apiUrl = sysConfigDao.findSysConfigByName(WalletConstants.QBAO_PAY_RATE_URL).getValue();
            String responseBody = NetworkUtil.callHttpReq(HttpMethod.GET, apiUrl, null, null, String.class);
            Map<String, LinkedTreeMap> map = gson.fromJson(responseBody, Map.class);
            LinkedTreeMap<String, List> mapData = map.get("data");
            List mapBids = mapData.get("bids");
            List mapAsks = mapData.get("asks");
            //获取bids的price
            String priceBids = ((LinkedTreeMap) mapBids.get(0)).get("price").toString();
            //获取asks的price
            String priceAsks = ((LinkedTreeMap) mapAsks.get(0)).get("price").toString();
            //汇率 QTUM／KRW
            BigDecimal rate = new BigDecimal(priceBids).add(new BigDecimal(priceAsks)).divide(new BigDecimal(2));

            Contract contract = contractDao.findContractByNameAndIsDeleteIsFalse(WalletConstants.QTUM_TOKEN_NAME);
            ContractCurrencyPrice contractCurrencyPrice = new ContractCurrencyPrice();
            contractCurrencyPrice.setContract(contract.getId());
            contractCurrencyPrice.setCurrency(WalletConstants.CURRENCY_KRW);
            contractCurrencyPrice.setRate(rate);
            contractCurrencyPriceDao.save(contractCurrencyPrice);

            BatchResult successResult = task.getSuccessResult("success");
            return successResult;
        }catch (Exception e){
            BatchResult failResult = task.getFailedResult("QTUM/KRW failed to obtain exchange rate. url:"+apiUrl);
            return failResult;
        }

    }
}


