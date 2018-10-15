package com.aethercoder.core.entity.json;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/2/3
 * @modified By:
 */
public class WinnerInfo implements Serializable {
    protected static final long serialVersionUID = -1L;

    private Integer drawLevel;

    private String header;

    private String accountName;

    private BigDecimal sumAmount;

    private String drawNumber;

    private Integer rank;

    private Long unit;

    private String luckNumber;

    private String accountNo;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getLuckNumber() {
        return luckNumber;
    }

    public void setLuckNumber(String luckNumber) {
        this.luckNumber = luckNumber;
    }

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getDrawNumber() {
        return drawNumber;
    }

    public void setDrawNumber(String drawNumber) {
        this.drawNumber = drawNumber;
    }

    public Integer getDrawLevel() {
        return drawLevel;
    }

    public void setDrawLevel(Integer drawLevel) {
        this.drawLevel = drawLevel;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getSumAmount() {
        return sumAmount;
    }

    public void setSumAmount(BigDecimal sumAmount) {
        this.sumAmount = sumAmount;
    }

    @Override
    public String toString() {
        return "WinnerInfo{" +
                "drawLevel=" + drawLevel +
                ", header='" + header + '\'' +
                ", accountName='" + accountName + '\'' +
                ", sumAmount=" + sumAmount +
                ", drawNumber='" + drawNumber + '\'' +
                ", rank=" + rank +
                '}';
    }
}
