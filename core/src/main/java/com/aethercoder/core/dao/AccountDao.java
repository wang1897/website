package com.aethercoder.core.dao;

import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.foundation.contants.CommonConstants;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
 * Created by hepengfei on 2017/8/30.
 */
@Repository
@CacheConfig(cacheNames = CommonConstants.REDIS_CACHE_NAME_ACCOUNT)
public interface AccountDao extends JpaRepository<Account, Long>{

    //only call in JwtAuthenticationTokenFilter
    @Cacheable(key ="T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_WALLET_ACCOUNT.concat(#p0)")
    @Deprecated
    Account findAccountByAccountNo(String accountNo);

    Account findByAccountNo(String accountNo);

    @Query("select w from Account w join w.addresses a WHERE a.address = :address")
    Account findByAddress(@Param("address") String address);

    Account findByEmail(String email);

    List<Account> findAccountsByAccountNameLike(String accountName);

    Page<Account> findAll(Specification<Account> specification, Pageable pageable);


    @Query(value = "select ac.* from qbao_schema.wallet_account ac where not exists(select member_no from chat_group_member where member_no=account_no and group_no= :groupNo and is_deleted = FALSE) order by ?#{#pageable}",
            countQuery = "select count(*) from qbao_schema.wallet_account ac where not exists(select member_no from chat_group_member where member_no=account_no and group_no= :groupNo and is_deleted = FALSE) ",
            nativeQuery = true)
    Page<Account> findAllNotInGroup(@Param("groupNo") String groupNo, Pageable pageable);

    @Query(value = "select ac.* from qbao_schema.wallet_account ac where not exists(select member_no from chat_group_member where member_no=account_no and group_no= :groupNo and is_deleted = FALSE) and (locate(:accountName, account_name)>0 or locate(:accountNo,account_no)>0) order by ?#{#pageable}",
            countQuery = "select count(*) from qbao_schema.wallet_account ac where not exists(select member_no from chat_group_member where member_no=account_no and group_no= :groupNo and is_deleted = FALSE) and (locate(:accountName, account_name)>0 or locate(:accountNo,account_no)>0) ",
            nativeQuery = true)
    Page<Account> findAllByMemberNoOrNameAndNotInGroup(@Param("groupNo") String groupNo, @Param("accountNo") String accountNo, @Param("accountName") String accountName, Pageable pageable);

    @Query(value = "select count(sh.account_no) inviteCodeCount, sh.account_no, sh.share_code,sh.account_name,sh.header from wallet_account sh, wallet_account inv where sh.share_code = inv.invite_code and sh.share_code is not null group by sh.share_code order by inviteCodeCount desc",
            nativeQuery = true)
    List getInviteRankingList();

    Account findAccountByShareCode(String inviteCode);

    List<Account> findByInviteCode(String inviteCode);

    @Query(value = "SELECT count(a.account_no) FROM qbao_schema.wallet_account a where  a.create_time >= :beforeTime  and a.invite_code= :inviteCode", nativeQuery = true)
    Integer countByReceiveNumber(@Param("beforeTime") Date time, @Param("inviteCode") String inviteCode);

    Integer countByInviteCode(String shareCode);

    @Query(value = "select COUNT(account_no) from qbao_schema.wallet_account ", nativeQuery = true)
    Integer countAll();

    @Query(value = "select COUNT(account_no) from qbao_schema.wallet_account where create_time >= :startTime and create_time <= :endTime", nativeQuery = true)
    Integer countAccounts(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    //获取今天的新用户
    @Query(value = "select COUNT(account_no) from qbao_schema.wallet_account  where to_days(create_time) = to_days(now())", nativeQuery = true)
    Integer countAccountsByDay();

    //获取本月新用户
    @Query(value = "SELECT COUNT(account_no) FROM qbao_schema.wallet_account WHERE DATE_FORMAT( create_time, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' )", nativeQuery = true)
    Integer countAccountsByMouth();

    List<Account> findAccountsByAccountNoIn(List<String> accountNo);

    @Override
    @CacheEvict(key = "T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_WALLET_ACCOUNT.concat(#p0.getAccountNo())")
    Account saveAndFlush(Account account);

    @Override
    @CacheEvict(key = "T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_WALLET_ACCOUNT.concat(#p0.getAccountNo())")
    Account save(Account account);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Account> List<S> save(Iterable<S> var1);

    @Override
    @CacheEvict(key = "T(com.aethercoder.core.contants.RedisConstants).REDIS_KEY_WALLET_ACCOUNT.concat(#p0.getAccountNo())")
    void delete(Account var1);

    @Override
    @CacheEvict(allEntries = true)
    void delete(Iterable<? extends Account> var1);

    @Override
    @CacheEvict(allEntries = true)
    void deleteAll();

    List<Account> findAccountByLevel(Long level);

    List<Account> findAccountByLevel(Boolean authority);

    List<Account> findByCreateTimeAfter(Date date);
}
