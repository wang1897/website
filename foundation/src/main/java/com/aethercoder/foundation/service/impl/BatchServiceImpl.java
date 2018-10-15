package com.aethercoder.foundation.service.impl;

import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.dao.BatchDefinitionDao;
import com.aethercoder.foundation.dao.BatchResultDao;
import com.aethercoder.foundation.dao.BatchTaskCompleteDao;
import com.aethercoder.foundation.dao.BatchTaskDao;
import com.aethercoder.foundation.entity.batch.BatchDefinition;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.foundation.entity.batch.BatchTaskComplete;
import com.aethercoder.foundation.service.BatchService;
import com.aethercoder.basic.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hepengfei on 08/12/2017.
 */
@Service
public class BatchServiceImpl implements BatchService {

    @Autowired
    private BatchDefinitionDao batchDefinitionDao;

    @Autowired
    private BatchTaskDao batchTaskDao;

    @Autowired
    private BatchTaskCompleteDao batchTaskCompleteDao;

    @Autowired
    private BatchResultDao batchResultDao;

    @Override
    public BatchTask createBatchTask(String name, Date expireDate, String className, String resourceTable, Long resourceId) {
        if (resourceId != null && resourceTable != null) {
            BatchTask task = batchTaskDao.findBatchTaskByStatusAndResourceTableAndResourceIdAndName(CommonConstants.BATCH_TASK_STATUS_ACTIVE, resourceTable, resourceId, name);
            if (task != null) {
                task.setExpireTime(expireDate);
                return saveBatchTask(task);
            }
        }
        BatchTask batchTask = new BatchTask();
        batchTask.setClassName(className);
        batchTask.setName(name);
        batchTask.setExpireTime(expireDate);
        batchTask.setResourceTable(resourceTable);
        batchTask.setResourceId(resourceId);
        return saveBatchTask(batchTask);
    }

    @Override
    public void deleteBatchTask(String name, String resourceTable, Long resourceId) {
        BatchTask task = batchTaskDao.findBatchTaskByStatusAndResourceTableAndResourceIdAndName(CommonConstants.BATCH_TASK_STATUS_ACTIVE, resourceTable, resourceId, name);
        if(task!=null) {
            batchTaskDao.delete(task);
        }
    }

    @Override
    public void deleteBatchTasks( String resourceTable, Long resourceId) {
        List<BatchTask> tasks = batchTaskDao.findBatchTasksByStatusAndResourceTableAndResourceId(CommonConstants.BATCH_TASK_STATUS_ACTIVE, resourceTable, resourceId);
        tasks.forEach(batchTask -> {
            if (batchTask.getStatus().equals(CommonConstants.BATCH_TASK_STATUS_ACTIVE)){
                batchTaskDao.delete(tasks);
            }
        });
    }

    @Override
    public BatchDefinition saveBatchDefinition(BatchDefinition batchDefinition) {
        if (batchDefinition.getIsActive() == null) {
            batchDefinition.setIsActive(true);
        }

        if (batchDefinition.getExpireTime() == null) {
            batchDefinition.setExpireTime(batchDefinition.getStartTime());
        }

        BatchDefinition pBatchDefinition = batchDefinitionDao.save(batchDefinition);

        return pBatchDefinition;
    }

    @Override
    public List<BatchDefinition> getAvailableDefinition(Date date) {
        return batchDefinitionDao.findBatchDefinitionsByIsActiveAndExpireTimeBefore(true, date);
    }

    @Override
    public BatchTask runingTask(BatchTask batchTask) {
        batchTask.setStatus(CommonConstants.BATCH_TASK_STATUS_RUNNING);
        batchTask.setExecuteTime(new Date());
        return saveBatchTask(batchTask);
    }

    @Override
    public void completeTask(BatchTask batchTask) {
        batchTask.setCompleteTime(new Date());
        batchTask.setStatus(CommonConstants.BATCH_TASK_STATUS_COMPLETED);

        BatchTaskComplete batchTaskComplete = new BatchTaskComplete();
        BeanUtils.copyProperties(batchTask, batchTaskComplete, "id");
        batchTaskComplete.setTaskId(batchTask.getId());

        batchTaskCompleteDao.save(batchTaskComplete);
        batchTaskDao.delete(batchTask);
    }

