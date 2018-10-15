package com.aethercoder.core.dao;

import com.aethercoder.core.entity.media.Media;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author lilangfeng
 * @date 2018/01/18
 */

public interface MediaDao extends JpaRepository<Media, Long> {


    Page<Media> findAll(Specification<Media> specification, Pageable pageable);

    List<Media> findMediaByLanguageTypeAndStatus(String languageType, Integer status);

    Media findMediaById(Long id);
}
