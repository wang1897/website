package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jiawei.tao on 2018/1/23.
 */
@Entity
@Table( name = "withdraw", schema = "qbao_schema", catalog = "" )
public class WithdrawApply extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column( name = "account_no")
    private String accountNo;

    @Column( name = "to_address", nullable = false )
    private String toAddress;

    @Column( name = "from_address")
    private String fromAddress;

    @Column( name = "amount", nullable = false )
    private BigDecimal amount;

    @Column( name = "fee")
    private BigDecimal fee;

    @Column( name = "status" )
    private Integer status;

    @Column( name = "service_pool" )
    private Long servicePool;

    @Column( name = "unit", nullable = false  )
    private Long unit;

    @Column( name = "fee_unit" )
    private Long feeUnit;

    @Column( name = "transaction_hash" )
    private String transactionHash;

    @Column( name = "exchange_no" )
    private String exchangeNo;

    @Column( name = "fee_per_kb" )
    private BigDecimal feePerKb;

    @Version
    @Column(name = "version")
    private Integer versionForLock;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "apply_time" )
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date applyTime;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "exchange_time")
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date exchangeTime;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "confirm_time" )
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date confirmTime;
    @Transient
    private SysWalletAddress sysWalletAddress;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getServicePool() {
        return servicePool;
    }

    public void setServicePool(Long servicePool) {
        this.servicePool = servicePool;
    }

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public Long getFeeUnit() {
        return feeUnit;
    }

    public void setFeeUnit(Long feeUnit) {
        this.feeUnit = feeUnit;
    }

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getExchangeTime() {
        return exchangeTime;
    }

    public void setExchangeTime(Date exchangeTime) {
        this.exchangeTime = exchangeTime;
    }

    public Date getConfirmTime() {
        return confirmTime;
    }

    public void setConfirmTime(Date confirmTime) {
        this.confirmTime = confirmTime;
    }

    public String getExchangeNo() {
        return exchangeNo;
    }

    public void setExchangeNo(String exchangeNo) {
        this.exchangeNo = exchangeNo;
    }

    public BigDecimal getFeePerKb() {
        return feePerKb;
    }

    public void setFeePerKb(BigDecimal feePerKb) {
        this.feePerKb = feePerKb;
    }

    public SysWalletAddress getSysWalletAddress() {
        return sysWalletAddress;
    }

    public void setSysWalletAddress(SysWalletAddress sysWalletAddress) {
        this.sysWalletAddress = sysWalletAddress;
    }

    public Integer getVersionForLock() {
        return versionForLock;
    }

    public void setVersionForLock(Integer versionForLock) {
        this.versionForLock = versionForLock;
    }


}
