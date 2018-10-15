package com.aethercoder.activity.service;

import com.aethercoder.activity.entity.guessGamble.GuessGamble;
import com.aethercoder.activity.entity.guessGamble.JoinGamble;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * Created by hepengfei on 27/02/2018.
 */
public interface GuessGambleService {
    GuessGamble saveGuessGamble(GuessGamble guessGamble);

    void updateGuessGamble(GuessGamble guessGamble);

    void deleteGuessGamble(Long id);

    Page<GuessGamble> findGuessGamblesByPage(Integer page,Integer size,Long gameId);

    GuessGamble findGuessGambleInfo(Long id);

    GuessGamble openAward(GuessGamble guessGamble);

    Map getGuessGambleList(Long gameId,String accountNo,Integer page, Integer size);

    Map getGuessGambleInfo(Long id,String accountNo);

    Map getGuessResultInfo(Long id,String accountNo);

    void joinGamble(JoinGamble joinGamble);

    Map getJoinGamblePerson(String accountNo,Long gameId,Integer page, Integer size);

    Map getJoinGambleRank(Long gameId);

    String getGambleGameShareUrl(Long gambleId,String language );

    Map getGambleGameShareParams(Long gambleId,String language );

}
