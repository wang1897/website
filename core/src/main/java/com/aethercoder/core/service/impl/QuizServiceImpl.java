package com.aethercoder.core.service.impl;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.*;
import com.aethercoder.core.entity.json.Data;
import com.aethercoder.core.entity.quiz.Quiz;
import com.aethercoder.core.entity.quiz.QuizAnswer;
import com.aethercoder.core.entity.quiz.QuizRank;
import com.aethercoder.core.entity.quiz.QuizType;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.AccountBalanceService;
import com.aethercoder.core.service.QuizService;
import com.aethercoder.core.service.SysConfigService;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.entity.i18n.Message;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import com.aethercoder.foundation.service.LocaleMessageService;
import com.aethercoder.foundation.util.DateUtil;
import org.apache.commons.lang3.StringUtils;
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
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;


/**
 * Created by Guo Feiyan on 2018/01/06.
 */
@Service
public class QuizServiceImpl implements QuizService {

    private static Logger logger = LoggerFactory.getLogger(QuizServiceImpl.class);

    private String quizTable = "quiz";
    private String questionField = "question";
    private String option1Field = "option1";
    private String option2Field = "option2";
    private String option3Field = "option3";
    private String option4Field = "option4";
    private String comment = "comment";

    @Autowired
    private QuizDao quizDao;

    @Autowired
    private QuizAnswerDao quizAnswerDao;

    @Autowired
    private QuizTypeDao quizTypeDao;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private SysConfigDao sysConfigDao;

    @Autowired
    private LocaleMessageService localeMessageService;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private AccountBalanceService accountBalanceService;

    @Autowired
    private QuizRankDao quizRankDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public List<QuizType> saveQuizType(List<QuizType> quizTypes) {
        return quizTypeDao.save(quizTypes);
    }

    @Override
    public void updateQuizType(QuizType quizType) {
        quizTypeDao.save(quizType);
    }

    @Override
    public void deleteQuizType(Long[] ids) {
        List<QuizType> quizTypeList = quizTypeDao.findAll();
        if (quizTypeList.size() - ids.length >= 1) {
            for (int i = 0; i < ids.length; i++) {
                List<Quiz> quizList = quizDao.findQuizzesByType(Long.parseLong(ids[i].toString()));
                quizList.forEach(quiz -> {
                    quiz.setType(null);
                    quizDao.save(quiz);
                });
                quizTypeDao.delete(ids[i]);
            }
        } else {
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }

    }

    @Override
    public List<QuizType> findAllQuizTypes() {
        return quizTypeDao.findAll();
    }

