package com.aethercoder.core.dao;

import com.aethercoder.core.entity.event.FundUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jiawei.tao on 2017/11/3.
 */
@Repository
public interface FundUserDao extends JpaRepository<FundUser, Long>{

    FundUser findByEmail(String email);

    FundUser findByUsername(String username);

    Page<FundUser> findAll(Specification<FundUser> specification, Pageable pageable);

    FundUser findByUniqueId(String uniqueId);


//    @Query(value="select ac.* from qbao_schema.wallet_account ac where not exists(select member_no from chat_group_member where member_no=account_no and group_no= :groupNo and is_deleted = FALSE) order by ?#{#pageable}",
//            countQuery = "select count(*) from qbao_schema.wallet_account ac where not exists(select member_no from chat_group_member where member_no=account_no and group_no= :groupNo and is_deleted = FALSE) ",
//            nativeQuery = true)
//    Page<Account> findAllNotInGroup(@Param( "groupNo" ) String groupNo, Pageable pageable);
//
//    @Query(value="select ac.* from qbao_schema.wallet_account ac where not exists(select member_no from chat_group_member where member_no=account_no and group_no= :groupNo and is_deleted = FALSE) and (locate(:accountName, account_name)>0 or locate(:accountNo,account_no)>0) order by ?#{#pageable}",
//            countQuery = "select count(*) from qbao_schema.wallet_account ac where not exists(select member_no from chat_group_member where member_no=account_no and group_no= :groupNo and is_deleted = FALSE) and (locate(:accountName, account_name)>0 or locate(:accountNo,account_no)>0) ",
//            nativeQuery = true)
//    Page<Account> findAllByMemberNoOrNameAndNotInGroup(@Param( "groupNo" ) String groupNo, @Param( "accountNo" ) String accountNo, @Param( "accountName" ) String accountName, Pageable pageable);
}
