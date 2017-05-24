package com.capcare.harbor.service.logic;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.capcare.harbor.protocol.Alarm;
import com.capcare.harbor.vo.BaseMessage;

@Component
@Scope("singleton")
public class AlarmService implements DataService{
	private static Logger logger = LoggerFactory.getLogger(AlarmService.class);
	
	@Resource
	private JmsTemplate jmsTemplate;
	
	@Resource
	PositionService positionService;
	
	/**
	 * 设备上传告警
	 */
	public void handleAlarm(Alarm alarm) {
		
		long currTime = System.currentTimeMillis();
		if(!"A".equals(alarm.getMode())){
//			logger.error("no_gps_signal, reset time:" + alarm.toString());
			alarm.setTime(currTime);
		}
		long receive = alarm.getTime();
			
		// 如果上传的是3分钟后的时间点， 则不做处理
		if (receive - currTime>180000) {
			logger.error("future point:" + alarm.toString());
			return;
		}
		
		// 如果补传三天前的点， 则不做处理
		if (receive < currTime && (currTime - receive)>259200000) {
			logger.error("old point:" + alarm.toString());
			return;
		}
		
		
		jmsTemplate.send(alarm);
	}
	
	
	public void save(BaseMessage message) {
		Alarm alarm =(Alarm) message.getBusinessData();
		alarm.setSystime(System.currentTimeMillis());
		handleAlarm(alarm);
		
//		Position position = alarmToPosition(alarm);
//		message.setBusinessData(position);
//		positionService.save(message);
	}

//	private Position alarmToPosition(Alarm alarm){
//		Position position = new Position();
//		position.setAccMode(alarm.getAccMode());
//		position.setDeviceSn(alarm.getDeviceSn());
//		position.setDirection(alarm.getDirection());
//		position.setLat(alarm.getLat());
//		position.setLng(alarm.getLng());
//		position.setMode(alarm.getMode());
//		position.setReceive(alarm.getTime());
//		position.setSpeed(alarm.getSpeed());
//		position.setSystime(alarm.getSystime());
//		position.setStatus(1);
//		position.setSteps(alarm.getSteps());
//		position.setBattery(alarm.getBattery());
//		position.setAccMode(alarm.getAccMode());
//		position.setMode433(alarm.getMode433());
//		position.setCell(alarm.getCell());
//		return position;
//	}
}
