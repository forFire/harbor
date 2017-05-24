package com.capcare.harbor.service.logic;

import javax.annotation.Resource;

import module.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.capcare.harbor.dao.DeviceDao;
import com.capcare.harbor.model.Device;
import com.capcare.harbor.protocol.Position;
import com.capcare.harbor.service.cache.DeviceCache;
import com.capcare.harbor.service.cache.PositionCache;
import com.capcare.harbor.vo.BaseMessage;

/**
 * @author fyq
 */
@Component
@Scope("singleton")
public class PositionService implements DataService {

	private static Logger logger = LoggerFactory.getLogger(PositionService.class);

	@Resource
	private PositionCache positionCache;
	@Resource
	private DeviceCache deviceCache;
	@Resource
	private DeviceDao deviceDao;

	@Resource
	private JmsTemplate jmsTemplate;

	// ******************************************

	public void deviceOffLine(String deviceSn) {
		Device device = deviceCache.getDevice(deviceSn);
		if (device == null) {
			device = deviceDao.get(deviceSn);
			deviceCache.setDevice(deviceSn, device);
		}
		if (!"2".equals(device.getType())) {
			Position position = positionCache.getPosition(deviceSn);
			Assert.notNull(position, "库中无此设备状态");
			position.setStatus(2);
			position.setSpeed(0D);
			positionCache.setPosition(deviceSn, position);
			jmsTemplate.send(position);
			logger.info("设备状态设为离线,sn=" + deviceSn);
		}
	}

	@Override
	public void save(BaseMessage message) {
		
		
		Position position = (Position) message.getBusinessData();
		position.setSystime(System.currentTimeMillis());
		position.setStatus(1);
		
		logger.info("---------PositionService.save()---------" + position.getDeviceSn()+"===="+position.getMode());
		
		
		if ("E".equals(position.getMode())) {
			// 不下发星历。
			logger.info("---------下发授时---------" + position.getDeviceSn());
			deviceCache.setNeedIssue(position.getDeviceSn());// 下发授时
		}
		jmsTemplate.send(position);
	}

}
