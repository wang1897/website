package com.aethercoder.core.entity.contractRate;

import java.util.Map;

/**
 * @author lilangfeng
 * @date 2018/01/11
 */
public class ZipRate {
    private Map<String,String> ticker;
    private String date;

    public Map<String, String> getTicker() {
        return ticker;
    }

    public void setTicker(Map<String, String> ticker) {
        this.ticker = ticker;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
