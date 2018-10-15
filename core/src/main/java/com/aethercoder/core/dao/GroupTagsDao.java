package com.aethercoder.core.dao;

import com.aethercoder.core.entity.social.GroupTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2018/1/3 下午2:46
 */
public interface GroupTagsDao extends JpaRepository<GroupTags, Long> {


    GroupTags findGroupTagsByIdAndIsDeleteFalse(long id);

    GroupTags findGroupTagsByNameAndAndIsDeleteFalse(String name);

    List<GroupTags> findAllByIsDeleteFalse();

    @Query(value ="SELECT gt.id,gt.name,count(go.tag) from   qbao_schema.group_tags gt  left join qbao_schema.chat_group go on go.tag = gt.name where gt.is_delete = 0  group by go.tag,gt.id order by gt.sequence ", nativeQuery = true)
    List findGroupTags();

    @Query(value = "SELECT gt.* FROM qbao_schema.group_tags gt inner join qbao_schema.group_of_tags got on gt.id = got.tag where gt.is_delete = 0 and got.group_no = :groupNo", nativeQuery = true)
    List<GroupTags> findGroupTagsByGroupNo(@Param("groupNo") String groupNo);

    GroupTags findFirstByIsDeleteFalseOrderBySequenceDesc();

}
