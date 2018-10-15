package com.aethercoder.core.dao;

import com.aethercoder.core.entity.social.GroupMember;
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
 * Created by jiawei.tao on 2017/10/16.
 */
@Repository
public interface GroupMemberDao extends JpaRepository<GroupMember, Long>{

    GroupMember findGroupMemberByGroupNoAndMemberNo(String groupNo, String MemberNo);

    GroupMember findGroupMemberByGroupNoAndRoleAndIsDeletedFalse(String groupNo, Integer role);

    GroupMember findGroupMemberByGroupNoAndMemberNoAndIsDeletedFalse(String groupNo, String MemberNo);

    Integer countByGroupNo(String groupNo);

    Integer countByGroupNoAndIsDeletedIsFalse(String groupNo);

    Integer countByGroupNoAndIsDeletedIsFalseAndRoleIsGreaterThanEqual(String groupNo, Integer role);

    List<GroupMember> findAllByGroupNoAndRoleGreaterThanEqualAndIsDeletedIsFalse(String groupNo,Integer role);

    List<GroupMember> findAllByMemberNoAndIsDeletedIsFalse(String memberNo);

    List<GroupMember> findAllByGroupNoAndIsDeletedIsFalse(String groupNo);

    List<GroupMember> findGroupMembersByRoleAndIsDeletedFalseOrderByIdDesc(Integer role);

    List<GroupMember> findFirst4ByGroupNoAndIsDeletedIsFalseOrderByRoleDescCreateTimeAsc(String groupNo);

    List<GroupMember> findAllByGroupNoAndUpdateTimeAfter(String groupNo, Date updateTime);

    @Query(value = "select gm.* from qbao_schema.chat_group_member gm join qbao_schema.user_group ug on gm.group_no = ug.group_no where ug.account_no =:accountNo and gm.update_time >= :updateTime",nativeQuery = true)
    List<GroupMember> findAllByUpdateTimeAfterForSync (@Param("accountNo")String accountNo,@Param("updateTime")Date updateTime);

    Page<GroupMember> findAll(Specification<GroupMember> specification, Pageable pageable);

    Page<GroupMember> findAllByGroupNoAndIsDeletedIsFalse(Specification<GroupMember> specification, Pageable pageable);

   /* @Transactional
    @Query(value = "UPDATE qbao_schema.chat_group_member cg,qbao_schema.user_group ug SET cg.display_name = :displayName, cg.header_url = :headerUrl WHERE cg.id IN (SELECT  temp.id FROM (SELECT id FROM qbao_schema.chat_group_member WHERE member_no = :accountNo) AS temp) AND cg.member_no = ug.account_no AND cg.is_deleted = 0 AND ug.is_deleted = 0 AND ug.display_name IS NULL AND ug.header_url IS NULL", nativeQuery = true)
    @Modifying
    void updateGroupMemeberByAccountNo(@Param("accountNo") String accountNo, @Param("headerUrl")String headerUrl, @Param("displayName")String displayName);*/
}
