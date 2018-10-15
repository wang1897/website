package com.aethercoder.foundation.dao;

import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.foundation.entity.i18n.Language;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@CacheConfig(cacheNames = CommonConstants.REDIS_CACHE_NAME_LANGUAGE)
public interface LanguageDao extends JpaRepository<Language, Long> {

    @Cacheable(key ="T(com.aethercoder.foundation.contants.CommonConstants).REDIS_KEY_WALLET_LANGUAGE.concat(#root.methodName)")
    Language findByIsDefault(boolean isDefault);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Language> List<S> save(Iterable<S> iterable);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Language> S saveAndFlush(S s);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Language> S save(S s);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Long aLong);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Language language);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Iterable<? extends Language> iterable);

    @Override
    @CacheEvict(allEntries = true)
    void deleteAll();
}
