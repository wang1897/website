package com.aethercoder.core.dao;

import com.aethercoder.core.entity.guess.GuessRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guofeiyan on 2018/01/29.
 */
@Repository
public interface GuessRecordDao extends JpaRepository<GuessRecord, Long> {


    GuessRecord findGuessRecordByAccountNoAndGuessNumberId(String accountNo, Long id);

    List<GuessRecord> findByGuessNumberIdAndDrawLevelIsNotNullOrderByDrawLevel(Long guessNumberId);

}
