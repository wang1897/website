package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by wanglinghua on 2018/4/18.
 */
@Entity
@Table(name = "wallet_account_assets")
public class AccountAssets extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "type")
    private Integer type;

    @Column(name = "assets")
    private BigDecimal assets;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getAssets() {
        return assets;
    }

    public void setAssets(BigDecimal assets) {
        this.assets = assets;
    }


}
