package com.aethercoder.core.service;

import com.aethercoder.core.entity.guess.GuessNumberGame;
import com.aethercoder.core.entity.guess.GuessRecord;
import com.aethercoder.core.entity.json.BaseWinner;
import com.aethercoder.core.entity.json.GameJson;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * Created by guofeiyan on 2018/01/29.
 */
public interface GuessNumberGameService {
    GuessNumberGame saveGuessNumberGame(GuessNumberGame guessNumberGame);

    void updateGuessNumberGame(GuessNumberGame guessNumberGame);

    void deleteGuessNumberGame(Long id);

    void updateAward(GuessNumberGame guessNumberGame);

    Page<GuessNumberGame> findGuessNumberGames(Integer page, Integer size,Boolean isShow,Long gameId);

    GuessRecord guessNumberByAccount(GuessRecord guessRecord);
//
//    Map getPauseNumber();

    GameJson getAvailGameInfo(String accountNo,Long id);

    List<GuessNumberGame> getGuessNumberGamePerson(String accountNo,Long gameId);

    BaseWinner getWinnerList(Long guessNumberId, String accountNo);

    BaseWinner getRecentlyWinnerList(Long gameId,String accountNo);

    void runningLuck(Long guessId);

    Map getWinnerListByAdmin(Integer page, Integer size, Long guessNumberId,Long unit);

    void testDummyLottery();
}
