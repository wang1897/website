package com.aethercoder.foundation.dao;

import com.aethercoder.foundation.entity.batch.BatchDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by hepengfei on 08/12/2017.
 */
@Repository
public interface BatchDefinitionDao extends JpaRepository<BatchDefinition, Long> {
    List<BatchDefinition> findBatchDefinitionsByIsActiveAndExpireTimeBefore(Boolean isActive, Date expireTime);

    BatchDefinition findByName(String name);
}
