package com.aethercoder.core.dao;

import com.aethercoder.core.entity.quiz.QuizType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizTypeDao extends JpaRepository<QuizType, Long> {
}
