package com.aethercoder.core.entity.fundUser;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @auther Guo Feiyan
 * @date 2017/11/8 下午5:40
 */
@Entity
@Table( name = "user_address", schema = "user_address", catalog = "" )
public class FundUserAddress extends BaseEntity{
    private static final long serialVersionUID = -1L;

    @Column(name = "user_id", nullable = false)
    private long userid;
    @Column(name = "user_address", nullable = false)
    private String userAddress;
    @Column(name = "qtum_address", nullable = false)
    private String qtumAddress;
    @Column(name = "btc_address")
    private String btcAddress;
    @Column(name = "eth_address")
    private String ethAddress;
    @Column(name = "amount")
    private BigDecimal amount;

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getQtumAddress() {
        return qtumAddress;
    }

    public void setQtumAddress(String qtumAddress) {
        this.qtumAddress = qtumAddress;
    }

    public String getBtcAddress() {
        return btcAddress;
    }

    public void setBtcAddress(String btcAddress) {
        this.btcAddress = btcAddress;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
