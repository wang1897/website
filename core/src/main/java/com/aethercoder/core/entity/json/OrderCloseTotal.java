package com.aethercoder.core.entity.json;

import com.aethercoder.core.contants.WalletConstants;

import java.math.BigDecimal;

/**
 * @auther Guo Feiyan
 * @date 2018/3/21 下午3:54
 */
public class OrderCloseTotal {

    private Integer number;

    private BigDecimal amount;

    private BigDecimal tokenAmount;

    private String numberName;

    private String amountName;

    private String tokenAmountName;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTokenAmount() {
        return tokenAmount;
    }

    public void setTokenAmount(BigDecimal tokenAmount) {
        this.tokenAmount = tokenAmount;
    }

    public String getNumberName() {
        return numberName;
    }

    public void setNumberName(String numberName) {
        this.numberName = numberName;
    }

    public String getAmountName() {
        return amountName;
    }

    public void setAmountName(String amountName) {
        this.amountName = amountName;
    }

    public String getTokenAmountName() {
        return tokenAmountName;
    }

    public void setTokenAmountName(String tokenAmountName) {
        this.tokenAmountName = tokenAmountName;
    }

    public OrderCloseTotal(String language) {
        if (language.equals(WalletConstants.LANGUAGE_TYPE_ZH)){
            this.numberName = "总计交易笔数";
            this.amountName = "交易金额";
            this.tokenAmountName = "法币金额";
        }else if (language.equals(WalletConstants.LANGUAGE_TYPE_EN)){
            this.numberName = "Total orders";
            this.amountName = "Order Amount";
            this.tokenAmountName = "Amount in Currency";
        }else if (language.equals(WalletConstants.LANGUAGE_TYPE_KO)){
            this.numberName = "총 거래 수";
            this.amountName = "거래 금액";
            this.tokenAmountName = "기축 통화 금액";
        }

    }
}
