package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.*;
import com.aethercoder.core.entity.guess.*;
import com.aethercoder.core.entity.guess.GuessAward;
import com.aethercoder.core.entity.guess.GuessNumberGame;
import com.aethercoder.core.entity.guess.GuessRecord;
import com.aethercoder.core.entity.guess.GuessUnit;
import com.aethercoder.core.entity.json.BaseWinner;
import com.aethercoder.core.entity.json.GameJson;
import com.aethercoder.core.entity.json.WinnerInfo;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.GuessNumberGameService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.util.DateUtil;
import com.aethercoder.foundation.util.NumberUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by guofeiyan on 2018/01/29.
 */
@Service
public class GuessNumberGameServiceImpl implements GuessNumberGameService {

    private static Logger logger = LoggerFactory.getLogger(GuessNumberGameServiceImpl.class);
    @Autowired
    private GuessNumberGameDao guessNumberGameDao;

    @Autowired
    private GuessRecordDao guessRecordDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private GuessUnitDao guessUnitDao;

    @Autowired
    private AccountBalanceDao accountBalanceDao;

    @Autowired
    private ExchangeLogDao exchangeLogDao;

    @Autowired
    private GamesDao gamesDao;

    @Autowired
    private GuessAwardDao guessAwardDao;

    @Autowired
    private SysConfigDao sysConfigDao;

    @Override
    @Transactional
    public GuessNumberGame saveGuessNumberGame(GuessNumberGame guessNumberGame) {
        logger.info("saveGuessNumberGame");
        checkGuess(guessNumberGame);
        GuessNumberGame guessNumberGame1 = guessNumberGameDao.save(guessNumberGame);
        Set<GuessUnit> guessUnits = guessNumberGame.getGuessUnits();
        guessUnits.forEach(guessUnit -> {
            guessUnit.setGuessId(guessNumberGame.getId());
            guessUnitDao.save(guessUnit);
        });
//        Boolean exist = redisTemplate.hasKey(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL);
//        //添加redis set
//        redisTemplate.execute(new SessionCallback() {
//            @Override
//            public Object execute(RedisOperations operations)
//                    throws DataAccessException {
//                operations.multi();
//                if (!exist) {
//                    for (int i = 0; i < 100; i++) {
//                        String[] numbers = new String[1000];
//                        for (int j = 0; j < 1000; j++) {
//                            numbers[j] = NumberUtil.formatNumberToString(j + i * 1000, "00000");
//                            System.out.print(numbers[j] + ",");
//                        }
//
//                        operations.opsForSet().add(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL, numbers);
//
//                    }
//                }
//                operations.opsForSet().unionAndStore(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL,
//                        RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame1.getId(),
//                        RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame1.getId());
////                operations.expire(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame1.getId(), 30, TimeUnit.DAYS);
//                operations.exec();
//                return null;
//
//            }
//        });

        return guessNumberGame1;
    }

    @Override
    public void updateGuessNumberGame(GuessNumberGame guessNumberGame) {
        logger.info("updateGuessNumberGame");
        GuessNumberGame guessNumberGameUpd = guessNumberGameDao.findOne(guessNumberGame.getId());
        //check unit 不能重复
        checkUnit(guessNumberGame);
        if (guessNumberGameUpd.getGameStartTime() != null && new Date().compareTo(guessNumberGameUpd.getGameStartTime()) > 0) {
            throw new AppException(ErrorCode.EVENT_NOT_UPDATE);
        }
        if (guessNumberGameUpd.getEndBlock().compareTo(guessNumberGame.getEndBlock()) != 0
                || guessNumberGameUpd.getBeginBlock().compareTo(guessNumberGame.getBeginBlock()) != 0) {
            //已存在有效活动
            Long count = guessNumberGameDao.getGuessNumberGameActivate(guessNumberGame.getBeginBlock(), guessNumberGame.getEndBlock(), guessNumberGame.getGameId());
            if (count != null && count > 1) {
                throw new AppException(ErrorCode.EVENT_OVERLAP);
            }
        }

        guessNumberGameDao.save(guessNumberGame);

        guessUnitDao.save(guessNumberGame.getGuessUnits());
    }

    @Override
    public void deleteGuessNumberGame(Long id) {
        logger.info("deleteGuessNumberGame");
        GuessNumberGame guessNumberGame = guessNumberGameDao.findByIdAndIsDeleteIsFalse(id);
        if (guessNumberGame != null && guessNumberGame.getGameStartTime() != null
                && (new Date().compareTo(guessNumberGame.getGameStartTime()) > 0)) {
            throw new AppException(ErrorCode.CHECK_GUESS_NOT_DELETE);
        } else if (guessNumberGame != null && !guessNumberGame.getIsDelete()) {
            guessNumberGame.setDelete(true);
            guessNumberGameDao.save(guessNumberGame);
            //删除币种
            Set<GuessUnit> guessUnits = guessUnitDao.findGuessUnitsByGuessId(guessNumberGame.getId());
            guessUnits.forEach(guessUnit -> {
                guessUnitDao.delete(guessUnit.getId());
            });
        } else {
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }
    }

    @Override
    public void updateAward(GuessNumberGame guessNumberGame) {
        logger.info("updateAward");
        GuessNumberGame guessNumberGameUpd = guessNumberGameDao.findOne(guessNumberGame.getId());
        if (guessNumberGameUpd != null && !guessNumberGameUpd.getIsDelete()
                && guessNumberGameUpd.getLuckNumber() == null
                && guessNumberGame.getLuckNumber().length() == 5) {
            guessNumberGameUpd.setLuckNumber(guessNumberGame.getLuckNumber());
            guessNumberGameDao.save(guessNumberGameUpd);
            if (guessNumberGameUpd.getLuckTime() != null) {
                runningLuck(guessNumberGame.getId());
            }
        }
    }

