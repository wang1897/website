package com.aethercoder.core.dao;

import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.entity.wallet.SysConfig;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
@Repository
@CacheConfig(cacheNames = RedisConstants.REDIS_CACHE_NAME_CONFIG)
public interface SysConfigDao extends JpaRepository<SysConfig, Long> {

    @Cacheable(key = "T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_SYS_CONFIG.concat(#p0)")
    SysConfig findSysConfigByName(String name);

    List<SysConfig> findSysConfigByNameIsStartingWith(String name);

    @Override
    @CacheEvict(allEntries = true)
    SysConfig saveAndFlush(SysConfig var1);

    @Override
    @CacheEvict(allEntries = true)
    SysConfig save(SysConfig var1);

    @Override
    @CacheEvict(allEntries = true)
    <S extends SysConfig> List<S> save(Iterable<S> var1);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Long var1);

    @Override
    @CacheEvict(allEntries = true)
    void delete(SysConfig var1);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Iterable<? extends SysConfig> var1);

    @Override
    @CacheEvict(allEntries = true)
    void deleteAll();
}
