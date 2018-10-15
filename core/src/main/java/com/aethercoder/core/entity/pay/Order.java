package com.aethercoder.core.entity.pay;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author guofeiyan
 * @date 2018/3/20 下午4:34
 */
@Entity
@Table( name = "t_order",schema = "qbao_schema")
public class Order extends BaseEntity{

    private static final long serialVersionUID = -1L;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "order_time")
    @Temporal( TemporalType.TIMESTAMP )
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date orderTime;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "status")
    private Integer status;

    @Column(name = "type")
    private Integer type;

    @Column(name = "unit")
    private String unit;

    @Column(name = "token_amount")
    private BigDecimal tokenAmount;

    @Column(name = "token_unit")
    private Long tokenUnit;

    @Column(name = "fee")
    private BigDecimal fee;

    @Column(name = "fee_unit")
    private Long feeUnit;

    @Transient
    @JsonFormat( pattern = "yyyy-MM-dd ", timezone = "GMT+8" )
    private Date cleanTime;

    @Column(name = "pay_rate")
    private BigDecimal payRate;

    @Transient
    private Long orderTimestamp;

    @Transient
    private String accountName;

    @Transient
    private String customerName;

    @Transient
    private String customerLogo;

    @Transient
    private String token;

    @Transient
    private String nickWords;

    @Transient
    private String tokenName;

    @Transient
    private String typeName;

    @Transient
    private String customerDes;
    @Transient
    private List<Object> rateList;

    @Transient
    @JsonFormat(pattern = "MM-dd HH:mm", timezone = "GMT+8")
    private Date orderTimeSMS;

    public Date getOrderTimeSMS() {
        return orderTimeSMS;
    }

    public void setOrderTimeSMS(Date orderTimeSMS) {
        this.orderTimeSMS = orderTimeSMS;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
        this.orderTimestamp = orderTime.getTime();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getTokenAmount() {
        return tokenAmount;
    }

    public void setTokenAmount(BigDecimal tokenAmount) {
        this.tokenAmount = tokenAmount;
    }

    public Long getTokenUnit() {
        return tokenUnit;
    }

    public void setTokenUnit(Long tokenUnit) {
        this.tokenUnit = tokenUnit;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Long getFeeUnit() {
        return feeUnit;
    }

    public void setFeeUnit(Long feeUnit) {
        this.feeUnit = feeUnit;
    }

    public Date getCleanTime() {
        return cleanTime;
    }

    public void setCleanTime(Date cleanTime) {
        this.cleanTime = cleanTime;
    }

    public BigDecimal getPayRate() {
        return payRate;
    }

    public void setPayRate(BigDecimal payRate) {
        this.payRate = payRate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerLogo() {
        return customerLogo;
    }

    public void setCustomerLogo(String customerLogo) {
        this.customerLogo = customerLogo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCustomerDes() {
        return customerDes;
    }

    public void setCustomerDes(String customerDes) {
        this.customerDes = customerDes;
    }

    public Long getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(Long orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public List<Object> getRateList() {
        return rateList;
    }

    public void setRateList(List<Object> rateList) {
        this.rateList = rateList;
    }

    public String getNickWords() {
        return nickWords;
    }

    public void setNickWords(String nickWords) {
        this.nickWords = nickWords;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    @Override
    public String toString() {
        return "{" +
                "\"orderId\":\""+orderId+ "\"" +
                ", \"customerLogo\":\"" + customerLogo + "\"" +
                ", \"customerName\":\"" + customerName + "\"" +
                ", \"tokenAmount\":" + tokenAmount +
                ", \"tokenUnit\":" + tokenUnit +
                ", \"status\":" + status +
                ", \"type\":" + type +
                ", \"orderTime\":" + orderTimestamp +
                ", \"extra\":\"\"" +
                "}";
    }

}
