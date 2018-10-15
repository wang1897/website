package io.rong.messages;

import io.rong.util.GsonUtil;

/**
 *
 * 群组提示条（小灰条）通知消息。此类型消息没有 Push 通知。
 *
 */
public class GroupNtfMessage  {
	private String operatorUserId = "";
	private String operation = "";
	private String data = "";
	private String message = "";
	private transient static final String TYPE = "RC:GrpNtf";

	public GroupNtfMessage(String operatorUserId, String operation, String data, String message) {
		this.message = message;
		this.operatorUserId = operatorUserId;
		this.operation = operation;
		this.data = data;
	}

	public String getType() {
		return TYPE;
	}
	
	/**
	 * 获取提示条消息内容。
	 *
	 * @returnString
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * 设置提示条消息内容。
	 *
	 * @return
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return GsonUtil.toJson(this, GroupNtfMessage.class);
	}
}