package io.rong.messages;

/**
 *
 * Q包通知消息。（提币 充值）
 *
 */
public class QbagMessage extends BaseMessage{
	private transient static final String TYPE = "Qbao:QbagMessage";
	private String content;

	public QbagMessage(String content) {
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