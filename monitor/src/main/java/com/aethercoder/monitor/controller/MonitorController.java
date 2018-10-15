package com.aethercoder.monitor.controller;

import com.aethercoder.monitor.service.ExchangeLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by hepengfei on 16/04/2018.
 */
@RestController
@RequestMapping("monitor")
public class MonitorController {
    private static Logger logger = LoggerFactory.getLogger(MonitorController.class);

    @Autowired
    private ExchangeLogService exchangeLogService;

    @RequestMapping(value = "/admin/exchangeLog", method = RequestMethod.GET, produces = "application/json")
    public Map getExchangeLogHistory(@RequestParam(value = "fromDate") String fromDateStr, @RequestParam(value = "toDate") String toDateStr,
                                     @RequestParam Integer unit, @RequestParam String combination) {
        logger.info("monitor/admin/exchangeLog");

        return exchangeLogService.getExchangeLog(fromDateStr, toDateStr, combination, unit);
    }

    @RequestMapping(value = "/admin/account", method = RequestMethod.GET, produces = "application/json")
    public Map getAccountHistory(@RequestParam(value = "fromDate") String fromDateStr, @RequestParam(value = "toDate") String toDateStr,
                                 @RequestParam String combination) {
        logger.info("monitor/admin/account");

        return exchangeLogService.getAccount(fromDateStr, toDateStr, combination);
    }
}
