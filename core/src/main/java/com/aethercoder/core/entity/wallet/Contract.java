package com.aethercoder.core.entity.wallet;


import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * Created by GuoFeiYan on 2017/8/30.
 */
@Entity
@Table(name = "smart_contract")
public class Contract extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "icon")
    private String icon;
    @Column(name = "is_delete")
    private Boolean isDelete;
    @Column(name = "is_show")
    private Boolean  ishow;
    @Column(name = "contract_decimal")
    private String contractDecimal;
    @Column(name = "value")
    private BigDecimal value;
    @Column(name = "api_address")
    private String apiAddress;
    @Column(name = "sequence")
    private Integer sequence;
    @Column(name = "withdraw_fee")
    private BigDecimal withdrawFee;
    @Column(name = "withdraw_limit")
    private BigDecimal withdrawLimit;
    @Column(name = "withdraw_one_limit")
    private BigDecimal withdrawOneLimit;
    @Column(name = "withdraw_day_limit")
    private BigDecimal withdrawDayLimit;
    @Column(name = "in_service")
    private Boolean inService;

    @Column(name = "use_pay")
    private Boolean usePay;

    @Column(name = "type")
    private Integer type;

    @Transient
    private BigDecimal leftAmount;

    public Boolean getUsePay() {
        return usePay;
    }

    public void setUsePay(Boolean usePay) {
        this.usePay = usePay;
    }

    public BigDecimal getWithdrawFee() {
        return withdrawFee;
    }

    public void setWithdrawFee(BigDecimal withdrawFee) {
        this.withdrawFee = withdrawFee;
    }

    public BigDecimal getWithdrawLimit() {
        return withdrawLimit;
    }

    public void setWithdrawLimit(BigDecimal withdrawLimit) {
        this.withdrawLimit = withdrawLimit;
    }

    public BigDecimal getWithdrawOneLimit() {
        return withdrawOneLimit;
    }

    public void setWithdrawOneLimit(BigDecimal withdrawOneLimit) {
        this.withdrawOneLimit = withdrawOneLimit;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getIshow() {
        return ishow;
    }

    public void setIshow(Boolean ishow) {
        this.ishow = ishow;
    }

    public String getContractDecimal() {
        return contractDecimal;
    }

    public void setContractDecimal(String contractDecimal) {
        this.contractDecimal = contractDecimal;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }


    public String getApiAddress() {
        return apiAddress;
    }

    public void setApiAddress(String apiAddress) {
        this.apiAddress = apiAddress;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public BigDecimal getWithdrawDayLimit() {
        return withdrawDayLimit;
    }

    public void setWithdrawDayLimit(BigDecimal withdrawDayLimit) {
        this.withdrawDayLimit = withdrawDayLimit;
    }

    public Boolean getInService() {
        return inService;
    }

    public void setInService(Boolean inService) {
        this.inService = inService;
    }

    public BigDecimal getLeftAmount() {
        return leftAmount;
    }

    public void setLeftAmount(BigDecimal leftAmount) {
        this.leftAmount = leftAmount;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Contract{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", icon='" + icon + '\'' +
                ", isDelete=" + isDelete +
                ", isShow=" + ishow +
                ", contractDecimal=" + contractDecimal +
                '}';
    }
}
