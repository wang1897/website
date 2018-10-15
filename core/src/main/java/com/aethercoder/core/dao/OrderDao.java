package com.aethercoder.core.dao;

import com.aethercoder.core.entity.pay.Order;
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
import java.util.Date;
import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2018/3/20 下午4:43
 */
@Repository
public interface OrderDao extends JpaRepository<Order, Long> {


    @Query(value = "SELECT * FROM qbao_schema.t_order o where order_time >= :beginTime and order_time <= :endTime  and o.customer_id = :customerNo and status = 1",nativeQuery = true)
    List<Order> findOrdersByCustomerId(@Param("beginTime") Date beginTime,@Param("endTime") Date endTime, @Param("customerNo") String customerNo);

    Page<Order> findAll(Specification<Order> specification, Pageable pageable);

    List<Order> findAll(Specification<Order> specification);

    Order findByOrderId(String orderId);
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Order findOrderByOrderId(String orderId);

    @Query(value = "SELECT sum(amount) from Order where orderTime >=:beginTime and status <= :status")
    BigDecimal sumTodayAmount(@Param("beginTime") Date beginTime, @Param("status") Integer status);

    List<Order> findByStatusAndOrderTimeBefore(@Param("status") Integer status,@Param("orderTime") Date orderTime);
}
