package com.aethercoder.core.entity.admin;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Guo Feiyan on 2017/9/13.
 */
@Entity
@Table(name = "admin_account")
public class AdminAccount extends BaseEntity{
    private static final long serialVersionUID = -1L;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
