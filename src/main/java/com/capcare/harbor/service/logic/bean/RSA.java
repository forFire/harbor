package com.capcare.harbor.service.logic.bean;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

public class RSA implements MessageCreator, Serializable {

	private static final long serialVersionUID = -7956801655550501865L;
	private Integer e;
	private String n;

	public RSA() {
	}

	public RSA(Integer e, String n) {
		this.e = e;
		this.n = n;
	}

	public Integer getE() {
		return e;
	}

	public void setE(Integer e) {
		this.e = e;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}

}
