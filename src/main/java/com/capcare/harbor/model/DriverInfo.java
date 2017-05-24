package com.capcare.harbor.model;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

public class DriverInfo implements MessageCreator, Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 7416897249289146706L;
    // 驾驶员姓名长度
	private Integer nameLength;
	// 驾驶员姓名
	private String driverName;
	// 驾驶员身份证
	private String driverNo;
	// 从业资格证编码
	private String cardCode;
	// 发证机构名称长度
	private Integer certLength;
	// 发证机构名称
	private String certName;

	public Integer getNameLength() {
		return nameLength;
	}

	public void setNameLength(Integer nameLength) {
		this.nameLength = nameLength;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getDriverNo() {
		return driverNo;
	}

	public void setDriverNo(String driverNo) {
		this.driverNo = driverNo;
	}

	public String getCardCode() {
		return cardCode;
	}

	public void setCardCode(String cardCode) {
		this.cardCode = cardCode;
	}

	public Integer getCertLength() {
		return certLength;
	}

	public void setCertLength(Integer certLength) {
		this.certLength = certLength;
	}

	public String getCertName() {
		return certName;
	}

	public void setCertName(String certName) {
		this.certName = certName;
	}

	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}

}
