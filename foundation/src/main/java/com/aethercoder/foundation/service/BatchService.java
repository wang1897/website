package com.aethercoder.foundation.service;

import com.aethercoder.foundation.entity.batch.BatchDefinition;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

/**
 * Created by hepengfei on 08/12/2017.
 */
public interface BatchService {
    BatchDefinition saveBatchDefinition(BatchDefinition batchDefinition);

    BatchTask saveBatchTask(BatchTask batchTask);

    List<BatchDefinition> getAvailableDefinition(Date date);

    List<BatchTask> getAvailableTask(Date date);

    BatchResult saveBatchResult(BatchResult batchResult);

//    BatchTask saveNextTask(BatchTask batchTask);

    void completeTask(BatchTask batchTask);

    BatchTask runingTask(BatchTask batchTask);

    BatchTask createBatchTask(String name, Date expireDate, String className, String resourceTable, Long resourceId);

    void deleteBatchTask(String name, String resourceTable, Long resourceId);

    void deleteBatchTasks(String resourceTable, Long resourceId);

    BatchDefinition setToNextTimeSlot(BatchTask batchTask);

    Page<BatchDefinition> findAllDefinition(Integer page, Integer size);

    BatchDefinition findDefinitionById(long id);

    BatchDefinition findDefinitionByName(String name);
}
