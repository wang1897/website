package com.aethercoder.core.entity.social;

import com.aethercoder.core.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

/**
 * @author li langfeng
 * @date  2018/01/0
 */
@Entity
@Table(name = "sys_notice", schema = "qbao_schema")
public class SystemNotice extends BaseEntity {
    private static final long serialVersionUID = -1L;

  @Column(name = "language_type")
    private String languageType;
  @Column(name = "title")
    private String title;
  @Column(name = "content")
    private String content;
  @Column(name = "release_by")
  private Long releaseBy;
  @Column(name = "status")
    private Integer status;
    @Column(name = "is_delete")
    private Boolean isDelete;
    @Column(name = "issue_time")
    @Temporal( TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date issueTime;
    @Column(name = "update_time")
    @Temporal( TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
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

    public Long getReleaseBy() {
        return releaseBy;
    }

    public void setReleaseBy(Long releaseBy) {
        this.releaseBy = releaseBy;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer noticeStatus) {
        this.status = noticeStatus;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean delete) {
        isDelete = delete;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

    @Override
    @JsonIgnore(value = false)
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    @JsonIgnore(value = false)
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }


}
