package com.aethercoder.core.service;

import com.aethercoder.core.entity.batch.RedPacketBatchDefinition;
import com.aethercoder.core.entity.event.Event;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by jiawei.tao on 2017/9/13.
 */
public interface EventService {
    Event createEvent(Event event);

    Event updateEvent(Event event);

    void deleteEvent(Long id);

    BigDecimal getEventSurplus(Long id);

    Event findEventByEventID(Long id);

    Event findByEventID(Long id);

    List<Event> findEventAll();

    List<Event> findEventsNow();

    Page<Event> findAvailableEvensByPage(Integer page, Integer size);

    Page<Event> findEvensByPage(Integer page, Integer size);

    Page<Event> findEvensByPage(Integer page, Integer size, String eventName, String originalCurrency, String destCurrency, Boolean eventAvailable, String beginDate, String endDate);

    void createRedPacketEvent(RedPacketBatchDefinition event);

    void updateRedPacketEvent(RedPacketBatchDefinition event);
}
