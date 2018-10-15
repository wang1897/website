package com.aethercoder.core.dao;

import com.aethercoder.core.entity.ticket.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author lilangfeng
 * @date 2018/01/03
 **/
public interface TicketTypeDao extends JpaRepository<TicketType,Long> {

}
