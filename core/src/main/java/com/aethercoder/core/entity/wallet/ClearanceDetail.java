package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jiawei.tao on 2017/9/13.
 */
@Entity
@Table(name = "clearance_detail", schema = "qbao_schema", catalog = "")
public class ClearanceDetail extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "qbao_exchange_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
    private Date qbaoExchangeTime;

    @Column(name = "clearance_id")
    private Long clearanceId;
    @Column(name = "qbao_id")
    private Long qbaoId;

    @Column(name = "qbao_txid", nullable = false)
    private String qbaoTxid;

    @Column(name = "qbao_from_address", nullable = false)
    private String qbaoFromAddress;

    @Column(name = "qbao_to_address", nullable = false)
    private String qbaoToAddress;

    @Column(name = "qbao_type", nullable = false )
    private Integer qbaoType;

    @Column(name = "qbao_unit", nullable = false)
    private Long qbaoUnit;

    @Column(name = "qbao_amount", nullable = false)
    private BigDecimal qbaoAmount;

    @Column(name = "qbao_fee_unit")
    private Long qbaoFeeUnit;

    @Column(name = "qbao_fee_amount", nullable = false)
    private BigDecimal qbaoFeeAmount;

    @Column(name = "chain_block")
    private Long chainBlock;

    @Column(name = "chain_txid")
    private String chainTxid;

    @Column(name = "chain_from_address", nullable = false)
    private String chainFromAddress;

    @Column(name = "chain_to_address", nullable = false)
    private String chainToAddress;

    @Column(name = "chain_type", nullable = false )
    private Integer chainType;

    @Column(name = "chain_unit", nullable = false)
    private Long chainUnit;

    @Column(name = "chain_amount", nullable = false)
    private BigDecimal chainAmount;

    @Column(name = "chain_fee_unit")
    private Long chainFeeUnit;

    @Column(name = "chain_fee_amount", nullable = false)
    private BigDecimal chainFeeAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "chain_exchange_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
    private Date chainExchangeTime;


    @Column(name = "is_clear")
    private Boolean isClear;
    public Date getQbaoExchangeTime() {
        return qbaoExchangeTime;
    }

    public void setQbaoExchangeTime(Date qbaoExchangeTime) {
        this.qbaoExchangeTime = qbaoExchangeTime;
    }


    public Long getQbaoId() {
        return qbaoId;
    }

    public void setQbaoId(Long qbaoId) {
        this.qbaoId = qbaoId;
    }


    public Long getClearanceId() {
        return clearanceId;
    }

    public void setClearanceId(Long clearanceId) {
        this.clearanceId = clearanceId;
    }

    public String getQbaoTxid() {
        return qbaoTxid;
    }

    public void setQbaoTxid(String qbaoTxid) {
        this.qbaoTxid = qbaoTxid;
    }

    public String getQbaoFromAddress() {
        return qbaoFromAddress;
    }

    public void setQbaoFromAddress(String qbaoFromAddress) {
        this.qbaoFromAddress = qbaoFromAddress;
    }

    public String getQbaoToAddress() {
        return qbaoToAddress;
    }

    public void setQbaoToAddress(String qbaoToAddress) {
        this.qbaoToAddress = qbaoToAddress;
    }

    public Integer getQbaoType() {
        return qbaoType;
    }

    public void setQbaoType(Integer qbaoType) {
        this.qbaoType = qbaoType;
    }

    public Long getQbaoUnit() {
        return qbaoUnit;
    }

    public void setQbaoUnit(Long qbaoUnit) {
        this.qbaoUnit = qbaoUnit;
    }

    public BigDecimal getQbaoAmount() {
        return qbaoAmount;
    }

    public void setQbaoAmount(BigDecimal qbaoAmount) {
        this.qbaoAmount = qbaoAmount;
    }

    public Long getQbaoFeeUnit() {
        return qbaoFeeUnit;
    }

    public void setQbaoFeeUnit(Long qbaoFeeUnit) {
        this.qbaoFeeUnit = qbaoFeeUnit;
    }

    public BigDecimal getQbaoFeeAmount() {
        return qbaoFeeAmount;
    }

    public void setQbaoFeeAmount(BigDecimal qbaoFeeAmount) {
        this.qbaoFeeAmount = qbaoFeeAmount;
    }

    public Long getChainBlock() {
        return chainBlock;
    }

    public void setChainBlock(Long chainBlock) {
        this.chainBlock = chainBlock;
    }

    public String getChainTxid() {
        return chainTxid;
    }

    public void setChainTxid(String chainTxid) {
        this.chainTxid = chainTxid;
    }

    public String getChainFromAddress() {
        return chainFromAddress;
    }

    public void setChainFromAddress(String chainFromAddress) {
        this.chainFromAddress = chainFromAddress;
    }

    public String getChainToAddress() {
        return chainToAddress;
    }

    public void setChainToAddress(String chainToAddress) {
        this.chainToAddress = chainToAddress;
    }

    public Integer getChainType() {
        return chainType;
    }

    public void setChainType(Integer chainType) {
        this.chainType = chainType;
    }

    public Long getChainUnit() {
        return chainUnit;
    }

    public void setChainUnit(Long chainUnit) {
        this.chainUnit = chainUnit;
    }

    public BigDecimal getChainAmount() {
        return chainAmount;
    }

    public void setChainAmount(BigDecimal chainAmount) {
        this.chainAmount = chainAmount;
    }

    public Long getChainFeeUnit() {
        return chainFeeUnit;
    }

    public void setChainFeeUnit(Long chainFeeUnit) {
        this.chainFeeUnit = chainFeeUnit;
    }

    public BigDecimal getChainFeeAmount() {
        return chainFeeAmount;
    }

    public void setChainFeeAmount(BigDecimal chainFeeAmount) {
        this.chainFeeAmount = chainFeeAmount;
    }

    public Date getChainExchangeTime() {
        return chainExchangeTime;
    }

    public void setChainExchangeTime(Date chainExchangeTime) {
        this.chainExchangeTime = chainExchangeTime;
    }

    public Boolean getIsClear() {
        return isClear;
    }

    public void setIsClear(Boolean clear) {
        isClear = clear;
    }
}