    @Override
    public Page<GuessNumberGame> findGuessNumberGames(Integer page, Integer size, Boolean isShow, Long gameId) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<GuessNumberGame> guessNumberGames = guessNumberGameDao.findAll(new Specification<GuessNumberGame>() {
            @Override
            public Predicate toPredicate(Root<GuessNumberGame> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (isShow != null) {
                    list.add(criteriaBuilder.equal(root.get("isShow").as(Boolean.class), isShow));
                }
                if (gameId != null) {
                    list.add(criteriaBuilder.equal(root.get("gameId").as(Long.class), gameId));
                }
                list.add(criteriaBuilder.equal(root.get("isDelete").as(Boolean.class), false));
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);

        guessNumberGames.forEach(guessNumberGame -> {
            Set<GuessUnit> guessUnits = guessUnitDao.findGuessUnitsByGuessId(guessNumberGame.getId());
            guessNumberGame.setGuessUnits(guessUnits);
        });

        return guessNumberGames;
    }

    @Override
    @Transactional
    public GuessRecord guessNumberByAccount(GuessRecord guessRecord) {
        logger.info("guessNumberByAccount" + guessRecord.getAccountNo());
        //check 游戏是否有效 是否是进行中
        GuessNumberGame guessNumberGame = guessNumberGameDao.findByIdAndIsDeleteIsFalse(guessRecord.getGuessNumberId());
        if (guessNumberGame == null) {
            throw new AppException(ErrorCode.EVENT_NOT_EXIST);
        } else {
            if (guessNumberGame.getGameStartTime() == null
                    || guessNumberGame.getGameStartTime().compareTo(new Date()) >= 0) {
                throw new AppException(ErrorCode.EVENT_HAS_NOT_START);

            } else if (guessNumberGame.getGameEndTime() != null && guessNumberGame.getGameEndTime().compareTo(new Date()) < 0) {

                throw new AppException(ErrorCode.EVENT_IS_OVER);
            }
            // 达到百万上限则报活动已结束。
            SysConfig totalConfig = sysConfigDao.findSysConfigByName(WalletConstants.GUESS_PERSON_LIMIT_KEY);
            String totalSize = totalConfig.getValue();
            Long numberSize = redisTemplate.opsForHash().size(RedisConstants.REDIS_NAME_GUESS_NUMBER + guessRecord.getGuessNumberId());
            if (numberSize.compareTo(new Long(totalSize)) >= 0) {
                throw new AppException(ErrorCode.EVENT_IS_OVER);
            }
        }
        //redis 用户参与的竞猜数字集合
        //check 用户是否参与本次活动
        Object accountNo = redisTemplate.opsForHash().get(RedisConstants.REDIS_NAME_GUESS_NUMBER + guessRecord.getGuessNumberId(), guessRecord.getAccountNo());
        if (accountNo != null) {
            //用户已参与
            throw new AppException(ErrorCode.CHECK_ACCOUNT_JOIN_GAME);
        }
//        //check number已存在
//        Boolean numberType = redisTemplate.opsForSet().isMember(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_UNAVAIL + guessNumberGame.getId(), guessRecord.getDrawNumber());
//        if (numberType) {
//            //数字已存在
//            throw new AppException(ErrorCode.CHECK_GAME_NUMBER_EXIST);
//        }
        guessRecord.setGuessNumberId(guessRecord.getGuessNumberId());
        guessRecord.setDrawTime(new Date());
        //add redis set 参与用户所有的竞猜答案
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations)
                    throws DataAccessException {
                operations.multi();
//                operations.opsForSet().move(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame.getId(),
//                        guessRecord.getDrawNumber(), RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_UNAVAIL + guessNumberGame.getId());//移到集合元素
                //add redis
                operations.opsForHash().put(RedisConstants.REDIS_NAME_GUESS_NUMBER + guessRecord.getGuessNumberId(), guessRecord.getAccountNo(), guessRecord.getDrawNumber());
//                operations.expire(RedisConstants.REDIS_NAME_GUESS_NUMBER + guessRecord.getGuessNumberId(), 30, TimeUnit.DAYS);
//                operations.expire(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_UNAVAIL + guessNumberGame.getId(), 30, TimeUnit.DAYS);
                operations.exec();
                return null;

            }
        });

        return guessRecordDao.save(guessRecord);

    }

