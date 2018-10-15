package com.aethercoder.core.dao;

import com.aethercoder.core.entity.guess.GuessAward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuessAwardDao extends JpaRepository<GuessAward, Long> {
    GuessAward findByGuessIdAndUnit(Long guessId,Long unit);

    List<GuessAward> findByGuessId(Long guessId);

}
