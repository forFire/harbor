/**
 * 
 */
package com.capcare.harbor.protocol;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

/**
 * @author fss
 *
 */
public class PushMsg implements MessageCreator, Serializable{

	private static final long serialVersionUID = 3169619780063447470L;
	
	private Long userId;
	
	private String appName;

	private String content;

	public PushMsg(Long userId, String appName, String content) {
		this.userId = userId;
		this.appName = appName;
		this.content = content;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}
	
	@Override
	public String toString() {
		return "DeviceProtocol [userId=" + userId
				+ ", appName=" + appName +", content="+content+"]";
	}
}
