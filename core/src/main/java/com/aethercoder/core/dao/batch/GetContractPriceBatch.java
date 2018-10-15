package com.aethercoder.core.dao.batch;

import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.ContractDao;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.core.entity.contractRate.ExxRate;
import com.aethercoder.core.entity.contractRate.GateRate;
import com.aethercoder.core.entity.contractRate.ZipRate;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.core.service.ContractHistoryPriceService;
import com.aethercoder.core.service.impl.ContractServiceImpl;
import com.aethercoder.foundation.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilangfeng
 * @Date 2018/01/11
 */
@Service
public class GetContractPriceBatch implements BatchInterface {
    private static Logger logger = LoggerFactory.getLogger(ContractServiceImpl.class);
    @Autowired
    private ContractDao contractDao;

    @Autowired
    private ContractHistoryPriceService contractHistoryPriceService;

    @Override
    @Transactional
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("GetContractPriceBatch");
        String appUrl = null;
        List<Contract> contracts = contractDao.findContractsByIsDeleteIsFalseOrderBySequenceAsc();

        for (Contract contract : contracts) {
            appUrl = contract.getApiAddress();
            if (StringUtils.isEmpty(appUrl)) {
                continue;
            }
            if (appUrl.contains("gate")) {
                String responseBody = NetworkUtil.callHttpReq(HttpMethod.GET, appUrl, null, null, String.class);
                try {
                    GateRate gateResult = BeanUtils.jsonToObject(responseBody, GateRate.class);
                    if (gateResult != null) {
                        String gateLastPrice = gateResult.getLast();
                        if (gateLastPrice != null) {
                            contract.setValue(new BigDecimal(gateLastPrice));
                        }
                        contractDao.save(contract);
                    }
                } catch (Exception e) {
                    // 獲取API数据出错
                    logger.error("获取GATE API数据出错");
                }
            }
            if (appUrl.contains("exx")) {
                String[] url = StringUtils.split(appUrl, "?");
                String responseBody = NetworkUtil.callHttpReq(HttpMethod.GET, url[0], url[1], null, String.class);
                try {
                    ExxRate exxResult = BeanUtils.jsonToObject(responseBody, ExxRate.class);
                    if (exxResult != null) {
                        Map<String, String> exxLastPrice = exxResult.getTicker();
                        updateToLastPrice(contract, WalletConstants.Contract_Last_Price, exxLastPrice);
                    }
                } catch (Exception e) {
                    // 獲取API数据出错
                    logger.error("获取EXX API数据出错");
                }
            }
            if (appUrl.contains("coinmarketcap")) {

                String[] url = StringUtils.split(appUrl, "?");
                String responseBody = NetworkUtil.callHttpReq(HttpMethod.GET, url[0], url[1], null, String.class);
                try {
                    List<Map> list = BeanUtils.jsonToList(responseBody, Map.class);
                    String price = null;
                    Map<String, String> map = new HashMap<>();
                    for (int i = 0; i < list.size(); i++) {
                        map = list.get(i);
                        if (map.containsKey(WalletConstants.CONTRACT_PRICE_USD)) {
                            price = map.get(WalletConstants.CONTRACT_PRICE_USD);
                            break;
                        }
                    }

                    if (price != null) {
                        contract.setValue(new BigDecimal(price));
                        contractDao.save(contract);
                    }
                } catch (Exception e) {
                    // 獲取API数据出错
                    logger.error("获取Coinmarketcap API数据出错");
                }
            }

            //
            if (appUrl.contains("zb")) {
                String[] url = StringUtils.split(appUrl, "?");
                String responseBody = NetworkUtil.callHttpReq(HttpMethod.GET, url[0], url[1], null, String.class);
                try {
                    ZipRate zipResult = BeanUtils.jsonToObject(responseBody, ZipRate.class);
                    if (zipResult != null) {
                        Map<String, String> zipLastPrice = zipResult.getTicker();
                        updateToLastPrice(contract, WalletConstants.Contract_Last_Price, zipLastPrice);
                    }
                } catch (Exception e) {
                    // 獲取API数据出错
                    logger.error("获取ZB API数据出错");
                }
            }
            if (contract.getValue() != null) {
                contractHistoryPriceService.insertContractHistoryPrice(contract);
            }
        }
        BatchResult successResult = task.getSuccessResult("success");
        return successResult;
    }

    private void updateToLastPrice(Contract contract, String currencyCny, Map<String, String> rateMap) {
        String lastPrice = rateMap.get(currencyCny);
        if (lastPrice != null) {
            contract.setValue(new BigDecimal(lastPrice));
        }
        contractDao.save(contract);
    }
}


