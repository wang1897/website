package com.aethercoder.core.entity.batch;

import com.aethercoder.foundation.entity.batch.BatchDefinition;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by hepengfei on 08/12/2017.
 */
public class RedPacketBatchDefinition extends BatchDefinition {
    private static final long serialVersionUID = -1L;

    @Transient
    private String accountNo;

    @Transient
    private Long unit;

    @Transient
    private BigDecimal amount;

    @Transient
    private Integer number;

    @Transient
    private Integer type;

    @Transient
    private String comment;

    @Transient
    private List<String> groupNoList;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getGroupNoList() {
        return groupNoList;
    }

    public void setGroupNoList(List<String> groupNoList) {
        this.groupNoList = groupNoList;
    }
}
