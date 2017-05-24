package com.capcare.harbor.protocol;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

public class SmsAlarm implements MessageCreator, Serializable{

	private static final long serialVersionUID = 8310499136485796877L;

	private String deviceSn;
	
	private String content;
	
	
	
	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
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
		StringBuilder sb = new StringBuilder();
		sb.append("SmsAlarm[");
		sb.append("sn=");
		sb.append(getDeviceSn());
		sb.append(",content=");
		sb.append(getContent());
		sb.append("]");
		return sb.toString();
	}
}
