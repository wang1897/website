package com.aethercoder.core.entity.event;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:17
 */
@Entity
@Table( name = "send_red_packet", schema = "qbao_schema" )
public class SendRedPacket extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column( name = "account_no" )
    private String accountNo;

    @Column( name = "unit" )
    private long unit;

    @Column( name = "amount" )
    private BigDecimal amount;

    @Column( name = "balance" )
    private BigDecimal balance;

    @Column( name = "number" )
    private Integer number;

    @Column( name = "type" )
    private Integer type;

    @Temporal( TemporalType.TIMESTAMP )
    @Column( name = "send_time", nullable = false )
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8" )
    private Date sendTime;

    @Column( name = "is_available" )
    private Boolean isAvailable;

    @Column( name = "comment" )
    private String comment;

    @Column( name = "theme" )
    private long theme;

    @Transient
    private String spendTime;

    @Transient
    private String accountName;

    @Transient
    private String header;

    @ApiModelProperty( position = 1, value = "发红包用户ID" )
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @ApiModelProperty( position = 2, value = "币种 关联币种表ID" )
    public long getUnit() {
        return unit;
    }

    public void setUnit(long unit) {
        this.unit = unit;
    }

    @ApiModelProperty( position = 3, value = "金额" )
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @ApiModelProperty( position = 4, value = "红包个数" )
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @ApiModelProperty( position = 5, value = "红包类型，0是普通，1是凭手气" )
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @ApiModelProperty( position = 6, value = "发送红包时间 只读" )
    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    @ApiModelProperty( position = 7, value = "红包有效无效 只读" )
    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @ApiModelProperty( position = 8, value = "红包备注 封面语" )
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @ApiModelProperty( position = 9, value = "红包主题 暂未使用" )
    public long getTheme() {
        return theme;
    }

    public void setTheme(long theme) {
        this.theme = theme;
    }

    @ApiModelProperty( position = 10, value = "红包抢剩金额 只读" )
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @ApiModelProperty( position = 10, value = "抢红包花费时间 只读" )
    public String getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(String spendTime) {
        this.spendTime = spendTime;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public String toString() {
        return "{" +
                "\"idstr\":"+id+
                ", \"accountNo\":\"" + accountNo + "\"" +
                ", \"unit\":" + unit +
                ", \"amount\":" + amount +
                ", \"balance\":" + balance +
                ", \"number\":" + number +
                ", \"type\":" + type +
                ", \"isAvailable\":" + isAvailable +
                ", \"comment\":\"" + comment + '\"' +
                ", \"extra\":\"\"" +
                "}";
    }

}
