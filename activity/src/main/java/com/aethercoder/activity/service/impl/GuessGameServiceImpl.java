package com.aethercoder.activity.service.impl;

import com.aethercoder.activity.batch.GambleAwardBatch;
import com.aethercoder.activity.batch.GambleClosedBatch;
import com.aethercoder.activity.batch.GambleFlowBatch;
import com.aethercoder.activity.batch.GambleRunBatch;
import com.aethercoder.activity.contants.ActivityContants;
import com.aethercoder.activity.dao.guessGamble.GuessGambleDao;
import com.aethercoder.activity.dao.guessGamble.JoinGambleDao;
import com.aethercoder.activity.entity.guessGamble.GambleRank;
import com.aethercoder.activity.entity.guessGamble.GuessGamble;
import com.aethercoder.activity.entity.guessGamble.JoinGamble;
import com.aethercoder.activity.entity.json.GambleResult;
import com.aethercoder.activity.service.GuessGambleService;
import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.utils.BeanUtils;
import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.AccountBalanceDao;
import com.aethercoder.core.dao.AccountDao;
import com.aethercoder.core.dao.ContractDao;
import com.aethercoder.core.dao.ExchangeLogDao;
import com.aethercoder.core.entity.event.AccountBalance;
import com.aethercoder.core.entity.event.ExchangeLog;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.AccountBalanceService;
import com.aethercoder.core.service.SysConfigService;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.BatchService;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.DateUtil;
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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hepengfei on 27/02/2018.
 */
@Service
public class GuessGameServiceImpl implements GuessGambleService {
    private static Logger logger = LoggerFactory.getLogger(GuessGameServiceImpl.class);
    @Autowired
    private GuessGambleDao guessGambleDao;

    @Autowired
    private LocaleMessageService localeMessageService;

    @Autowired
    private JoinGambleDao joinGambleDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private ExchangeLogDao exchangeLogDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private AccountBalanceDao accountBalanceDao;

    private String gambleTable = "t_guess_gamble";
    private String titleField = "title";
    private String option1Field = "option1";
    private String option2Field = "option2";
    private String contentField = "content";

    @Override
    @Transactional("transactionManagerSecondary")
    public GuessGamble saveGuessGamble(GuessGamble guessGamble) {
        //check 该游戏是开盘中 或者未开始
        if (guessGamble.getOpenTime().compareTo(new Date()) < 0) {
            guessGamble.setStatus(ActivityContants.GUESS_GAMBLE_STATUS_RUNNING);
        } else {
            guessGamble.setStatus(ActivityContants.GUESS_GAMBLE_STATUS_NOT_APPLY);
        }
        guessGamble.setIsMin(true);
        GuessGamble guessGambleL = guessGambleDao.save(guessGamble);
        //多语言
        //zh
        saveGuessGambleMessageZH(guessGamble);
        //en
        saveGuessGambleMessageEN(guessGamble);
        //ko
        saveGuessGambleMessageKO(guessGamble);
        if (ActivityContants.GUESS_GAMBLE_STATUS_RUNNING.equals(guessGambleL.getStatus())) {
            // 创建批处理 - 动态封盘
            batchService.createBatchTask("GuessGambleClosed", guessGamble.getCloseTime(), GambleClosedBatch.class.getName(), guessGamble.getClass().getSimpleName(), guessGamble.getId());
        } else if (ActivityContants.GUESS_GAMBLE_STATUS_NOT_APPLY.equals(guessGambleL.getStatus())) {
            // 创建批处理 - 动态开盘
            batchService.createBatchTask("GuessGambleRunning", guessGamble.getOpenTime(), GambleRunBatch.class.getName(), guessGamble.getClass().getSimpleName(), guessGambleL.getId());
        }

        return guessGambleL;
    }