//    @Override
//    public Map getPauseNumber() {
//        //机选
//        Map map = new HashMap();
//        Object number = redisTemplate.opsForSet().randomMember(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL);
//        map.put("result", number);
//        return map;
//    }

    @Override
    public GameJson getAvailGameInfo(String accountNo, Long id) {
        GameJson gameJson = new GameJson();
        GuessNumberGame guessNumberGame = guessNumberGameDao.findGuessNumberGameAvail(id);
        if (guessNumberGame != null) {
            GuessRecord guessRecord = guessRecordDao.findGuessRecordByAccountNoAndGuessNumberId(accountNo, guessNumberGame.getId());
            if (guessRecord != null) {
                //已参与
                gameJson.setType(WalletConstants.JOIN_GAME);
                gameJson.setGuessNumberGame(guessNumberGame);
                gameJson.setGuessRecord(guessRecord);
            } else {
                // 达到百万上限则报活动已结束。
                SysConfig totalConfig = sysConfigDao.findSysConfigByName(WalletConstants.GUESS_PERSON_LIMIT_KEY);
                String totalSize = totalConfig.getValue();
                Long numberSize = redisTemplate.opsForHash().size(RedisConstants.REDIS_NAME_GUESS_NUMBER + guessNumberGame.getId());
                if (numberSize.compareTo(new Long(totalSize)) >= 0) {
                    //没有有效竞猜活动
                    gameJson.setType(WalletConstants.NOT_AWARD);
                } else {
                    //未参与
                    gameJson.setType(WalletConstants.NOT_JOIN_GAME);
                    gameJson.setGuessNumberGame(guessNumberGame);
                }
            }
        } else {
            //没有有效竞猜活动
            gameJson.setType(WalletConstants.NOT_AWARD);
        }

        return gameJson;
    }

    @Override
    public List<GuessNumberGame> getGuessNumberGamePerson(String accountNo, Long gameId) {
        List<GuessNumberGame> guessNumberGames = guessNumberGameDao.findByGameIdAndIsDeleteIsFalseOrderByBeginBlockDesc(gameId);
        for (int i = 0; i < guessNumberGames.size(); i++) {
            GuessNumberGame guessNumberGame = guessNumberGames.get(i);
            GuessRecord guessRecord = guessRecordDao.findGuessRecordByAccountNoAndGuessNumberId(accountNo, guessNumberGame.getId());
            Date now = new Date();
            Date luckTime = guessNumberGame.getLuckTime();

            if (guessRecord == null) {
                // 未参与
                guessNumberGame.setType(WalletConstants.NOT_JOIN_GAME);
            } else if (luckTime == null || (luckTime != null && now.compareTo(luckTime) < 0)) {
                // 未开奖
                guessNumberGame.setType(WalletConstants.NOT_AWARD);
            } else if (luckTime != null) {
                // 已开奖
                guessNumberGame.setType(WalletConstants.JOIN_GAME);
                guessNumberGame.setLevel(guessRecord.getDrawLevel() == null ? WalletConstants.AWARD_LEVEL_No : guessRecord.getDrawLevel());
            }
            guessNumberGame.setAccountNo(accountNo);
        }
        return guessNumberGames;
    }

    @Override
    public BaseWinner getWinnerList(Long guessNumberId, String accountNo) {
        GuessNumberGame guessNumberGame = guessNumberGameDao.findByIdAndIsDeleteIsFalse(guessNumberId);
        if (guessNumberGame == null) {
            throw new AppException(ErrorCode.EVENT_NOT_EXIST);
        }
        Long gameId = guessNumberGame.getGameId();
        if (gameId == null) {
            throw new AppException(ErrorCode.EVENT_NOT_EXIST);
        }
        BaseWinner baseWinner = new BaseWinner();
        Set<GuessUnit> guessUnitSet = guessUnitDao.findGuessUnitsByGuessId(guessNumberId);
        baseWinner.setGuessUnitSet(guessUnitSet);

        List<GuessAward> guessAwards = guessAwardDao.findByGuessId(guessNumberId);
        baseWinner.setGuessAwards(guessAwards);
        baseWinner.setZhName(guessNumberGame.getZhName());
        baseWinner.setKoName(guessNumberGame.getKoName());
        baseWinner.setEnName(guessNumberGame.getEnName());
        baseWinner.setLuckTime(guessNumberGame.getLuckTime());
        baseWinner.setJoinNumber(guessNumberGame.getJoinNumber());
        baseWinner.setLuckNumber(guessNumberGame.getLuckNumber());

        if (accountNo == null) {
            return baseWinner;
        }
        baseWinner.setHeader(accountDao.findByAccountNo(accountNo).getHeader());
        GuessRecord guessRecord = guessRecordDao.findGuessRecordByAccountNoAndGuessNumberId(accountNo, guessNumberId);
        if (guessRecord != null) {
            baseWinner.setDrawLevel(guessRecord.getDrawLevel());
            baseWinner.setDrawTime(guessRecord.getDrawTime());
            baseWinner.setDrawNumber(guessRecord.getDrawNumber());
        }
        return baseWinner;
    }

    @Override
    public BaseWinner getRecentlyWinnerList(Long gameId, String accountNo) {
        List<GuessNumberGame> guessNumberGames = guessNumberGameDao.findByGameIdAndIsDeleteIsFalseOrderByGameEndTimeDesc(gameId);
        if (guessNumberGames == null || guessNumberGames.size() == 0) {
            throw new AppException(ErrorCode.EVENT_NOT_EXIST);
        }

        GuessNumberGame recentlyGuessNumberGame = null;
        for (GuessNumberGame guessNumberGame : guessNumberGames) {
            if (guessNumberGame.getLuckNumber() != null && guessNumberGame.getLuckTime() != null
                    && DateUtil.dateCompare(guessNumberGame.getGameEndTime(), new Date())) {
                recentlyGuessNumberGame = guessNumberGame;
                break;
            }
        }
        BaseWinner baseWinner = new BaseWinner();
        if (recentlyGuessNumberGame == null) {
            return baseWinner;
        }
        return getWinnerList(recentlyGuessNumberGame.getId(), accountNo);

    }

    @Override
    public Map getWinnerListByAdmin(Integer page, Integer size, Long guessNumberId, Long unit) {
        GuessNumberGame guessNumberGame = guessNumberGameDao.findByIdAndIsDeleteIsFalse(guessNumberId);
        if (guessNumberGame == null) {
            throw new AppException(ErrorCode.EVENT_NOT_EXIST);
        }
        String luckNumber = guessNumberGame.getLuckNumber();

        List<WinnerInfo> winnerInfoList = new ArrayList<WinnerInfo>();
        Map map = new HashMap();
        Set<Object> range = redisTemplate.opsForZSet().range("guessGameRank" + guessNumberId, 0, 1);
        if (range == null || range.size() == 0) {
            if (unit == null) {
                Set<GuessUnit> guessUnits = guessNumberGame.getGuessUnits();
                for (GuessUnit guessUnit : guessUnits) {
                    Long guessId = guessUnit.getGuessId();
                    List<GuessRecord> guessRecordList = guessRecordDao.findByGuessNumberIdAndDrawLevelIsNotNullOrderByDrawLevel(guessId);
                    for (GuessRecord guessRecord : guessRecordList) {
                        WinnerInfo winnerInfo = new WinnerInfo();
                        winnerInfo.setLuckNumber(luckNumber);
                        winnerInfo.setDrawLevel(guessRecord.getDrawLevel());
                        winnerInfo.setAccountNo(guessRecord.getAccountNo());
                        winnerInfo.setDrawNumber(guessRecord.getDrawNumber());

                        winnerInfo.setUnit(guessUnit.getUnit());
                        winnerInfo.setSumAmount(guessUnit.getAmount());
                        redisTemplate.opsForZSet().add("guessGameRank" + guessNumberId, winnerInfo, guessRecord.getDrawLevel());
                    }
                }
            } else {
                List<GuessRecord> guessRecordList = guessRecordDao.findByGuessNumberIdAndDrawLevelIsNotNullOrderByDrawLevel(guessNumberId);

                for (GuessRecord guessRecord : guessRecordList) {
                    WinnerInfo winnerInfo = new WinnerInfo();
                    if (guessRecord == null) {
                        continue;
                    }
                    GuessAward guessAward = guessAwardDao.findByGuessIdAndUnit(guessRecord.getGuessNumberId(), unit);
                    if (null == guessAward) {
                        continue;
                    }
                    if (guessRecord.getDrawLevel().equals(WalletConstants.AWARD_LEVEL_SPECIAL)) {
                        winnerInfo.setSumAmount(guessAward.getSpecialAward() == null ? BigDecimal.valueOf(0) : guessAward.getSpecialAward());
                    } else if (guessRecord.getDrawLevel().equals(WalletConstants.AWARD_LEVEL_ONE)) {
                        winnerInfo.setSumAmount(guessAward.getFristAward() == null ? BigDecimal.valueOf(0) : guessAward.getFristAward());
                    } else if (guessRecord.getDrawLevel().equals(WalletConstants.AWARD_LEVEL_TWO)) {
                        winnerInfo.setSumAmount(guessAward.getSecondAward() == null ? BigDecimal.valueOf(0) : guessAward.getSecondAward());
                    } else if (guessRecord.getDrawLevel().equals(WalletConstants.AWARD_LEVEL_THREE)) {
                        winnerInfo.setSumAmount(guessAward.getThirdAward() == null ? BigDecimal.valueOf(0) : guessAward.getThirdAward());
                    } else {
                        winnerInfo.setSumAmount(guessAward.getFourthAward() == null ? BigDecimal.valueOf(0) : guessAward.getFourthAward());
                    }

                    winnerInfo.setDrawLevel(guessRecord.getDrawLevel());
                    winnerInfo.setAccountNo(guessRecord.getAccountNo());
                    winnerInfo.setDrawNumber(guessRecord.getDrawNumber());
                    winnerInfo.setUnit(unit);
                    redisTemplate.opsForZSet().add("guessGameRank" + guessNumberId, winnerInfo, guessRecord.getDrawLevel());
                }
            }
        } else {
            Long totalElements = redisTemplate.opsForZSet().zCard("guessGameRank" + guessNumberId);
            //从redis中取出分页返回
            Integer start = page * size;
            Integer end = start + size - 1;
            Set<Object> winnerInfos = redisTemplate.opsForZSet().range("guessGameRank" + guessNumberId, start, end);
            for (Object winnerInfo : winnerInfos) {
                winnerInfoList.add((WinnerInfo) winnerInfo);
            }
            map.put("winnerInfoList", winnerInfoList);
            map.put("totalElements", totalElements);
        }
        return map;
    }


    @Override
    public void runningLuck(Long guessId) {
        GuessNumberGame guessNumberGame = guessNumberGameDao.findByIdAndIsDeleteIsFalse(guessId);

        Set<GuessUnit> guessUnits = guessUnitDao.findGuessUnitsByGuessId(guessId);
        if (guessUnits.size() == 0) {
            //该竞猜游戏币种不存在
            throw new AppException(ErrorCode.GUESS_NOT_EXIST);
        }
        if (guessNumberGame == null) {
            //该竞猜游戏不存在
            throw new AppException(ErrorCode.GUESS_NOT_EXIST);
        }
        Games games = gamesDao.findOne(guessNumberGame.getGameId());
        if (games == null) {
            //该游戏已不存在
            throw new AppException(ErrorCode.GAME_NOT_EXIST);
        }
        Long gameId = guessNumberGame.getId();
        //获取所有参与该竞猜的记录
        Map numbers = redisTemplate.opsForHash().entries(RedisConstants.REDIS_NAME_GUESS_NUMBER + gameId);
        logger.info("redis 参与竞猜记录 setNumbers:" + numbers);
//        List<GuessRecord> guessRecordSpecial = new ArrayList<>();
//        List<GuessRecord> guessRecordOne = new ArrayList<>();
//        List<GuessRecord> guessRecordTwo = new ArrayList<>();
//        List<GuessRecord> guessRecordThree = new ArrayList<>();
//        List<GuessRecord> guessRecordFourth = new ArrayList<>();
//        Map<String, List<GuessRecord>> guessRecordMap = new HashMap<>();

        //特等奖中奖个数
        Integer specialNumber = 0;
        //一等奖中奖个数
        Integer firstNumber = 0;
        //二等奖中奖个数
        Integer secondNumber = 0;
        //三等奖中奖个数
        Integer thirdNumber = 0;
        //三等奖中奖个数
        Integer fourthNumber = 0;

        String luckNumber = guessNumberGame.getLuckNumber();
        List luck = Arrays.asList(luckNumber.split(""));
        Set accountNoSet = numbers.keySet();
        StringBuffer sbSp = new StringBuffer();
        StringBuffer sbOne = new StringBuffer();
        StringBuffer sbTwo = new StringBuffer();
        StringBuffer sbThree = new StringBuffer();
        StringBuffer sbFour = new StringBuffer();
        for (Object accountNoObj : accountNoSet) {
            String accountNo = (String) accountNoObj;
            String number = (String) numbers.get(accountNoObj);
            List numberList = Arrays.asList(number.split(""));
            java.util.Collection<Object> collection = CollectionUtils.intersection(numberList, luck);
            if (collection.size() == 0) {
                continue;
            }
            GuessRecord guessRecord = guessRecordDao.findGuessRecordByAccountNoAndGuessNumberId(accountNo, gameId);
            if(guessRecord!=null) {

                if (collection.size() == 5) {
                    //特等奖
                    guessRecord.setDrawLevel(WalletConstants.AWARD_LEVEL_SPECIAL);
//                    guessRecordSpecial.add(guessRecord);
                    sbSp.append(accountNo+",");
                    specialNumber++;
                } else if (collection.size() == 4) {
                    //一等奖
                    guessRecord.setDrawLevel(WalletConstants.AWARD_LEVEL_ONE);
                    sbOne.append(accountNo+",");
                    firstNumber++;

                } else if (collection.size() == 3) {
                    //二等奖
                    guessRecord.setDrawLevel(WalletConstants.AWARD_LEVEL_TWO);
                    sbTwo.append(accountNo+",");
                    secondNumber++;

                } else if (collection.size() == 2) {
                    //三等奖
                    guessRecord.setDrawLevel(WalletConstants.AWARD_LEVEL_THREE);
                    sbThree.append(accountNo+",");
                    thirdNumber++;

                } else if (collection.size() == 1) {
                    guessRecord.setDrawLevel(WalletConstants.AWARD_LEVEL_FOURTH);
                    sbFour.append(accountNo+",");
                    fourthNumber++;
                }
                guessRecordDao.save(guessRecord);
            }
        }
        Map<String,String> awards = new HashMap<>();
        awards.put(WalletConstants.AWARD_LEVEL_SPECIAL+"",sbSp.toString());
        awards.put(WalletConstants.AWARD_LEVEL_ONE+"",sbOne.toString());
        awards.put(WalletConstants.AWARD_LEVEL_TWO+"",sbTwo.toString());
        awards.put(WalletConstants.AWARD_LEVEL_THREE+"",sbThree.toString());
        awards.put(WalletConstants.AWARD_LEVEL_FOURTH+"",sbFour.toString());
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations)
                    throws DataAccessException {
                operations.multi();
                operations.opsForHash().putAll(RedisConstants.REDIS_NAME_GUESS_NUMBER_AWARD + gameId, awards);
//                operations.opsForSet().unionAndStore(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL,
//                        RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame1.getId(),
//                        RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame1.getId());
//                operations.expire(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame1.getId(), 30, TimeUnit.DAYS);
                operations.exec();
                return null;

            }
        });
