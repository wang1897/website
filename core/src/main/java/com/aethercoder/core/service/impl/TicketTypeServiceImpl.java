package com.aethercoder.core.service.impl;

import com.aethercoder.core.dao.TicketTypeDao;
import com.aethercoder.core.entity.ticket.TicketType;
import com.aethercoder.core.service.TicketTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketTypeServiceImpl implements TicketTypeService{
    @Autowired
    TicketTypeDao ticketTypeDao;
    @Override
    public List<TicketType> findTicketTypeAll(){
        return ticketTypeDao.findAll();
    }

}
