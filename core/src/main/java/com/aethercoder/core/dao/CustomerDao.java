package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/8
 * @modified By:
 */
@Repository
public interface CustomerDao extends JpaRepository<Customer,Long> {

    Customer findByCustomerNo(String customerNo);

    Customer findByCustomerNoAndStatus(String customerNo,Integer status);

    @Query(value = "SELECT id FROM qbao_schema.t_customer where  cancellation_time <=:cleanTime and status = :status",nativeQuery = true)
    List<Object> findCustomerIds(@Param("cleanTime") Date cleanTime, @Param("status") Integer status);

    Page<Customer> findAll(Specification<Customer> specification, Pageable pageable);

    Customer findCustomerByCustomerNoAndPassword(String customerName,String password);

    Customer getFirstByOrderByCreateTimeDesc();
}
