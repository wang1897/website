package com.aethercoder.activity.dao.guessGamble;

import com.aethercoder.activity.entity.guessGamble.GuessGamble;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by hepengfei on 27/02/2018.
 */
@Repository
public interface GuessGambleDao extends JpaRepository<GuessGamble, Long> {

    @Query(value = "SELECT * FROM d_qbao_activity.t_guess_gamble g where g.game_id = :gameId  and g.status !=0 order by ?#{#pageable}",countQuery = "SELECT count(*) FROM d_qbao_activity.t_guess_gamble g where g.game_id = :gameId  and g.status not in(0)",nativeQuery = true)
    Page<GuessGamble> queryGuessGamblesActivited(@Param("gameId") Long gameId,Pageable pageable);

    List<GuessGamble> queryGuessGamblesByStatus(Integer status);

    Page<GuessGamble> findAll(Specification<GuessGamble> specification, Pageable pageable);

  /*  @Query(value = "SELECT g.* FROM d_qbao_activity.t_guess_gamble g inner join d_qbao_activity.t_join_gamble j on  j.gamble_id = g.id where g.game_id = :gameId and j.account_no = :accountNo GROUP BY g.id order by ?#{#pageable}",
            countQuery = "SELECT COUNT(*) FROM d_qbao_activity.t_guess_gamble g inner join d_qbao_activity.t_join_gamble j on  j.gamble_id = g.id where g.game_id = :gameId and j.account_no = :accountNo GROUP BY g.id ",
            nativeQuery = true)
    Page<GuessGamble> queryAllByAccountNo(@Param("accountNo") String accountNo, @Param("gameId") Long gameId, Pageable pageable);
*/
    @Query(value = "SELECT g.id FROM d_qbao_activity.t_guess_gamble g where g.game_id = :gameId and g.luck_time >= :beginTime and g.luck_time <=:endTime",
            nativeQuery = true)
    Long[] queryGuessGamblesByLuckTime(@Param("gameId")Long gameId , @Param("beginTime")Date beginTime,@Param("endTime")Date endTime);
}
