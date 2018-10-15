package com.aethercoder.activity.entity.guessGamble;

import com.aethercoder.foundation.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by guofeiyan on 27/02/2018.
 */
@Entity
@Table(name = "t_join_gamble")
public class JoinGamble extends BaseEntity {

    private static final long serialVersionUID = -1L;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "gamble_id")
    private Long gambleId;

    @Column(name = "account_option")
    private Character option;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "is_luck")
    private Boolean isLuck;

    @Column(name = "win_amount")
    private BigDecimal winAmount;

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

    public Character getOption() {
        return option;
    }

    public void setOption(Character option) {
        this.option = option;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getIsLuck() {
        return isLuck;
    }

    public void setIsLuck(Boolean isLuck) {
        this.isLuck = isLuck;
    }

    public BigDecimal getWinAmount() {
        return winAmount;
    }

    public void setWinAmount(BigDecimal winAmount) {
        this.winAmount = winAmount;
    }
}