    private void saveGuessGambleMessageZH(GuessGamble guessGamble) {
        if (guessGamble.getTitle() != null) {
            localeMessageService.saveMessage(gambleTable, titleField, guessGamble.getId() + "", ActivityContants.LANGANGE_ZH, guessGamble.getTitle());
        }
        if (guessGamble.getOption1() != null) {
            localeMessageService.saveMessage(gambleTable, option1Field, guessGamble.getId() + "", ActivityContants.LANGANGE_ZH, guessGamble.getOption1());
        }
        if (guessGamble.getOption2() != null) {
            localeMessageService.saveMessage(gambleTable, option2Field, guessGamble.getId() + "", ActivityContants.LANGANGE_ZH, guessGamble.getOption2());
        }
        if (guessGamble.getContent() != null) {
            localeMessageService.saveMessage(gambleTable, contentField, guessGamble.getId() + "", ActivityContants.LANGANGE_ZH, guessGamble.getContent());
        }
    }

    private void saveGuessGambleMessageEN(GuessGamble guessGamble) {
        if (guessGamble.getTitle_en() != null) {
            localeMessageService.saveMessage(gambleTable, titleField, guessGamble.getId() + "", ActivityContants.LANGANGE_EN, guessGamble.getTitle_en());
        }
        if (guessGamble.getOption1_en() != null) {
            localeMessageService.saveMessage(gambleTable, option1Field, guessGamble.getId() + "", ActivityContants.LANGANGE_EN, guessGamble.getOption1_en());
        }
        if (guessGamble.getOption2_en() != null) {
            localeMessageService.saveMessage(gambleTable, option2Field, guessGamble.getId() + "", ActivityContants.LANGANGE_EN, guessGamble.getOption2_en());
        }
        if (guessGamble.getContent_en() != null) {
            localeMessageService.saveMessage(gambleTable, contentField, guessGamble.getId() + "", ActivityContants.LANGANGE_EN, guessGamble.getContent_en());
        }
    }

    private void saveGuessGambleMessageKO(GuessGamble guessGamble) {
        if (guessGamble.getTitle_ko() != null) {
            localeMessageService.saveMessage(gambleTable, titleField, guessGamble.getId() + "", ActivityContants.LANGANGE_KO, guessGamble.getTitle_ko());
        }
        if (guessGamble.getOption1_ko() != null) {
            localeMessageService.saveMessage(gambleTable, option1Field, guessGamble.getId() + "", ActivityContants.LANGANGE_KO, guessGamble.getOption1_ko());
        }
        if (guessGamble.getOption2_ko() != null) {
            localeMessageService.saveMessage(gambleTable, option2Field, guessGamble.getId() + "", ActivityContants.LANGANGE_KO, guessGamble.getOption2_ko());
        }
        if (guessGamble.getContent_ko() != null) {
            localeMessageService.saveMessage(gambleTable, contentField, guessGamble.getId() + "", ActivityContants.LANGANGE_KO, guessGamble.getContent_ko());
        }
    }

    private void translateGuessGamble(GuessGamble guessGamble, String language, int type) {
        if (guessGamble.getTitle() != null) {
            String questionI18n = localeMessageService.getMessageByTableFieldId(gambleTable, titleField, guessGamble.getId() + "", type, language);
            guessGamble.setTitle(questionI18n);
        }

        if (guessGamble.getOption1() != null) {
            String option1 = localeMessageService.getMessageByTableFieldId(gambleTable, option1Field, guessGamble.getId() + "", type, language);
            guessGamble.setOption1(option1);
        }

        if (guessGamble.getOption2() != null) {
            String option2 = localeMessageService.getMessageByTableFieldId(gambleTable, option2Field, guessGamble.getId() + "", type, language);
            guessGamble.setOption2(option2);
        }

        if (guessGamble.getContent() != null) {
            String content = localeMessageService.getMessageByTableFieldId(gambleTable, contentField, guessGamble.getId() + "", type, language);
            guessGamble.setContent(content);
        }
    }

    private void translateGuessGambleEN(GuessGamble guessGamble, int type) {
        String questionI18n = localeMessageService.getMessageByTableFieldId(gambleTable, titleField, guessGamble.getId() + "", type, ActivityContants.LANGANGE_EN);
        guessGamble.setTitle_en(questionI18n);

        String option1 = localeMessageService.getMessageByTableFieldId(gambleTable, option1Field, guessGamble.getId() + "", type, ActivityContants.LANGANGE_EN);
        guessGamble.setOption1_en(option1);

        String option2 = localeMessageService.getMessageByTableFieldId(gambleTable, option2Field, guessGamble.getId() + "", type, ActivityContants.LANGANGE_EN);
        guessGamble.setOption2_en(option2);

        String content = localeMessageService.getMessageByTableFieldId(gambleTable, contentField, guessGamble.getId() + "", type, ActivityContants.LANGANGE_EN);
        guessGamble.setContent_en(content);
    }

