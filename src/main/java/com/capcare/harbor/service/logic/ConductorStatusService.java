package com.capcare.harbor.service.logic;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.capcare.harbor.protocol.Conductor_Status;
import com.capcare.harbor.vo.BaseMessage;


@Component
@Scope("singleton")
public class ConductorStatusService implements DataService {

	private static Logger logger = LoggerFactory.getLogger(ConductorStatusService.class);

//	@Resource
//	private PositionCache positionCache;
//	@Resource
//	private DeviceCache deviceCache;
//	@Resource
//	private DeviceDao deviceDao;

	@Resource
	private JmsTemplate jmsTemplate;

	// ******************************************

//	public void deviceOffLine(String deviceSn) {
//		Device device = deviceCache.getDevice(deviceSn);
//		if (device == null) {
//			device = deviceDao.get(deviceSn);
//			deviceCache.setDevice(deviceSn, device);
//		}
//		if(device.getType() != 2){
//			Position position = positionCache.getPosition(deviceSn);
//			Assert.notNull(position, "库中无此设备状态");
//			position.setStatus(2);
//			position.setSpeed(0D);
//			positionCache.setPosition(deviceSn, position);
//			jmsTemplate.send(position);
//			logger.info("设备状态设为离线,sn="+deviceSn);
//		}
//	}


	@Override
	public void save(BaseMessage message) {
		Conductor_Status status = (Conductor_Status) message.getBusinessData();
//		position.setSystime(System.currentTimeMillis());
//		position.setStatus(1);
		jmsTemplate.send(status);
		logger.info("Send to MQ : {}", message.getAct().name());
	}

}
