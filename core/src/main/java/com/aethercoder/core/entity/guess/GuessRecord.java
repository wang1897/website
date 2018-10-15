package com.aethercoder.core.entity.guess;


import com.aethercoder.core.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by guofeiyan on 2018/01/29.
 */
@Entity
@Table(name = "guess_record")
@ApiModel(value="竞猜抽奖记录",description="竞猜抽奖记录")
public class GuessRecord extends BaseEntity{


    private static final long serialVersionUID = -1L;


    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "guess_number_id")
    private Long guessNumberId;

    @Column(name = "draw_number")
    private String drawNumber;

    @Column(name = "draw_time")
    private Date drawTime;

    @Column(name = "draw_level")
    private Integer drawLevel;

    @Transient
    private BigDecimal sumAmount;

    @Transient
    private String accountName;

    //开奖号码
    @Transient
    private String luckNumber;

    public String getLuckNumber() {
        return luckNumber;
    }

    public void setLuckNumber(String luckNumber) {
        this.luckNumber = luckNumber;
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

    @ApiModelProperty(position = 1, value="竞猜游戏id")
    public Long getGuessNumberId() {
        return guessNumberId;
    }

    public void setGuessNumberId(Long guessNumberId) {
        this.guessNumberId = guessNumberId;
    }

    @ApiModelProperty(position = 4, value="抽奖数字")
    public String getDrawNumber() {
        return drawNumber;
    }

    public void setDrawNumber(String drawNumber) {
        this.drawNumber = drawNumber;
    }

    @ApiModelProperty(position = 5, value="抽奖时间")
    public Date getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(Date drawTime) {
        this.drawTime = drawTime;
    }

    @ApiModelProperty(position = 6, value="中奖等级")
    public Integer getDrawLevel() {
        return drawLevel;
    }

    public void setDrawLevel(Integer drawLevel) {
        this.drawLevel = drawLevel;
    }

    @ApiModelProperty(position = 8, value="钱包id")
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @Override
    public String toString() {
        return "GuessRecord{" +
                "accountNo='" + accountNo + '\'' +
                ", guessNumberId=" + guessNumberId +
                ", drawNumber='" + drawNumber + '\'' +
                ", drawTime=" + drawTime +
                ", drawLevel=" + drawLevel +
                ", sumAmount=" + sumAmount +
                ", accountName='" + accountName + '\''+
                ", luckNumber='" + luckNumber + '\'' +
                '}';
    }
}
