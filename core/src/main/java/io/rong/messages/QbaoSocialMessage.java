package io.rong.messages;

/**
 *
 * 社交好友通知消息。
 *
 */
public class QbaoSocialMessage extends BaseMessage{
	private transient static final String TYPE = "Qbao:SocialSystemMsg";
	private String content;

	public QbaoSocialMessage(String content) {
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