package com.aethercoder.core.entity.wallet;


import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
@Entity
@Table(name = "sys_wallet")
public class SysWallet extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Column(name = "name")
    private String name;
    @Column(name = "value")
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
