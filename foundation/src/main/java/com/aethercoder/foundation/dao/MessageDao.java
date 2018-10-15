package com.aethercoder.foundation.dao;

import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.entity.i18n.Message;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@CacheConfig(cacheNames = CommonConstants.REDIS_CACHE_NAME_MESSAGE)
public interface MessageDao extends JpaRepository<Message, Long> {

    @Cacheable(key ="T(com.aethercoder.foundation.contants.CommonConstants).REDIS_KEY_WALLET_MESSAGE.concat(T(com.aethercoder.foundation.contants.CommonConstants).REDIS_KEY_WALLET_MESSAGE_FIELD)" +
            ".concat(#p0).concat('-').concat(#p1).concat('-').concat(#p2).concat('-').concat(#p3)")
    Message findByTableAndFieldAndResourceIdAndLanguage(String table, String field, String resourceId, String language);

    @Cacheable(key ="T(com.aethercoder.foundation.contants.CommonConstants).REDIS_KEY_WALLET_MESSAGE.concat(T(com.aethercoder.foundation.contants.CommonConstants).REDIS_KEY_WALLET_MESSAGE_CODE)" +
            ".concat(#p0).concat('-').concat(#p1).concat('-').concat(#p2)")
    Message findByTableAndCodeAndLanguage(String table, String code, String language);


    List<Message> findByTableAndFieldAndResourceId(String table, String field, String resourceId);

    List<Message> findByTableAndFieldAndLanguageAndMessageLike(String table, String field,String language,String message);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Message> List<S> save(Iterable<S> iterable);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Message> S saveAndFlush(S s);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Message> S save(S s);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Long aLong);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Message message);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Iterable<? extends Message> iterable);

    @Override
    void deleteAll();
}
