package com.aethercoder.core.entity.pay;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * @auther JiaWei.Tao
 * @date 2017/12/7 下午5:18
 */
@Entity
@Table(name = "customer_balance", schema = "qbao_schema")
public class CustomerBalance extends BaseEntity{
    private static final long serialVersionUID = -1L;

    @Transient
    private String customerName;

    @Transient
    private String header;

    @Column( name = "customer_id" )
    private String customerId;

    @Column( name = "unit" )
    private Long unit;

    @Column( name = "amount" )
    private BigDecimal amount;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "AccountBalance{" +
                "customerId='" + customerId + '\'' +
                ", unit=" + unit +
                ", amount=" + amount +
                '}';
    }
}
