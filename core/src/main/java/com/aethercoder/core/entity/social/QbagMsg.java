package com.aethercoder.core.entity.social;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.rong.util.GsonUtil;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jiawei.tao on 2017/10/19.
 */
public class QbagMsg {

    private String content;

    private String type;

    private String status;

    private Long unit;

    private BigDecimal amount;

    @Temporal( TemporalType.TIMESTAMP )
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date time;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
    @Override
    public String toString() {
        return GsonUtil.toJson(this, QbagMsg.class);
    }
}
