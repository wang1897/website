package com.aethercoder.core.dao;

import com.aethercoder.core.entity.event.AccountBalance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:39
 */
@Repository
public interface AccountBalanceDao extends JpaRepository<AccountBalance, Long> {

    List<AccountBalance> findByAccountNo(String accountNo);

    AccountBalance findByAccountNoAndUnit(String accountNo, Long unit);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    AccountBalance findAccountBalanceByAccountNoAndUnit(String accountNo, Long unit);

    Integer countAllByUnit(Long unit);

    @Query(value = "SELECT COUNT(account_no) FROM qbao_schema.account_balance ", nativeQuery = true)
    Integer countAll();

    @Query(value = "SELECT SUM(amount) FROM qbao_schema.account_balance where unit =:unit and amount >= :amount", nativeQuery = true)
    BigDecimal sumAmountByQBELessThan(@Param("unit") Long unit, @Param("amount")BigDecimal amount);

    @Query(value = "SELECT SUM(amount) FROM qbao_schema.account_balance where unit =:unit ", nativeQuery = true)
    BigDecimal sumAmountByUnit(@Param("unit") Long unit);

    @Query(value = "SELECT count(account_no) FROM qbao_schema.account_balance where unit =:unit and amount >:amount", nativeQuery = true)
    Integer countAllByUnitAndAmountGreaterThanEqual(@Param("unit")Long unit, @Param("amount")BigDecimal amount);

    @Query(value = "SELECT avg(amount) FROM qbao_schema.account_balance where unit =:unit ", nativeQuery = true)
    BigDecimal avgQBE( @Param("unit")Long unit);

    Page<AccountBalance> findAll(Specification<AccountBalance> specification, Pageable pageable);
}
