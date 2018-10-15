package com.aethercoder.foundation.schedule;

import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.entity.batch.BatchDefinition;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.foundation.service.BatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by hepengfei on 2017/9/13.
 */
@Component
@ConditionalOnProperty(name="schedule.enabled", havingValue="true")
public class ScheduledTask {

    private static Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Autowired
    private BatchService batchService;

    @Scheduled(cron = "${schedule.scheduledBatchCron}")
    public void scheduledBatch() {
        logger.info("开始执行scheduledBatch");
        MDC.put("batch", "scheduleBatch");
        try {
            Date currentDate = new Date();
            List<BatchDefinition> batchDefinitionList = batchService.getAvailableDefinition(currentDate);
            if (batchDefinitionList != null && !batchDefinitionList.isEmpty()) {
                logger.info("schedule batch count：{}", batchDefinitionList.size());
                //call thread pool to run batch in threads
                ExecutorService executor = Executors.newFixedThreadPool(batchDefinitionList.size());
                for (BatchDefinition batchDefinition : batchDefinitionList) {
                    logger.info("run batch: id: {}, name: {}", batchDefinition.getId(), batchDefinition.getName());
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                runBatchDefinition(batchDefinition);
                                logger.info("end batch: id: {}, name: {}", batchDefinition.getId(), batchDefinition.getName());
                            } catch (Exception ex) {
                                logger.error(" 批处理错误调度 ", ex);
                            }
                        }
                    });
                }
                executor.shutdown();
            } else {
                logger.info("schedule batch count：{}", 0);
            }
        } catch (Exception e) {
            logger.error(" 批处理错误调度 ", e);
        }
        logger.info("任务结束");
        MDC.remove("batch");
        logger.info("结束执行scheduledBatch");
    }

    @Scheduled(cron = "${schedule.adhocBatchCron}")
    public void adhocBatch() {
        logger.info("开始执行adhocBatch");
        MDC.put("batch", "adhocBatch");
        try {
            Date currentDate = new Date();

            List<BatchTask> batchTaskList = batchService.getAvailableTask(currentDate);
            if (batchTaskList != null && !batchTaskList.isEmpty()) {
                logger.info("adhoc batch count：{}", batchTaskList.size());
                long start = System.currentTimeMillis();
                boolean isTimeout = false;
                long timeout = 1000 * 60 * 10; //一个批处理timeout时间10分钟
                //call thread pool to run batch in threads
                ExecutorService executor = Executors.newFixedThreadPool(batchTaskList.size());
                for (BatchTask batchTask: batchTaskList) {
                    logger.info("run batch: id:{}, name:{}", batchTask.getId(), batchTask.getName());
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                runBatchTask(batchTask);
                            } catch (Exception ex) {
                                logger.error(" 批处理错误调度 ", ex);
                            }
                        }
                    });
                    logger.info("end batch: id:{}, name:{}", batchTask.getId(), batchTask.getName());
                }
                executor.shutdown();

                if (!executor.isTerminated()) {
                    logger.warn("adhoc Batch 子任务未结束");
                }

                while (!executor.awaitTermination(10, TimeUnit.SECONDS) && !isTimeout ) {
                    long end = System.currentTimeMillis();
//                    System.out.println(end - start);
                    isTimeout = (end - start) > timeout;
                }
            } else {
                logger.info("adhoc batch count：{}", "0");
            }
        } catch (Exception e) {
            logger.error(" 批处理错误调度 ", e);
        }
        logger.info("任务结束");
        MDC.remove("batch");
        logger.info("结束执行adhocBatch");
    }
//
//    @Scheduled(cron = "${schedule.withdrawBatchCron}")
//    public void withdrawBatch() {
//        logger.info("开始执行withdrawBatch");
//        MDC.put("batch", "withdrawBatch");
//        try {
//            Date currentDate = new Date();
//
//            List<BatchTask> batchTaskList = batchService.getAvailableTask(currentDate);
//            if (batchTaskList != null && !batchTaskList.isEmpty()) {
//                logger.info("adhoc batch count：{}", batchTaskList.size());
//                long start = System.currentTimeMillis();
//                boolean isTimeout = false;
//                long timeout = 1000 * 60 * 10; //一个批处理timeout时间10分钟
//                //call thread pool to run batch in threads
//                ExecutorService executor = Executors.newFixedThreadPool(batchTaskList.size());
//                BatchTask
////                for (BatchTask batchTask: batchTaskList) {
//                    logger.info("run batch: id:{}, name:{}", batchTask.getId(), batchTask.getName());
//                    executor.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                runBatchTask(batchTask);
//                            } catch (Exception ex) {
//                                logger.error(" 批处理错误调度 ", ex);
//                            }
//                        }
//                    });
//                    logger.info("end batch: id:{}, name:{}", batchTask.getId(), batchTask.getName());
////                }
//                executor.shutdown();
//
//                if (!executor.isTerminated()) {
//                    logger.warn("withdraw Batch 子任务未结束");
//                }
//
//                while (!executor.awaitTermination(10, TimeUnit.SECONDS) && !isTimeout ) {
//                    long end = System.currentTimeMillis();
////                    System.out.println(end - start);
//                    isTimeout = (end - start) > timeout;
//                }
//            } else {
//                logger.info("withdraw batch count：{}", "0");
//            }
//        } catch (Exception e) {
//            logger.error(" 批处理错误调度 ", e);
//        }
//        logger.info("任务结束");
//        MDC.remove("batch");
//        logger.info("结束执行withdrawBatch");
//    }

    private void runBatchDefinition(BatchDefinition batchDefinition) throws Exception {
        BatchTask batchTask = new BatchTask();
        batchTask.setDefinitionId(batchDefinition.getId());
        batchTask.setName(batchDefinition.getName());
        batchTask.setExpireTime(batchDefinition.getStartTime());
        batchTask.setStatus(CommonConstants.BATCH_TASK_STATUS_RUNNING);
        batchTask.setClassName(batchDefinition.getClassName());
        batchTask.setParameter1(batchDefinition.getParameterName1());
        batchTask.setParameter2(batchDefinition.getParameterName2());
        batchTask.setParameter3(batchDefinition.getParameterName3());
        batchTask.setParameter4(batchDefinition.getParameterName4());
        batchTask.setParameter5(batchDefinition.getParameterName5());
        batchTask.setParameter6(batchDefinition.getParameterName6());
        BatchTask pBatchTask = batchService.saveBatchTask(batchTask);
        runBatchTask(pBatchTask);
        batchService.setToNextTimeSlot(batchTask);

    }

    private void runBatchTask(BatchTask batchTask) throws Exception {
        batchService.runingTask(batchTask);
        BatchResult batchResult = executeBatch(batchTask);
        batchService.saveBatchResult(batchResult);
        batchService.completeTask(batchTask);
    }

    private BatchResult executeBatch(BatchTask batchTask) throws Exception {
        String className = batchTask.getClassName();

//        BatchInterface batchInterface  = (BatchInterface)ClassUtils.forName(className, Thread.currentThread().getContextClassLoader()).newInstance();
        BatchResult batchResult;
        try {
            BatchInterface batchInterface = BatchContext.getBean(className);
            logger.debug(" 执行批处理 {}", batchTask.getId());
            batchResult = batchInterface.run(batchTask);
        } catch (Exception ex) {
            logger.error(" 批处理错误执行 {}", batchTask.getId(), ex);
            String result = ex.getMessage();
            StackTraceElement[] stackTraceElements = ex.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                result += "\n" + stackTraceElement.toString();
            }
            batchResult = batchTask.getFailedResult(result);
        }
        return batchResult;
    }
}
