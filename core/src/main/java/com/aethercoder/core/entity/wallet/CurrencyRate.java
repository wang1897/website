package com.aethercoder.core.entity.wallet;


import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jiawei.tao on 2017/12/12.
 */
@Entity
@Table(name = "currency_rate")
public class CurrencyRate extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column(name = "currency")
    private String currency;
    @Column(name = "rate")
    private BigDecimal rate;
    @Column(name = "last_check_time")
    @Temporal( TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastCheckTime;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Date getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(Date lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }
}
