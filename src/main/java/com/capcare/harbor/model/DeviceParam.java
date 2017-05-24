package com.capcare.harbor.model;

import java.io.Serializable;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

public class DeviceParam implements MessageCreator, Serializable{

	private static final long serialVersionUID = -7956801655550501865L;

	private Integer serial;
	private Integer num;
	private List<ItemReply> items;

	public List<ItemReply> getItems() {
		return items;
	}

	public void setItems(List<ItemReply> items) {
		this.items = items;
	}

	public DeviceParam() {
		super();
	}

	public Integer getSerial() {
		return serial;
	}

	public void setSerial(Integer serial) {
		this.serial = serial;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}

}
