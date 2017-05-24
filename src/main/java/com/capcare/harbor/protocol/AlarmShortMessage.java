package com.capcare.harbor.protocol;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.mina.core.session.IoSession;

import com.capcare.harbor.handler.device.beidou.Beidou;
import com.capcare.harbor.model.ShortMessage;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.LoginDevice;

public class AlarmShortMessage extends ShortMessage{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -572932065083099413L;


	public BaseMessage dealWith(String content,IoSession session){
		
		Object businessData = null;
		BaseMessage message = new BaseMessage(Act.ALARM,businessData);
		
		Alarm alarm = new Alarm();
		alarm.setDeviceSn(this.getSendHandsetId());
		String latlngStr = content.substring(content.indexOf("(")+1, content.indexOf(")"));
		String[] strArr = latlngStr.split(",");
		alarm.setLat(Double.parseDouble(strArr[1]));
		alarm.setLng(Double.parseDouble(strArr[0]));
		alarm.setInfo("SOS");
		alarm.setType(5);
		alarm.setSpeed(0.0D);
		alarm.setRead(2);
		LoginDevice loginDevice = new LoginDevice ();
        loginDevice.setDt (new Beidou ());
        loginDevice.setSn (session.getAttribute("duid").toString());
        message.setLoginDevice (loginDevice);
        message.setBusinessData(alarm);
		return message;
	}
	

	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}


}
