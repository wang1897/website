package com.aethercoder.core.entity.event;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by jiawei.tao on 2017/9/13.
 */
@Entity
public class Event extends BaseEntity {
    private static final long serialVersionUID = -1L;

    private String eventName;

    @Column(name = "begin_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginDate;

    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;
    private long eventTotalAmount;

    @Column(name = "event_available", nullable=false)
    private Boolean eventAvailable;
    @Column(name = "h5_url", nullable = false, length = 150)
    private String h5Url;
    private String eventBanner;
    private Long upperLimit;
    private Long lowerLimit;
    private String originalCurrency;
    private String destCurrency;
    private String expression;
    @Column(name = "type")
    private Integer type;


    @Transient
    private String eventBanner_en;
    @Transient
    private String eventBanner_ko;

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    //    private Collection<EventApply> eventAppliesById;

    @Basic
    @Column(name = "event_name", nullable = false, length = 200)
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Basic
    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    @Basic
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Basic
    @Column(name = "event_total_amount", nullable = false)
    public long getEventTotalAmount() {
        return eventTotalAmount;
    }

    public void setEventTotalAmount(long eventTotalAmount) {
        this.eventTotalAmount = eventTotalAmount;
    }
    @Basic
    public Boolean getEventAvailable() {
        return eventAvailable;
    }

    public void setEventAvailable(Boolean eventAvailable) {
        this.eventAvailable = eventAvailable;
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
    }

    @Basic
    @Column(name = "event_banner", nullable = false, length = 200)
    public String getEventBanner() {
        return eventBanner;
    }

    public void setEventBanner(String eventBanner) {
        this.eventBanner = eventBanner;
    }

    @Basic
    @Column(name = "upper_limit", nullable = true)
    public Long getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Long upperLimit) {
        this.upperLimit = upperLimit;
    }

    @Basic
    @Column(name = "lower_limit", nullable = true)
    public Long getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Long lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    @Basic
    @Column(name = "original_currency", nullable = false, length = 10)
    public String getOriginalCurrency() {
        return originalCurrency;
    }

    public void setOriginalCurrency(String originalCurrency) {
        this.originalCurrency = originalCurrency;
    }

    @Basic
    @Column(name = "dest_currency", nullable = false, length = 10)
    public String getDestCurrency() {
        return destCurrency;
    }

    public void setDestCurrency(String destCurrency) {
        this.destCurrency = destCurrency;
    }

    @Basic
    @Column(name = "expression", nullable = false, length = 45)
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getEventBanner_en() {
        return eventBanner_en;
    }

    public void setEventBanner_en(String eventBanner_en) {
        this.eventBanner_en = eventBanner_en;
    }

    public String getEventBanner_ko() {
        return eventBanner_ko;
    }

    public void setEventBanner_ko(String eventBanner_ko) {
        this.eventBanner_ko = eventBanner_ko;
    }

    /*@OneToMany(mappedBy = "eventByEventId")
    public Collection<EventApply> getEventAppliesById() {
        return eventAppliesById;
    }

    public void setEventAppliesById(Collection<EventApply> eventAppliesById) {
        this.eventAppliesById = eventAppliesById;
    }*/
}
