package com.aethercoder.activity.dao.guessGamble;

import com.aethercoder.activity.entity.guessGamble.GambleRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guofeiyan on 27/02/2018.
 */
@Repository
public interface GambleRankDao extends JpaRepository<GambleRank, Long> {
}
