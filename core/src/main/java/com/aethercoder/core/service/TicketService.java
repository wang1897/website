package com.aethercoder.core.service;

import com.aethercoder.core.entity.ticket.Ticket;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author lilangfeng
 * @date 2018/01/03
 **/
public interface TicketService {

    Ticket createTicket(Ticket ticket);

    Ticket updateTicket(Ticket ticket);

    Page<Ticket> findEvensByPage(Integer page, Integer size);

    Page<Ticket> findTicketsByPage(Integer page, Integer size, String question, Long type, Integer status, String startTime, String endTime);

    List countByType();
}

