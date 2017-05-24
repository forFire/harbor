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
public class Conductor_UserData implements MessageCreator, Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1752513097391945927L;
	
	private String accMode;//类型
	private String conductor_sn;
	private String conductor_addr;//地址
	private String frequentness;//频度
	private String conductor_type;//类型
	private String conductor_level;//通信等级
	private String encryption;//是否加密
	private String conductor_bean;//入站波束
	private String user_no;//所辖用户数
	
	//get set
	public String getAccMode() {
		return accMode;
	}
	public void setAccMode(String accMode) {
		this.accMode = accMode;
	}
	public String getConductor_sn() {
		return conductor_sn;
	}
	public void setConductor_sn(String conductor_sn) {
		this.conductor_sn = conductor_sn;
	}
	public String getConductor_addr() {
		return conductor_addr;
	}
	public void setConductor_addr(String conductor_addr) {
		this.conductor_addr = conductor_addr;
	}
	public String getFrequentness() {
		return frequentness;
	}
	public void setFrequentness(String frequentness) {
		this.frequentness = frequentness;
	}
	public String getConductor_type() {
		return conductor_type;
	}
	public void setConductor_type(String conductor_type) {
		this.conductor_type = conductor_type;
	}
	public String getConductor_level() {
		return conductor_level;
	}
	public void setConductor_level(String conductor_level) {
		this.conductor_level = conductor_level;
	}
	public String getEncryption() {
		return encryption;
	}
	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}
	public String getConductor_bean() {
		return conductor_bean;
	}
	public void setConductor_bean(String conductor_bean) {
		this.conductor_bean = conductor_bean;
	}
	public String getUser_no() {
		return user_no;
	}
	public void setUser_no(String user_no) {
		this.user_no = user_no;
	}
	
	@Override
    public Message createMessage (Session session) throws JMSException {

        ObjectMessage objectMessage = session.createObjectMessage (this);
        return objectMessage;
    }

//    @Override
//    public String toString () {
//
//        StringBuilder sb = new StringBuilder ();
//        sb.append ("Position[");
//        sb.append ("sn=");
//        sb.append (getDeviceSn ());
//        sb.append (",time=");
//        sb.append (getReceive ());
//        sb.append (",lng=");
//        sb.append (getLng ());
//        sb.append (",lat=");
//        sb.append (getLat ());
//        sb.append (",speed=");
//        sb.append (getSpeed ());
//        sb.append (",direction=");
//        sb.append (getDirection ());
//        sb.append (",mode=");
//        sb.append (getMode ());
//        sb.append (",acc=");
//        sb.append (getAccMode ());
//        sb.append (",433=");
//        sb.append (getMode433 ());
//        sb.append (",battery=");
//        sb.append (getBattery ());
//        sb.append (",steps=");
//        sb.append (getSteps ());
//        sb.append (",cell=");
//        sb.append (getCell ());
//        sb.append ("]");
//        return sb.toString ();
//    }
}
