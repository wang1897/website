package com.aethercoder.core.dao;

import com.aethercoder.core.entity.social.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Qbao2.0 社交-我的好友
 * @auther Guo Feiyan
 * @date 2017/10/12 下午12:02
 */
@Repository
public interface UserContactDao extends JpaRepository<UserContact, Long> {

    UserContact findUserContactByAccountNoAndContactNo(String accountNo, String contactNo);

    List<UserContact> findUserContactsByAccountNo(String accountNo);

    //同步通讯录
    @Query(value = "select u.* from qbao_schema.user_contact u where u.account_no = :accountNo and u.update_time >= :updateTime ",nativeQuery = true)
    List<UserContact> findByAccountNoAndUpdateTimeAfter(@Param("accountNo")String contractNo, @Param("updateTime") Date date);
}