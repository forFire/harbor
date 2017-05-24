package com.capcare.harbor.protocol;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

/*
 * 当前位置 最新状态 点
 */
public class Conductor_GPS implements MessageCreator, Serializable {

	private static final long serialVersionUID = 6315693568553855786L;
	
	private String accMode;//类型
	private String satellite_no;
	
	
	public String getAccMode() {
		return accMode;
	}

	public void setAccMode(String accMode) {
		this.accMode = accMode;
	}

	public String getSatellite_no() {
		return satellite_no;
	}

	public void setSatellite_no(String satellite_no) {
		this.satellite_no = satellite_no;
	}
	
	@Override
    public Message createMessage (Session session) throws JMSException {

        ObjectMessage objectMessage = session.createObjectMessage (this);
        return objectMessage;
    }
}
