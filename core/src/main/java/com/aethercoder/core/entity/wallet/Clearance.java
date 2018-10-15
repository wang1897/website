package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jiawei.tao on 2017/9/13.
 */
@Entity
@Table( name = "clearance", schema = "qbao_schema", catalog = "" )
public class Clearance extends BaseEntity {
    private static final long serialVersionUID = -1L;
//
//    @CreationTimestamp
    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "clearance_day", nullable = false )
    @JsonFormat( pattern = "yyyy-MM-dd", timezone = "GMT+8" )
    private Date clearanceDay;

    @Column( name = "unit", nullable = false )
    private Long unit;

    @Column( name = "type", nullable = false )
    private Integer type;

    @Column( name = "qbao_amount", nullable = false )
    private BigDecimal qbaoAmount;

    @Column( name = "chain_amount", nullable = false )
    private BigDecimal chainAmount;

    @Column( name = "is_clear" )
    private Boolean isClear;

    @Column( name = "qbao_id" )
    private Long qbaoId;

    @Column( name = "chain_block" )
    private Long chainBlock;

    @Column( name = "qbao_number" )
    private Long qbaoNumber;

    @Column( name = "chain_number" )
    private Long chainNumber;

    @Column( name = "account_day")
    private Date accountDay;

    @Column( name = "account_remark")
    private String accountRemark;

    @Column( name = "account_status")
    private Integer accountStatus;

    public Date getClearanceDay() {
        return clearanceDay;
    }

    public void setClearanceDay(Date clearanceDay) {
        this.clearanceDay = clearanceDay;
    }

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getQbaoAmount() {
        return qbaoAmount;
    }

    public void setQbaoAmount(BigDecimal qbaoAmount) {
        this.qbaoAmount = qbaoAmount;
    }

    public BigDecimal getChainAmount() {
        return chainAmount;
    }

    public void setChainAmount(BigDecimal chainAmount) {
        this.chainAmount = chainAmount;
    }

    public Boolean getIsClear() {
        return isClear;
    }

    public void setIsClear(Boolean clear) {
        isClear = clear;
    }

    public Long getQbaoId() {
        return qbaoId;
    }

    public void setQbaoId(Long qbaoId) {
        this.qbaoId = qbaoId;
    }

    public Long getChainBlock() {
        return chainBlock;
    }

    public void setChainBlock(Long chainBlock) {
        this.chainBlock = chainBlock;
    }

    public Long getQbaoNumber() {
        return qbaoNumber;
    }

    public void setQbaoNumber(Long qbaoNumber) {
        this.qbaoNumber = qbaoNumber;
    }

    public Long getChainNumber() {
        return chainNumber;
    }

    public void setChainNumber(Long chainNumber) {
        this.chainNumber = chainNumber;
    }


    public Date getAccountDay() {
        return accountDay;
    }

    public void setAccountDay(Date accountDay) {
        this.accountDay = accountDay;
    }

    public Integer getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(Integer accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAccountRemark() {
        return accountRemark;
    }

    public void setAccountRemark(String accountRemark) {
        this.accountRemark = accountRemark;
    }





}