//        guessRecordMap.put(WalletConstants.AWARD_LEVEL_SPECIAL.toString(), guessRecordSpecial);
//        guessRecordMap.put(WalletConstants.AWARD_LEVEL_ONE.toString(), guessRecordOne);
//        guessRecordMap.put(WalletConstants.AWARD_LEVEL_TWO.toString(), guessRecordTwo);
//        guessRecordMap.put(WalletConstants.AWARD_LEVEL_THREE.toString(), guessRecordThree);
//        guessRecordMap.put(WalletConstants.AWARD_LEVEL_FOURTH.toString(), guessRecordFourth);


        logger.info("特等奖中奖个数 specialNumber:" + specialNumber);
        logger.info("一等奖中奖个数 firstNumber:" + firstNumber);
        logger.info("二等奖中奖个数 secondNumber:" + secondNumber);
        logger.info("三等奖中奖个数 thirdNumber:" + thirdNumber);
        logger.info("四等奖中奖个数 thirdNumber:" + fourthNumber);
        guessNumberGame.setJoinNumber(numbers.size());
        //更新活动详情
        guessNumberGameDao.save(guessNumberGame);

        //循环币种
        for (Iterator<GuessUnit> it = guessUnits.iterator(); it.hasNext(); ) {
            GuessUnit guessUnit = it.next();

            //计算各等级中奖金额
            GuessAward guessAward = new GuessAward();
            guessAward.setGuessId(guessUnit.getGuessId());
            guessAward.setUnit(guessUnit.getUnit());
            BigDecimal amountTotal = guessUnit.getAmount();

            guessAward.setSpecialNumber(specialNumber);
            guessAward.setFristNumber(firstNumber);
            guessAward.setSecondNumber(secondNumber);
            guessAward.setThirdNumber(thirdNumber);
            guessAward.setFourthNumber(fourthNumber);

            BigDecimal specialAmount = specialNumber.equals(0) ? new BigDecimal(0) : new BigDecimal("0.008").multiply(amountTotal).divide(new BigDecimal(specialNumber), 6, BigDecimal.ROUND_FLOOR);
            BigDecimal firstAmount = firstNumber.equals(0) ? new BigDecimal(0) : new BigDecimal("0.135").multiply(amountTotal).divide(new BigDecimal(firstNumber), 6, BigDecimal.ROUND_FLOOR);
            BigDecimal secondAmount = secondNumber.equals(0) ? new BigDecimal(0) : new BigDecimal("0.41").multiply(amountTotal).divide(new BigDecimal(secondNumber), 6, BigDecimal.ROUND_FLOOR);
            BigDecimal thirdAmount = thirdNumber.equals(0) ? new BigDecimal(0) : new BigDecimal("0.353").multiply(amountTotal).divide(new BigDecimal(thirdNumber), 6, BigDecimal.ROUND_FLOOR);
            BigDecimal fourthAmount = fourthNumber.equals(0) ? new BigDecimal(0) : new BigDecimal("0.094").multiply(amountTotal).divide(new BigDecimal(fourthNumber), 6, BigDecimal.ROUND_FLOOR);
            logger.info("特等奖中奖金额 specialAmount:" + specialAmount);
            logger.info("一等奖中奖金额 firstAmount:" + firstAmount);
            logger.info("二等奖中奖金额 secondAmount:" + secondAmount);
            logger.info("三等奖中奖金额 thirdAmount:" + thirdAmount);
            logger.info("四等奖中奖金额 fourthAmount:" + fourthAmount);
            guessAward.setSpecialAward(specialAmount);
            guessAward.setFristAward(firstAmount);
            guessAward.setSecondAward(secondAmount);
            guessAward.setThirdAward(thirdAmount);
            guessAward.setFourthAward(fourthAmount);
            //保存竞猜币种对应的中奖等级和金额
            guessAwardDao.save(guessAward);
            /*
            for (int j = 0; j < guessRecordMap.size(); j++) {
                List<GuessRecord> guessRecords = guessRecordMap.get(j + "");

                Integer awardRank = new Integer(j);
                BigDecimal amount = new BigDecimal(0);
                if (WalletConstants.AWARD_LEVEL_SPECIAL.equals(awardRank)) {
                    amount = specialAmount;
                } else if (WalletConstants.AWARD_LEVEL_ONE.equals(awardRank)) {
                    amount = firstAmount;
                } else if (WalletConstants.AWARD_LEVEL_TWO.equals(awardRank)) {
                    amount = secondAmount;
                } else if (WalletConstants.AWARD_LEVEL_THREE.equals(awardRank)) {
                    amount = thirdAmount;
                } else if (WalletConstants.AWARD_LEVEL_FOURTH.equals(awardRank)) {
                    amount = fourthAmount;
                }
                if (guessRecords != null) {
                    for (int k = 0; k < guessRecords.size(); k++) {
                        GuessRecord guessRecord = guessRecords.get(k);
                        ExchangeLog exchangeLog = new ExchangeLog();
                        exchangeLog.setStatus(WalletConstants.CONFIRMED);
                        exchangeLog.setAccountNo(guessRecord.getAccountNo());
                        exchangeLog.setUnit(guessAward.getUnit());
                        exchangeLog.setAmount(amount);
                        exchangeLog.setEventApplyId(guessRecord.getGuessNumberId());
                        exchangeLog.setType(WalletConstants.GAME_AWARD);
                        exchangeLogDao.save(exchangeLog);
                        logger.info("中奖金额记录：exchangeLog" + exchangeLog.toString());
                        AccountBalance accountBalance = accountBalanceDao.findByAccountNoAndUnit(guessRecord.getAccountNo(), guessAward.getUnit());
                        if (accountBalance != null) {
                            accountBalance.setAmount(accountBalance.getAmount().add(amount));
                        } else {
                            accountBalance = new AccountBalance();
                            accountBalance.setAmount(amount);
                            accountBalance.setUnit(guessAward.getUnit());
                            accountBalance.setAccountNo(guessRecord.getAccountNo());
                        }
                        accountBalanceDao.save(accountBalance);
                        logger.info("该用户余额：accountBalance" + accountBalance.toString());
                    }
                }
            }**/
        }
        redisTemplate.delete(RedisConstants.REDIS_NAME_GUESS_NUMBER + guessNumberGame.getId());
