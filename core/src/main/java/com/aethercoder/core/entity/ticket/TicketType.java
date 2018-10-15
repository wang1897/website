package com.aethercoder.core.entity.ticket;
/**
 * @author lilangfeng
 * @date 2018/01/03
 **/
import com.aethercoder.core.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ticket_type")
public class TicketType extends BaseEntity{
    private static final long serialVersionUID = -1L;

    @Column(name = "name")
    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


}
