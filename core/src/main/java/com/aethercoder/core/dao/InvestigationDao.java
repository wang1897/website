package com.aethercoder.core.dao;

import com.aethercoder.core.entity.event.Investigation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jiawei.tao on 2017/10/24.
 */
@Repository
public interface InvestigationDao extends JpaRepository<Investigation, Long>{
    Investigation findInverstigationByEmail(String email);

    Investigation findByAccountNo(String accountNo);

    Page<Investigation> findAll(Specification<Investigation> email, Pageable pageable);
}
