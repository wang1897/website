package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.*;

/**
 * Created by hepengfei on 2017/8/30.
 */
@Entity
@Table(name = "wallet_address")
public class Address extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column(name = "address")
    private String address;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "is_default")
    private Boolean isDefault;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}