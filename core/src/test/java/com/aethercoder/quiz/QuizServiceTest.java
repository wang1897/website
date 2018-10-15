package com.aethercoder.quiz;

import com.aethercoder.TestApplication;
import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.SysConfigDao;
import com.aethercoder.core.entity.quiz.Quiz;
import com.aethercoder.core.entity.wallet.SysConfig;
import com.aethercoder.core.service.QuizService;
import com.aethercoder.foundation.service.LocaleMessageService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by hepengfei on 24/02/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class QuizServiceTest {
    private String quizTable = "quiz";
    private String questionField = "question";
    private String option1Field = "option1";
    private String option2Field = "option2";
    private String option3Field = "option3";
    private String option4Field = "option4";

    @Autowired
    private QuizService quizService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private LocaleMessageService localeMessageService;

    @Autowired
    private SysConfigDao sysConfigDao;

    @Test
    public void testQuiz() {
        /*Quiz quiz1 = new Quiz();
        quiz1.setLanguage("en");
        quiz1.setQuestion("test1");
        quiz1.setOption1("option1");
        quiz1.setOption2("option2");
        quiz1.setOption3("option3");
        quiz1.setOption4("option4");
        quiz1.setAnswer(2);
        quiz1.setType(1L);
        Quiz pq1 = quizService.saveQuiz(quiz1);
        Assert.assertTrue(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq1.getId()));

        Quiz quiz2 = new Quiz();
        quiz2.setLanguage("en");
        quiz2.setQuestion("test2");
        quiz2.setOption1("option1");
        quiz2.setOption2("option2");
        quiz2.setOption3("option3");
        quiz2.setOption4("option4");
        quiz2.setAnswer(2);
        quiz2.setType(1L);
        Quiz pq2 = quizService.saveQuiz(quiz2);
        Assert.assertTrue(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq2.getId()));

        Quiz quiz3 = new Quiz();
        quiz3.setLanguage("en");
        quiz3.setQuestion("test3");
        quiz3.setOption1("option1");
        quiz3.setOption2("option2");
        quiz3.setOption3("option3");
        quiz3.setOption4("option4");
        quiz3.setAnswer(2);
        quiz3.setType(1L);
        Quiz pq3 = quizService.saveQuiz(quiz3);
        Assert.assertTrue(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq3.getId()));

        Quiz quiz4 = new Quiz();
        quiz4.setLanguage("en");
        quiz4.setQuestion("test4");
        quiz4.setOption1("option1");
        quiz4.setOption2("option2");
        quiz4.setOption3("option3");
        quiz4.setOption4("option4");
        quiz4.setAnswer(2);
        quiz4.setType(1L);
        Quiz pq4 = quizService.saveQuiz(quiz4);
        Assert.assertTrue(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq4.getId()));

        Quiz quiz5 = new Quiz();
        quiz5.setLanguage("en");
        quiz5.setQuestion("test1");
        quiz5.setOption1("option1");
        quiz5.setOption2("option2");
        quiz5.setOption3("option3");
        quiz5.setOption4("option4");
        quiz5.setAnswer(2);
        quiz5.setType(1L);
        Quiz pq5 = quizService.saveQuiz(quiz5);
        Assert.assertTrue(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq5.getId()));

        Quiz quiz6 = new Quiz();
        quiz6.setLanguage("en");
        quiz6.setQuestion("test1");
        quiz6.setOption1("option1");
        quiz6.setOption2("option2");
        quiz6.setOption3("option3");
        quiz6.setOption4("option4");
        quiz6.setAnswer(2);
        quiz6.setType(1L);
        Quiz pq6 = quizService.saveQuiz(quiz6);
        Assert.assertTrue(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq6.getId()));

        Quiz quiz7 = new Quiz();
        quiz7.setLanguage("en");
        quiz7.setQuestion("test1");
        quiz7.setOption1("option1");
        quiz7.setOption2("option2");
        quiz7.setOption3("option3");
        quiz7.setOption4("option4");
        quiz7.setAnswer(2);
        quiz7.setType(1L);
        Quiz pq7 = quizService.saveQuiz(quiz7);
        Assert.assertTrue(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq7.getId()));

        List<Quiz> quizList = quizService.getAccountQuiz();
        Set<Long> set = new HashSet<>();
        for (Quiz quiz : quizList) {
            set.add(quiz.getId());
        }

        SysConfig quizSizeConfig = sysConfigDao.findSysConfigByName(WalletConstants.QUIZ_SIZE);
        Assert.assertEquals(set.size(), Integer.parseInt(quizSizeConfig.getValue()));

        quizService.deleteQuiz(pq1.getId());
        Assert.assertFalse(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq1.getId()));

        quizService.deleteQuiz(pq2.getId());
        Assert.assertFalse(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq2.getId()));

        quizService.deleteQuiz(pq3.getId());
        Assert.assertFalse(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq3.getId()));

        quizService.deleteQuiz(pq4.getId());
        Assert.assertFalse(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq4.getId()));

        quizService.deleteQuiz(pq5.getId());
        Assert.assertFalse(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq5.getId()));

        quizService.deleteQuiz(pq6.getId());
        Assert.assertFalse(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq6.getId()));

        quizService.deleteQuiz(pq7.getId());
        Assert.assertFalse(redisTemplate.opsForSet().isMember(RedisConstants.REDIS_KEY_QUIZ, pq7.getId()));

        localeMessageService.deleteMessageByTableFieldId(quizTable, questionField, pq1.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option1Field, pq1.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option2Field, pq1.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option3Field, pq1.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option4Field, pq1.getId() + "");

        localeMessageService.deleteMessageByTableFieldId(quizTable, questionField, pq2.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option1Field, pq2.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option2Field, pq2.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option3Field, pq2.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option4Field, pq2.getId() + "");

        localeMessageService.deleteMessageByTableFieldId(quizTable, questionField, pq3.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option1Field, pq3.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option2Field, pq3.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option3Field, pq3.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option4Field, pq3.getId() + "");

        localeMessageService.deleteMessageByTableFieldId(quizTable, questionField, pq4.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option1Field, pq4.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option2Field, pq4.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option3Field, pq4.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option4Field, pq4.getId() + "");

        localeMessageService.deleteMessageByTableFieldId(quizTable, questionField, pq5.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option1Field, pq5.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option2Field, pq5.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option3Field, pq5.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option4Field, pq5.getId() + "");

        localeMessageService.deleteMessageByTableFieldId(quizTable, questionField, pq6.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option1Field, pq6.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option2Field, pq6.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option3Field, pq6.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option4Field, pq6.getId() + "");

        localeMessageService.deleteMessageByTableFieldId(quizTable, questionField, pq7.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option1Field, pq7.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option2Field, pq7.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option3Field, pq7.getId() + "");
        localeMessageService.deleteMessageByTableFieldId(quizTable, option4Field, pq7.getId() + "");*/
    }
}
