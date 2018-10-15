package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.WithdrawApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Repository
public interface WithdrawApplyDao extends JpaRepository<WithdrawApply,Long> {

    @Query("select sum(w.amount) From WithdrawApply w where w.unit = :unit and w.status <= :status and w.applyTime between :startTime and :endTime ")
    BigDecimal sumAmountByUnitAndStatusIsLessThanEqual(@Param("unit") Long unit, @Param("status") Integer status, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<WithdrawApply> findByStatus(@Param("status") Integer status);

    WithdrawApply findFirst1ByUnitAndStatus(@Param("unit") Long unit, @Param("status") Integer status);

    WithdrawApply findFirst1ByUnitAndStatusAndAmountIsLessThanEqual(@Param("unit") Long unit, @Param("status") Integer status,@Param("amount") BigDecimal amount);

    List<WithdrawApply> findByUnitAndStatus(@Param("unit") Long unit, @Param("status") Integer status);

    Integer countByStatusAndExchangeTimeAfter(@Param("status") Integer status,@Param("exchangeTime")Date exchangeTime);
}
