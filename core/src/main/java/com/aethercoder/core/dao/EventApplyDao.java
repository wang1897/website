package com.aethercoder.core.dao;

import com.aethercoder.core.entity.event.EventApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by jiawei.tao on 2017/9/14.
 */
@Repository
public interface EventApplyDao extends JpaRepository<EventApply, Long> {

    EventApply findByAccountNo(String accountNo);

    @Query("select ep from EventApply ep where ep.accountNo = :accountNo and ep.eventId = :eventId ")
    EventApply getApplyByAccountNoAndEventId(@Param("accountNo") String accountNo, @Param("eventId") Long eventId);

    @Query("select sum(e.actualIncome) from EventApply e where e.accountNo = :accountNo and e.applyStatus = :applyStatus")
    BigDecimal sumActualIncomeByAccountNo(@Param("accountNo") String accountNo, @Param("applyStatus") Integer applyStatus);

    @Query(value = "select sum(ep.actual_income) from qbao_schema.event_apply ep,qbao_schema.event e where ep.event_id = e.id and ep.account_no = :accountNo and ep.apply_status = :applyStatus and e.dest_currency = :destCurrency",nativeQuery = true)
    BigDecimal sumActualIncomeByAccountNoAndDestCurrency(@Param("accountNo") String accountNo, @Param("applyStatus") Integer applyStatus, @Param("destCurrency") String destCurrency);

    @Query("select ep from EventApply ep join ep.event e where e.endDate <:endDate and ep.applyStatus= 1 and e.eventAvailable = true")
    List<EventApply> getUnpaidEventApplies(@Param("endDate") Date now);


    Long countEventAppliesByEventId(Long id);

    @Query("select sum(e.expectedIncome) from EventApply e where e.eventId = :eventId and e.applyStatus not in (2)")
    BigDecimal sumByExpectedIncome(@Param("eventId")Long id);

    @Query("select sum(e.actualIncome) from EventApply e where e.eventId = :eventId")
    BigDecimal sumByActualIncome(@Param("eventId")Long id);

    List<EventApply> findEventAppliesByEventId(Long id);

    @Query("select e from EventApply e ,Account a where e.accountNo = a.accountNo and e.eventId = :eventId and (e.accountNo like :accountNo or a.accountName like :accountNo) ")
    Page<EventApply> findByAccount_AccountNameLikeOrAccountNoLikeAndEventId(@Param("eventId")long eventId , @Param("accountNo")String accountNo, Pageable pageable);

    List<EventApply> getEventAppliesByAccountNo(@Param("account") String accountNo);

    List<EventApply> findEventApplyByAccountNoAndApplyStatus(String accountNo,Integer status);

    List<EventApply> findEventApplyByApplyStatus(Integer status);

}