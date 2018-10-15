package com.aethercoder.foundation.entity.i18n;

import com.aethercoder.foundation.entity.BaseEntity;
import io.swagger.annotations.ApiModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by guofeiyan on 2018/02/09.
 */
@Entity
@Table(name = "language")
@ApiModel(value="语言",description="语言")
public class Language extends BaseEntity {

    private static final long serialVersionUID = -1L;

    @Column(name = "language_code")
    private String languageCode;

    @Column(name = "is_default")
    private Boolean isDefault;

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
