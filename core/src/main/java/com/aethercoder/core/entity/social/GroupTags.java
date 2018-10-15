package com.aethercoder.core.entity.social;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.*;

/**
 * @auther Guo Feiyan
 * @date 2018/1/3 下午2:34
 */
@Entity
@Table(name = "group_tags", schema = "qbao_schema")
public class GroupTags extends BaseEntity{
    private static final long serialVersionUID = -1L;

    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "sequence")
    private Integer sequence = 0 ;

    @Basic
    @Column(name = "is_delete")
    private Boolean isDelete = false;

    @Transient
    private Integer groupCounts;

    public Integer getGroupCounts() {
        return groupCounts;
    }

    public void setGroupCounts(Integer groupCounts) {
        this.groupCounts = groupCounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
       this.isDelete = isDelete;
    }
}
