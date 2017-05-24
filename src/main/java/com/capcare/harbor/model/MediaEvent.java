package com.capcare.harbor.model;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

public class MediaEvent implements MessageCreator, Serializable {

	private static final long serialVersionUID = -7956801655550501865L;

	private Integer mediaId;
	private Integer mediaType;
	private Integer mediaCode;
	private Integer channelId;
	private Integer eventCode;

	public MediaEvent() {
	}

	public MediaEvent(Integer mediaId, Integer mediaType, Integer mediaCode,
			Integer channelId, Integer eventCode) {
		super();
		this.mediaId = mediaId;
		this.mediaType = mediaType;
		this.mediaCode = mediaCode;
		this.channelId = channelId;
		this.eventCode = eventCode;
	}

	public Integer getMediaId() {
		return mediaId;
	}

	public void setMediaId(Integer mediaId) {
		this.mediaId = mediaId;
	}

	public Integer getMediaType() {
		return mediaType;
	}

	public void setMediaType(Integer mediaType) {
		this.mediaType = mediaType;
	}

	public Integer getMediaCode() {
		return mediaCode;
	}

	public void setMediaCode(Integer mediaCode) {
		this.mediaCode = mediaCode;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public Integer getEventCode() {
		return eventCode;
	}

	public void setEventCode(Integer eventCode) {
		this.eventCode = eventCode;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}

}