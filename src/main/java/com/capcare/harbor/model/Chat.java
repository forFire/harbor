package com.capcare.harbor.model;

import java.io.Serializable;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

/**
 * 聊天消息体
 */
@SuppressWarnings("serial")
public class Chat implements MessageCreator, Serializable {
	private String userId;// 发送者id
	private String friendId;// 接受者id
	private Long groupId;// 群组id
	private Date time;// 消息发送时间
	private Integer read;// 1已读 2未读
	private Integer contentType = 1;// 消息类型（1文字,2图片）
	private String content;
	private String info;// 附加内容


	public Chat(String userId, String friendId, String content, Integer contentType, Date time) {
		this.userId = userId;
		this.friendId = friendId;
		this.content = content;
		this.time = time;
		this.contentType = contentType;
	}

	public Chat() {

	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Integer getRead() {
		return read;
	}

	public void setRead(Integer read) {
		this.read = read;
	}

	public Integer getContentType() {
		return contentType;
	}

	public void setContentType(Integer contentType) {
		this.contentType = contentType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Chat[");
		sb.append(",time=");
		sb.append(getTime());
		sb.append(",userId=");
		sb.append(getUserId());
		sb.append(",friendId=");
		sb.append(getFriendId());
		sb.append(",contentType=");
		sb.append(getContentType());
		sb.append(",content=");
		sb.append(getContent());
		sb.append(",info=");
		sb.append(getInfo());
		sb.append("]");
		return sb.toString();
	}
}
