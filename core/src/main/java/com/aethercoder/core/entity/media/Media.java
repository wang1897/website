package com.aethercoder.core.entity.media;

import com.aethercoder.core.entity.BaseEntity;
import com.aethercoder.foundation.contants.CommonConstants;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.codec.binary.Base64;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.nio.charset.Charset;
import java.util.Date;

@Entity
@Table(name = "media")
public class Media extends BaseEntity {
    @Column(name = "language_type")
    private String languageType;
    @Column(name = "name")
    private String name;
    @Column(name = "notes")
    private String notes;
    @Column(name = "status" )
    private Integer status;
    @Column(name = "click_number")
    private Long clickNumber;
    @Column(name = "icon")
    private String icon;
    @Column(name = "url")
    private String url;
    @Column(name = "sequence")
    private Integer sequence;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;

    @Override
    @JsonIgnore(value = false)
    public Date getCreateTime() {
        return createTime;
    }

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = languageType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getClickNumber() {
        return clickNumber;
    }

    public void setClickNumber(Long clickNumber) {
        this.clickNumber = clickNumber;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    @PrePersist
    @PreUpdate
    public void encodeQuestion() {
        Charset charset = Charset.forName(CommonConstants.CHARACTER_ENCODE);
        this.notes = new String(Base64.encodeBase64(notes.getBytes(charset)), charset);
    }

    @PostLoad
    public void decodeQuestion() {
        Charset charset = Charset.forName(CommonConstants.CHARACTER_ENCODE);
        this.notes = new String(Base64.decodeBase64(notes.getBytes(charset)), charset);
    }
}
