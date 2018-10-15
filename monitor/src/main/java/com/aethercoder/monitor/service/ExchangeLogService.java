package com.aethercoder.monitor.service;

import com.aethercoder.foundation.util.NetworkUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
 * Created by hepengfei on 16/04/2018.
 */
@Service
public class ExchangeLogService {

    @Value("${monitor.url}")
    private String monitorUrl;

    public Map getExchangeLog(String fromDate, String toDate, String combination, Integer unit) {
        String exchangeLogUrl = monitorUrl + "/monitor/exchangeLog";
        String params = "fromDate=" + fromDate + "&toDate=" + toDate + "&combination=" + combination + "&unit=" + unit;
//        List<NameValuePair> urlParameters = new ArrayList<>();
//        urlParameters.add(new BasicNameValuePair("fromDate", fromDate));
//        urlParameters.add(new BasicNameValuePair("toDate", toDate));
//        urlParameters.add(new BasicNameValuePair("combination", combination));
//        urlParameters.add(new BasicNameValuePair("unit", unit.toString()));
//
//        String params = URLEncodedUtils.format(urlParameters, CommonConstants.CHARACTER_ENCODE);
//        Map<String, Object> params = new HashMap<>();
//        params.put("fromDate", fromDate);
//        params.put("toDate", toDate);
//        params.put("combination", combination);
//        params.put("unit", unit);
        Map result = NetworkUtil.callHttpReq(HttpMethod.GET, exchangeLogUrl, params, null, Map.class);
        return result;
    }

    public Map getAccount(String fromDate, String toDate, String combination) {
        String accountUrl = monitorUrl + "/monitor/account";
        String params = "fromDate=" + fromDate + "&toDate=" + toDate + "&combination=" + combination;

        Map result = NetworkUtil.callHttpReq(HttpMethod.GET, accountUrl, params, null, Map.class);
        return result;
    }

}
