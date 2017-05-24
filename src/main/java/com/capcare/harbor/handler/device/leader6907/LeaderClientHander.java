package com.capcare.harbor.handler.device.leader6907;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.chengan.StringUtil;
import com.capcare.harbor.protocol.DeviceOperation;
import com.capcare.harbor.protocol.EquipmentTime;
import com.capcare.harbor.protocol.FireAlarm;
import com.capcare.harbor.service.cache.DeviceIpCache;
import com.capcare.harbor.service.cache.RoomLastTimeCache;
import com.capcare.harbor.service.logic.PositionService;
import com.capcare.harbor.util.DateUtil;

/**
 * 
 */
@Component
public class LeaderClientHander implements IoHandler {

	private static Logger logger = LoggerFactory.getLogger(LeaderClientHander.class);

	@Autowired
	RoomLastTimeCache roomLastTimeCache;
	@Resource
	private JmsTemplate jmsTemplate;

	@Resource
	PositionService positionService;

	String primaryAddress = "";
	String lastTime = "";
	@Autowired
	DeviceIpCache deviceIpCache;

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {

		/**
		 * 获取采集设备code编码 ip----》code
		 */
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) deviceIpCache.getAll();
		String leaderCode = "";
		String ipStr = session.getRemoteAddress().toString().split("/")[1].split(":")[0];
		String ipKey = "";
		for (String key : map.keySet()) {
			ipKey = map.get(key).split(":")[0];
			if (ipStr.equalsIgnoreCase(ipKey)) {
				leaderCode = key;
			}
		}

		logger.info("利达返回消息信息:=========>" + message + "==leaderCode====>" + leaderCode);

		if ("55AA".equalsIgnoreCase(message.toString())) {

			return;
		}

		long currentTime = System.currentTimeMillis();
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String dateNowStr = sdf.format(d);
		lastTime = String.valueOf(currentTime);
		
		// 握手成功
		if ("7F".equalsIgnoreCase(message.toString())) {
			logger.info("与采集设备握手成功=========");
		} else {

//			火警010001 01000A
			
			StringUtil su = new StringUtil(message.toString());
			// 1命令
			String order = su.getStr(2);
			// 2参数1 回路
			String arg1 = String.valueOf(Integer.parseInt(su.getStr(2),16));
			// 3探测器地址
			String arg2 = String.valueOf(Integer.parseInt(su.getStr(2),16));

			// arg1 = "00000";
			// arg2 = "005";
			logger.info("order=========>"+order);
			
			// 如果是火警
			if ("01".equalsIgnoreCase(order)) {
				// if ("40".equalsIgnoreCase(order)) {
				// if ("40".equalsIgnoreCase(message.toString())) {
				FireAlarm fireAlarm = new FireAlarm();
				fireAlarm.setType("in002");
//				logger.info("火警设备地址========="+toString((leaderCode), 6) + toString((toString(arg1,5) + toString(arg2,3)), 8));
				
				fireAlarm.setSn(toString((leaderCode), 12) + toString((toString(arg1,5) + toString(arg2,3)), 8));
				// fireAlarm.setSn(arg2);
				fireAlarm.setAlarmTime(dateNowStr);

				jmsTemplate.send(fireAlarm);
			}

			// 如果是故障
			if ("02".equalsIgnoreCase(order)) {
				FireAlarm fireAlarm = new FireAlarm();
				fireAlarm.setType("in001");
//				logger.info("设备地址========="+toString((leaderCode), 6) + toString((toString(arg1,5) + toString(arg2,3)), 8));

				fireAlarm.setSn(toString((leaderCode), 12) + toString((toString(arg1,5) + toString(arg2,3)), 8));
				// fireAlarm.setSn(arg2);
				fireAlarm.setAlarmTime(dateNowStr);
				jmsTemplate.send(fireAlarm);
			}

			// 如果是系统恢复
			// if ("80".equalsIgnoreCase(order)) {
			if ("50".equalsIgnoreCase(order)) {
				DeviceOperation deviceOperation = new DeviceOperation();
				deviceOperation.setPrimaryAddress(leaderCode);
				// 复位标志
				deviceOperation.setOperationFlag("0");
				jmsTemplate.send(deviceOperation);
			}

		}
		
		
		EquipmentTime equipmentTime = new EquipmentTime();
		// 心跳每十分钟把时间和编码存到redis
		String str1 = roomLastTimeCache.getEquipmentTime(toString((leaderCode), 6));
//		logger.info("心跳时间=========" + str1);

		if (str1 == null || str1 == "" || str1.length() == 0) {
			roomLastTimeCache.setEquipmentTime(toString((leaderCode), 12), lastTime);
		} else {
			long l = DateUtil.timediff(str1);
			// 8分钟发一次，前台面根据10分钟判断是否断线 2:小于1000 防止 时间传错 突然很大的数保存到数据库

//			logger.info("当前系统时间:=========>" + lastTime + "==缓存时间====>" + str1 + "时间差====》" + l + "dateNowStr========>" + dateNowStr);

			if (l > 480 && l < 1000) {
				roomLastTimeCache.setEquipmentTime(toString((leaderCode), 12), lastTime);
				equipmentTime.setEquipmentCode(toString((leaderCode), 12));
				equipmentTime.setTime(dateNowStr);

				// 设备连接时间
				jmsTemplate.send(equipmentTime);

			} else if (l < 0 || l > 1000) {
//				logger.info("当前系统时间:====l < 0 || l >1000=====>");

				// 防止时间跑乱 时间差为负数修改不回来
				roomLastTimeCache.setEquipmentTime(toString((leaderCode), 12), lastTime);
			} else {
			}
		}

	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// ((AbstractIoSession) session).getProcessor().flush(session);
		System.out.println("client发送信息" + message.toString());
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
//		System.out.println("client与:" + session.getRemoteAddress().toString() + "断开连接1");
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
//		System.out.println("client与:" + session.getRemoteAddress().toString() + "建立连接发送消息");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
//		System.out.println("IDLE " + session.getIdleCount(status));
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		session.write("20H");
	}

	@Override
	public void exceptionCaught(IoSession arg0, Throwable arg1) throws Exception {
		arg1.printStackTrace();
	}

	/**
	 * 补齐长度
	 * 
	 * @param value
	 * @param size
	 * @return
	 */
	public static String toString(String value, int size) {
		// String s = "00000000000000000000000000000000000000000000000000" +
		// value;
		// return s.substring(s.length() - 8, s.length());
		// value ="233123000";
		while (value.length() < size) {
			value = "0" + value;
		}

		if (value.length() > size) {
			value = value.substring(0, size);
		}
		return value;
	}

}