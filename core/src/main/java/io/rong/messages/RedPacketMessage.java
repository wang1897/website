package io.rong.messages;

import com.aethercoder.core.entity.event.SendRedPacket;

/**
 *
 * 群组提示条（小灰条）通知消息。此类型消息没有 Push 通知。
 *
 */
public class RedPacketMessage extends BaseMessage{
	private SendRedPacket content = new SendRedPacket();
	private transient static final String TYPE = "Qbao:RedPacketMsg";

	public RedPacketMessage(SendRedPacket content) {
		this.content = content;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String toString() {
		return this.content.toString();
	}
}