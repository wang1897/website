package com.aethercoder.core.entity.pay;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author guofeiyan
 * @date 2018/3/20 下午4:34
 */
@Entity
@Table( name = "t_contract_currency_price",schema = "qbao_schema")
public class ContractCurrencyPrice extends BaseEntity{

    private static final long serialVersionUID = -1L;

    @Column(name = "contract")
    private Long contract;

    @Column(name = "currency")
    private String currency;

    @Column(name = "rate")
    private BigDecimal rate;

    public Long getContract() {
        return contract;
    }

    public void setContract(Long contract) {
        this.contract = contract;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
