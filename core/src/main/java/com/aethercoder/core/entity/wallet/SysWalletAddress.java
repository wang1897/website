package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by jiawei.tao on 2018/1/23.
 */
@Entity
@Table( name = "sys_wallet_address", schema = "qbao_schema", catalog = "" )
public class SysWalletAddress extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column( name = "address", nullable = false )
    private String address;

    @Column( name = "seed", nullable = false )
    private String key;

    @Column( name = "last_left_amount")
    private BigDecimal lastLeftAmount;

    @Column( name = "qbt_left_amount")
    private BigDecimal qbtLeftAmount;

    @Column( name = "qtum_left_amount")
    private BigDecimal qtumLeftAmount;

    @Column( name = "keep_service" )
    private Boolean keepService;

    @Column( name = "max_minutes" )
    private Integer maxMinutes;

    @Column( name = "contract_id" )
    private Long contractId;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public BigDecimal getLastLeftAmount() {
        return lastLeftAmount;
    }

    public void setLastLeftAmount(BigDecimal lastLeftAmount) {
        this.lastLeftAmount = lastLeftAmount;
    }

    public BigDecimal getQbtLeftAmount() {
        return qbtLeftAmount;
    }

    public void setQbtLeftAmount(BigDecimal qbtLeftAmount) {
        this.qbtLeftAmount = qbtLeftAmount;
    }

    public Boolean getKeepService() {
        return keepService;
    }

    public void setKeepService(Boolean keepService) {
        this.keepService = keepService;
    }

    public Integer getMaxMinutes() {
        return maxMinutes;
    }

    public void setMaxMinutes(Integer maxMinutes) {
        this.maxMinutes = maxMinutes;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public BigDecimal getQtumLeftAmount() {
        return qtumLeftAmount;
    }

    public void setQtumLeftAmount(BigDecimal qtumLeftAmount) {
        this.qtumLeftAmount = qtumLeftAmount;
    }

}
