package com.aethercoder.core.dao;

import com.aethercoder.core.entity.social.GroupOfTags;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2018/1/3 下午2:47
 */
public interface GroupOfTagsDao extends JpaRepository<GroupOfTags, Long> {

    void deleteByGroupNo(String groupNo);

    List<GroupOfTags> findGroupOfTagsByTagIn(Long[] tag);

}
