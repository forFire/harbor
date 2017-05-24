package com.capcare.harbor.protocol;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;


/**
 *中控室（城安设备）心跳时间
 *用来判断设备是否连通（工作）
 **/
public class RoomTime implements MessageCreator , Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3108557717538381823L;

	/**
	 * 
	 */

	private String roomCode;
	
	private String time;

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
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
