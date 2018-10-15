package io.rong.messages;

/**
 *
 * 支付扫码通知消息。
 *
 */
public class QbaoPayMessage extends BaseMessage{
	private transient static final String TYPE = "Qbao:QbaoPayMsg";
	private String content;

	public QbaoPayMessage(String content) {
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