package com.aethercoder.core.entity.event;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.*;
import java.util.UUID;

/***
 * @author jiawei.tao
 * Create on 2017/11/3
 */
@Entity
@Table( name = "fund_user", schema = "qbao_schema" )
public class FundUser extends BaseEntity {
    private static final long serialVersionUID = -1L;

    private String email;
    private String password;
    private String username;
    private String activateType;
    private String icoQualification;
    private String uniqueId;

    @Transient
    private String address;

    @Transient
    private Long investigationId;

    @Basic
    @Column( name = "email", nullable = false, length = 255 )
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Basic
    @Column( name = "password", nullable = false, length = 32 )
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Basic
    @Column( name = "username", nullable = true, length = 30 )
    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    @Basic
    @Column( name = "activate_type", nullable = false, length = 1 )
    public String getActivateType() {
        return activateType;
    }

    public void setActivateType(String activateType) {
        this.activateType = activateType;
    }

    @Basic
    @Column( name = "ico_qualification")
    public String getIcoQualification() {
        return icoQualification;
    }

    public void setIcoQualification(String icoQualification) {
        this.icoQualification = icoQualification;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getInvestigationId() {
        return investigationId;
    }

    public void setInvestigationId(Long investigationId) {
        this.investigationId = investigationId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @PrePersist
    public void initializeUUID() {
        if (uniqueId == null) {
            uniqueId = UUID.randomUUID().toString();
        }
    }
}
