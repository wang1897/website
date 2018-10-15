package com.aethercoder.core.entity.event;

import com.aethercoder.core.entity.BaseEntity;
import com.aethercoder.core.entity.wallet.Account;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jiawei.tao on 2017/9/13.
 */
@Entity
@Table(name = "event_apply", schema = "qbao_schema")
public class EventApply extends BaseEntity {
    private static final long serialVersionUID = -1L;

    private String accountNo;
    private BigDecimal applyAmount;
    private BigDecimal expectedIncome;

    @Column(name = "apply_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applyTime;

    @Column(name = "cancel_time", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date cancelTime;
    private Integer applyStatus;
    private BigDecimal actualAmount;
    private BigDecimal actualIncome;
    @Column(name = "event_id")
    private Long eventId;

    @ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH})
    @JoinColumn(name = "event_id", referencedColumnName="id", insertable=false, updatable=false)
    private Event event;

    @Transient
    private Account account;

    @Basic
    @Column(name = "account_no")
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @Basic
    @Column(name = "apply_amount", nullable = false, precision = 0)
    public BigDecimal getApplyAmount() {
        return applyAmount;
    }

    public void setApplyAmount(BigDecimal applyAmount) {
        this.applyAmount = applyAmount;
    }

    @Basic
    @Column(name = "expected_income", nullable = false, precision = 0)
    public BigDecimal getExpectedIncome() {
        return expectedIncome;
    }

    public void setExpectedIncome(BigDecimal expectedIncome) {
        this.expectedIncome = expectedIncome;
    }

    @Basic
    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    @Basic
    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    @Basic
    @Column(name = "apply_status", nullable = false, length = 1)
    public Integer getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }

    @Basic
    @Column(name = "actual_amount", nullable = true, precision = 0)
    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    @Basic
    @Column(name = "actual_income", nullable = true, precision = 0)
    public BigDecimal getActualIncome() {
        return actualIncome;
    }

    public void setActualIncome(BigDecimal actualIncome) {
        this.actualIncome = actualIncome;
    }


    @Basic
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "EventApply{" +
                "accountNo='" + accountNo + '\'' +
                ", applyAmount=" + applyAmount +
                ", expectedIncome=" + expectedIncome +
                ", applyTime=" + applyTime +
                ", cancelTime=" + cancelTime +
                ", applyStatus=" + applyStatus +
                ", actualAmount=" + actualAmount +
                ", actualIncome=" + actualIncome +
                ", eventId=" + eventId +
                ", event=" + event +
                ", id=" + id +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
