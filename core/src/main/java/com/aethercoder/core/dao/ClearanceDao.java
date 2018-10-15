package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.Clearance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jiawei.tao
 * @date 2018/01/03
 **/
@Repository
public interface ClearanceDao extends JpaRepository<Clearance,Long> {
    Clearance findFirst1ClearanceByUnitOrderByIdDesc(Long unit);

    Page<Clearance> findAll(Specification<Clearance> specification, Pageable pageable);
//    Clearance findByQbaoId(Long qbaoId);

    Clearance findFirstClearanceByTypeOrderByIdDesc(Integer type);

}
