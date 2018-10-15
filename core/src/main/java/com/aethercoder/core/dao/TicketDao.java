
package com.aethercoder.core.dao;

import com.aethercoder.core.entity.ticket.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lilangfeng
 * @date 2018/01/03
 **/
@Repository
public interface TicketDao extends JpaRepository<Ticket,Long>{
    Page<Ticket> findAll(Specification<Ticket> question, Pageable pageable);

    @Query(value = "SELECT ti.name,count(t.type)   FROM qbao_schema.ticket t inner join qbao_schema.ticket_type ti on t.type = ti.id group by t.type,ti.name", nativeQuery = true)
    List countByType();

}
