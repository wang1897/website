package com.aethercoder.core.entity.guess;


import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author guofeiyan
 */
@Entity
@Table(name = "guess_unit", schema = "qbao_schema")
public class GuessUnit extends BaseEntity {

    private static final long serialVersionUID = -1L;

    @Column(name = "guess_id")
    private Long guessId;

    @Column(name = "unit")
    private Long unit;

    @Column(name = "amount")
    private BigDecimal amount;

    public Long getGuessId() {
        return guessId;
    }

    public void setGuessId(Long guessId) {
        this.guessId = guessId;
    }

    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
