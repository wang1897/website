package com.aethercoder.activity.entity.guessGamble;

import com.aethercoder.foundation.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * Created by guofeiyan on 27/02/2018.
 */
@Entity
@Table(name = "t_gameble_rank")
public class GambleRank extends BaseEntity {

    private static final long serialVersionUID = -1L;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "gamble_id")
    private Long gambleId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Transient
    private String accountName;

    @Transient
    private String header;

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

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Long getGambleId() {
        return gambleId;
    }

    public void setGambleId(Long gambleId) {
        this.gambleId = gambleId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
