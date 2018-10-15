package com.aethercoder.core.dao;

import com.aethercoder.core.entity.guess.GuessNumberGame;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by guofeiyan on 2018/01/29.
 */
@Repository
public interface GuessNumberGameDao extends JpaRepository<GuessNumberGame, Long> {

    GuessNumberGame findGuessNumberGameByZhNameAndIsDeleteFalseAndGameId(String name, Long id);

    List<GuessNumberGame> findByBeginBlockIsGreaterThanAndIsDeleteIsFalseAndLuckTimeIsNull(Integer beginBlock);

    List<GuessNumberGame> findGuessNumberGamesByGameIdAndIsDeleteFalse(Long id);

    @Query(value = "select count(*) from qbao_schema.guess_number_game g Where game_id= :id and  is_delete=0 and ( ((:beginBlock between g.begin_block and g.end_block) or :endBlock between g.begin_block and g.end_block))", nativeQuery = true)
    Long getGuessNumberGameActivate(@Param("beginBlock") Integer beginBlock, @Param("endBlock") Integer endBlock, @Param("id") long id);

    Page<GuessNumberGame> findAll(Specification<GuessNumberGame> specification, Pageable pageable);

    @Query(value = "select*from qbao_schema.guess_number_game g where game_id=:gameId and g.game_start_time<=now() and g.game_end_time is null and is_delete = 0", nativeQuery = true)
    GuessNumberGame findGuessNumberGameAvail(@Param("gameId") Long id);

    GuessNumberGame findByIdAndIsDeleteIsFalse(Long id);

    List<GuessNumberGame> findByGameIdAndIsDeleteIsFalseOrderByBeginBlockDesc(Long gameId);
    List<GuessNumberGame> findByGameIdAndIsDeleteIsFalseOrderByGameEndTimeDesc(Long gameId);

    GuessNumberGame findGuessNumberGameByBeginBlock(Integer beginBlock);

    GuessNumberGame findGuessNumberGameByEndBlock(Integer endBlock);

    GuessNumberGame findGuessNumberGameByLuckBlock(Integer luckBlock);

}