    private void translateGuessGambleKO(GuessGamble guessGamble, int type) {
        String questionI18n = localeMessageService.getMessageByTableFieldId(gambleTable, titleField, guessGamble.getId() + "", type, ActivityContants.LANGANGE_KO);
        guessGamble.setTitle_ko(questionI18n);

        String option1 = localeMessageService.getMessageByTableFieldId(gambleTable, option1Field, guessGamble.getId() + "", type, ActivityContants.LANGANGE_KO);
        guessGamble.setOption1_ko(option1);

        String option2 = localeMessageService.getMessageByTableFieldId(gambleTable, option2Field, guessGamble.getId() + "", type, ActivityContants.LANGANGE_KO);
        guessGamble.setOption2_ko(option2);

        String content = localeMessageService.getMessageByTableFieldId(gambleTable, contentField, guessGamble.getId() + "", type, ActivityContants.LANGANGE_KO);
        guessGamble.setContent_ko(content);
    }


    @Override
    @Transactional("transactionManagerSecondary")
    public void updateGuessGamble(GuessGamble guessGamble) {
        GuessGamble guessGambleP = guessGambleDao.findOne(guessGamble.getId());
        //check 该游戏是已开奖
        if (guessGambleP != null && ActivityContants.GUESS_GAMBLE_STATUS_AWARD.equals(guessGambleP.getStatus())) {
            throw new AppException(ErrorCode.EVENT_NOT_UPDATE);
        }
        if (guessGamble.getCloseTime().compareTo(guessGambleP.getCloseTime()) != 0) {
            batchService.createBatchTask("GuessGambleClosed", guessGamble.getCloseTime(), GambleClosedBatch.class.getName(), guessGamble.getClass().getSimpleName(), guessGambleP.getId());
        }


        if (guessGamble.getOpenTime().compareTo(guessGambleP.getOpenTime()) != 0) {
            if (guessGamble.getOpenTime().compareTo(new Date()) > 0) {
                //更新成未开始状态 之前是已开盘状态
                batchService.deleteBatchTask("GuessGambleClosed", guessGamble.getClass().getSimpleName(), guessGambleP.getId());
                guessGamble.setStatus(ActivityContants.GUESS_GAMBLE_STATUS_NOT_APPLY);
                batchService.createBatchTask("GuessGambleRunning", guessGamble.getOpenTime(), GambleRunBatch.class.getName(), guessGamble.getClass().getSimpleName(), guessGambleP.getId());
            } else if (guessGamble.getOpenTime().compareTo(new Date()) <= 0) {
                //更新成已开盘状态 之前是未开始状态
                guessGamble.setStatus(ActivityContants.GUESS_GAMBLE_STATUS_RUNNING);
                batchService.deleteBatchTask("GuessGambleRunning", guessGamble.getClass().getSimpleName(), guessGambleP.getId());
                batchService.createBatchTask("GuessGambleClosed", guessGamble.getCloseTime(), GambleClosedBatch.class.getName(), guessGamble.getClass().getSimpleName(), guessGambleP.getId());
            }

        }
        //多语言更新
        //zh
        saveGuessGambleMessageZH(guessGamble);
        //en
        saveGuessGambleMessageEN(guessGamble);
        //ko
        saveGuessGambleMessageKO(guessGamble);

        BeanUtils.copyPropertiesWithoutNull(guessGamble,guessGambleP);

        guessGambleDao.save(guessGambleP);
    }

