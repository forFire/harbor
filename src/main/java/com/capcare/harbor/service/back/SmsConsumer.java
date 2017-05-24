package com.capcare.harbor.service.back;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.SessionManager;
import com.capcare.harbor.handler.SessionWriter;
import com.capcare.harbor.protocol.Alarm;
import com.capcare.harbor.protocol.Position;
import com.capcare.harbor.protocol.SmsAlarm;

@Component
public class SmsConsumer implements MessageListener {

	@Resource
	private SessionManager sessionManager;

	private static Logger logger = LoggerFactory.getLogger(SmsConsumer.class);
	
	@Override
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			ObjectMessage msg = (ObjectMessage) message;
			try {
				Object obj = msg.getObject();
				if(obj instanceof SmsAlarm){
					SmsAlarm smsAlarm = (SmsAlarm)obj;
					logger.info("MQ_Consumer:" + smsAlarm);
					IoSession session = sessionManager.getSession(smsAlarm.getDeviceSn());
					if (session == null) {
						logger.error("device is off line,smsAlarm="+smsAlarm);
						return;
					}
					session.write(smsAlarm.getContent());
				}		
			} catch (Exception e) {
				logger.error("", e);
			}
		}
	}
}
