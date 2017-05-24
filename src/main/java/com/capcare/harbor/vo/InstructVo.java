package com.capcare.harbor.vo;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

/**
 * @author fyq
 */
public class InstructVo implements MessageCreator, Serializable {

	private static final long serialVersionUID = 3803153283622046688L;

	private String deviceSn;

	private Map<String, Object> cmdMap;
	
	private InstructType instructType;


	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}

	public InstructType getInstructType() {
		return instructType;
	}

	public void setInstructType(InstructType instructType) {
		this.instructType = instructType;
	}

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	public Map<String, Object> getCmdMap() {
		return cmdMap;
	}


	public void setCmdMap(Map<String, Object> cmdMap) {
		this.cmdMap = cmdMap;
	}

	
	public static void main(String[] args){

			UUID uid = UUID.randomUUID();
			System.out.println(uid.toString());
			System.out.println(uid.toString().getBytes());
			System.out.println(uid.getLeastSignificantBits());
		
	}
	
}
