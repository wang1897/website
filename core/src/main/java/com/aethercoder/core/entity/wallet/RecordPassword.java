package com.aethercoder.core.entity.wallet;

import com.aethercoder.core.entity.BaseEntity;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;


/**
 * Created by GuoFeiYan on 2017/8/30.
 */
@Entity
@Table(name = "password_record")
public class RecordPassword extends BaseEntity{


    @Column(name = "account_id")
    private long accountId;

    @Column(name = "result")
    private String result;

    @Column(name = "lapse_type")
    private String lapseType;

    @Column(name = "code", unique = true,length = 32)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid")
    private String code;

    public String getLapseType() {
        return lapseType;
    }

    public void setLapseType(String lapseType) {
        this.lapseType = lapseType;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getLapsetype() {
        return lapseType;
    }

    public void setLapsetype(String lapsetype) {
        this.lapseType = lapsetype;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


    @PrePersist
    public void initializeUUID() {
        if (code == null) {
            code = UUID.randomUUID().toString();
        }
    }
}
