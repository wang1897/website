package com.aethercoder.core.dao;

import com.aethercoder.core.entity.social.SystemNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SystemNoticeDao extends JpaRepository<SystemNotice,Long> {
    Page<SystemNotice> findAll(Specification<SystemNotice> content, Pageable pageable);
    List<SystemNotice> findAllByLanguageTypeAndStatus(String language,Integer status);
}
