package com.aethercoder.core.entity.android;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @auther Guo Feiyan
 * @date 2017/9/26 上午11:09
 */
@Entity
@Table(name = "android_package")
public class Android extends BaseEntity{
    private static final long serialVersionUID = -1L;

    @Column(name = "version_name")
    private String versionName;

    @Column(name = "version_code")
    private String versionCode;

    @Column(name = "description")
    private String description;

    @Column(name = "force_update")
    private Boolean forceUpdate;

    @Column(name = "path")
    private String path;

    @Column(name = "source")
    private Boolean source;

    @Transient
    private String descriptionKo;

    @Transient
    private String descriptionEn;

    @Transient
    private String descriptionJa;

    public String getDescriptionJa() {
        return descriptionJa;
    }

    public void setDescriptionJa(String descriptionJa) {
        this.descriptionJa = descriptionJa;
    }

    public String getDescriptionKo() {
        return descriptionKo;
    }

    public void setDescriptionKo(String descriptionKo) {
        this.descriptionKo = descriptionKo;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public Boolean getSource() {
        return source;
    }

    public void setSource(Boolean source) {
        this.source = source;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(Boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
