package com.aethercoder.core.dao;

import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.entity.wallet.Contract;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Created by Guo Feiyan on 2017/8/30.
 */
@Repository
@CacheConfig(cacheNames = RedisConstants.REDIS_CACHE_NAME_CONTRACT)
public interface ContractDao extends JpaRepository<Contract, Long> {

    @Cacheable(key = "T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_SMART_CONTRACT.concat(#p0).concat('-').concat(#p1)")
    Contract findContractByNameAndType(String name,Integer type);

    @Cacheable(key = "T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_SMART_CONTRACT.concat(#p0).concat('-false-false')")
    Contract findContractByNameAndIsDeleteIsFalse(String name);

    @Cacheable(key = "T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_SMART_CONTRACT.concat(#root.methodName)")
    List<Contract> findContractsByIsDeleteIsFalseOrderBySequenceAsc();


    @Cacheable(key = "T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_SMART_CONTRACT.concat(#root.methodName).concat(#p0)")
    List<Contract> findContractsByTypeAndIsDeleteIsFalseOrderBySequenceAsc(Integer type);

    Page<Contract> findAll(Specification<Contract> accountNo, Pageable pageable);

    @Cacheable(key = "T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_SMART_CONTRACT.concat(#root.methodName)")
    List<Contract> findAll();

    @Override
    @CacheEvict(allEntries = true)
    Contract saveAndFlush(Contract var1);

    @Override
    @CacheEvict(allEntries = true)
    Contract save(Contract var1);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Contract> List<S> save(Iterable<S> var1);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Long var1);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Contract var1);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Iterable<? extends Contract> var1);

    @Override
    @CacheEvict(allEntries = true)
    void deleteAll();
}
