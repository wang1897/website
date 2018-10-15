package com.aethercoder.core.entity.social;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.*;

/**
 * Created by jiawei.tao on 2017/10/09.
 */
@Entity
@Table(name = "chat_group_member", schema = "qbao_schema")
public class GroupMember extends BaseEntity {
    private static final long serialVersionUID = -1L;
    @Basic
    @Column(name = "group_no")
    private String groupNo;
    @Basic
    @Column(name = "member_no")
    private String memberNo;
    @Basic
    @Column(name = "display_name")
    private String displayName;
    @Basic
    @Column(name = "role")
    private Integer role;
    @Basic
    @Column(name = "level")
    private Integer level;
    @Basic
    @Column(name = "level_status")
    private Boolean levelStatus;
    @Basic
    @Column(name = "header_url")
    private String headerUrl;
    @Basic
    @Column(name = "invite_source")
    private String inviteSource;
    @Basic
    @Column(name = "is_deleted", nullable = true)
    private Boolean isDeleted;
    @Basic
    @Column(name = "is_gap", nullable = true)
    private Boolean isGap;

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getMemberNo() {
        return memberNo;
    }

    public void setMemberNo(String memberNo) {
        this.memberNo = memberNo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public String getInviteSource() {
        return inviteSource;
    }

    public void setInviteSource(String inviteSource) {
        this.inviteSource = inviteSource;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getIsGap() {
        return isGap;
    }

    public void setIsGap(Boolean isGap) {
        this.isGap = isGap;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getLevelStatus() {
        return levelStatus;
    }

    public void setLevelStatus(Boolean levelStatus) {
        this.levelStatus = levelStatus;
    }

}
