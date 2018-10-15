package com.aethercoder.core.entity.event;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:18
 */
@Entity
@Table(name = "account_balance", schema = "qbao_schema")
public class AccountBalance extends BaseEntity{
    private static final long serialVersionUID = -1L;

    @Transient
    private String accountName;

    @Transient
    private String header;

    @Column( name = "account_no" )
    private String accountNo;

    @Column( name = "unit" )
    private long unit;

    @Column( name = "amount" )
    private BigDecimal amount;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public long getUnit() {
        return unit;
    }

    public void setUnit(long unit) {
        this.unit = unit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    @Override
    public String toString() {
        return "AccountBalance{" +
                "accountNo='" + accountNo + '\'' +
                ", unit=" + unit +
                ", amount=" + amount +
                '}';
    }
}