//        redisTemplate.delete(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_UNAVAIL + guessNumberGame.getId());
//        redisTemplate.delete(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame.getId());
    }

    private void checkGuess(GuessNumberGame guessNumberGame) {
        GuessNumberGame guessNumberGameUpd = guessNumberGameDao.findGuessNumberGameByZhNameAndIsDeleteFalseAndGameId(guessNumberGame.getZhName(), guessNumberGame.getGameId());
        if (guessNumberGameUpd != null) {
            //名称已存在
            throw new AppException(ErrorCode.CHECK_GAME_EXIST);
        } else if (guessNumberGame.getZhName().length() > 40) {
            //名称超过40个汉字
            throw new AppException(ErrorCode.CHECK_GUESS_NUMBER_NAME_LENGTH);
        } else if (guessNumberGame.getBeginBlock() < 0 || guessNumberGame.getLuckBlock() < 0 || guessNumberGame.getEndBlock() < 0 || !(guessNumberGame.getBeginBlock() < guessNumberGame.getEndBlock() && guessNumberGame.getEndBlock() < guessNumberGame.getLuckBlock())) {
            //结束时间>开始时间
            throw new AppException(ErrorCode.DATE_RANGE_ERROR);
        } /*else if (guessNumberGame.getTotalAmount().compareTo(new BigDecimal(0)) <= 0) {
            //总金额小于等于0
            throw new AppException(ErrorCode.CHECK_GUESS_NUMBER_AMOUNT);
        }*/ else if (guessNumberGame.getGuessUnits().size() == 0) {
            throw new AppException(ErrorCode.OPERATION_FAIL);
        } else {
            //check unit 不能重复
            checkUnit(guessNumberGame);
            //check block 是否重复
            GuessNumberGame guessNumberGame1 = guessNumberGameDao.findGuessNumberGameByBeginBlock(guessNumberGame.getBeginBlock());
            if (guessNumberGame1 != null) {
                throw new AppException(ErrorCode.OPERATION_FAIL);
            }
            GuessNumberGame guessNumberGame2 = guessNumberGameDao.findGuessNumberGameByEndBlock(guessNumberGame.getEndBlock());
            if (guessNumberGame2 != null) {
                throw new AppException(ErrorCode.OPERATION_FAIL);
            }
            GuessNumberGame guessNumberGame3 = guessNumberGameDao.findGuessNumberGameByLuckBlock(guessNumberGame.getLuckBlock());

            if (guessNumberGame3 != null) {
                throw new AppException(ErrorCode.OPERATION_FAIL);
            }
            //已存在有效活动
            Long count = guessNumberGameDao.getGuessNumberGameActivate(guessNumberGame.getBeginBlock(), guessNumberGame.getEndBlock(), guessNumberGame.getGameId());
            if (count != null && count > 1) {
                throw new AppException(ErrorCode.EVENT_OVERLAP);
            }
        }

    }

    private void checkUnit(GuessNumberGame guessNumberGame) {
        //check unit 不能重复
        Set<GuessUnit> guessUnits = guessNumberGame.getGuessUnits();
        Map map = new HashMap();
        for (Iterator<GuessUnit> it = guessUnits.iterator(); it.hasNext(); ) {
            GuessUnit guessUnit = it.next();
            if (guessUnit.getAmount() == null || guessUnit.getUnit() == null || (guessUnit.getAmount() != null && guessUnit.getAmount().compareTo(new BigDecimal(0)) <= 0)) {
                throw new AppException(ErrorCode.OPERATION_FAIL);
            }
            if (map.containsKey(guessUnit.getUnit())) {
                throw new AppException(ErrorCode.CHECK_GAME_METHOD);
            }
            map.put(guessUnit.getUnit(), guessUnit);

        }
    }

    @Override
    public void testDummyLottery() {
        //添加redis set

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations)
                    throws DataAccessException {
                operations.multi();
                for (int i = 0; i < 1000; i++) {
                    String[] numbers = new String[1000];
                    for (int j = 0; j < 1000; j++) {
                        numbers[j] = NumberUtil.formatNumberToString(j + i * 1000, "000000");
                        System.out.print(numbers[j] + ",");
                        operations.opsForHash().put(RedisConstants.REDIS_NAME_GUESS_NUMBER + "TEST", numbers[j], numbers[j]);
                    }


                }
//                operations.opsForSet().unionAndStore(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL,
//                        RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame1.getId(),
//                        RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame1.getId());
//                operations.expire(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame1.getId(), 30, TimeUnit.DAYS);
                operations.exec();
                return null;

            }
        });
        // load
