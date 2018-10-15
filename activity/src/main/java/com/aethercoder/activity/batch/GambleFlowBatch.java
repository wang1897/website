package com.aethercoder.activity.batch;

import com.aethercoder.activity.contants.ActivityContants;
import com.aethercoder.activity.dao.guessGamble.GuessGambleDao;
import com.aethercoder.activity.dao.guessGamble.JoinGambleDao;
import com.aethercoder.activity.entity.guessGamble.GuessGamble;
import com.aethercoder.activity.entity.guessGamble.JoinGamble;
import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.ContractDao;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.service.AccountBalanceService;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.foundation.schedule.BatchInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 投注小游戏-流盘-退钱
 * @auther Guo Feiyan
 * @date 2018/2/28 下午3:07
 */
@Service
public class GambleFlowBatch implements BatchInterface {

    private static Logger logger = LoggerFactory.getLogger(GambleFlowBatch.class);

    @Autowired
    private GuessGambleDao guessGambleDao;

    @Autowired
    private JoinGambleDao joinGambleDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Override
    @Transactional
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("GambleFlowBatch");
        GuessGamble guessGamble = guessGambleDao.findOne(task.getResourceId());
        if (guessGamble==null){
            // 投注小游戏不存在
            BatchResult failResult = task.getFailedResult("the gamble not exist");
            return failResult;
        }
        if (!ActivityContants.GUESS_GAMBLE_STATUS_FLOW.equals(guessGamble.getStatus())){
            // 该游戏并未流盘
            BatchResult failResult = task.getFailedResult("The gamble does not flow. ");
            return failResult;
        }
        List<JoinGamble> joinGambleList = joinGambleDao.queryAllByGambleId(task.getResourceId());
        Contract contractQBE = contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE);
        Long unit = contractQBE.getId();
        joinGambleList.forEach(joinGamble -> {
            accountBalanceService.accountReward(joinGamble.getAccountNo(),unit,joinGamble.getAmount(),WalletConstants.GAMBLE_REFUND_TYPE);
            logger.info(" 该用户："+joinGamble.getAmount() + " 在投注小游戏流盘后 退款金额："+ joinGamble.getAmount());
        });
        redisTemplate.opsForHash().delete(RedisConstants.REDIS_NAME_GAMBLE, guessGamble.getId());
        BatchResult successResult = task.getSuccessResult("success");
        return successResult;
    }
}
