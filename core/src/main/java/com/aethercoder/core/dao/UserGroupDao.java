package com.aethercoder.core.dao;

import com.aethercoder.core.entity.social.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by jiawei.tao on 2017/10/17.
 */
@Repository
public interface UserGroupDao extends JpaRepository<UserGroup, Long>{
    List<UserGroup> findUserGroupsByAccountNo(String accounNo);

    List<UserGroup> findUserGroupsByGroupNoAndIsDeletedIsFalse(String groupNo);

    UserGroup findUserGroupByGroupNoAndAccountNo(String groupNo, String accounNo);

    List<UserGroup> findAllByAccountNoAndUpdateTimeAfter(String accountNo, Date updateTime);

    List<UserGroup> findUserGroupsByAccountNoAndIsDeletedIsFalse(String accountNo);


//    Page<UserGroup> findAll(Specification<UserGroup> specification, Pageable pageable);
}
