package com.aethercoder.core.service;


import com.aethercoder.core.entity.quiz.Quiz;
import com.aethercoder.core.entity.quiz.QuizAnswer;
import com.aethercoder.core.entity.quiz.QuizType;
import com.aethercoder.core.entity.wallet.SysConfig;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by Guo Feiyan on 2018/01/06.
 */
public interface QuizService  {

    List<QuizType> saveQuizType(List<QuizType> quizTypes);

    void updateQuizType(QuizType quizType);

    void deleteQuizType(Long[] id);

    List<QuizType> findAllQuizTypes();

    Quiz saveQuiz(Quiz quiz);

    void updateQuiz(Quiz quiz);

    void deleteQuiz(Long id);

    Page<Quiz> findQuizzes(Integer page,Integer size,Long type, String language);

    Page<QuizAnswer> findQuizAnswers(Integer page, Integer size, String answerTime, Integer rightNumber);

    Map getAccountQuiz(String accountNo);

    Map DailyAnswer(List<QuizAnswer> quizAnswer,String accountNo);

    List<SysConfig> getQuizInfo();

    List<SysConfig> updateQuizInfo(List<SysConfig> sysConfigs);

    String getQuizShareUrl(String language);


}
