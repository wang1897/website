package com.aethercoder.core.dao;

import com.aethercoder.core.entity.social.GroupManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @auther Guo Feiyan
 * @date 2017/12/6 上午11:29
 */
@Repository
public interface GroupManagerDao  extends JpaRepository<GroupManager, Long> {


    Page<GroupManager> findAll(Specification<GroupManager> email, Pageable pageable);

    GroupManager findByAccountNo(String accountNo);


}
