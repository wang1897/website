package com.aethercoder.core.service;

import com.aethercoder.core.entity.guess.Games;
import org.springframework.data.domain.Page;

/**
 * Created by guofeiyan on 2018/01/29.
 */

public interface GamesService {
    Games saveGame(Games games);

    void updateGame(Games games);

    void deleteGame(Long id,String method);

    Page<Games> findGamesAll(Integer page,Integer size,Boolean isShow,String createTime);

    Page<Games> findActivatedGame(Integer page,Integer size);

}
