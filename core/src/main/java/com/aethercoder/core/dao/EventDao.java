package com.aethercoder.core.dao;

import com.aethercoder.core.entity.event.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by jiawei.tao on 2017/9/13.
 */
@Repository
public interface EventDao extends JpaRepository<Event, Long>{

    Event findByEndDate(Date endDate);

    Event findEventByIdAndEventAvailableIsTrue(Long id);

    @Query("select sum(ep.expectedIncome) from EventApply ep Where ep.eventId = :eventId and ep.applyStatus <> 2")
    BigDecimal getApplyIncomesByEventId(@Param("eventId") Long eventId);

    @Query("select e from Event e Where :nowDate between e.beginDate and e.endDate and e.eventAvailable = true")
    List<Event> findEventsNow(@Param("nowDate")Date nowDate);

    Event findFirst1ByEndDateIsBeforeAndEventAvailableIsTrueOrderByEndDateDesc(Date nowDate);

    @Query("select count(e) from Event e Where ((:beginDate between e.beginDate and e.endDate) or :endDate between e.beginDate and e.endDate) and e.originalCurrency =:origCurrency and e.destCurrency=:destCurrency and e.eventAvailable = true")
    Long countEventsByDate(@Param("beginDate")Date beginDate,@Param("endDate")Date endDate,
                           @Param("origCurrency")String origCurrency,@Param("destCurrency")String destCurrency);

    Page<Event> findAll(Specification<Event> specification, Pageable pageable);


}
