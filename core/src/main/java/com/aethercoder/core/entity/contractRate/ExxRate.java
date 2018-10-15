package com.aethercoder.core.entity.contractRate;


import java.util.Map;

public class ExxRate {
    private Map<String,String> ticker;
    private Long date;

    public Map<String, String> getTicker() {
        return ticker;
    }

    public void setTicker(Map<String, String> ticker) {
        this.ticker = ticker;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
