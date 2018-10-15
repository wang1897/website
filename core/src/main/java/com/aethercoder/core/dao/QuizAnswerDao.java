package com.aethercoder.core.dao;

import com.aethercoder.core.entity.quiz.QuizAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface QuizAnswerDao extends JpaRepository<QuizAnswer, Long> {
    Page<QuizAnswer> findAll(Specification<QuizAnswer> specification, Pageable pageable);

    List<QuizAnswer> queryQuizAnswersByAccountNoAndAnswer(String accountNo,Integer answer);

    List<QuizAnswer> queryQuizAnswersByAccountNoAndAnswerTimeGreaterThanEqual(String accountNo, Date answerTime);
}
