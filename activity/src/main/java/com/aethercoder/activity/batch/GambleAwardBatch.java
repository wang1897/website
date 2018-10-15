package com.aethercoder.activity.batch;

import com.aethercoder.activity.contants.ActivityContants;
import com.aethercoder.activity.dao.guessGamble.GambleRankDao;
import com.aethercoder.activity.dao.guessGamble.GuessGambleDao;
import com.aethercoder.activity.dao.guessGamble.JoinGambleDao;
import com.aethercoder.activity.entity.guessGamble.GambleRank;
import com.aethercoder.activity.entity.guessGamble.GuessGamble;
import com.aethercoder.activity.entity.guessGamble.JoinGamble;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.ContractDao;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.service.AccountBalanceService;
import com.aethercoder.foundation.entity.batch.BatchResult;
import com.aethercoder.foundation.entity.batch.BatchTask;
import com.aethercoder.foundation.schedule.BatchInterface;
import com.aethercoder.foundation.service.BatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * 投注小游戏-开奖
 *
 * @auther Guo Feiyan
 * @date 2018/2/28 下午3:07
 */
@Service
public class GambleAwardBatch implements BatchInterface {
    private static Logger logger = LoggerFactory.getLogger(GambleAwardBatch.class);

    @Autowired
    private GuessGambleDao guessGambleDao;

    @Autowired
    private GambleRankDao gambleRankDao;

    @Autowired
    private JoinGambleDao joinGambleDao;

    @Autowired
    private ContractDao contractDao;
    @Autowired
    private BatchService batchService;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public BatchResult run(BatchTask task) throws Exception {
        logger.info("GambleAwardBatch");
        GuessGamble guessGamble = guessGambleDao.findOne(task.getResourceId());
        if (guessGamble == null) {
            // 投注小游戏不存在
            BatchResult failResult = task.getFailedResult("the gamble did not exist");
            return failResult;
        } else if (!ActivityContants.GUESS_GAMBLE_STATUS_CLOSE.equals(guessGamble.getStatus())) {
            // 投注小游戏尚未封盘
            // 创建批处理 - 已开奖
            Date endDate = new Date();
            batchService.createBatchTask("GuessGambleAward", endDate, GambleAwardBatch.class.getName(), guessGamble.getClass().getSimpleName(), task.getResourceId());

            BatchResult failResult = task.getFailedResult("the gamble is not end");
            return failResult;
        }

        guessGamble.setStatus(ActivityContants.GUESS_GAMBLE_STATUS_AWARD);
        guessGambleDao.save(guessGamble);
        List<JoinGamble> joinGambleList = joinGambleDao.queryAllByGambleId(task.getResourceId());
        //中奖总金额
        BigDecimal winAmount = guessGamble.getOption1_amount();
        //输掉总金额
        BigDecimal loseAmount = guessGamble.getOption2_amount();
        Character winOption = guessGamble.getLuckOption();
        if (guessGamble.getLuckOption().equals(ActivityContants.OPTION_B)) {
            winAmount = guessGamble.getOption2_amount();
            loseAmount = guessGamble.getOption1_amount();
        }

        BigDecimal totalAmount = winAmount.add(loseAmount);

        Contract contractQBE = contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE);
        Long unit = contractQBE.getId();
        Map<String, GambleRank> gambleRankMap = new HashMap<String, GambleRank>();
        for (JoinGamble joinGamble : joinGambleList) {
            String accounNo = joinGamble.getAccountNo();
            GambleRank gambleRank = null;
            if (gambleRankMap.containsKey(accounNo)) {
                gambleRank = gambleRankMap.get(accounNo);
            }
            if (joinGamble.getOption().equals(winOption)) {
                joinGamble.setIsLuck(true);
                //中奖
                BigDecimal amount = joinGamble.getAmount();
                //赚多少钱
                BigDecimal winAmountAccountNo = amount.multiply(totalAmount).divide(winAmount, 6, BigDecimal.ROUND_FLOOR);

                logger.info(" 该用户：" + joinGamble.getAccountNo() + " 投注金额：" + joinGamble.getAmount() + " 本次投注中奖金额：" + winAmountAccountNo);
                if (gambleRank == null) {
                    gambleRank = new GambleRank();
                    gambleRank.setAmount(winAmountAccountNo);
                    gambleRank.setGambleId(joinGamble.getGambleId());
                    gambleRank.setAccountNo(joinGamble.getAccountNo());
                } else {
                    gambleRank.setAmount(gambleRank.getAmount().add(winAmountAccountNo));
                }
                joinGamble.setWinAmount(winAmountAccountNo);
                gambleRankMap.put(accounNo, gambleRank);
            } else {
                joinGamble.setIsLuck(false);
                joinGamble.setWinAmount(new BigDecimal(0));
            }
        }
        joinGambleDao.save(joinGambleList);
        for (Map.Entry<String, GambleRank> gambleRankEntry : gambleRankMap.entrySet()) {
            GambleRank gambleRank = gambleRankEntry.getValue();
            gambleRankDao.save(gambleRank);
            if (gambleRank.getAmount().compareTo(new BigDecimal(0)) > 0) {
                accountBalanceService.accountReward(gambleRank.getAccountNo(), unit, gambleRank.getAmount(), WalletConstants.GAME_AWARD);
            }
        }

        BatchResult successResult = task.getSuccessResult("success");
        return successResult;
    }


}
