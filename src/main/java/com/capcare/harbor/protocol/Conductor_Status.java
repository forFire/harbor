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
public class Conductor_Status implements MessageCreator, Serializable {

	
	private static final long serialVersionUID = -7428643430478742286L;
	
	private String accMode;//类型
	private String bean1;//波束1
	private String bean2;//
	private String bean3;//
	private String bean4;//
	private String bean5;//
	private String bean6;//
	private String bean7;//
	private String bean8;//
	private String bean9;//
	private String bean10;//
	
	
	public String getAccMode() {
		return accMode;
	}
	public void setAccMode(String accMode) {
		this.accMode = accMode;
	}
	public String getBean1() {
		return bean1;
	}
	public void setBean1(String bean1) {
		this.bean1 = bean1;
	}
	public String getBean2() {
		return bean2;
	}
	public void setBean2(String bean2) {
		this.bean2 = bean2;
	}
	public String getBean3() {
		return bean3;
	}
	public void setBean3(String bean3) {
		this.bean3 = bean3;
	}
	public String getBean4() {
		return bean4;
	}
	public void setBean4(String bean4) {
		this.bean4 = bean4;
	}
	public String getBean5() {
		return bean5;
	}
	public void setBean5(String bean5) {
		this.bean5 = bean5;
	}
	public String getBean6() {
		return bean6;
	}
	public void setBean6(String bean6) {
		this.bean6 = bean6;
	}
	public String getBean7() {
		return bean7;
	}
	public void setBean7(String bean7) {
		this.bean7 = bean7;
	}
	public String getBean8() {
		return bean8;
	}
	public void setBean8(String bean8) {
		this.bean8 = bean8;
	}
	public String getBean9() {
		return bean9;
	}
	public void setBean9(String bean9) {
		this.bean9 = bean9;
	}
	public String getBean10() {
		return bean10;
	}
	public void setBean10(String bean10) {
		this.bean10 = bean10;
	}
	
	@Override
    public Message createMessage (Session session) throws JMSException {

        ObjectMessage objectMessage = session.createObjectMessage (this);
        return objectMessage;
    }
}
