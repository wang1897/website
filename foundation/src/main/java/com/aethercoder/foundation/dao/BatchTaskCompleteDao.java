package com.aethercoder.foundation.dao;

import com.aethercoder.foundation.entity.batch.BatchTaskComplete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by hepengfei on 22/02/2018.
 */
@Repository
public interface BatchTaskCompleteDao extends JpaRepository<BatchTaskComplete, Long> {
}
