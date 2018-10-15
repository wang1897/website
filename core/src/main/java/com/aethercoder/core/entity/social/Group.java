package com.aethercoder.core.entity.social;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.*;

/**
 * Created by jiawei.tao on 2017/10/09.
 */
@Entity
@Table(name = "chat_group", schema = "qbao_schema")
public class Group extends BaseEntity{
    private static final long serialVersionUID = -1L;
    @Basic
    @Column(name = "name")
    private String name;
    @Basic
    @Column(name = "logo_url")
    private String logoUrl;
    @Basic
    @Column(name = "comment")
    private String comment;
    @Basic
    @Column(name = "tag")
    private String tag;
    @Basic
    @Column(name = "update_admin_id")
    private String updateAdminId;
    @Basic
    @Column(name = "create_admin_id")
    private String createAdminId;
    @Basic
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Basic
    @Column(name = "level")
    private Integer level;

    @Basic
    @Column(name = "max_member")
    private Integer maxMember;

    @Basic
    @Column(name = "group_no")
    private String groupNo;

    @Basic
    @Column(name = "member_num")
    private Integer memberNum;

    @Basic
    @Column(name = "limit_unit")
    private Long limitUnit;

    @Basic
    @Column(name = "limit_amount")
    private Integer limitAmount;

    @Basic
    @Column(name = "sequence")
    private Integer sequence;

    @Basic
    @Column(name = "confirm_status")
    private Integer confirmStatus;

    @Basic
    @Column(name = "command_info")
    private String commandInfo;

    public String getCommandInfo() {
        return commandInfo;
    }

    public Integer getConfirmStatus() {
        return confirmStatus;
    }

    public void setConfirmStatus(Integer confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    public void setCommandInfo(String commandInfo) {
        this.commandInfo = commandInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getUpdateAdminId() {
        return updateAdminId;
    }

    public void setUpdateAdminId(String updateAdminId) {
        this.updateAdminId = updateAdminId;
    }

    public String getCreateAdminId() {
        return createAdminId;
    }

    public void setCreateAdminId(String createAdminId) {
        this.createAdminId = createAdminId;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(Integer maxMember) {
        this.maxMember = maxMember;
    }
    public Integer getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(Integer memberNum) {
        this.memberNum = memberNum;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public Long getLimitUnit() {
        return limitUnit;
    }

    public void setLimitUnit(Long limitUnit) {
        this.limitUnit = limitUnit;
    }

    public Integer getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(Integer limitAmount) {
        this.limitAmount = limitAmount;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

}
