package com.aethercoder.core.dao;

import com.aethercoder.core.entity.social.Group;
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
 * Created by jiawei.tao on 2017/10/10.
 */
@Repository
public interface GroupDao extends JpaRepository<Group, Long>{
    Group findGroupByName(String name);

    Group findGroupByGroupNoAndIsDeletedIsFalse(String groupNo);

    Group findGroupByGroupNo(String groupNo);

    @Query(value = "SELECT cg.* FROM qbao_schema.chat_group cg JOIN qbao_schema.user_group ug ON cg.group_no = ug.group_no AND cg.is_deleted = FALSE AND ug.is_deleted = FALSE AND ug.account_no = :accountNo AND cg.update_time >= :updateTime",nativeQuery = true)
    List<Group> findAllByUpdateTimeAfterForSync (@Param("accountNo")String accountNo, @Param("updateTime")Date updateTime);

    Page<Group> findAll(Specification<Group> specification, Pageable pageable);

    Page<Group> findByIsDeletedIsFalse(Specification<Group> specification, Pageable pageable);

    Group findByGroupNo(String groupNo);

    @Query(value = "select c.* from qbao_schema.chat_group_member m , qbao_schema.chat_group c  where  m.group_no = c.group_no and m.member_no = :accountNo  and c.is_deleted = 0 and m.is_deleted = 0 and m.role = 2 ORDER BY c.id DESC ",nativeQuery = true)
    List<Group> findGroupsByHoster(@Param("accountNo") String accountNo);

    List<Group> findByLevelAndIsDeletedIsFalseOrderBySequenceAsc(Integer level);

    List<Group> findGroupsByTagAndIsDeletedFalse(String tag);

    @Query(value = "SELECT * from   qbao_schema.chat_group go left join qbao_schema.group_tags gt  on go.tag = gt.name where go.level=2 and gt.is_delete = 0 and go.is_deleted = 0  order by gt.sequence, go.sequence ",nativeQuery = true)
    List<Group> getHotGroups();
}
