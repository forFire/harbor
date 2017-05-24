package com.capcare.harbor.protocol;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;


/**
 *采集设备心跳时间
 *用来判断设备是否连通（工作）
 **/
public class EquipmentTime implements MessageCreator , Serializable, Cloneable {
	
	/**
	 * 
	 */

	/**
	 * 
	 */
	private static final long serialVersionUID = 2729532989078906857L;

	/**
	 * 
	 */
	private String equipmentCode;
	
	private String time;

	
	public String getEquipmentCode() {
		return equipmentCode;
	}

	public void setEquipmentCode(String equipmentCode) {
		this.equipmentCode = equipmentCode;
	}


	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}
	
	
	
	

}