    @Override
    @Transactional("transactionManagerSecondary")
    public void deleteGuessGamble(Long id) {
        GuessGamble guessGambleP = guessGambleDao.findOne(id);
        //check 该游戏是已开奖
        if (guessGambleP != null && ActivityContants.GUESS_GAMBLE_STATUS_AWARD.equals(guessGambleP.getStatus())) {
            throw new AppException(ErrorCode.EVENT_NOT_UPDATE);
        }
        if (ActivityContants.GUESS_GAMBLE_STATUS_RUNNING.equals(guessGambleP.getStatus())) {
            // 更新封盘时间的批处理
            batchService.deleteBatchTask("GuessGambleRunning", guessGambleP.getClass().getSimpleName(), guessGambleP.getId());

        } else if (ActivityContants.GUESS_GAMBLE_STATUS_CLOSE.equals(guessGambleP.getStatus())) {
            batchService.deleteBatchTask("GuessGambleClosed", guessGambleP.getClass().getSimpleName(), guessGambleP.getId());
        }
        //删除i8n
        localeMessageService.deleteMessageByTableFieldId(gambleTable, titleField, id + "");
        localeMessageService.deleteMessageByTableFieldId(gambleTable, contentField, id + "");
        localeMessageService.deleteMessageByTableFieldId(gambleTable, option1Field, id + "");
        localeMessageService.deleteMessageByTableFieldId(gambleTable, option2Field, id + "");

        guessGambleDao.delete(id);
    }

