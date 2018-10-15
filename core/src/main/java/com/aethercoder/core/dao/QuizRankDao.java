package com.aethercoder.core.dao;

import com.aethercoder.core.entity.quiz.QuizRank;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @auther Guo Feiyan
 * @date 2018/2/26 下午1:53
 */
public interface QuizRankDao extends JpaRepository<QuizRank, Long> {

    QuizRank queryQuizRankByAccountNo(String accountNo);
}
