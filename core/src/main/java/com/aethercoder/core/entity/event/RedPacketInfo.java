package com.aethercoder.core.entity.event;

import java.util.List;

/**
 * @auther jiawei.tao
 * @date 2017/12/9 下午5:17
 */
public class RedPacketInfo {
    private SendRedPacket sendRedPacket;

    private List<GetRedPacket> redPacketList;

    public SendRedPacket getSendRedPacket() {
        return sendRedPacket;
    }

    public void setSendRedPacket(SendRedPacket sendRedPacket) {
        this.sendRedPacket = sendRedPacket;
    }

    public List<GetRedPacket> getRedPacketList() {
        return redPacketList;
    }

    public void setRedPacketList(List<GetRedPacket> redPacketList) {
        this.redPacketList = redPacketList;
    }
}
