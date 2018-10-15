package com.aethercoder.core.entity.social;

import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @auther Guo Feiyan
 * @date 2018/1/3 下午2:37
 */
@Entity
@Table(name = "group_of_tags", schema = "qbao_schema")
public class GroupOfTags extends BaseEntity{
    private static final long serialVersionUID = -1L;

    @Basic
    @Column(name = "group_no")
    private String groupNo;

    @Basic
    @Column(name = "tag")
    private long tag;

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public long getTag() {
        return tag;
    }

    public void setTag(long tag) {
        this.tag = tag;
    }
}
