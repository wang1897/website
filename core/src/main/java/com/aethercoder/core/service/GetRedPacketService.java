package com.aethercoder.core.service;

import com.aethercoder.core.entity.event.GetRedPacket;
import org.springframework.data.domain.Page;

/**
 * @auther Guo Feiyan
 * @date 2017/12/7 下午5:42
 */
public interface GetRedPacketService {

    GetRedPacket getRedPacket(GetRedPacket redPacket);

    Page<GetRedPacket> findGetPacketsByAccountNo(Integer page, Integer size, String accountNo,Long unit);
}
