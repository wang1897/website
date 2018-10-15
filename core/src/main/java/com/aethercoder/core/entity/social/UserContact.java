package com.aethercoder.core.entity.social;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by jiawei.tao on 2017/10/09.
 */
@Entity
@Table(name = "user_contact", schema = "qbao_schema")
@ApiModel(value="我的好友对象",description="我的好友")
public class UserContact extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Basic
    @Column(name = "account_no")
    private String accountNo;
    @Basic
    @Column(name = "relationship")
    private Integer relationship;
    @Basic
    @Column(name = "contact_no")
    private String contactNo;
    @Basic
    @CreationTimestamp
    @Column(name = "add_friend_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addFriendDate;
    @Basic
    @Column(name = "add_blacklist_date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date addBlacklistDate;
    @Basic
    @Column(name = "comment")
    private String comment;
    @Basic
    @Column(name = "display_name")
    private String displayName;
    @Basic
    @Column(name = "is_top")
    private Boolean isTop;
    @Basic
    @Column(name = "tag")
    private String tag;
    @Basic
    @Column(name = "version")
    private Long version;
    @Basic
    @Column(name = "no_disturb")
    private Boolean noDisturb;

    @Transient
    private String accountName;

    @Transient
    private String header;

    @ApiModelProperty(position = 1, value="好友名称")
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @ApiModelProperty(position = 2, value="好友头像")
    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Boolean getTop() {
        return isTop;
    }

    public void setTop(Boolean top) {
        isTop = top;
    }

    @ApiModelProperty(position = 3, value="本人的accounNo")
    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    @ApiModelProperty(position = 4, value="好友关系")
    public Integer getRelationship() {
        return relationship;
    }

    public void setRelationship(Integer relationship) {
        this.relationship = relationship;
    }

    @ApiModelProperty(position = 5, value="好友的accounNo")
    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    @ApiModelProperty(position = 6, value="添加好友时间")
    public Date getAddFriendDate() {
        return addFriendDate;
    }

    public void setAddFriendDate(Date addFriendDate) {
        this.addFriendDate = addFriendDate;
    }

    @ApiModelProperty(position = 6, value="添加黑名单的时间")
    public Date getAddBlacklistDate() {
        return addBlacklistDate;
    }

    public void setAddBlacklistDate(Date addBlacklistDate) {
        this.addBlacklistDate = addBlacklistDate;
    }

    @ApiModelProperty(position = 7, value="好友备注")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @ApiModelProperty(position = 8, value="好友屏显名")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @ApiModelProperty(position = 9, value=" 置顶\n" +
            "1：置顶\n" +
            "0：非置顶")
    public Boolean getIsTop() {
        return isTop;
    }

    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    @ApiModelProperty(position = 10, value="好友标签")
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @ApiModelProperty(position = 11, value="更新版本号")
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @ApiModelProperty(position = 12, value="免打扰\n" +
            "0：正常\n" +
            "1：免打扰")
    public Boolean getNoDisturb() {
        return noDisturb;
    }

    public void setNoDisturb(Boolean noDisturb) {
        this.noDisturb = noDisturb;
    }

}
