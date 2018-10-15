package com.aethercoder.core.service;

import com.aethercoder.core.entity.ticket.TicketType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lilangfeng
 * @date 2018/01/03
 **/
@Service
public interface TicketTypeService {
    List<TicketType> findTicketTypeAll();
}
