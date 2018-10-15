package com.aethercoder.core.dao;

import com.aethercoder.core.entity.guess.GuessUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface GuessUnitDao extends JpaRepository<GuessUnit, Long> {

    Set<GuessUnit> findGuessUnitsByGuessId(Long guessId);
}
