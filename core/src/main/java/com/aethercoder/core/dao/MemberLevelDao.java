package com.aethercoder.core.dao;

import com.aethercoder.core.entity.member.MemberLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/1/23
 * @modified By:
 */
@Repository
public interface MemberLevelDao extends JpaRepository<MemberLevel, Long> {


    MemberLevel findMemberLevelByLevel(Integer level);

    @Query(name = "SELECT * FROM MemberLevel order by id desc limit 0,1",nativeQuery = true)
    MemberLevel findFirstByMoneyIsNotNullOrderByMoneyDesc();
}
