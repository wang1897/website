package com.aethercoder.core.entity.event;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:17
 */
@Entity
@Table(name = "get_red_packet", schema = "qbao_schema")
public class GetRedPacket extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column( name = "account_no" )
    private String accountNo;

    @Column( name = "unit" )
    private long unit;

    @Column( name = "red_packet_id" )
    private long redPacketId;

    @Column( name = "amount" )
    private BigDecimal amount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "get_time", nullable = true)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date getTime;

    @Column( name = "sequence" )
    private Integer sequence;

    @Column( name = "comment" )
    private String comment;

    @Column( name = "best_luck" )
    private Boolean bestLuck;

    @Transient
    private String accountName;

    @Transient
    private String header;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public long getUnit() {
        return unit;
    }

    public void setUnit(long unit) {
        this.unit = unit;
    }

    public long getRedPacketId() {
        return redPacketId;
    }

    public void setRedPacketId(long redPacketId) {
        this.redPacketId = redPacketId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getGetTime() {
        return getTime;
    }

    public void setGetTime(Date getTime) {
        this.getTime = getTime;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getBestLuck() {
        return bestLuck;
    }

    public void setBestLuck(Boolean bestLuck) {
        this.bestLuck = bestLuck;
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
        return "GetRedPacket{" +
                "accountNo='" + accountNo + '\'' +
                ", unit=" + unit +
                ", redPacketId=" + redPacketId +
                ", amount=" + amount +
                ", getTime=" + getTime +
                ", sequence=" + sequence +
                ", comment='" + comment + '\'' +
                ", bestLuck=" + bestLuck +
                ", accountName='" + accountName + '\'' +
                ", header='" + header + '\'' +
                '}';
    }
}
