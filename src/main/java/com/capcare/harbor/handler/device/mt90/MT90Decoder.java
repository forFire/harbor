package com.capcare.harbor.handler.device.mt90;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capcare.harbor.handler.device.DeviceType;
import com.capcare.harbor.handler.device.m2616.M2616;
import com.capcare.harbor.protocol.Alarm;
import com.capcare.harbor.protocol.AlarmType;
import com.capcare.harbor.protocol.Position;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.DeviceReply;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.LoginDevice;

public class MT90Decoder {

	private static Logger logger = LoggerFactory.getLogger(MT90Decoder.class);

	private static final int IMEI_LENGTH = 15;

	private static final byte MT90_BEGIN = 0x24;

	private static final byte MT_COMMA = 0X2C;

	private static final byte MT_STAR = 0X2A;

	private static final int MT_CMD_CONTENT_LENGTH = 26;

	private static final byte MT_END1 = 0x0d;

	private static final byte MT_END0 = 0x0a;

	private static SimpleDateFormat df = new SimpleDateFormat("yyMMddhhmmss");


	// $$G142,013226008748994,AAA,35,39.980038,116.498705,130725163414,A,9,22,0,146,1,18,387224,446641,460|1|10CD|C7CC,0000,0000|0000|0000|0A1D|0B81,,*CA
	public static boolean decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
		
		BinaryProtocolStack stack = getStack(session);

		byte[] bytes = new byte[in.limit()];
		in.get(bytes);
		logger.info("MT90_GOT:[" + new String(bytes) + "]");
		stack.append(bytes);

		while (true) {
			int cur = stack.indexOf(MT90_BEGIN, 0);

			if (stack.buffer[cur + 1] != MT90_BEGIN) {
				stack.clear();
				return true;
			}

			if (stack.buffer[cur + 2] < 0x41 || stack.buffer[cur + 2] > 0x7A) {
				stack.clear();
				return true;
			}

			int next = stack.indexOf(MT_COMMA, cur + 3);
			if (next == -1)
				return true;
			int length = Integer.parseInt(new String(stack.buffer, cur + 3, next - cur - 3));
			cur = next;

			next = stack.indexOf(MT_COMMA, cur + 1);
			if (next == -1)
				return true;
			// 验证IMEI长度和数字
			if (next != (cur + IMEI_LENGTH + 1) || !checkDigit(stack.buffer, cur + 1, IMEI_LENGTH)) {
				stack.clear();
				return true;
			}
			String duid = new String(stack.buffer, cur + 1, IMEI_LENGTH);
			
			cur = next;
			next = stack.indexOf(MT_COMMA, cur + 1);
			if (next == -1)
				return true;
			// 验证指令长度
			if (next != (cur + 3 + 1)) {
				stack.clear();
				return true;
			}
			
			String instruct = new String(stack.buffer,cur + 1, 3);

			cur = next;
			next = stack.indexOf(MT_STAR, cur + 1);
			if (next == -1)
				return true;
			// 验证指令内容的长度
			if (next != cur + length - MT_CMD_CONTENT_LENGTH + 1) {
				stack.clear();
				return true;
			}
			byte[] content = new byte[length - MT_CMD_CONTENT_LENGTH];
			System.arraycopy(stack.buffer, cur + 1, content, 0, length - MT_CMD_CONTENT_LENGTH);
		
			Object protocol = null;
			if("AAA".equals(instruct)){
				protocol = decodePosition(new String(content), duid);
			}else{
				protocol = decodeReply(instruct, new String(content), (String)session.getAttribute("duid"));
			}

			if (protocol == null) {
				stack.clear();
				return true;
			}

			cur = next;
			byte[] before = new byte[cur + 1];
			System.arraycopy(stack.buffer, 0, before, 0, cur + 1);
			int temp = 0;
			for (byte b : before) {
				short t = (short) (b & 0xff);
				temp += t;
			}
			byte added = (byte) temp;
			byte[] check = new byte[2];
			System.arraycopy(stack.buffer, cur + 1, check, 0, 2);
			// 检验验证码
			if (added != (byte) Integer.parseInt(new String(check), 16)) {
				System.out.println("check code error");
				logger.error("验证码检查出现问题");
				stack.clear();
				return true;
			}

			if (stack.buffer[cur + 3] != MT_END1 || stack.buffer[cur + 4] != MT_END0) {
				stack.clear();
				return true;
			}

			stack.remove(0, cur + 5);
			out.write(protocol);

			if (stack.limit == 0) {
				break;
			}
		}

