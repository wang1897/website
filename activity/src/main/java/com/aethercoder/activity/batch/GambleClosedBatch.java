package com.aethercoder.activity.batch;

import com.aethercoder.activity.contants.ActivityContants;
import com.aethercoder.activity.dao.guessGamble.GuessGambleDao;
import com.aethercoder.activity.entity.guessGamble.GuessGamble;
import com.aethercoder.activity.entity.json.GambleResult;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.foundation.schedule.BatchInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 投注小游戏-动态封盘
 *
 * @auther Guo Feiyan
 * @date 2018/2/28 下午3:07
 */
@Service
public class GambleClosedBatch implements BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(GambleClosedBatch.class);

    @Autowired
    private GuessGambleDao guessGambleDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("GambleClosedBatch");

        GuessGamble guessGamble = guessGambleDao.findOne(task.getResourceId());
        if (guessGamble==null){
            // 投注小游戏不存在
            BatchResult failResult = task.getFailedResult("the gamble not exist");
            return failResult;
        }

        if (guessGamble.getStatus().equals(ActivityContants.GUESS_GAMBLE_STATUS_RUNNING)){
            guessGamble.setStatus(ActivityContants.GUESS_GAMBLE_STATUS_CLOSE);
        }
        //更新统计
        Object gambleResultObject = redisTemplate.opsForHash().get(RedisConstants.REDIS_NAME_GAMBLE, guessGamble.getId());

       if (gambleResultObject!=null){
           GambleResult gambleResult = BeanUtils.jsonToObject(gambleResultObject.toString(),GambleResult.class);
           guessGamble.setOption1_amount(gambleResult.getOption1_amount());
           guessGamble.setOption1_number(gambleResult.getOption1_number());
           guessGamble.setOption2_amount(gambleResult.getOption2_amount());
           guessGamble.setOption2_number(gambleResult.getOption2_number());
       }else {
           guessGamble.setOption1_amount(new BigDecimal(0));
           guessGamble.setOption1_number(0);
           guessGamble.setOption2_amount(new BigDecimal(0));
           guessGamble.setOption2_number(0);
       }

        redisTemplate.opsForHash().delete(RedisConstants.REDIS_NAME_GAMBLE, guessGamble.getId());
        guessGambleDao.save(guessGamble);

        BatchResult successResult = task.getSuccessResult("success");
        return successResult;
    }
}
