package com.aethercoder.foundation.dao;

import com.aethercoder.foundation.entity.batch.BatchTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by hepengfei on 08/12/2017.
 */
@Repository
public interface BatchTaskDao extends JpaRepository<BatchTask, Long> {
    List<BatchTask> findBatchTaskByStatusAndExpireTimeBefore(Integer status, Date expireTime);

    BatchTask findBatchTaskByStatusAndResourceTableAndResourceIdAndName(Integer status, String resourceTable, Long resourceId, String name);

    List<BatchTask> findBatchTasksByStatusAndResourceTableAndResourceId(Integer status, String resourceTable, Long resourceId);
}
