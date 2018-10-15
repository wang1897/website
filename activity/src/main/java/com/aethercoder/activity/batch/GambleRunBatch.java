package com.aethercoder.activity.batch;

import com.aethercoder.activity.contants.ActivityContants;
import com.aethercoder.activity.dao.guessGamble.GuessGambleDao;
import com.aethercoder.activity.entity.guessGamble.GuessGamble;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.foundation.service.BatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 投注小游戏-动态开盘
 *
 * @auther Guo Feiyan
 * @date 2018/2/28 下午3:07
 */
@Service
public class GambleRunBatch implements BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(GambleRunBatch.class);

    @Autowired
    private GuessGambleDao guessGambleDao;

    @Autowired
    private BatchService batchService;

    @Override
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("GambleRunBatch");

        GuessGamble guessGamble = guessGambleDao.findOne(task.getResourceId());
        if (guessGamble==null){
            // 投注小游戏不存在
            BatchResult failResult = task.getFailedResult("the gamble not exist");
            return failResult;
        }
        if (!ActivityContants.GUESS_GAMBLE_STATUS_NOT_APPLY.equals(guessGamble.getStatus())){
            // 投注小游戏并不是待开始状态
            BatchResult failResult = task.getFailedResult("Betting on a small game is not a start.");
            return failResult;
        }
        guessGamble.setStatus(ActivityContants.GUESS_GAMBLE_STATUS_RUNNING);
        guessGambleDao.save(guessGamble);
        // 创建批处理 - 动态封盘
        batchService.createBatchTask("GuessGambleClosed", guessGamble.getCloseTime(), GambleClosedBatch.class.getName(), guessGamble.getClass().getSimpleName(), guessGamble.getId());

        BatchResult successResult = task.getSuccessResult("success");
        return successResult;
    }
}
