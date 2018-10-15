package com.aethercoder.foundation.entity.batch;

import com.aethercoder.foundation.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created by hepengfei on 08/12/2017.
 */
@Entity
@Table(name = "batch_definition")
public class BatchDefinition extends BaseEntity {
    private static final long serialVersionUID = -1L;
    private String name;
    private Integer frequency;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    private Boolean isActive;
    private String className;
    private Integer timeSlot = 1;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;
    private String parameterName1;
    private String parameterName2;
    private String parameterName3;
    private String parameterName4;
    private String parameterName5;
    private String parameterName6;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getParameterName1() {
        return parameterName1;
    }

    public void setParameterName1(String parameterName1) {
        this.parameterName1 = parameterName1;
    }

    public String getParameterName2() {
        return parameterName2;
    }

    public void setParameterName2(String parameterName2) {
        this.parameterName2 = parameterName2;
    }

    public String getParameterName3() {
        return parameterName3;
    }

    public void setParameterName3(String parameterName3) {
        this.parameterName3 = parameterName3;
    }

    public String getParameterName4() {
        return parameterName4;
    }

    public void setParameterName4(String parameterName4) {
        this.parameterName4 = parameterName4;
    }

    public String getParameterName5() {
        return parameterName5;
    }

    public void setParameterName5(String parameterName5) {
        this.parameterName5 = parameterName5;
    }

    public String getParameterName6() {
        return parameterName6;
    }

    public void setParameterName6(String parameterName6) {
        this.parameterName6 = parameterName6;
    }

    public Integer getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Integer timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
