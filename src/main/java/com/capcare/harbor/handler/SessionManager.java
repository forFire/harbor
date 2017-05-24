package com.capcare.harbor.handler;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import module.util.Assert;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.capcare.harbor.dao.DeviceDao;
import com.capcare.harbor.handler.device.DeviceHandler;
import com.capcare.harbor.model.Device;
import com.capcare.harbor.service.cache.DeviceCache;

@Component
public class SessionManager implements InitializingBean {

	private static final Logger logger = LoggerFactory
			.getLogger(SessionManager.class);

	@Resource
	private IoAcceptor ioAcceptor;

	@Resource
	private DeviceCache deviceCache;
	@Resource
	private DeviceDao deviceDao;

	@Resource
	private DeviceHandler deviceHandler;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(ioAcceptor, "未注册 ioAcceptor");
	}

	/** 设备的duid与session关系MAP */
	private static Map<String, Long> deviceSessionMap = new HashMap<String, Long>();

	/**
	 * 第一次收到设备或APP消息后触发:
	 * 
	 * 1.在其session中写入其duid
	 * 
	 * 2.记录duid与session关系MAP
	 */
	public void checkin(String deviceSn, IoSession session) {
		logger.info("-------checkin----------");
		if (session.getAttribute("duid") == null
				&& session.containsAttribute("deviceType")) {
			session.setAttribute("duid", deviceSn);
			deviceSessionMap.put(deviceSn, session.getId());
			deviceCache.addOnlineDevice(deviceSn);

			Device device = deviceCache.getDevice(deviceSn);
			if (device == null) {
				device = deviceDao.getDeviceBySn(deviceSn);
				if (device == null) {
					return;
				}
			}
			logger.info("----------type------------"+device.getType());
			session.setAttribute("type", Integer.parseInt(device.getType()));
			
			//最近一条余额查询指令发送时间
			Long last_send_time = deviceCache.getFeeSmsTime(deviceSn);
			if(last_send_time != null){
				session.setAttribute("fee_check_cmd_last_send_time", last_send_time);
			}
		}
	}

	/**
	 * session关闭时触发:
	 * 
	 * 注意发MQ后,session已关闭问题
	 */
	public void checkout(IoSession session) {
		String duid = (String) session.getAttribute("duid");
		logger.info("断开连接--" + duid);
		if (duid != null) {
			deviceSessionMap.remove(duid);
			deviceCache.removeOnlineDevice(duid);
		}
	}

	public IoSession getSession(String deviceSn) {
		Long sessionId = deviceSessionMap.get(deviceSn);
		if (sessionId != null)
			return ioAcceptor.getManagedSessions().get(sessionId);
		return null;
	}

}
