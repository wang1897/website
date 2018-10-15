package com.aethercoder.core.dao;

import com.aethercoder.core.entity.quiz.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface QuizDao extends JpaRepository<Quiz, Long> {


   /* @Query(value = "select * from qbao_schema.quiz as r1 join (select round(rand() * (select max(id) from qbao_schema.quiz)) as id2) as r2 where r1.id >= r2.id2 and is_delete =0 order by r1.id asc limit 5",nativeQuery = true)
    List<Quiz> getQuizzesRound();*/

//    @Cacheable(key ="T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_QUIZ.concat(#p0)")
//    List<Quiz> findQuizzesByLanguageAndIsDeleteFalse(String language);

    Page<Quiz> findAll(Specification<Quiz> specification, Pageable pageable);


    List<Quiz> findQuizzesByType(Long type);
/*
    @Override
    @CacheEvict(key = "T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_QUIZ.concat(#p0.getId())")
    Quiz saveAndFlush(Quiz quiz);

    @Override
    @CacheEvict(key = "T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_QUIZ.concat(#p0.getId())")
    Quiz save(Quiz quiz);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Quiz> List<S> save(Iterable<S> var1);

    @Override
    @CacheEvict(key = "T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_QUIZ.concat(#p0.getId())")
    void delete(Quiz quiz);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Iterable<? extends Quiz> var1);

    @Override
    @CacheEvict(allEntries = true)
    void deleteAll();*/


}
