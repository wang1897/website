package com.aethercoder.foundation.dao;

import com.aethercoder.foundation.entity.batch.BatchResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by hepengfei on 08/12/2017.
 */
@Repository
public interface BatchResultDao extends JpaRepository<BatchResult, Long> {
}
