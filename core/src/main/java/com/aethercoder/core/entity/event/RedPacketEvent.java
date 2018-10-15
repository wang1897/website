package com.aethercoder.core.entity.event;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @auther Guo Feiyan
 * @date 2018/1/4 下午3:17
 */
@Entity
@Table(name = "red_packet_event", schema = "qbao_schema", catalog = "")
@ApiModel(value="天降红包Entity",description="天降红包")
public class RedPacketEvent extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column( name = "group_no" )
    private String groupNo;

    @Column( name = "start_time" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    @Column( name = "end_time" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @Column( name = "expire_time" )
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    @Column( name = "daily" )
    private Integer daily;

    @Column( name = "minutes" )
    private Integer minutes;

    @Column( name = "number" )
    private Integer number;

    @Column( name = "unit" )
    private Long unit;

    @Column( name = "amount" )
    private BigDecimal amount;

    @Column( name = "redType" )
    private Integer redType;

    @Column( name = "redNumber" )
    private Integer redNumber;

    @Column( name = "red_comment" )
    private String redComment;

    @Column( name = "event_info" )
    private String eventInfo;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Transient
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ApiModelProperty(position = 1, value="群id")
    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    @ApiModelProperty(position = 2, value="开始时间 yyyy-MM-dd")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @ApiModelProperty(position = 2, value="结束时间 yyyy-MM-dd")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @ApiModelProperty(position = 2, value="执行时间 HH:mm:ss")
    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    @ApiModelProperty(position = 2, value="日起间隔-天 ")
    public Integer getDaily() {
        return daily;
    }

    public void setDaily(Integer daily) {
        this.daily = daily;
    }

    @ApiModelProperty(position = 2, value="日起间隔-分钟 ")
    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    @ApiModelProperty(position = 2, value="日起间隔-N次 ")
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @ApiModelProperty(position = 2, value="红包币种 ")
    public Long getUnit() {
        return unit;
    }

    public void setUnit(Long unit) {
        this.unit = unit;
    }

    @ApiModelProperty(position = 2, value="红包金额 ")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @ApiModelProperty(position = 2, value="红包类型")
    public Integer getRedType() {
        return redType;
    }

    public void setRedType(Integer redType) {
        this.redType = redType;
    }

    @ApiModelProperty(position = 2, value="红包个数 ")
    public Integer getRedNumber() {
        return redNumber;
    }

    public void setRedNumber(Integer redNumber) {
        this.redNumber = redNumber;
    }

    @ApiModelProperty(position = 2, value="红包标语")
    public String getRedComment() {
        return redComment;
    }

    public void setRedComment(String redComment) {
        this.redComment = redComment;
    }

    @ApiModelProperty(position = 2, value="活动摘要 ")
    public String getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(String eventInfo) {
        this.eventInfo = eventInfo;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
