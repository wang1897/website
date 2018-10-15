package com.aethercoder.core.service;

import com.aethercoder.core.entity.event.RedPacketInfo;
import com.aethercoder.core.entity.event.SendRedPacket;
import org.springframework.data.domain.Page;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:41
 */
public interface SendRedPacketService {

    SendRedPacket createRedPacket(SendRedPacket redPacket);

    SendRedPacket findOne(long id);

    RedPacketInfo getRedPacketInfo(Long redPacketId);

    Page<SendRedPacket> findSendPacketsByAccountNo(Integer page, Integer size, String accountNo,Long unit);
}
