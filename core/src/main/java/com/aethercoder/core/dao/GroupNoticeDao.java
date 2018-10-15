package com.aethercoder.core.dao;

import com.aethercoder.core.entity.social.GroupNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @auther Guo Feiyan
 * @date 2017/12/18 下午6:49
 */
public interface GroupNoticeDao  extends JpaRepository<GroupNotice, Long> {

    GroupNotice findGroupNoticeByIdAndIsDeleteFalse(Long id);


    Page<GroupNotice> findAll(Specification<GroupNotice> groupNo, Pageable pageable);

    GroupNotice findFirstByGroupNoAndIsDeleteFalseOrderByCreateTimeDesc(String groupNo);
}
