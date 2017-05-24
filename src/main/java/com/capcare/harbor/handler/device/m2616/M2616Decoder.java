package com.capcare.harbor.handler.device.m2616;

import java.util.HashMap;
import java.util.Map;

import module.util.DateUtil;
import module.util.NumUtil;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capcare.harbor.handler.device.m2616.exception.NoIndexException;
import com.capcare.harbor.handler.device.m2616.util.GetUtil;
import com.capcare.harbor.handler.device.m2616.util.LatLng;
import com.capcare.harbor.protocol.Alarm;
import com.capcare.harbor.protocol.AlarmType;
import com.capcare.harbor.protocol.Position;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.DeviceReply;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.LoginDevice;

/**
 * @author fyq
 */
public class M2616Decoder {

	private static final Logger logger = LoggerFactory.getLogger(M2616Decoder.class);
	
	private static byte BITACC = 1;
	private static byte BITMODE = (1 << 1);
	private static byte BIT433 = (1 << 2);
	
	@SuppressWarnings("unused")
	private static final String[] sms_keywords = {"余额"};
	
	public static BaseMessage decode(IoSession session, IoBuffer in) {
		
		//宠物，人 , 余额查询短信
		BaseMessage protocol = GetUtil.parseSms(in);
		if(protocol != null){
			return protocol;
		}
		
		//车，余额短信
/*		if(session.getAttribute("duid") != null && (Integer)session.getAttribute("type") == 1){
			in.mark();
			byte[] bytes = new byte[in.remaining()];
			in.get(bytes);
			
			String rsInStr = new String(bytes,Charset.forName("UTF-8"));
			
			for(String keyword:sms_keywords){
				if(rsInStr.indexOf(keyword) != -1){
					BaseMessage message=new BaseMessage(Act.FEE_CHECK,rsInStr);
					LoginDevice ld=new LoginDevice();
					ld.setDt(new M2616());
					ld.setSn((String)session.getAttribute("duid"));
					message.setLoginDevice(ld);
					message.setBusinessData(rsInStr);
					return message;
				}
			}
			in.reset();
		}*/
		
		try {
			String ack = GetUtil.checkMsg(in);
			
				
			if(ack == null){			
				protocol = decodeNormalMsg(in);
			}else{
				protocol = decodeReplyMsg(ack, (String)session.getAttribute("duid"));
			}
			return protocol;
			
		} catch (NoIndexException ex) {
			logger.error("NoIndexException:"+ex.getMessage());
		} catch (Exception ex) {
			in.limit(0);
			logger.error(ex.getMessage(),ex);
		}
		return null;
	}
	
