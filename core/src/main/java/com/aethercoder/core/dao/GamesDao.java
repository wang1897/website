package com.aethercoder.core.dao;

import com.aethercoder.core.entity.guess.Games;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by guofeiyan on 2018/01/29.
 */
@Repository
public interface GamesDao extends JpaRepository<Games, Long> {

    Games findByZhNameAndUnit(String name,Long unit);

    Games queryGamesByZhName(String name);

    Page<Games> findAll(Specification<Games> isShow, Pageable pageable);
}
