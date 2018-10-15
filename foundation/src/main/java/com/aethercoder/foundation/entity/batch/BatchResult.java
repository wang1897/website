package com.aethercoder.foundation.entity.batch;

import com.aethercoder.foundation.entity.BaseEntity;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by hepengfei on 08/12/2017.
 */
@Entity
@Table(name = "batch_result")
public class BatchResult extends BaseEntity {
    private static final long serialVersionUID = -1L;
    private Long taskId;
    @Type(type="text")
    private String result;
    private Integer status;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