//获取所有参与该竞猜的记录
        Map numbers = redisTemplate.opsForHash().entries(RedisConstants.REDIS_NAME_GUESS_NUMBER + "TEST");
        logger.info("redis 参与竞猜记录 setNumbers:" + numbers);
        //特等奖中奖个数
        Integer specialNumber = 0;
        //一等奖中奖个数
        Integer firstNumber = 0;
        //二等奖中奖个数
        Integer secondNumber = 0;
        //三等奖中奖个数
        Integer thirdNumber = 0;
        //三等奖中奖个数
        Integer fourthNumber = 0;

        String luckNumber = "12345";
        List luck = Arrays.asList(luckNumber.split(""));
        Set accountNoSet = numbers.keySet();
        StringBuffer sbSp = new StringBuffer();
        StringBuffer sbOne = new StringBuffer();
        StringBuffer sbTwo = new StringBuffer();
        StringBuffer sbThree = new StringBuffer();
        StringBuffer sbFour = new StringBuffer();
        for (Object accountNoObj : accountNoSet) {
            String accountNo = (String) accountNoObj;
            String number = (String) numbers.get(accountNoObj);
            List numberList = Arrays.asList(number.split(""));
            java.util.Collection<Object> collection = CollectionUtils.intersection(numberList, luck);
            if (collection.size() == 0) {
                continue;
            }
            GuessRecord guessRecord = new GuessRecord();
            guessRecord.setAccountNo(accountNo);
            guessRecord.setDrawNumber(accountNo);
//            GuessRecord guessRecord = guessRecordDao.findGuessRecordByAccountNoAndGuessNumberId(accountNo, guessNumberGame.getId());

                if(guessRecord!=null) {

                    if (collection.size() == 5) {
                        //特等奖
                        guessRecord.setDrawLevel(WalletConstants.AWARD_LEVEL_SPECIAL);
//                    guessRecordSpecial.add(guessRecord);
                        sbSp.append(accountNo+",");
                        specialNumber++;
                    } else if (collection.size() == 4) {
                        //一等奖
                        guessRecord.setDrawLevel(WalletConstants.AWARD_LEVEL_ONE);
                        sbOne.append(accountNo+",");
                        firstNumber++;

                    } else if (collection.size() == 3) {
                        //二等奖
                        guessRecord.setDrawLevel(WalletConstants.AWARD_LEVEL_TWO);
                        sbTwo.append(accountNo+",");
                        secondNumber++;

                    } else if (collection.size() == 2) {
                        //三等奖
                        guessRecord.setDrawLevel(WalletConstants.AWARD_LEVEL_THREE);
                        sbThree.append(accountNo+",");
                        thirdNumber++;

                    } else if (collection.size() == 1) {
                        guessRecord.setDrawLevel(WalletConstants.AWARD_LEVEL_FOURTH);
                        sbFour.append(accountNo+",");
                        fourthNumber++;
                    }
                    guessRecordDao.save(guessRecord);
                }
            }
            Map<String,String> awards = new HashMap<>();
            awards.put(WalletConstants.AWARD_LEVEL_SPECIAL+"",sbSp.toString());
            awards.put(WalletConstants.AWARD_LEVEL_ONE+"",sbOne.toString());
            awards.put(WalletConstants.AWARD_LEVEL_TWO+"",sbTwo.toString());
            awards.put(WalletConstants.AWARD_LEVEL_THREE+"",sbThree.toString());
            awards.put(WalletConstants.AWARD_LEVEL_FOURTH+"",sbFour.toString());
            redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations)
                        throws DataAccessException {
                    operations.multi();
                    operations.opsForHash().putAll(RedisConstants.REDIS_NAME_GUESS_NUMBER_AWARD + "TEST", awards);
//                operations.opsForSet().unionAndStore(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL,
//                        RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame1.getId(),
//                        RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame1.getId());
//                operations.expire(RedisConstants.REDIS_NAME_GUESS_NUMBER + RedisConstants.REDIS_NAME_GUESS_NUMBER_AVAIL + guessNumberGame1.getId(), 30, TimeUnit.DAYS);
                    operations.exec();
                    return null;

                }
            });
