package com.capcare.harbor.service.logic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import module.util.EntityUtil;
import module.util.JsonUtils;
import module.util.Tools;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.capcare.harbor.dao.DeviceDao;
import com.capcare.harbor.dao.InstructDao;
import com.capcare.harbor.handler.SessionManager;
import com.capcare.harbor.handler.SessionWriter;
import com.capcare.harbor.handler.device.DeviceHandler;
import com.capcare.harbor.handler.device.DeviceType;
import com.capcare.harbor.model.Device;
import com.capcare.harbor.model.Fence;
import com.capcare.harbor.model.Instruct;
import com.capcare.harbor.model.SpeetInstructVo;
import com.capcare.harbor.protocol.Position;
import com.capcare.harbor.service.cache.DeviceCache;
import com.capcare.harbor.service.cache.PositionCache;
import com.capcare.harbor.util.InstructConvertor;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.InstructVo;

@Component
public class InstructService {
	private static Logger logger = LoggerFactory.getLogger(InstructService.class);

	@Resource
	private DeviceDao deviceDao;

	@Resource
	private InstructDao instructDao;

	@Resource
	private PositionCache positionCache;

	@Resource
	private JmsTemplate jmsTemplate;

	@Resource
	private DeviceCache deviceCache;

	@Resource
	private DeviceHandler deviceHandler;
	@Resource
	private SessionManager sessionManager;

	// *****************************************

	/**
	 * 服务器下发指令给设备
	 */
	public void pushInstruct(String deviceSn, IoSession session) {
		
		SessionWriter sessionWriter = (SessionWriter) session.getAttribute("cap_session_writer");
		if (sessionWriter == null) {
			sessionWriter = new SessionWriter();
		}
		List<Instruct> instructs = instructDao.find(deviceSn);
		if (instructs != null && instructs.size() > 0) {

			for (Instruct instruct : instructs) {
				try {
					InstructVo instructVo = InstructConvertor.instruct(instruct, deviceSn);
					Object cmd = encodeInstruct(session, instructVo);
					if (cmd != null) {
						if (cmd instanceof List) {
							sessionWriter.addAll((List) cmd);
						} else {
							sessionWriter.add(cmd);
						}
					}

				} catch (Exception e) {
					logger.error("error-instruct:指令格式错误", instruct);
					logger.error("error:具体错误", e);
				}
			}
			session.setAttribute("cap_session_writer", sessionWriter);
		}

	}

	/**
	 * 编码指令
	 * 
	 * @param protocol
	 * @throws Exception
	 */
	private Object encodeInstruct(IoSession session, InstructVo protocol) {

		DeviceType deviceType = (DeviceType) session.getAttribute("deviceType");

		Object tmpList = null;
		try {
			tmpList = deviceHandler.getDeviceProtocol(deviceType).encode(session, protocol);
		} catch (Exception e) {
			logger.error("pushInstruct", e);
		}

		return tmpList;

	}

	/**
	 * 指令回复确认
	 */
	public void dictateConfirm(String deviceSn, boolean result, InstructType instructType) {

		int operate = instructType.getNum();
		if ((operate < 1 || operate > 8) && (operate != 11)) {
			return;
		}
		Instruct entityInstruct = instructDao.find(deviceSn, operate);
		if (entityInstruct == null) {
			logger.error(deviceSn + "无此指令:类型为" + operate);
			return;
		}

		if (!result) {
			int type = entityInstruct.getType();
			if (11 == type) {
				entityInstruct.setReply(1);
				entityInstruct.setContent("清除OBD故障码失败");
				instructDao.saveOrUpdate(entityInstruct);
				return;
			}
		}

		if (!result) {
			Device entityDevice = deviceDao.get(deviceSn);
			int type = entityInstruct.getType();
			String content = entityInstruct.getOriginal();// 回滚与成功区别
			/*
			 * if (1 == type) {// 围栏 entityDevice.setFence(content); } else
			 */if (2 == type) {// 上传间隔
				entityDevice.setTick(Integer.parseInt(content));
				/*
				 * } else if (3 == type) {// 超速(开/关-开加最大速度) SpeetInstructVo siv
				 * = JsonUtils.str2Obj(content, SpeetInstructVo.class);
				 * entityDevice.setSpeedingSwitch(siv.getOpen());
				 * entityDevice.setSpeedThreshold(siv.getMax()); } else if (4 ==
				 * type) {// sos entityDevice.setSosNum(content); } else if (7
				 * == type) {// 围栏开关 //t808协议需要fence信息上面是老版本指令，为兼容老版本下发指令 try {
				 * Map <String, Object> str2Map = JsonUtils.str2Map (content);
				 * int switchType = Tools.toInt (str2Map.get ("switch"));
				 * entityDevice.setFenceSwitch (switchType); } catch (Exception
				 * e) { //老版本下发指令 entityDevice.setFenceSwitch
				 * (Integer.getInteger (content)); } } else if (type == 8) {//
				 * 移动报警开关 entityDevice.setMoveSwitch(Integer.parseInt(content));
				 */
			}
			deviceDao.saveOrUpdate(entityDevice);

			Position position = positionCache.getPosition(deviceSn);
			if (position != null) {
				position.setStamp(new Date().getTime());
				positionCache.setPosition(deviceSn, position);
				jmsTemplate.send(position);
			}
		}

		entityInstruct.setReply(1);
		instructDao.saveOrUpdate(entityInstruct);

	}

	public void sendReplyMessage2Back(final String message) {
		String mq = "config_from_harbor";
		jmsTemplate.send(mq, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(message);
			}
		});
		logger.info("toMq:" + mq + "config reply:" + message);
	}

}
