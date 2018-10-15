package com.aethercoder.core.entity.social;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.*;

/**
 * Created by jiawei.tao on 2017/10/09.
 */
@Entity
@Table(name = "user_group", schema = "qbao_schema")
public class UserGroup extends BaseEntity{
    private static final long serialVersionUID = -1L;

    @Basic
    @Column(name = "account_no")
    private String accountNo;
    @Basic
    @Column(name = "group_no")
    private String groupNo;
    @Basic
    @Column(name = "display_name")
    private String displayName;
    @Basic
    @Column(name = "header_url")
    private String headerUrl;
    @Basic
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    @Basic
    @Column(name = "is_top")
    private Boolean isTop;
    @Basic
    @Column(name = "version")
    private Long version;
    @Basic
    @Column(name = "no_distrub")
    private Boolean noDistrub;
    @Basic
    @Column(name = "role")
    private Integer role;

    @Transient
    private GroupInfo groupInfo;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Boolean getIsTop() {
        return isTop;
    }

    public void setIsTop(Boolean isTop) {
        this.isTop = isTop;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getNoDistrub() {
        return noDistrub;
    }

    public void setNoDistrub(Boolean noDistrub) {
        this.noDistrub = noDistrub;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

}
