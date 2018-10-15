package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.ClearanceDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jiawei.tao
 * @date 2018/01/03
 **/
@Repository
public interface ClearanceDetailDao extends JpaRepository<ClearanceDetail,Long> {
    ClearanceDetail findByQbaoId(Long qbaoId);

    List<ClearanceDetail> findByClearanceIdAndAndIsClearIsFalse(Long clearanceId);

    Page<ClearanceDetail> findAll(Specification<ClearanceDetail> clearanceId, Pageable pageable);

    ClearanceDetail findByChainTxid(String ChainTxid);

    List<ClearanceDetail> findByClearanceIdOrderByIsClear(Long clearanceId);
}
