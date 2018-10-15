package com.aethercoder.core.dao;

import com.aethercoder.core.entity.member.MemberLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/1/23
 * @modified By:
 */
@Repository
public interface MemberDao extends JpaRepository<MemberLevel, Long> {

    List<MemberLevel> findAll();
}