	private static BaseMessage decodeReplyMsg(String msg, String deviceSn){
		String space = " ";
		String imei = "Imei";
		int imeiNext = msg.indexOf(imei);
		if(imeiNext != -1) {
			BaseMessage message=new BaseMessage(Act.CONFIG_REPLY,GetUtil.getConfigHead(msg)+msg);
			LoginDevice ld=new LoginDevice();
			ld.setDt(new M2616());
			ld.setSn(deviceSn);
			message.setLoginDevice(ld);
			return message;
		}
		int next = msg.indexOf(space);
		if (next == -1){
			return null;
		}
		String instruction = msg.substring(0, next);
		if(!GetUtil.isNumber(instruction)){
			return null;
		}
		
		InstructType instructType = InstructMapping.mapToCapcare(Integer.parseInt(instruction));
		
		if(instructType != null){
			DeviceReply protocol = new DeviceReply();
			protocol.setDeviceSn(deviceSn);
			protocol.setInstructType(instructType);
			String result = msg.substring(next+1, next + 3);
			if("OK".equals(result)){
				protocol.setSuccess(true);
			}else{
				protocol.setSuccess(false);
			}
			BaseMessage message=new BaseMessage(Act.REPLY,protocol);
			LoginDevice ld=new LoginDevice();
			ld.setDt(new M2616());
			ld.setSn(deviceSn);
			message.setLoginDevice(ld);
			return message;
		}
		return null;
	}
	
	
	private static BaseMessage decodeNormalMsg(IoBuffer in) throws Exception{
		in.mark();
		GetUtil.checkHead(in);// 头
		String sn = GetUtil.getString(in);// 设备号
		GetUtil.getString(in);// 用户名
		String state = GetUtil.getString(in);// 状态位
		
		String ext = GetUtil.getString(in);// 密码
		String type = GetUtil.getString(in);// 数据类别
		String nums = GetUtil.getString(in);// 数据量
		String cell = GetUtil.getString(in);// 基站信息
		String jwsf = GetUtil.getString(in);// 经 纬 速度 方向
		String date = GetUtil.getString(in);// 日期ddMMyy
		String time = GetUtil.getString(in);// 时间HHmmss.SSS
		
//		logger.info("=========cell==================》"+cell);
		GetUtil.checkTail(in);// 尾
		int stateInt = 0;
		
		//#893620160920018##41|2001#91#AUT#1#00000000_0_0,00_0_0,00_0_0@||000,||000,||000,||000,||000|0|0|04000002#0.0000,E,0.0000,N,0.00,0.00,0.000#010104#000007.000##
		//如果第二位是65 或 64 （十六进制是41 40）则不解析
		int flag = 1;
//		logger.info("==========||==================》"+state);
		Map<Integer, Object> statusMap = null;
		if (state.indexOf("|") == -1) {
			stateInt = Integer.valueOf(state, 16);
			flag = stateInt;
//			logger.info("==========||==================》"+stateInt);
			statusMap = getStatus(stateInt);
		} else {
			String arr[] = state.split("\\|");
			flag = Integer.valueOf(arr[0], 16);
//			logger.info("===========flag=================》"+flag);
			
			statusMap = getStatus(Integer.valueOf(arr[0], 16));
			String str = arr[1];
			if ("2001".equals(str)) {//需要请求星历，状态置为E
				statusMap.put(1, "E");
			}
		}
		
//		logger.info("状态位=============================》"+flag);
//		logger.info("(stateInt&40)=============================》"+(flag&64));
		boolean b = true;
		//状态位不与0x40不为0的就不要
		if((flag&64) != 0){
			b = false;
		}
		
		//acc, gps
		int accMode = (Integer)statusMap.get(0);
		String gpsMode = (String) statusMap.get(1);
		int mode433 = (Integer)statusMap.get(2);
		BaseMessage message=null;
		logger.info("b============================》"+b);
		//位置
		if (type.equals("AUT") || type.equals("SMS")) {
			
			logger.info("AUT============================》");
			
			Position position = new Position();		
			position.setDeviceSn(sn);
			position.setAccMode(accMode);
			position.setMode(gpsMode);
			position.setMode433(mode433);
			
			//判断是否要水压
			position.setFlag(b);
			//包含水压信息
			position.setCell(cell);
			
			message=new BaseMessage(Act.POSITION,position);
			LoginDevice ld=new LoginDevice();
			ld.setDt(new M2616());
			ld.setSn(position.getDeviceSn());
			message.setLoginDevice (ld);			
			try{
				String[] jwsfArray = jwsf.split(",");
				position.setLng(LatLng.num(jwsfArray[0]));
				if(!"E".equals(jwsfArray[1])){//西经
					position.setLng(position.getLng()*-1);
				}
				position.setLat(LatLng.num(jwsfArray[2]));
				if(!"N".equals(jwsfArray[3])){//南纬
					position.setLat(position.getLat()*-1);
				}
				position.setSpeed(Double.parseDouble(jwsfArray[4]));
				position.setSpeed(NumUtil.round(position.getSpeed()*1.852,2));
				position.setDirection(Float.parseFloat(jwsfArray[5]));
				String receiveStr = date + time;
				Long receiveTime = DateUtil.strLong(receiveStr, "ddMMyyHHmmss.SSS");
				receiveTime += 8 * 60 * 60 * 1000;// 设备时间为格林尼治时间，需转为北京时间
				
				logger.info("设备时间================================"+receiveTime);
				
				position.setReceive(receiveTime);
				position.setSteps(Integer.parseInt(nums));
				position.setBattery(Integer.parseInt(ext));			
			}catch(NumberFormatException e){
				position.setLat(0D);
				position.setLng(0D);
				logger.error(e.getMessage());
			}
		}else{
			
			logger.info("AUT1============================》");
			
			AlarmType alarmType = AlarmMapping.getMappedAlarm(type);
			if(alarmType == null){
				return null;
			}
			Alarm alarm = new Alarm();	
			alarm.setDeviceSn(sn);
			alarm.setAccMode(accMode);
			alarm.setMode(gpsMode);	
			alarm.setMode433(mode433);
			alarm.setType(alarmType.getNum());
			alarm.setRead(2);
			alarm.setInfo(alarmType.getInfo());
			
			String[] jwsfArray = jwsf.split(",");
			alarm.setLng(LatLng.num(jwsfArray[0]));
			if(!"E".equals(jwsfArray[1])){//西经
				alarm.setLng(alarm.getLng()*-1);
			}
			alarm.setLat(LatLng.num(jwsfArray[2]));
			if(!"N".equals(jwsfArray[3])){//南纬
				alarm.setLat(alarm.getLat()*-1);
			}
			alarm.setSpeed(Double.parseDouble(jwsfArray[4]));
			alarm.setSpeed(NumUtil.round(alarm.getSpeed()*1.852,2));
			alarm.setDirection(Float.parseFloat(jwsfArray[5]));
			String receiveStr = date + time;
			Long receiveTime = DateUtil.strLong(receiveStr, "ddMMyyHHmmss.SSS");
			receiveTime += 8 * 60 * 60 * 1000;// 设备时间为格林尼治时间，需转为北京时间
			logger.info("设备时间1================================"+receiveTime);
			
			alarm.setTime(receiveTime);
			alarm.setSteps(Integer.parseInt(nums));
			alarm.setBattery(Integer.parseInt(ext));
			alarm.setCell(cell);
			message=new BaseMessage(Act.ALARM,alarm);
			LoginDevice ld=new LoginDevice();
			ld.setDt(new M2616());
			ld.setSn(alarm.getDeviceSn());
			message.setLoginDevice(ld);
		}
		return message;

	}

	private static Map<Integer, Object> getStatus(int status) {
		Map<Integer, Object> map = new HashMap<Integer, Object>();

		if ((status & BITACC) == 1) {
			map.put(0, 1);
		} else {
			map.put(0, 0);
		}
		if ((status & BITMODE) == 1 << 1) {
			map.put(1, "A");
		} else {
			map.put(1, "V");
		}
		
		if ((status & BIT433) == 1 << 2) {
			map.put(2, 1);
		} else {
			map.put(2, 0);
		}

		return map;
	}
	
}
