package com.aethercoder.core.dao;

import com.aethercoder.core.entity.event.ExchangeLog;
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
 * Created by Guo Feiyan on 2017/9/18.
 */
@Repository
public interface ExchangeLogDao extends JpaRepository<ExchangeLog, Long> {

    List<ExchangeLog> findByAccountNo(String accountNo);

    ExchangeLog findByTransactionHash(String transactionHash);

    @Query(value = "SELECT   el.account_no 'accountNo', SUM(el.amount) 'amount', count( el.account_no) 'number' FROM qbao_schema.exchange_log el WHERE el.type = 5 AND el.is_deleted = 0 GROUP BY el.account_no  order by SUM(el.amount) desc limit 10", nativeQuery = true)
    List getTakeBonusRankingList();

    @Query(value = "SELECT  el.account_no 'accountNo', SUM(el.amount) 'amount', COUNT(el.account_no) 'number' FROM qbao_schema.exchange_log el WHERE el.type = 5 AND el.is_deleted = 0 GROUP BY el.account_no ORDER BY SUM(el.amount)  DESC", nativeQuery = true)
    List getTakeBonusRankingListByCountAndAccountNo();

    Page<ExchangeLog> findAll(Specification<ExchangeLog> accountNo, Pageable pageable);

    @Query(value = "SELECT sum(amount) FROM qbao_schema.exchange_log where unit = :unit and type = :types", nativeQuery = true)
    BigDecimal findExchangeLogsByUnitAndType(@Param("unit") Long unit, @Param("types") Integer types);

    List findExchangeLogsByType(Integer type);

    ExchangeLog findFirst1ByExchangeTimeAfter(Date exchangeTime);


    ExchangeLog findFirstByExchangeTimeBeforeOrderByIdDesc(Date exchangeTime);

    List<ExchangeLog> findExchangeLogByTypeAndStatusOrderByIdDesc(Integer type,Integer status);
    List<ExchangeLog> findExchangeLogByIdAfterAndTypeAndStatusOrderByIdDesc(Long startId,Integer type,Integer status);
    List<ExchangeLog> findExchangeLogByIdBeforeAndTypeAndStatusOrderByIdDesc(Long endId,Integer type,Integer status);
    List<ExchangeLog> findExchangeLogByIdBetweenAndTypeAndStatusOrderByIdDesc(Long startId, Long endId,Integer type,Integer status);

    @Query("select sum(e.amount) from ExchangeLog e where e.type = :type and e.accountNo = :accountNo and e.unit = :unit and e.status <= :status and (e.exchangeTime between :todayBegin and :todayEnd)")
    BigDecimal sumByTypeAndAccountNoAndUnitAndStatusAndExchangeTimeIsBetween(@Param("type") Integer type,@Param("accountNo")String accountNo,@Param("unit") Long unit,@Param("status") Integer status,@Param("todayBegin")Date todayBegin,@Param("todayEnd")Date todayEnd);

    @Query("select sum(amount) from ExchangeLog where type = :type and accountNo = :accountNo")
    BigDecimal sumByTypeAndAccountNo(@Param("type") Integer type,@Param("accountNo")String accountNo);

    @Query("select sum(amount) from ExchangeLog where type = :type and unit = :unit and status <= :status and (exchangeTime between :todayBegin and :todayEnd)")
    BigDecimal sumByTypeAndUnitAndStatusAndExchangeTimeIsBetween(@Param("type") Integer type,@Param("unit") Long unit,@Param("status") Integer status,@Param("todayBegin")Date todayBegin,@Param("todayEnd")Date todayEnd);

    ExchangeLog findFirst1ByAccountNoAndUnitAndTypeAndStatusAndAddress(String accountNo,Long unit,Integer type,Integer status,String address);
}
