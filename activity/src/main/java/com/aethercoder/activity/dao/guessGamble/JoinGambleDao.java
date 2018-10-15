package com.aethercoder.activity.dao.guessGamble;

import com.aethercoder.activity.entity.guessGamble.JoinGamble;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by guofeiyan on 27/02/2018.
 */
@Repository
public interface JoinGambleDao extends JpaRepository<JoinGamble, Long> {

    List<JoinGamble> queryAllByGambleId(Long id);

    Integer countAllByAccountNo(String accountNo);

    @Query(value = "SELECT sum(win_amount-amount) FROM d_qbao_activity.t_join_gamble where account_no = :accountNo and is_luck = 1", nativeQuery = true)
    BigDecimal sumAmount(@Param("accountNo") String accountNo);

    @Query(value = "SELECT sum(amount) FROM d_qbao_activity.t_join_gamble where account_no = :accountNo AND gamble_id = :gambleId and account_option = :option", nativeQuery = true)
    BigDecimal sumAmountByAccountNo(@Param("accountNo") String accountNo,@Param("option")Character option,@Param("gambleId")Long gamble);


    @Query(value = "SELECT gamble_id FROM  d_qbao_activity.t_join_gamble where account_no = :accountNo GROUP BY gamble_id order by ?#{#pageable}",
            countQuery = "select count(*) from (SELECT gamble_id FROM  d_qbao_activity.t_join_gamble where account_no = :accountNo  GROUP BY gamble_id order by gamble_id desc) d; ",
            nativeQuery = true)
    Page queryAllByAccountNo(@Param("accountNo") String accountNo, Pageable pageable);


    @Query(value = "SELECT account_no,sum(win_amount-amount) allAmount FROM d_qbao_activity.t_join_gamble where gamble_id in (:gambleIds) group by account_no order by allAmount desc limit 0,10",nativeQuery = true)
    List queryAllByGambleIdInOrderByAmountDesc(@Param("gambleIds") Long[] gambleIds);

    @Query(value = "SELECT account_no,sum(win_amount-amount) allAmount FROM d_qbao_activity.t_join_gamble where gamble_id in (:gambleIds) group by account_no order by allAmount asc limit 0,10",nativeQuery = true)
    List queryAllByGambleIdInOrderByAmountAsc(@Param("gambleIds") Long[] gambleIds);

}
