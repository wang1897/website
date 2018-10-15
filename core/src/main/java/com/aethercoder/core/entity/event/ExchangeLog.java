package com.aethercoder.core.entity.event;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jiawei.tao on 2017/9/13.
 */
@Entity
@Table(name = "exchange_log", schema = "qbao_schema", catalog = "")
public class ExchangeLog extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Transient
    private String accountName;

    @Transient
    private String header;

    @Transient
    private BigDecimal sumAmount;

    @Transient
    private Integer number;

    private String address;

    private BigDecimal amount;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "exchange_time", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exchangeTime;

    @Column(name = "transaction_hash", nullable = false)
    private String transactionHash;

    @Column(name = "event_apply_id", nullable = false)
    private Long eventApplyId;

    @Column(name = "is_deleted", nullable = false )
    private Boolean isDeleted = false;

    @Column(name = "unit", nullable = false)
    private long unit;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "account_no", nullable = false)
    private String accountNo;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "from_address", nullable = false)
    private String fromddress;

    @Column(name = "fee", nullable = false)
    private BigDecimal fee;

    @Column(name = "fee_unit", nullable = false)
    private Long feeUnit;

    @Column(name = "order_id")
    private String orderId;

    @Basic
    @Column(name = "address", nullable = false, length = 45)
    @ApiModelProperty(position = 1, value="交易地址toAddress")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Basic
    @Column(name = "amount", nullable = false)
    @ApiModelProperty(position = 2, value="实际交易金额")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Basic
    @ApiModelProperty(position = 3, value="提交交易时间")
    public Date getExchangeTime() {
        return exchangeTime;
    }

    public void setExchangeTime(Date exchangeTime) {
        this.exchangeTime = exchangeTime;
    }

    @ApiModelProperty(position = 4, value="交易单号")
    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    @ApiModelProperty(position = 5, value="参与活动的用户id")
    public Long getEventApplyId() {
        return eventApplyId;
    }

    public void setEventApplyId(Long eventApplyId) {
        this.eventApplyId = eventApplyId;
    }

    @ApiModelProperty(position = 6, value="币种 合约id")
    public long getUnit() {
        return unit;
    }

    public void setUnit(long unit) {
        this.unit = unit;
    }

    @ApiModelProperty(position = 7, value="交易类型 0-提币 -／ 1 -充值 + ／2-发红包 -／3-收红包 +／4 活动 + / 5 新人奖励+ /6 红包退款 + ／7 新人红包 + ／8 手续费 -/ 9 建群支付 -")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @ApiModelProperty(position = 8, value="交易用户accounto")
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @ApiModelProperty(position = 9, value="交易状态 0-未确认／1-已确认／2-失败")
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @ApiModelProperty(position = 10, value="交易地址fromAddress")
    public String getFromddress() {
        return fromddress;
    }

    public void setFromddress(String fromddress) {
        this.fromddress = fromddress;
    }

    @ApiModelProperty(position = 11, value="交易手续费")
    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    @ApiModelProperty(position = 12, value="交易手续费币种")
    public Long getFeeUnit() {
        return feeUnit;
    }

    public void setFeeUnit(Long feeUnit) {
        this.feeUnit = feeUnit;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public BigDecimal getSumAmount() {
        return sumAmount;
    }

    public void setSumAmount(BigDecimal sumAmount) {
        this.sumAmount = sumAmount;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "ExchangeLog{" +

                ", address='" + address + '\'' +
                ", amount=" + amount +
                ", exchangeTime=" + exchangeTime +
                ", transactionHash='" + transactionHash + '\'' +
                ", eventApplyId=" + eventApplyId +
                ", unit=" + unit +
                ", type=" + type +
                ", accountNo='" + accountNo + '\'' +
                ", status=" + status +
                ", fromAddress='" + fromddress + '\'' +
                '}';
    }
}
