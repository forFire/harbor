/**
 * 
 */
package com.capcare.harbor.model;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

/**
 * @author zf
 * 建筑消防设施系统状态
 */
public class SystemStatus  implements MessageCreator , Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 系统类型
	private String sysType;
	// 系统地址
	private	String sysAddress;
	// 系统状态
	private String sysStatus;
	
	//时间
	private String time;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getSysType() {
		return sysType;
	}
	public void setSysType(String sysType) {
		this.sysType = sysType;
	}
	public String getSysAddress() {
		return sysAddress;
	}
	public void setSysAddress(String sysAddress) {
		this.sysAddress = sysAddress;
	}
	public String getSysStatus() {
		return sysStatus;
	}
	public void setSysStatus(String sysStatus) {
		this.sysStatus = sysStatus;
	}
	/* (non-Javadoc)
	 * @see org.springframework.jms.core.MessageCreator#createMessage(javax.jms.Session)
	 */
	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}
	
	
	
	
	
	
	
	
	
	
	
}
