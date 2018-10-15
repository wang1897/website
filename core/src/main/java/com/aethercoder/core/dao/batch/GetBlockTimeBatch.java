package com.aethercoder.core.dao.batch;

import com.aethercoder.core.dao.GuessNumberGameDao;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.core.entity.guess.GuessNumberGame;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.core.service.GuessNumberGameService;
import com.aethercoder.core.service.QtumService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @auther jiawei.tao
 * @date 2018/12/10 下午1:38
 */
@Service
public class GetBlockTimeBatch implements BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(GetBlockTimeBatch.class);

    @Autowired
    private QtumService qtumService;
    @Autowired
    private GuessNumberGameDao guessNumberGameDao;
    @Autowired
    private GuessNumberGameService guessNumberGameService;

    @Override
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("GetBlockTimeBatch Start");

        // 获取 当前有效的活动
        List<GuessNumberGame> guessNumberGameList = guessNumberGameDao.findByBeginBlockIsGreaterThanAndIsDeleteIsFalseAndLuckTimeIsNull(0);
        if (guessNumberGameList == null || guessNumberGameList.size() == 0) {

            BatchResult successResult = task.getSuccessResult("No game now");
            logger.info("GetBlockTimeBatch End");
            return successResult;
        }
        for (GuessNumberGame guessNumberGame : guessNumberGameList) {
            if (guessNumberGame == null) {
                continue;
            }
            // 获取游戏开始时刻
            if (guessNumberGame.getGameStartTime() == null) {
                // 获取开始区块
                Integer startBlock = guessNumberGame.getBeginBlock();
                if (startBlock != null) {
                    // 查询区块
                    HashMap info = qtumService.getInfo();
                    // 当前区块号
                    Integer nowBlockNo = (Integer) info.get("blocks");
                    if (nowBlockNo.compareTo(startBlock) >= 0) {
                        guessNumberGame.setGameStartTime(new Date());
                        guessNumberGameDao.save(guessNumberGame);
                    }
                }
            }else

            // 获取游戏结束时刻
            if (guessNumberGame.getGameEndTime() == null) {
                // 获取开始区块
                Integer endBlock = guessNumberGame.getEndBlock();
                if (endBlock != null) {
                    // 查询区块
                    HashMap info = qtumService.getInfo();
                    // 当前区块号
                    Integer nowBlockNo = (Integer) info.get("blocks");
                    if (nowBlockNo.compareTo(endBlock) >= 0) {
                        guessNumberGame.setGameEndTime(new Date());
                        guessNumberGameDao.save(guessNumberGame);
                    }
                }
            }else
            // 获取游戏开奖时刻
            if (guessNumberGame.getLuckTime() == null) {
                // 获取开奖区块
                Integer luckBlock = guessNumberGame.getLuckBlock();
                if (luckBlock != null) {
                    // 查询区块
                    HashMap info = qtumService.getInfo();
                    // 当前区块号
                    Integer nowBlockNo = (Integer) info.get("blocks");
                    if (nowBlockNo.compareTo(luckBlock) >= 0) {
//                        guessNumberGame.setLuckTime(new Date());
//                        guessNumberGameDao.save(guessNumberGame);
                        if (guessNumberGame.getLuckNumber() != null) {
                            guessNumberGame.setLuckTime(new Date());
                            guessNumberGameDao.save(guessNumberGame);
                            guessNumberGameService.runningLuck(guessNumberGame.getId());
                        }
                    }
                }
            }
        }
        BatchResult successResult = task.getSuccessResult("success");

        logger.info("GetBlockTimeBatch End");
        return successResult;
    }

}
