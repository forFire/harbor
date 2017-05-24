package com.capcare.harbor.service.logic.bean;

import java.io.Serializable;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

/**
 * 存储多媒体数据检索命令
 * 
 * @author xp.zhang
 * 
 */
public class MediaRetrievalReply implements MessageCreator, Serializable {

	private static final long serialVersionUID = -7956801655550501865L;

	private Integer num;

	private List<MediaItem> items;

	public MediaRetrievalReply() {
		super();
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	public List<MediaItem> getItems() {
		return items;
	}

	public void setItems(List<MediaItem> items) {
		this.items = items;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}

}