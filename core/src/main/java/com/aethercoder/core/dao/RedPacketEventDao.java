package com.aethercoder.core.dao;

import com.aethercoder.core.entity.event.RedPacketEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @auther Guo Feiyan
 * @date 2018/1/4 下午3:28
 */
public interface RedPacketEventDao extends JpaRepository<RedPacketEvent, Long> {
    Page<RedPacketEvent> findAll(Specification<RedPacketEvent> specification, Pageable pageable);
}
