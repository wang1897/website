package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author lilangfeng
 * @date 2018/01/12
 */
@Entity
@Table(name = "contract_history")
public class ContractHistoryPrice extends BaseEntity{
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "contract_id")
    private Long contractId;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }
}