    @Override
    public Quiz saveQuiz(Quiz quiz) {
        //add redis set 根据语言进行题目分类
        Quiz pQuiz = quizDao.save(quiz);
        pQuiz.setLanguage(quiz.getLanguage());
        saveQuizMessage(pQuiz);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations)
                    throws DataAccessException {
                operations.multi();
                //add redis
                operations.opsForSet().add(RedisConstants.REDIS_KEY_QUIZ + quiz.getLanguage(), pQuiz.getId());
                operations.exec();
                return null;

            }
        });
        return pQuiz;
    }

    @Override
    public void updateQuiz(Quiz quiz) {
        //更新redis
        Quiz pQuiz = quizDao.save(quiz);
        pQuiz.setLanguage(quiz.getLanguage());
        saveQuizMessage(pQuiz);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations)
                    throws DataAccessException {
                operations.multi();
                //add redis
                operations.opsForSet().add(RedisConstants.REDIS_KEY_QUIZ + quiz.getLanguage(), quiz.getId());
                operations.exec();
                return null;

            }
        });
    }

    @Override
    public void deleteQuiz(Long id) {

        Quiz quiz1 = quizDao.findOne(id);
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations)
                    throws DataAccessException {
                operations.multi();
                //add redis
                operations.opsForSet().remove(RedisConstants.REDIS_KEY_QUIZ + WalletConstants.LANGUAGE_TYPE_ZH, id);
                operations.opsForSet().remove(RedisConstants.REDIS_KEY_QUIZ + WalletConstants.LANGUAGE_TYPE_EN, id);
                operations.opsForSet().remove(RedisConstants.REDIS_KEY_QUIZ + WalletConstants.LANGUAGE_TYPE_KO, id);
                operations.exec();
                return null;

            }
        });
        //delete不用处理多语言
        quiz1.setIsDelete(true);
        quizDao.save(quiz1);

    }

    private void saveQuizMessage(Quiz quiz) {
        if (quiz.getQuestion() != null) {
            Message questionMsg = new Message();
            questionMsg.setMessage(quiz.getQuestion());
            questionMsg.setLanguage(quiz.getLanguage());
            questionMsg.setTable(quizTable);
            questionMsg.setField(questionField);
            questionMsg.setResourceId(quiz.getId() + "");
            localeMessageService.saveMessage(questionMsg);
        }

        if (quiz.getOption1() != null) {
            Message option1Msg = new Message();
            option1Msg.setMessage(quiz.getOption1());
            option1Msg.setLanguage(quiz.getLanguage());
            option1Msg.setTable(quizTable);
            option1Msg.setField(option1Field);
            option1Msg.setResourceId(quiz.getId() + "");
            localeMessageService.saveMessage(option1Msg);
        }

        if (quiz.getOption2() != null) {
            Message option2Msg = new Message();
            option2Msg.setMessage(quiz.getOption2());
            option2Msg.setLanguage(quiz.getLanguage());
            option2Msg.setTable(quizTable);
            option2Msg.setField(option2Field);
            option2Msg.setResourceId(quiz.getId() + "");
            localeMessageService.saveMessage(option2Msg);
        }

        if (quiz.getOption3() != null) {
            Message option3Msg = new Message();
            option3Msg.setMessage(quiz.getOption3());
            option3Msg.setLanguage(quiz.getLanguage());
            option3Msg.setTable(quizTable);
            option3Msg.setField(option3Field);
            option3Msg.setResourceId(quiz.getId() + "");
            localeMessageService.saveMessage(option3Msg);
        }

        if (quiz.getOption4() != null) {
            Message option4Msg = new Message();
            option4Msg.setMessage(quiz.getOption4());
            option4Msg.setLanguage(quiz.getLanguage());
            option4Msg.setTable(quizTable);
            option4Msg.setField(option4Field);
            option4Msg.setResourceId(quiz.getId() + "");
            localeMessageService.saveMessage(option4Msg);
        }

        if (quiz.getComment() != null) {
            Message commentMsg = new Message();
            commentMsg.setMessage(quiz.getComment());
            commentMsg.setLanguage(quiz.getLanguage());
            commentMsg.setTable(quizTable);
            commentMsg.setField(comment);
            commentMsg.setResourceId(quiz.getId() + "");
            localeMessageService.saveMessage(commentMsg);
        }
    }

    private void translateQuiz(Quiz quiz, String language, int type) {
        if (quiz.getQuestion() != null) {
            String questionI18n = localeMessageService.getMessageByTableFieldId(quizTable, questionField, quiz.getId() + "", type, language);
            quiz.setQuestion(questionI18n);
        }

        if (quiz.getOption1() != null) {
            String option1 = localeMessageService.getMessageByTableFieldId(quizTable, option1Field, quiz.getId() + "", type, language);
            quiz.setOption1(option1);
        }

        if (quiz.getOption2() != null) {
            String option2 = localeMessageService.getMessageByTableFieldId(quizTable, option2Field, quiz.getId() + "", type, language);
            quiz.setOption2(option2);
        }

        if (quiz.getOption3() != null) {
            String option3 = localeMessageService.getMessageByTableFieldId(quizTable, option3Field, quiz.getId() + "", type, language);
            quiz.setOption3(option3);
        }

        if (quiz.getOption4() != null) {
            String option4 = localeMessageService.getMessageByTableFieldId(quizTable, option4Field, quiz.getId() + "", type, language);
            quiz.setOption4(option4);
        }

        if (quiz.getComment() != null) {
            String commentMsg = localeMessageService.getMessageByTableFieldId(quizTable, comment, quiz.getId() + "", type, language);
            quiz.setComment(commentMsg);
        }
    }

    @Override
    public Page<Quiz> findQuizzes(Integer page, Integer size, Long type, String language) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<Quiz> quizzes = quizDao.findAll(new Specification<Quiz>() {
            @Override
            public Predicate toPredicate(Root<Quiz> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();

                //活动名称
                if (null != type) {
                    list.add(criteriaBuilder.equal(root.get("type").as(Long.class), type));
                }
                //是否有效
                list.add(criteriaBuilder.equal(root.get("isDelete").as(Boolean.class), false));

                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);

        quizzes.forEach(quiz -> translateQuiz(quiz, language, CommonConstants.I18N_SHOW_DEFAULT));
        return quizzes;
    }

    @Override
    public Page<QuizAnswer> findQuizAnswers(Integer page, Integer size, String answerTime, Integer rightNumber) {
        Pageable pageable = new PageRequest(page, size, Sort.Direction.DESC, "id");
        Page<QuizAnswer> quizAnswers = quizAnswerDao.findAll(new Specification<QuizAnswer>() {
            @Override
            public Predicate toPredicate(Root<QuizAnswer> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();

                if (StringUtils.isNotBlank(answerTime)) {
                    //大于或等于传入时间
                    list.add(criteriaBuilder.equal(root.get("answerTime").as(Date.class), answerTime));
                }
                //是否有效
                if (null != rightNumber) {
                    list.add(criteriaBuilder.equal(root.get("rightNumber").as(Integer.class), rightNumber));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);

        return quizAnswers;
    }

    @Override
    public Map getAccountQuiz(String accountNo) {
        logger.info("getAccountQuiz");
        Map map = new HashMap();
        Account account = accountDao.findByAccountNo(accountNo);
        map.put("header",account.getHeader());

        //check 今天是否答题
        Data dataToday = checkTodayIsQuiz(accountNo);
        if (null != dataToday){
            map.put("data",dataToday);
            return map;
        }
        //当天未答题  分两种 ：1。数据库已经分配过题目 但该用户并未完成提交答题 2。 没有参与当天答题
        List<Quiz> quizzes = new ArrayList<>();
        //获取该用户未答题的题目
        SysConfig quizSizeConfig = sysConfigDao.findSysConfigByName(WalletConstants.QUIZ_SIZE);
        if (quizSizeConfig == null) {
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }
        int quizSize = Integer.parseInt(quizSizeConfig.getValue());
        List<QuizAnswer> quizAnswers = quizAnswerDao.queryQuizAnswersByAccountNoAndAnswer(accountNo,0);
        //1。数据库已经分配过题目 但该用户并未完成提交答题
        if (null != quizAnswers && !quizAnswers.isEmpty()) {
            for (int i = 0; i < quizAnswers.size(); i++) {
                Quiz quiz = quizDao.findOne(quizAnswers.get(i).getQuiz());
                translateQuiz(quiz, null, CommonConstants.I18N_SHOW_DEFAULT);
                quizzes.add(quiz);
            }

        } else {
            //2。 没有参与当天答题
            String language = localeMessageService.getLanguage();
            Long size = redisTemplate.opsForSet().size(RedisConstants.REDIS_KEY_QUIZ + language);
            if (size < quizSize) {
                String[] params = new String[]{quizSizeConfig.getValue()};
                throw new AppException(ErrorCode.QUESTION_TOO_FEW, params);
            }
            Set<Object> quizIdList = redisTemplate.opsForSet().distinctRandomMembers(RedisConstants.REDIS_KEY_QUIZ + language, Integer.parseInt(quizSizeConfig.getValue()));

            for (Object o : quizIdList) {
                Long quizId = (Long) o;
                Quiz quiz = quizDao.findOne(quizId);
                translateQuiz(quiz, null, CommonConstants.I18N_SHOW_DEFAULT);
                quizzes.add(quiz);
                QuizAnswer quizAnswer = new QuizAnswer();
                quizAnswer.setAccountNo(accountNo);
                quizAnswer.setQuiz(quiz.getId());
                quizAnswerDao.save(quizAnswer);

            }

        }
        if (null == map){
            map = new HashMap();
        }
        map.put("quizzes",quizzes);
        return map;
    }

    private Data checkTodayIsQuiz(String accountNo){
        Date todayBegin = DateUtil.getTodayBegin();
        List<QuizAnswer> quizAnswers = quizAnswerDao.queryQuizAnswersByAccountNoAndAnswerTimeGreaterThanEqual(accountNo,todayBegin);
        Data data = new Data();
        if (quizAnswers != null && !quizAnswers.isEmpty()){
            int rightQuizSize = 0;
            for (int i= 0; i<quizAnswers.size() ;i++ ){
                QuizAnswer quizAnswer = quizAnswers.get(i);
                if (quizAnswer.getAnswer().equals(quizAnswer.getRightAnswer())){
                    rightQuizSize++;
                }
            }
            data.setKey(String.valueOf(quizAnswers.size()));
            data.setValue(String.valueOf(rightQuizSize));
            return data;
        }
        return null;
    }

    @Transactional
    @Override
    public Map DailyAnswer(List<QuizAnswer> quizAnswers,String accountNo) {
        logger.info("DailyAnswer");

        //check 今天是否答题
        Data dataCheck = checkTodayIsQuiz(accountNo);
        if (dataCheck != null){
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }

        //check 每日答题奖励
        SysConfig quizAwardConfig = sysConfigDao.findSysConfigByName(WalletConstants.QUIZ_AWARD);

        if (quizAwardConfig == null) {
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }

        //check 每日答题题数
        SysConfig quizSizeConfig = sysConfigDao.findSysConfigByName(WalletConstants.QUIZ_SIZE);

        if (quizSizeConfig.getValue().equals(quizAnswers.size())) {
            throw new AppException(ErrorCode.QUESTION_TOO_FEW);
        }
        BigDecimal winToken = new BigDecimal(quizAwardConfig.getValue());
        //当天答题奖励
        BigDecimal winAmount = new BigDecimal(0);

        //当天答对几题
        int rightSize = 0;
        //获取分配题目
        List<QuizAnswer> quizAnswersCheck = quizAnswerDao.queryQuizAnswersByAccountNoAndAnswer(accountNo,0);
        if (null == quizAnswersCheck ){
            throw new AppException(ErrorCode.OPERATION_FAIL);
        }

        //check 答对几题
        for (int i = 0; i < quizAnswers.size(); i++) {
            //check 题目是否一致
            Long quizId = quizAnswersCheck.get(i).getQuiz();
            Long answerQuizId = quizAnswers.get(i).getQuiz();
            if (!quizId.equals(answerQuizId)) {
                throw new AppException(ErrorCode.OPERATION_FAIL);
            }
            QuizAnswer quizAnswerAnswer = quizAnswers.get(i);
            QuizAnswer quizAnswer = quizAnswersCheck.get(i);
            Quiz quiz = quizDao.findOne(quizAnswerAnswer.getQuiz());
            quizAnswer.setAnswerTime(new Date());
            quizAnswer.setRightAnswer(quiz.getAnswer());
            quizAnswer.setAnswer(quizAnswerAnswer.getAnswer());

            if (quiz != null && quiz.getAnswer().equals(quizAnswerAnswer.getAnswer())) {
                //答对奖励累加
                quizAnswer.setWinToken(winToken);
                winAmount = winAmount.add(winToken);
                rightSize++;
            }else {
                quizAnswer.setWinToken(new BigDecimal(0));
            }

        }
        quizAnswerDao.save(quizAnswersCheck);
        //答题奖励实时到账
        if (winAmount.compareTo(new BigDecimal(0)) > 0) {
            Contract contractQBE = contractDao.findContractByNameAndType(WalletConstants.QBAO_ENERGY,WalletConstants.CONTRACT_QTUM_TYPE);
            Long unit = contractQBE.getId();
            accountBalanceService.accountReward(accountNo,unit,winAmount,WalletConstants.QUIZ_AWARD_TYPE);

        }
        //实时更新rank表
        QuizRank quizRank = quizRankDao.queryQuizRankByAccountNo(accountNo);
        if (quizRank != null){
            quizRank.setAnswerQuiz(quizRank.getAnswerQuiz()+quizAnswers.size());
            quizRank.setRightQuiz(quizRank.getRightQuiz()+rightSize);
            quizRank.setWinToken(quizRank.getWinToken().add(winAmount));
        }else{
            quizRank = new QuizRank();
            quizRank.setAccountNo(accountNo);
            quizRank.setAnswerQuiz(quizAnswers.size());
            quizRank.setRightQuiz(rightSize);
            quizRank.setWinToken(winAmount);
        }
        QuizRank quizRank1 = quizRankDao.save(quizRank);
        Map map = new HashMap();
        Data dataToday = new Data();
        dataToday.setKey(String.valueOf(rightSize));
        dataToday.setValue(winAmount.toString());
        map.put("todayQuiz",dataToday);
        Data data = new Data();
        data.setKey(quizRank1.getRightQuiz().toString());
        data.setValue(quizRank1.getWinToken().toString());
        map.put("totalQuiz",data);
        return map;

    }

    @Override
    public List<SysConfig> getQuizInfo() {
        List<SysConfig> sysConfigs = new ArrayList<>();
        SysConfig quizAwardConfig = sysConfigDao.findSysConfigByName(WalletConstants.QUIZ_AWARD);
        SysConfig quizSizeConfig = sysConfigDao.findSysConfigByName(WalletConstants.QUIZ_SIZE);
        sysConfigs.add(quizAwardConfig);
        sysConfigs.add(quizSizeConfig);

        return sysConfigs;
    }

    @Override
    public List<SysConfig> updateQuizInfo(List<SysConfig> sysConfigs) {
        return sysConfigDao.save(sysConfigs);
    }

    @Override
    public String getQuizShareUrl(String language) {
        //获取每日答题Url
        SysConfig urlConfig = sysConfigService.findSysConfigByName(WalletConstants.ACTIVITIES_URL);

        String url = null;
        try {

            url = URLEncoder.encode(urlConfig.getValue() + "?lang=" + language,"UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return url;


    }
}
