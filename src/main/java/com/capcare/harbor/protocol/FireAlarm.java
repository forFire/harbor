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
 * @author zf 消防设施部件状态---设备报警表
 */
public class FireAlarm implements MessageCreator, Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	
	// 部件地址(探测器编码)---设备编码
	private String sn;

	// 报警时间 yyyyMMddhhssmm
	private String alarmTime;

	// 入库时间
	private String createTime;

	// 报警类型
	private String type;

	// 人工状态
	private String statusPerson;

	// 系统状态
	private String statusSystem;

	// 状态
	private String status;

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatusPerson() {
		return statusPerson;
	}

	public void setStatusPerson(String statusPerson) {
		this.statusPerson = statusPerson;
	}

	public String getStatusSystem() {
		return statusSystem;
	}

	public void setStatusSystem(String statusSystem) {
		this.statusSystem = statusSystem;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 此方法必须写 不然报空指针错误！！！！！！！！
	 * @see org.springframework.jms.core.MessageCreator#createMessage(javax.jms.
	 * Session)
	 */
	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}

}