    @Override
    public BatchTask saveBatchTask(BatchTask batchTask) {
        if (batchTask.getStatus() == null) {
            batchTask.setStatus(CommonConstants.BATCH_TASK_STATUS_ACTIVE);
        }
        return batchTaskDao.save(batchTask);
    }

    @Override
    public BatchResult saveBatchResult(BatchResult batchResult) {
        return batchResultDao.save(batchResult);
    }

    @Override
    public List<BatchTask> getAvailableTask(Date date) {
        return batchTaskDao.findBatchTaskByStatusAndExpireTimeBefore(CommonConstants.BATCH_TASK_STATUS_ACTIVE, date);
    }

    /*@Override
    public BatchTask saveNextTask(BatchTask batchTask) {
        //如果没有definitionId，代表是adhoc的schedule
        if (batchTask.getDefinitionId() == null) {
            return null;
        }
        BatchDefinition batchDefinition = batchDefinitionDao.findOne(batchTask.getDefinitionId());
        if (batchDefinition == null) {
            return null;
        }

        Date nextExpDate = calNextExpireTime(batchDefinition.getFrequency(), batchTask.getExpireTime());

        //如果下一个时间超过定义的definitionTime，就不用执行了
        if (nextExpDate.after(batchDefinition.getEndTime())) {
            return null;
        }

        BatchTask nextBatchTask = new BatchTask();
        BeanUtils.copyProperties(batchTask, nextBatchTask, "id", "expireTime", "status", "completeTime", "executeTime");
        nextBatchTask.setExpireTime(nextExpDate);
        return saveBatchTask(nextBatchTask);
    }*/

    @Override
    public BatchDefinition setToNextTimeSlot(BatchTask batchTask) {
        BatchDefinition batchDefinition = batchDefinitionDao.findOne(batchTask.getDefinitionId());
        Date nextExpiredTime = calNextExpireTime(batchDefinition.getFrequency(), batchDefinition.getExpireTime(), batchDefinition.getTimeSlot());

        //如果下一个时间超过定义的endTime，就不用执行了
        if (batchDefinition.getEndTime() != null && nextExpiredTime.after(batchDefinition.getEndTime())) {
            batchDefinition.setIsActive(false);
            return saveBatchDefinition(batchDefinition);
        }

        batchDefinition.setExpireTime(nextExpiredTime);
        return saveBatchDefinition(batchDefinition);
    }

    @Override
    public Page<BatchDefinition> findAllDefinition(Integer page, Integer size) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        return batchDefinitionDao.findAll(pageable);
    }

    @Override
    public BatchDefinition findDefinitionById(long id) {
        return batchDefinitionDao.findOne(id);
    }

    @Override
    public BatchDefinition findDefinitionByName(String name) {
        return batchDefinitionDao.findByName(name);
    }

    private Date calNextExpireTime(Integer frequency, Date startTime, Integer timeSlot) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startTime);

        if (frequency == CommonConstants.BATCH_FREQUENCY_HOURLY) {
            startCal.add(Calendar.HOUR, timeSlot);
        } else if (frequency == CommonConstants.BATCH_FREQUENCY_DAILY) {
            startCal.add(Calendar.DATE, timeSlot);
        } else if (frequency == CommonConstants.BATCH_FREQUENCY_WEEKLY) {
            startCal.add(Calendar.DATE, timeSlot * 7);
        } else if (frequency == CommonConstants.BATCH_FREQUENCY_MONTHLY) {
            startCal.add(Calendar.MONTH, timeSlot);
        } else if (frequency == CommonConstants.BATCH_FREQUENCY_YEARLY) {
            startCal.add(Calendar.YEAR, timeSlot);
        } else if (frequency == CommonConstants.BATCH_FREQUENCY_MINUTELY) {
            startCal.add(Calendar.MINUTE, timeSlot);
        }

        return startCal.getTime();
    }
}