    @Override
    public Page<GuessGamble> findGuessGamblesByPage(Integer page, Integer size, Long gameId) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<GuessGamble> guessGambles = guessGambleDao.findAll(new Specification<GuessGamble>() {
            @Override
            public Predicate toPredicate(Root<GuessGamble> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (gameId != null) {
                    list.add(criteriaBuilder.equal(root.get("gameId").as(Long.class), gameId));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);

        return guessGambles;
    }

    @Override
    @Transactional("transactionManagerSecondary")
    public GuessGamble findGuessGambleInfo(Long id) {
        GuessGamble guessGambleP = guessGambleDao.findOne(id);
        translateGuessGambleEN(guessGambleP, CommonConstants.I18N_SHOW_DEFAULT);
        translateGuessGambleKO(guessGambleP, CommonConstants.I18N_SHOW_DEFAULT);
        return guessGambleP;
    }


    @Override
    @Transactional("transactionManagerSecondary")
    public GuessGamble openAward(GuessGamble guessGamble) {
        GuessGamble guessGambleP = guessGambleDao.findOne(guessGamble.getId());
        Date date = new Date();
        Date luckDate = new Date();
        if (date.compareTo(guessGambleP.getCloseTime()) >= 0) {
            //开奖时间》封盘时间
            luckDate = date;
        } else {
            luckDate = guessGambleP.getCloseTime();
        }
        //封盘之后可开奖
        if (guessGambleP != null && (!guessGambleP.getStatus().equals(ActivityContants.GUESS_GAMBLE_STATUS_AWARD) || !guessGambleP.getStatus().equals(ActivityContants.GUESS_GAMBLE_STATUS_FLOW))) {
            if (guessGamble.getLuckOption().equals(ActivityContants.OPTION_C)) {
                //流盘
                guessGambleP.setStatus(ActivityContants.GUESS_GAMBLE_STATUS_FLOW);
                // 创建批处理 - 已流盘
                batchService.createBatchTask("GuessGambleFlow", date, GambleFlowBatch.class.getName(), guessGamble.getClass().getSimpleName(), guessGambleP.getId());
            } else {
                // 创建批处理 - 已开奖
                Date endDate = luckDate;
                batchService.createBatchTask("GuessGambleAward", endDate, GambleAwardBatch.class.getName(), guessGamble.getClass().getSimpleName(), guessGambleP.getId());
            }

        }
        guessGambleP.setLuckOption(guessGamble.getLuckOption());
        guessGambleP.setLuckTime(luckDate);
        guessGambleDao.save(guessGambleP);
        return guessGambleP;
    }

    @Override
    @Transactional("transactionManagerSecondary")
    public Map getGuessGambleList(Long gameId, String accountNo, Integer page, Integer size) {
        Map map = new HashMap();
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "status"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "close_time"));
        Pageable pageable = new PageRequest(page, size, new Sort(orders));
        Page<GuessGamble> guessGamblesPage = guessGambleDao.queryGuessGamblesActivited(gameId, pageable);
        List<GuessGamble> guessGambles = guessGamblesPage.getContent() == null ? new ArrayList<>() : guessGamblesPage.getContent();
        String language = localeMessageService.getLanguage();
        guessGambles.forEach(guessGamble -> translateGuessGamble(guessGamble, language, CommonConstants.I18N_SHOW_DEFAULT));
        Contract contractQBE = contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE);
        Long unit = contractQBE.getId();
        AccountBalance accountBalance = accountBalanceService.findByAccountNoAndUnit(accountNo, unit);
        map.put("amount", accountBalance == null ? new BigDecimal(0) : accountBalance.getAmount());
        map.put("guessGambleList", guessGambles);
        map.put("nowDate", new Date());
        return map;
    }

    @Override
    @Transactional("transactionManagerSecondary")
    public Map getGuessGambleInfo(Long id, String accountNo) {
        GuessGamble guessGambleP = guessGambleDao.findOne(id);
        String language = localeMessageService.getLanguage();
        translateGuessGamble(guessGambleP, language, CommonConstants.I18N_SHOW_DEFAULT);
        Map map = new HashMap();
        Contract contractQBE = contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE);
        Long unit = contractQBE.getId();
        AccountBalance accountBalance = accountBalanceService.findByAccountNoAndUnit(accountNo, unit);
        BigDecimal optionA = joinGambleDao.sumAmountByAccountNo(accountNo, ActivityContants.OPTION_A, id);
        BigDecimal optionB = joinGambleDao.sumAmountByAccountNo(accountNo, ActivityContants.OPTION_B, id);

        map.put("amount", accountBalance == null ? new BigDecimal(0) : accountBalance.getAmount());
        map.put("guessGamble", guessGambleP);
        map.put("nowDate", new Date());
        map.put("option1joinAmount", optionA == null ? new BigDecimal(0) : optionA);
        map.put("option2joinAmount", optionB == null ? new BigDecimal(0) : optionB);

        return map;
    }

    @Override
    @Transactional("transactionManagerSecondary")
    public Map getGuessResultInfo(Long id, String accountNo) {
        Map map = new HashMap();
        Object gambleResultObject = redisTemplate.opsForHash().get(RedisConstants.REDIS_NAME_GAMBLE, id);
        GambleResult gambleResult = new GambleResult();
        gambleResult.setGamble_id(id);
        if (gambleResultObject != null) {
            gambleResult = BeanUtils.jsonToObject(gambleResultObject.toString(), GambleResult.class);
        }
        map.put("gambleResult", gambleResult);
        return map;
    }

    @Override
    @Transactional("transactionManagerSecondary")
    public void joinGamble(JoinGamble joinGamble) {
        GuessGamble guessGamble = guessGambleDao.findOne(joinGamble.getGambleId());
        //check 超时
        if (guessGamble != null && guessGamble.getCloseTime().compareTo(new Date()) < 0) {
            throw new AppException(ErrorCode.GAMBLE_CLOSED);
        }
        //check 该投注小游戏是否可进行投注
        if (guessGamble == null || (guessGamble != null && !ActivityContants.GUESS_GAMBLE_STATUS_RUNNING.equals(guessGamble.getStatus()))) {
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }
        //check 是否流盘
        if (ActivityContants.GUESS_GAMBLE_STATUS_FLOW.equals(guessGamble.getStatus())) {
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }
        //check 竞猜额度是否符合条件
//        if (guessGamble.getIsMin()) {
        //min
        if (joinGamble.getAmount().compareTo(guessGamble.getAmount()) < 0) {
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }
//        } else {
//            //max
//            if (joinGamble.getAmount().compareTo(guessGamble.getAmount()) > 0) {
//                throw new AppException(ErrorCode.OPERATION_FAIL);
//            }
//        }
        Contract contractQBE = contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE);
        Long unit = contractQBE.getId();
        AccountBalance accountBalance = accountBalanceDao.findAccountBalanceByAccountNoAndUnit(joinGamble.getAccountNo(), unit);
        if (accountBalance == null || (accountBalance != null && accountBalance.getAmount().compareTo(joinGamble.getAmount()) < 0)) {
            throw new AppException(ErrorCode.CHECK_JOIN_GAMBLE_AMOUNT);
        } else {
            accountBalance.setAmount(accountBalance.getAmount().subtract(joinGamble.getAmount()));
        }
        //实时扣钱
        ExchangeLog exchangeLog = new ExchangeLog();
        exchangeLog.setType(WalletConstants.GAMBLE_AMOUNT_TYPE);
        exchangeLog.setStatus(WalletConstants.CONFIRMED);
        exchangeLog.setUnit(unit);
        exchangeLog.setAmount(joinGamble.getAmount());
        exchangeLog.setAccountNo(joinGamble.getAccountNo());
        exchangeLogDao.save(exchangeLog);
        accountBalanceService.saveAccountBalance(accountBalance);
        joinGambleDao.save(joinGamble);
        Object gambleResultObject = redisTemplate.opsForHash().get(RedisConstants.REDIS_NAME_GAMBLE, guessGamble.getId());
        GambleResult gambleResult = new GambleResult();
        if (gambleResultObject == null) {
            if (joinGamble.getOption().equals(ActivityContants.OPTION_A)) {
                gambleResult.setOption1_amount(joinGamble.getAmount());
                gambleResult.setOption1_number(1);
            } else if (joinGamble.getOption().equals(ActivityContants.OPTION_B)) {
                gambleResult.setOption2_amount(joinGamble.getAmount());
                gambleResult.setOption2_number(1);
            }
        } else {
            gambleResult = BeanUtils.jsonToObject(gambleResultObject.toString(), GambleResult.class);
            if (joinGamble.getOption().equals(ActivityContants.OPTION_A)) {
                gambleResult.setOption1_amount(gambleResult.getOption1_amount().add(joinGamble.getAmount()));
                gambleResult.setOption1_number(gambleResult.getOption1_number() + 1);
            } else if (joinGamble.getOption().equals(ActivityContants.OPTION_B)) {
                gambleResult.setOption2_amount(gambleResult.getOption2_amount().add(joinGamble.getAmount()));
                gambleResult.setOption2_number(gambleResult.getOption2_number() + 1);
            }

        }
        gambleResult.setGamble_id(guessGamble.getId());
        GambleResult finalGambleResult = gambleResult;
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations)
                    throws DataAccessException {
                operations.multi();
                operations.opsForHash().delete(RedisConstants.REDIS_NAME_GAMBLE, guessGamble.getId());
                operations.opsForHash().put(RedisConstants.REDIS_NAME_GAMBLE, guessGamble.getId(), com.aethercoder.basic.utils.BeanUtils.objectToJson(finalGambleResult));
                operations.exec();
                return null;
            }
        });
    }

    @Override
    @Transactional("transactionManagerSecondary")
    public Map getJoinGamblePerson(String accountNo, Long gameId, Integer page, Integer size) {
        //当前qbao Energy余量
        Contract contractQBE = contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE);
        Long unit = contractQBE.getId();
        AccountBalance accountBalance = accountBalanceService.findByAccountNoAndUnit(accountNo, unit);

        //joinGambleList
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "gamble_id");
        Page gambleIds = joinGambleDao.queryAllByAccountNo(accountNo, pageable);
        List<GuessGamble> guessGambles = new ArrayList<GuessGamble>();

        for (int i = 0; i < gambleIds.getContent().size(); i++) {
            Long gambleId = Long.parseLong(gambleIds.getContent().get(i).toString());
            GuessGamble guessGamble = new GuessGamble();
            guessGamble = guessGambleDao.findOne(gambleId);
            if (guessGamble != null) {
                guessGambles.add(guessGamble);
            }

        }
        String language = localeMessageService.getLanguage();
        guessGambles.forEach(guessGamble -> translateGuessGamble(guessGamble, language, CommonConstants.I18N_SHOW_DEFAULT));
        //参与了n场竞猜
        Integer number = joinGambleDao.countAllByAccountNo(accountNo);
        //赚取的qbao Energy
        BigDecimal winAmount = joinGambleDao.sumAmount(accountNo);
        Map map = new HashMap();
        map.put("amount", accountBalance == null ? new BigDecimal(0) : accountBalance.getAmount());
        map.put("guessGambleList", guessGambles);
        map.put("joinSize", number == null ? 0 : number);
        map.put("winAmount", winAmount == null ? new BigDecimal(0) : (winAmount.compareTo(new BigDecimal(0)) < 0 ? new BigDecimal(0) : winAmount));
        map.put("nowDate", new Date());
        return map;
    }

    @Override
     @Transactional("transactionManagerSecondary")
    public Map getJoinGambleRank(Long gameId) {
        Map map = new HashMap();
        List<GambleRank> gambleRankListWinRank = new ArrayList();
        List<GambleRank> gambleRankListLoseRank = new ArrayList();
        //redis
        Object gambleResultObjectWin = redisTemplate.opsForHash().get(RedisConstants.REDIS_NAME_GAMBLE_RANK, RedisConstants.REDIS_NAME_WIN);
        Object gambleResultObjectLose = redisTemplate.opsForHash().get(RedisConstants.REDIS_NAME_GAMBLE_RANK, RedisConstants.REDIS_NAME_LOSE);
        if (gambleResultObjectWin == null || gambleResultObjectLose==null) {


            //赌博排行榜
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
            SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String beforeTimeStr = sdf.format(DateUtil.getBeforeDay(new Date()));
            Date beforeDate = new Date();
            Date endDate = new Date();
            try {
                beforeDate = sdfs.parse(beforeTimeStr + " 00:00:00");
                endDate = sdfs.parse(beforeTimeStr + " 23:59:59");
            } catch (ParseException e) {
                logger.error("getJoinGambleRank error.");
            }
            //获取昨天开奖的所有投注游戏
            Long[] guessGambleList = guessGambleDao.queryGuessGamblesByLuckTime(gameId, beforeDate, endDate);
            List gambleRankListWin = new ArrayList();
            List gambleRankListLose = new ArrayList();

            if (guessGambleList.length != 0) {
                //win
                gambleRankListWin = joinGambleDao.queryAllByGambleIdInOrderByAmountDesc(guessGambleList);
                for (int i = 0; i < gambleRankListWin.size(); i++) {
                    Object[] objs = (Object[]) gambleRankListWin.get(i);
                    GambleRank gambleRankWin = new GambleRank();
                    gambleRankWin.setAmount(new BigDecimal(objs[1].toString()));
                    Account account = accountDao.findByAccountNo(objs[0].toString());
                    if (account != null) {
                        gambleRankWin.setAccountName(account.getAccountName());
                        gambleRankWin.setHeader(account.getHeader());
                    }
                    gambleRankListWinRank.add(gambleRankWin);
                }
                //lose
                gambleRankListLose = joinGambleDao.queryAllByGambleIdInOrderByAmountAsc(guessGambleList);
                for (int i = 0; i < gambleRankListLose.size(); i++) {
                    Object[] objs = (Object[]) gambleRankListLose.get(i);
                    GambleRank gambleRankLose = new GambleRank();
                    gambleRankLose.setAmount(new BigDecimal(objs[1].toString()));
                    Account account = accountDao.findByAccountNo(objs[0].toString());
                    if (account != null) {
                        gambleRankLose.setAccountName(account.getAccountName());
                        gambleRankLose.setHeader(account.getHeader());
                    }
                    gambleRankListLoseRank.add(gambleRankLose);
                }
            }
            List<GambleRank> finalGambleRankListWinRank = gambleRankListWinRank;
            List<GambleRank> finalGambleRankListLoseRank = gambleRankListLoseRank;
            final Date endTimeFinal = endDate;
            redisTemplate.execute(new SessionCallback() {
                @Override
                public Object execute(RedisOperations operations)
                        throws DataAccessException {
                    operations.multi();
                    operations.opsForHash().delete(RedisConstants.REDIS_NAME_GAMBLE_RANK, RedisConstants.REDIS_NAME_WIN);
                    operations.opsForHash().delete(RedisConstants.REDIS_NAME_GAMBLE_RANK, RedisConstants.REDIS_NAME_LOSE);
                    redisTemplate.opsForHash().put(RedisConstants.REDIS_NAME_GAMBLE_RANK, RedisConstants.REDIS_NAME_WIN, finalGambleRankListWinRank);
                    redisTemplate.opsForHash().put(RedisConstants.REDIS_NAME_GAMBLE_RANK, RedisConstants.REDIS_NAME_LOSE, finalGambleRankListLoseRank);
                    // redis过期时间
                    redisTemplate.expireAt(RedisConstants.REDIS_NAME_GAMBLE_RANK,endTimeFinal);
                    operations.exec();
                    return null;
                }
            });



        }else {
            gambleRankListWinRank = (List<GambleRank>) gambleResultObjectWin;
            gambleRankListLoseRank = (List<GambleRank>) gambleResultObjectLose;
        }
        map.put("gambleRankListWin", gambleRankListWinRank);
        map.put("gambleRankListLose", gambleRankListLoseRank);

        return map;
    }

    @Override
    public String getGambleGameShareUrl(Long gambleId, String language) {

        // 查询GuessGamble表判断竞猜是否存在
//        GuessGamble guessGamble = guessGambleDao.findOne(gambleId);
//        if (guessGamble != null) {
//
//            String questionI18n = localeMessageService.getMessageByTableFieldId(gambleTable, titleField, guessGamble.getId() + "", CommonConstants.I18N_SHOW_DEFAULT, language);
//
//            guessGamble.setTitle(questionI18n);
//
//            String option1 = localeMessageService.getMessageByTableFieldId(gambleTable, option1Field, guessGamble.getId() + "", CommonConstants.I18N_SHOW_DEFAULT, language);
//            guessGamble.setOption1(option1);
//
//            String option2 = localeMessageService.getMessageByTableFieldId(gambleTable, option2Field, guessGamble.getId() + "", CommonConstants.I18N_SHOW_DEFAULT, language);
//            guessGamble.setOption2(option2);
//
//            String content = localeMessageService.getMessageByTableFieldId(gambleTable, contentField, guessGamble.getId() + "", CommonConstants.I18N_SHOW_DEFAULT, language);
//            guessGamble.setContent(content);
//
//        }

        //获取竞猜小游戏地址
        SysConfig urlConfig = sysConfigService.findSysConfigByName(WalletConstants.GUESS_URL);

        String url = null;
        try {

//            url = URLEncoder.encode(urlConfig.getValue() + "?title=" + guessGamble.getTitle() + "&content=" + guessGamble.getContent()
//                    + "&option1=" + guessGamble.getOption1() + "&option2=" + guessGamble.getOption2() + "&lang=" + language,"UTF-8");

            url = URLEncoder.encode(urlConfig.getValue() + "?lang=" + language,"UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return url;

    }

    @Override
    public Map getGambleGameShareParams(Long gambleId, String language) {

        Map map = new HashMap();
        // 查询GuessGamble表判断竞猜是否存在
        GuessGamble guessGamble = guessGambleDao.findOne(gambleId);
        if (guessGamble != null) {

            String questionI18n = localeMessageService.getMessageByTableFieldId(gambleTable, titleField, guessGamble.getId() + "", CommonConstants.I18N_SHOW_DEFAULT, language);

            guessGamble.setTitle(questionI18n);

            String option1 = localeMessageService.getMessageByTableFieldId(gambleTable, option1Field, guessGamble.getId() + "", CommonConstants.I18N_SHOW_DEFAULT, language);
            guessGamble.setOption1(option1);

            String option2 = localeMessageService.getMessageByTableFieldId(gambleTable, option2Field, guessGamble.getId() + "", CommonConstants.I18N_SHOW_DEFAULT, language);
            guessGamble.setOption2(option2);

            String content = localeMessageService.getMessageByTableFieldId(gambleTable, contentField, guessGamble.getId() + "", CommonConstants.I18N_SHOW_DEFAULT, language);
            guessGamble.setContent(content);

        }

        map.put("guessGamble",guessGamble);
        return map;

    }


}
