package io.rong.messages;

import io.rong.util.GsonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;

/**
 *
 * 添加联系人消息。
 *
 */
@Entity
@ApiModel(value="添加联系人消息对象",description="添加联系人消息")
public class ContactNtfMessage extends BaseMessage {
	private String operation = "";
	private String extra = "";
	private String sourceUserId = "";
	private String targetUserId = "";
	private String message = "";
	private transient static final String TYPE = "RC:ContactNtf";
	
	public ContactNtfMessage(String operation, String extra, String sourceUserId, String targetUserId, String message) {
		this.operation = operation;
		this.extra = extra;
		this.sourceUserId = sourceUserId;
		this.targetUserId = targetUserId;
		this.message = message;
	}

	public ContactNtfMessage() {
	}

	public String getType() {
		return TYPE;
	}
	
	/**
	 * 获取操作名。
	 *
	 * @returnString
	 */
	@ApiModelProperty(position = 1, required = true, value="获取操作名")
	public String getOperation() {
		return operation;
	}
	
	/**
	 * 设置操作名。
	 *
	 * @return
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}  
	
	/**
	 * 获取为附加信息(如果开发者自己需要，可以自己在 App 端进行解析)。
	 *
	 * @returnString
	 */
	@ApiModelProperty(position = 2, required = false, value="获取为附加信息")
	public String getExtra() {
		return extra;
	}
	
	/**
	 * 设置为附加信息(如果开发者自己需要，可以自己在 App 端进行解析)。
	 *
	 * @return
	 */
	public void setExtra(String extra) {
		this.extra = extra;
	}  
	
	/**
	 * 获取请求者或者响应者的 UserId。
	 *
	 * @returnString
	 */
	@ApiModelProperty(position = 3, required = true, value="获取请求者userId")
	public String getSourceUserId() {
		return sourceUserId;
	}
	
	/**
	 * 设置请求者或者响应者的 UserId。
	 *
	 * @return
	 */
	public void setSourceUserId(String sourceUserId) {
		this.sourceUserId = sourceUserId;
	}  
	
	/**
	 * 获取被请求者或者被响应者的 UserId。
	 *
	 * @returnString
	 */
	@ApiModelProperty(position = 4, required = true, value="获取被响应者userId")
	public String getTargetUserId() {
		return targetUserId;
	}
	
	/**
	 * 设置被请求者或者被响应者的 UserId。
	 *
	 * @return
	 */
	public void setTargetUserId(String targetUserId) {
		this.targetUserId = targetUserId;
	}  
	
	/**
	 * 获取请求或者响应消息。
	 *
	 * @returnString
	 */
	@ApiModelProperty(position = 5, required = true, value="获取请求或者响应消息")
	public String getMessage() {
		return message;
	}
	
	/**
	 * 设置请求或者响应消息。
	 *
	 * @return
	 */
	public void setMessage(String message) {
		this.message = message;
	}  
	
	@Override
	public String toString() {
		return GsonUtil.toJson(this, ContactNtfMessage.class);
	}
}