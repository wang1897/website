package com.aethercoder.core.service;

import com.aethercoder.core.entity.event.RedPacketEvent;

/**
 * @auther Guo Feiyan
 * @date 2018/1/4 下午3:29
 */
public interface RedPacketEventService {

    RedPacketEvent saveRedPacketEvent(RedPacketEvent redPacketEvent);

    RedPacketEvent getRedPacketEventInfo(Long id);

    void updateRedPacketEvent(RedPacketEvent redPacketEvent);

    org.springframework.data.domain.Page<RedPacketEvent> findAllRedPacketEvent(Integer page, Integer size, String groupNo, Long unit, String beginDate, String endDate, Boolean isDeleted);

}
