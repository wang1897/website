package com.aethercoder.core.dao.batch;

import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.CurrencyRateDao;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.core.entity.wallet.CurrencyRate;
import com.aethercoder.core.entity.wallet.RateAPI;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.core.service.SysConfigService;
import com.aethercoder.foundation.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @auther Guo Feiyan
 * @date 2017/12/10 下午1:38
 */
@Service
public class GetCurrencyRateBatch implements  BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(GetCurrencyRateBatch.class);

    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private CurrencyRateDao currencyRateDao;

    @Override
    @Transactional
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("GetCurrencyRateBatch");

        SysConfig sysConfig = sysConfigService.findSysConfigByName(WalletConstants.GET_CURRENCY_RATE_API_URL);
        String appUrl = sysConfig.getValue();

        SysConfig sysConfigApiId = sysConfigService.findSysConfigByName(WalletConstants.RATE_API_APP_ID);
        String appId = sysConfigApiId.getValue();

        String params = "app_id="+appId;

        String responseBody = NetworkUtil.callHttpReq(HttpMethod.GET, appUrl, params, null, String.class);
        RateAPI result = BeanUtils.jsonToObject(responseBody, RateAPI.class);
        if(result!=null) {
            Map<String,BigDecimal>
                    rates = result.getRates();


            updateRateInCurrencyRate(WalletConstants.CURRENCY_CNY,rates);
            updateRateInCurrencyRate(WalletConstants.CURRENCY_KRW,rates);

        }
        BatchResult successResult = task.getSuccessResult("success");

        return successResult;
    }

    private void updateRateInCurrencyRate(String currencyCny,Map<String,BigDecimal> rateMap) {

        BigDecimal rate = rateMap.get(currencyCny);

        if(rate != null) {
            CurrencyRate curRate = currencyRateDao.findByCurrency(currencyCny);
            if (curRate == null) {
                curRate = new CurrencyRate();
                curRate.setCurrency(currencyCny);
            }
            curRate.setRate(rate);
            curRate.setLastCheckTime(new Date());
            currencyRateDao.save(curRate);
        }
    }


}
