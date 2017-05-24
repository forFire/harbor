package com.capcare.harbor.service.logic;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.capcare.harbor.protocol.DeviceOperation;
import com.capcare.harbor.protocol.EquipmentTime;
import com.capcare.harbor.model.SystemStatus;
import com.capcare.harbor.protocol.FireAlarm;
import com.capcare.harbor.vo.BaseMessage;
/**
 *  城安发送过来的报警传给logic
 */
@Component
@Scope("singleton")
public class FireAlarmService implements DataService{
	private static Logger logger = LoggerFactory.getLogger(FireAlarmService.class);
	
	@Resource
	private JmsTemplate jmsTemplate;
	
	/**
	 * 设备上传告警
	 */
	public void handleAlarm(MessageCreator alarm) {
		logger.info("Mq 发送数据-----handleAlarm()--------");
//		long currTime = System.currentTimeMillis();
//		logger.info("a"+alarm.getSn());
		jmsTemplate.send(alarm);
	}

	@Override
	public void save(BaseMessage message) {
		
		Object obj = message.getBusinessData();
		FireAlarm alarm = null;
		SystemStatus systemStatus =null;
		DeviceOperation deviceOperation =null;
		EquipmentTime roomTime =null;
		
		if(obj != null && (obj instanceof  FireAlarm)){
			alarm = (FireAlarm)obj;
			logger.info("FireAlarm===================="+alarm.getCreateTime());
			handleAlarm(alarm);
		}
		
		if(obj != null && (obj instanceof  SystemStatus)){
			logger.info("systemStatus====================>");
			systemStatus = (SystemStatus)obj;
			handleAlarm(systemStatus);
		}
		
		if(obj != null && (obj instanceof  DeviceOperation)){
			logger.info("DeviceOperation====================>");
			deviceOperation = (DeviceOperation)obj;
			handleAlarm(deviceOperation);
		}
		
		if(obj != null && (obj instanceof  EquipmentTime)){
			roomTime = (EquipmentTime)obj;
			logger.info("roomTime===================="+roomTime.getEquipmentCode()+roomTime.getTime());
			handleAlarm(roomTime);
			
		}
	}
	
	
}
