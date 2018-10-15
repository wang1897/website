package com.aethercoder.core.entity.social;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.codec.binary.Base64;

import javax.persistence.*;
import java.util.Date;

/**
 * @auther Guo Feiyan
 * @date 2017/12/18 下午1:23
 */
@Entity
@Table(name = "group_notice", schema = "qbao_schema")
public class GroupNotice extends BaseEntity {
    private static final long serialVersionUID = -1L;

    @Transient
    private String accountName;

    @Transient
    private String header;

    @Transient
    private Integer role;

    @Transient
    private String operator;

    @Column(name = "group_no")
    private String groupNo;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "create_by")
    private String createBy;
    @Column(name = "write_time")
    @Temporal( TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date writeTime;
    @Column(name = "is_delete")
    private Boolean isDelete;

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(Date writeTime) {
        this.writeTime = writeTime;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}
