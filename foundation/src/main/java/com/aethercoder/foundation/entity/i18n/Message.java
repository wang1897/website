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
@Table(name = "message")
@ApiModel(value="语言消息",description="语言消息")
public class Message extends BaseEntity {
    private static final long serialVersionUID = -1L;

    public Message() {
        super();
    }

    public Message(String table, String field, String resourceId, String language, String message) {
        this.table = table;
        this.field = field;
        this.resourceId = resourceId;
        this.language = language;
        this.message = message;
    }

    @Column(name = "table_ref")
    private String table;

    @Column(name = "field_ref")
    private String field;

    @Column(name = "code")
    private String code;

    @Column(name = "resource_id")
    private String resourceId;

    @Column(name = "language")
    private String language;

    @Column(name = "message")
    private String message;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