//        guessRecordMap.put(WalletConstants.AWARD_LEVEL_SPECIAL.toString(), guessRecordSpecial);
//        guessRecordMap.put(WalletConstants.AWARD_LEVEL_ONE.toString(), guessRecordOne);
//        guessRecordMap.put(WalletConstants.AWARD_LEVEL_TWO.toString(), guessRecordTwo);
//        guessRecordMap.put(WalletConstants.AWARD_LEVEL_THREE.toString(), guessRecordThree);
//        guessRecordMap.put(WalletConstants.AWARD_LEVEL_FOURTH.toString(), guessRecordFourth);


        logger.info("特等奖中奖个数 specialNumber:" + specialNumber);
        logger.info("一等奖中奖个数 firstNumber:" + firstNumber);
        logger.info("二等奖中奖个数 secondNumber:" + secondNumber);
        logger.info("三等奖中奖个数 thirdNumber:" + thirdNumber);
        logger.info("四等奖中奖个数 thirdNumber:" + fourthNumber);
//        guessNumberGame.setJoinNumber(numbers.size());
//        //更新活动详情
//        guessNumberGameDao.save(guessNumberGame);

        for (int i = 1; i < 6; i++) {
            GuessUnit guessUnit = new GuessUnit();
            guessUnit.setGuessId(1L);
            guessUnit.setUnit(new Long(i));
            guessUnit.setAmount(new BigDecimal(i * 10000));
            GuessAward guessAward = new GuessAward();
            guessAward.setGuessId(guessUnit.getGuessId());
            guessAward.setUnit(guessUnit.getUnit());
            BigDecimal amountTotal = guessUnit.getAmount();

            guessAward.setSpecialNumber(specialNumber);
            guessAward.setFristNumber(firstNumber);
            guessAward.setSecondNumber(secondNumber);
            guessAward.setThirdNumber(thirdNumber);
            guessAward.setFourthNumber(fourthNumber);

//            guessNumberGame.setJoinNumber(numbers.size());

            BigDecimal specialAmount = specialNumber == 0 ? new BigDecimal(0) : new BigDecimal("0.008").multiply(amountTotal).divide(new BigDecimal(specialNumber), 6, BigDecimal.ROUND_FLOOR);
            BigDecimal firstAmount = firstNumber == 0 ? new BigDecimal(0) : new BigDecimal("0.135").multiply(amountTotal).divide(new BigDecimal(firstNumber), 6, BigDecimal.ROUND_FLOOR);
            BigDecimal secondAmount = secondNumber == 0 ? new BigDecimal(0) : new BigDecimal("0.41").multiply(amountTotal).divide(new BigDecimal(secondNumber), 6, BigDecimal.ROUND_FLOOR);
            BigDecimal thirdAmount = thirdNumber == 0 ? new BigDecimal(0) : new BigDecimal("0.353").multiply(amountTotal).divide(new BigDecimal(thirdNumber), 6, BigDecimal.ROUND_FLOOR);
            BigDecimal fourthAmount = fourthNumber == 0 ? new BigDecimal(0) : new BigDecimal("0.094").multiply(amountTotal).divide(new BigDecimal(fourthNumber), 6, BigDecimal.ROUND_FLOOR);
            logger.info("特等奖中奖金额 specialAmount:" + specialAmount);
            logger.info("一等奖中奖金额 firstAmount:" + firstAmount);
            logger.info("二等奖中奖金额 secondAmount:" + secondAmount);
            logger.info("三等奖中奖金额 thirdAmount:" + thirdAmount);
            logger.info("四等奖中奖金额 fourthAmount:" + fourthAmount);
            guessAward.setSpecialAward(specialAmount);
            guessAward.setFristAward(firstAmount);
            guessAward.setSecondAward(secondAmount);
            guessAward.setThirdAward(thirdAmount);
            guessAward.setFourthAward(fourthAmount);
            //保存竞猜币种对应的中奖等级和金额
            guessAwardDao.save(guessAward);
            //更新活动详情
//            guessNumberGameDao.save(guessNumberGame);
//
//            for (int j = 0; j < guessRecordMap.size(); j++) {
//                List<GuessRecord> guessRecords = guessRecordMap.get(j + "");
//                Integer awardRank = new Integer(j);
//                BigDecimal amount = new BigDecimal(0);
//                if (WalletConstants.AWARD_LEVEL_SPECIAL.equals(awardRank)) {
//                    amount = specialAmount;
//                } else if (WalletConstants.AWARD_LEVEL_ONE.equals(awardRank)) {
//                    amount = firstAmount;
//                } else if (WalletConstants.AWARD_LEVEL_TWO.equals(awardRank)) {
//                    amount = secondAmount;
//                } else if (WalletConstants.AWARD_LEVEL_THREE.equals(awardRank)) {
//                    amount = thirdAmount;
//                } else if (WalletConstants.AWARD_LEVEL_FOURTH.equals(awardRank)) {
//                    amount = fourthAmount;
//                }
//                if (guessRecords != null) {
//                    for (int k = 0; k < guessRecords.size(); k++) {
//                        GuessRecord guessRecord = guessRecords.get(k);
//                        ExchangeLog exchangeLog = new ExchangeLog();
//                        exchangeLog.setStatus(WalletConstants.CONFIRMED);
//                        exchangeLog.setAccountNo(guessRecord.getAccountNo());
//                        exchangeLog.setUnit(guessAward.getUnit());
//                        exchangeLog.setAmount(amount);
//                        exchangeLog.setEventApplyId(guessRecord.getGuessNumberId());
//                        exchangeLog.setType(WalletConstants.GAME_AWARD);
//                        exchangeLogDao.save(exchangeLog);
////                        logger.info("中奖金额记录：exchangeLog" + exchangeLog.toString());
//                        AccountBalance accountBalance = accountBalanceDao.findByAccountNoAndUnit(guessRecord.getAccountNo(), guessAward.getUnit());
//                        if (accountBalance != null) {
//                            accountBalance.setAmount(accountBalance.getAmount().add(amount));
//                        } else {
//                            accountBalance = new AccountBalance();
//                            accountBalance.setAmount(amount);
//                            accountBalance.setUnit(guessAward.getUnit());
//                            accountBalance.setAccountNo(guessRecord.getAccountNo());
//                        }
//                        accountBalanceDao.save(accountBalance);
//                    }
//                }
//            }
        }
        redisTemplate.delete(RedisConstants.REDIS_NAME_GUESS_NUMBER + "TEST");
        redisTemplate.delete(RedisConstants.REDIS_NAME_GUESS_NUMBER_AWARD + "TEST");
    }

}