		return false;
	}


	/**
	 * 获取会话上下文
	 */
	private static BinaryProtocolStack getStack(IoSession session) {
		if (session == null) {
			return null;
		}
		BinaryProtocolStack stack = (BinaryProtocolStack) session.getAttribute("STACK");
		if (stack == null) {
			stack = new BinaryProtocolStack();
			session.setAttribute("STACK", stack);
		}
		return stack;
	}

	/**
	 * 检查是否是数字
	 */
	public static boolean checkDigit(byte[] bytes, int offset, int length) {
		if (bytes == null) {
			return true;
		}
		for (int i = offset; i < length; i++) {
			byte b = bytes[i];
			if (b < 48 || b > 57) {
				return false;
			}
		}
		return true;
	}

	private static BaseMessage decodeReply(String instruct,String content, String deviceSn){
		DeviceReply protocol = new DeviceReply();
		
		InstructType instructType = InstructMapping.mapToCapcare(instruct);
		protocol.setDeviceSn(deviceSn);
		protocol.setInstructType(instructType);
		if (!"OK".equals(new String(content))){
			protocol.setSuccess(false);
		}else{
			protocol.setSuccess(true);
		}
		BaseMessage message=new BaseMessage(Act.REPLY,protocol);
		LoginDevice ld=new LoginDevice();
		ld.setDt(new M2616());
		ld.setSn(deviceSn);
		message.setLoginDevice(ld);
		return 	message;
	}
	

	private static BaseMessage decodePosition(String content, String deviceSn){

		char separator = ',';
		char barSeparator = '|';
		int eventCode;
		double lat;
		double lng;
		long time;
		String mode = null;
		double speed;
		int direction;
		int battery;

		int cur = 0;
		int next = content.indexOf(separator, cur);
		eventCode = Integer.parseInt(content.substring(cur, next));

		cur = next + 1;
		next = content.indexOf(separator, cur);
		lat = Double.parseDouble(content.substring(cur, next));

		cur = next + 1;
		next = content.indexOf(separator, cur);
		lng = Double.parseDouble(content.substring(cur, next));

		cur = next + 1;
		next = content.indexOf(separator, cur);

		try {
			df.setTimeZone(TimeZone.getTimeZone("Etc/Greenwich"));
			time = df.parse(content.substring(cur, next)).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

		cur = next + 1;
		next = content.indexOf(separator, cur);
		mode = content.substring(cur, next);

		cur = next + 1;
		next = content.indexOf(separator, cur);
		//satelliteNum = Integer.parseInt(content.substring(cur, next));

		cur = next + 1;
		next = content.indexOf(separator, cur);
		//GSMStatus = content.substring(cur, next);

		cur = next + 1;
		next = content.indexOf(separator, cur);
		speed = Double.parseDouble(content.substring(cur, next));

		cur = next + 1;
		next = content.indexOf(separator, cur);
		direction = Integer.parseInt(content.substring(cur, next));

		cur = next + 1;
		next = content.indexOf(separator, cur);
		//hpa = Double.parseDouble(content.substring(cur, next));

		cur = next + 1;
		next = content.indexOf(separator, cur);
		//elevation = Integer.parseInt(content.substring(cur, next));

		cur = next + 1;
		next = content.indexOf(separator, cur);
		//distance = Double.parseDouble(content.substring(cur, next));

		cur = next + 1;
		next = content.indexOf(separator, cur);
		//totalRunTime = Integer.parseInt(content.substring(cur, next));

		cur = next + 1;
		next = content.indexOf(separator, cur);
		//baseStationInfo = content.substring(cur, next);

		cur = next + 1;
		next = content.indexOf(separator, cur);
		//iostatus = toBinaryStr(hex);

		cur = next + 1;
		next = content.indexOf(separator, cur);

		String elecPower = content.substring(cur, next);
		int bar1 = elecPower.indexOf(barSeparator, 0);
		int bar2 = elecPower.indexOf(barSeparator, bar1 + 1);
		int bar3 = elecPower.indexOf(barSeparator, bar2 + 1);
		int bar4 = elecPower.indexOf(barSeparator, bar3 + 1);
		// int bar5 = elecPower.indexOf(barSeparator, bar4+1);
		Double remained = Integer.valueOf(elecPower.substring(bar3 + 1, bar4), 16) * 3.3 * 2 / 4096;
		battery = (int) Math.round(remained);

		cur = next + 1;
		next = content.indexOf(separator, cur);
		BaseMessage message=null;
		if (eventCode == 17 || eventCode == 19 || eventCode == 20 || eventCode == 21) {
			AlarmType alarmType = AlarmMapping.getMappedAlarm(eventCode);
			if(alarmType == null){
				return null;
			}
			Alarm alarm = new Alarm();
			alarm.setDeviceSn(deviceSn);
			alarm.setTime(time);
			alarm.setSystime(System.currentTimeMillis());
			alarm.setMode(mode);
			alarm.setLat(lat);
			alarm.setLng(lng);
			alarm.setType(alarmType.getNum());
			alarm.setInfo(alarmType.getInfo());
			alarm.setDirection(Float.valueOf(direction));
			alarm.setSpeed(speed);
			alarm.setRead(2);
			message=new BaseMessage(Act.ALARM,alarm);
			LoginDevice ld=new LoginDevice();
			ld.setDt(new MT90());
			ld.setSn(alarm.getDeviceSn());
			message.setLoginDevice(ld);
			
		} else if (eventCode == 34 || eventCode == 35) {
			Position position = new Position();
			position.setDeviceSn(deviceSn);
			position.setReceive(time);
			position.setSystime(System.currentTimeMillis());
			position.setMode(mode);
			position.setLat(lat);
			position.setLng(lng);
			position.setBattery(battery);
			position.setDirection(Float.valueOf(direction));
			position.setSpeed(speed);
			message=new BaseMessage(Act.POSITION,position);
			LoginDevice ld=new LoginDevice();
			ld.setDt(new MT90());
			ld.setSn(position.getDeviceSn());
			message.setLoginDevice(ld);
		}
		
		return message;
	}
}
