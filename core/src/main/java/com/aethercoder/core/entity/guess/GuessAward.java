package com.aethercoder.core.entity.guess;


import com.aethercoder.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * Created by guofeiyan on 2018/02/11.
 */
@Entity
@Table(name = "guess_award")
public class GuessAward extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column(name = "guess_id")
    private Long guessId;

    @Column(name = "unit")
    private Long unit;

    @Column(name = "special_number")
    private Integer specialNumber;

    @Column(name = "frist_number")
    private Integer fristNumber;

    @Column(name = "second_number")
    private Integer secondNumber;

    @Column(name = "third_number")
    private Integer thirdNumber;

    @Column(name = "fourth_number")
    private Integer fourthNumber;

    @Column(name = "special_award")
    private BigDecimal specialAward;

    @Column(name = "frist_award")
    private BigDecimal fristAward;

    @Column(name = "second_award")
    private BigDecimal secondAward;

    @Column(name = "third_award")
    private BigDecimal thirdAward;

    @Column(name = "fourth_award")
    private BigDecimal fourthAward;

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

    public Integer getSpecialNumber() {
        return specialNumber;
    }

    public void setSpecialNumber(Integer specialNumber) {
        this.specialNumber = specialNumber;
    }

    public Integer getFristNumber() {
        return fristNumber;
    }

    public void setFristNumber(Integer fristNumber) {
        this.fristNumber = fristNumber;
    }

    public Integer getSecondNumber() {
        return secondNumber;
    }

    public void setSecondNumber(Integer secondNumber) {
        this.secondNumber = secondNumber;
    }

    public Integer getThirdNumber() {
        return thirdNumber;
    }

    public void setThirdNumber(Integer thirdNumber) {
        this.thirdNumber = thirdNumber;
    }

    public Integer getFourthNumber() {
        return fourthNumber;
    }

    public void setFourthNumber(Integer fourthNumber) {
        this.fourthNumber = fourthNumber;
    }

    public BigDecimal getSpecialAward() {
        return specialAward;
    }

    public void setSpecialAward(BigDecimal specialAward) {
        this.specialAward = specialAward;
    }

    public BigDecimal getFristAward() {
        return fristAward;
    }

    public void setFristAward(BigDecimal fristAward) {
        this.fristAward = fristAward;
    }

    public BigDecimal getSecondAward() {
        return secondAward;
    }

    public void setSecondAward(BigDecimal secondAward) {
        this.secondAward = secondAward;
    }

    public BigDecimal getThirdAward() {
        return thirdAward;
    }

    public void setThirdAward(BigDecimal thirdAward) {
        this.thirdAward = thirdAward;
    }

    public BigDecimal getFourthAward() {
        return fourthAward;
    }

    public void setFourthAward(BigDecimal fourthAward) {
        this.fourthAward = fourthAward;
    }
}
