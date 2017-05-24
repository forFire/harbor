package com.capcare.harbor.handler.device.eelink;

import java.math.BigDecimal;
import java.nio.BufferUnderflowException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import module.util.BytesConvert;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capcare.harbor.handler.device.m2616.exception.NoIndexException;
import com.capcare.harbor.protocol.Alarm;
import com.capcare.harbor.protocol.AlarmType;
import com.capcare.harbor.protocol.Cylinder;
import com.capcare.harbor.protocol.OBD;
import com.capcare.harbor.protocol.OBDError;
import com.capcare.harbor.protocol.OxygenSensor;
import com.capcare.harbor.protocol.Position;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.DeviceReply;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.LoginDevice;

/**
 * 移联通讯协议V1.8.5解码
 * 
 * @author XieHaiSheng
 * 
 */
public class EElinkDecoder {

	private static final Logger logger = LoggerFactory
			.getLogger(EElinkDecoder.class);

	private static boolean checkInBytes(IoBuffer in) {

		in.mark();
		int limit = in.limit();
		byte[] bytes = new byte[limit];
		try {
			in.get(bytes);
		} catch (BufferUnderflowException e) {

			in.reset();
			return false;
		}
		in.reset();

		int length = BytesConvert.bytes2Int(new byte[] { bytes[3], bytes[4] });// 包长度(包括序号)
		logger.info("on msg：[" + BytesConvert.encodeHexStr(bytes)
				+ "], package length=" + length);
		if (bytes.length - 5 < length) {
			logger.warn(" bytes length(" + limit + ") < msg length:" + length);
			return false;
		} else {
			return true;
		}

	}

	public static BaseMessage decode(IoBuffer in, String deviceSn) {
		boolean check = checkInBytes(in);
		if (!check) {
			return null;
		}
		try {
			in.get();
			in.get();
			byte act = in.get();// 数据类型号
			int length = BytesConvert
					.bytes2Int(new byte[] { in.get(), in.get() });// 包长度(包括序号)
			byte[] data = new byte[length - 2];
			EElinkResponse response = new EElinkResponse();
			response.setAct(act);
			response.setSequnce(new byte[] { in.get(), in.get() });// 序号
			response.setDatalength(new byte[] { 0x00, 0x02 });

			in.get(data);
			BaseMessage ret = null;
			LoginDevice ld = new LoginDevice();
			ld.setDt(new EELink());

			switch (act) {
			case 0x01:
				ld.setSn(login(data));
				ret = new BaseMessage(Act.LOGIN, ld.getSn());

				logger.info("device :" + ld.getSn() + " login "+ BytesConvert.encodeHexStr(data));
				response.setLanage(data[8]);
				response.setSn(data);
				break;
			case 0x02:
				response = null;
				Position p = gps(data);
				p.setDeviceSn(deviceSn);
				ret = new BaseMessage(Act.POSITION, p);
				break;
			case 0x05:
				break;
			case 0x03:
				Map status = heartBeat(data);
				ret = new BaseMessage(Act.HEART_BEAT, status);
				break;
			case 0x04:
				Alarm alarm = alarm(data);
				alarm.setDeviceSn(deviceSn);
				ret = new BaseMessage(Act.ALARM, alarm);
				break;

			case 0x07:
				OBD obd = obd(data);
				obd.setDeviceSn(deviceSn);
				ret = new BaseMessage(Act.OBD, obd);
				break;
			case (byte) 0x80:
				DeviceReply deviceReply = new DeviceReply();
				deviceReply.setDeviceSn(deviceSn);
				byte[] info = new byte[data.length - 5];
				System.arraycopy(data, 5, info, 0, info.length);
				String replay = new String(info, "UTF-8");
				logger.info("replay:" + replay);

				boolean success = replay.indexOf("OK") > -1 ? true : false;

				deviceReply.setSuccess(success);
				InstructType it = InstructType.getByNum(BytesConvert.bytes2Int(
						data[1], data[2], data[3], data[4]));

				if (it.getNum() == 11) {// 清除obd故障码
					boolean isClearOBDError = replay.indexOf("OBD,15,00,01") > -1 ? true
							: false;
					deviceReply.setSuccess(isClearOBDError);
				}

				deviceReply.setInstructType(it);
				ret = new BaseMessage(Act.REPLY, deviceReply);
				break;
			case 0x09:// 故障码
				OBDError error = ObdError(data);
				// for (Alarm alarm1 : error.getErrors()) {
				// alarm1.setDeviceSn(deviceSn);
				// }
				error.setDeviceSn(deviceSn);
				ret = new BaseMessage(Act.OBD_ALARM, error);
				break;
			}
			if (ld.getSn() == null && deviceSn != null) {
				ld.setSn(deviceSn);
			}
			ret.setLoginDevice(ld);
			ret.setResponse(response);

			return ret;

		} catch (NoIndexException ex) {
			logger.error("decode", ex);
		} catch (Exception ex) {
			in.limit(0);
			logger.error("decode", ex);
		}
		return null;
	}

	private static String login(byte[] data) {
		return BytesConvert.encodeHexStr(data).substring(1, 16);
	}

	private static double positionConvert(byte... data) {
		double x = BytesConvert.bytes2Int(data[0], data[1], data[2], data[3]);// 1/500
																				// s
		double p = x / 30000 / 60;
		BigDecimal bd = new BigDecimal(p);
		bd = bd.setScale(6, BigDecimal.ROUND_HALF_UP);
		p = bd.doubleValue();
		System.out.println(p);
		return p;
	}

	private static Position gps(byte[] data) {
		Position p = new Position();
		long receiveTime = BytesConvert.bytes2long(data[0], data[1], data[2],
				data[3]) * 1000;
		// receiveTime += 8 * 60 * 60 * 1000;// 设备时间为格林尼治时间，需转为北京时间
		p.setReceive(receiveTime);

		p.setSystime(new Date().getTime());
		logger.debug("gps msg：["
				+ BytesConvert.encodeHexStr(data[4], data[5], data[6], data[7])
				+ "]");
		p.setLat(positionConvert(data[4], data[5], data[6], data[7]));
		p.setLng(positionConvert(data[8], data[9], data[10], data[11]));
		p.setSpeed((BytesConvert.bytes2Int(data[12])) / 3.6);// 公里/小时 转米/秒
		p.setDirection((float) BytesConvert.bytes2Int(data[13], data[14]));
		// 9字节基站
		p.setCell(BytesConvert.encodeHexStr(data[17], data[18], data[19],
				data[20], data[21], data[22], data[23]));
		p.setMode(((int) data[24]) == 1 ? "A" : "V");
		// 2字节设备状态(同心跳包)

		// p.setBattery(bytes2Int(new byte[]{data[27],data[28]}));//电压转电量

		return p;
	}

	private static Map heartBeat(byte[] data) {
		Map map = null;
		if (data.length != 2) {// 非法数据
			logger.error("heartBeat data length error");
		} else {
			map = new HashMap();
			int status = BytesConvert.bytes2Int(data[0], data[1]);
			String binstr = BytesConvert.toFullBinaryString(status);
			binstr = binstr.substring(16);
			logger.error("heartBeat:" + binstr);
			// 高低位反转
			StringBuffer sb = new StringBuffer(binstr);
			binstr = sb.reverse().toString();

			map.put("GPS", binstr.charAt(0) == '1' ? true : false);

			String acc = String.valueOf(new char[] { binstr.charAt(1),
					binstr.charAt(2) });
			if (!"00".equals(acc)) {
				map.put("ACC", acc.equals("11") ? true : false);
			}
			// 9位以后保留
		}

		return map;
	}

	private static Alarm alarm(byte[] data) {
		Alarm ret = new Alarm();
		ret.setTime(new Date(BytesConvert.bytes2long(data[0], data[1], data[2],
				data[3]) * 1000).getTime());
		ret.setSystime(new Date().getTime());
		ret.setLat(positionConvert(data[4], data[5], data[6], data[7]));
		ret.setLng(positionConvert(data[8], data[9], data[10], data[11]));
		ret.setSpeed(((int) data[12]) / 3.6);// 公里/小时 转米/秒
		ret.setDirection((float) BytesConvert.bytes2Int(data[13], data[14]));
		// 9字节基站

		ret.setMode(((int) data[24]) == 1 ? "A" : "V");
		ret.setType(convert(data[25]).getNum());
		return ret;
	}

	private static AlarmType convert(byte type) {
		AlarmType ret = null;
		/*switch (type) {
		case 0x01:
			ret = AlarmType.PowerOFF;
			break;
		case 0x02:
			ret = AlarmType.SOS;
			break;
		case 0x03:
			ret = AlarmType.PowerLow;
			break;
		case 0x04:
			ret = AlarmType.SHAKE;
			break;
		case 0x05:
			ret = AlarmType.VIB;
			break;

		case 0x08:
			ret = AlarmType.GPSOPEN;
			break;
		case 0x09:
			ret = AlarmType.GPSCLOSE;
			break;
		case (byte) 0x81:
			ret = AlarmType.SPDLO;
			break;
		case (byte) 0x82:
			ret = AlarmType.SPDHI;
			break;
		case (byte) 0x83:
			ret = AlarmType.BNDIN;
			break;
		case (byte) 0x84:
			ret = AlarmType.BNDOUT;
			break;
		}*/
		return ret;
	}

	/*
	 * ' ' 按指定长度分割字符串 '
	 */
	private static String[] split(String msg, int num) {
		int len = msg.length();
		if (len <= num)
			return new String[] { msg };
		int count = len / (num - 1);
		count += len > (num - 1) * count ? 1 : 0; // 这里应该值得注意
		String[] result = new String[count];
		int pos = 0;
		int splitLen = num - 1;
		for (int i = 0; i < count; i++) {
			if (i == count - 1)
				splitLen = len - pos;
			result[i] = msg.substring(pos, pos + splitLen);
			pos += splitLen;
		}
		return result;
	}

	private static OBDError ObdError(byte[] data) {
		OBDError error = new OBDError();

		String errcodes = BytesConvert.encodeHexStr(data).substring(2);
		if (errcodes.length() > 6) {
			String[] code = split(errcodes, 6);
			for (String s : code) {

				error.add(s);
			}
		} else {

			error.add(errcodes);
		}

		return error;
	}

	private static OBD obd(byte[] data) {
		OBD obd = new OBD();
		long receiveTime = BytesConvert.bytes2long(data[0], data[1], data[2],
				data[3]) * 1000;
		 receiveTime -= 8 * 60 * 60 * 1000;// 转为北京时间
		obd.setReceive(receiveTime);

		obd.setSystime(new Date().getTime());
		for (int i = 0; i < obd.getCylinders().length; i++) {
			Cylinder cylinder = new Cylinder();
			obd.getCylinders()[i] = cylinder;
		}

		for (int i = 4; i < data.length; i += 5) {
			int abcd = BytesConvert.bytes2Int(data[i + 1], data[i + 2],
					data[i + 3], data[i + 4]);
			int a = BytesConvert.bytes2Int(data[i + 1]);
			int ab = BytesConvert.bytes2Int(data[i + 1], data[i + 2]);
			int cd = BytesConvert.bytes2Int(data[i + 3], data[i + 4]);
			switch (data[i]) {
			// 扩展部分
			case (byte) 0x88:
				obd.setFuleCostPerHour((float) abcd);
				break;
			case (byte) 0x89:
				obd.setFuleCostHundred((float) (abcd / 10));
				break;
			case (byte) 0x8A:
				obd.setTotalDistance((double) abcd);
				break;
			case (byte) 0x8B:
				int point = BytesConvert.bytes2Int((byte) 0x80, (byte) 0x00);
				if (abcd >= point) {
					obd.setLeftFule(((abcd - point) / 10) + "%");
				} else {
					obd.setLeftFule((abcd / 10) + "L");
				}
				break;
			// 扩展部分结束
			case (byte) 0x02:
				obd.setFrozenFaultCode(BytesConvert.encodeHexStr(data[i + 1],
						data[i + 2]));
				break;
			case (byte) 0x03:
				String sys1 = getFuelSystem(data[i + 1]);
				String sys2 = getFuelSystem(data[i + 2]);
				String splitComa = "";
				if (!StringUtils.isEmpty(sys1) && !StringUtils.isEmpty(sys2)) {
					splitComa = "###";
				}
				obd.setFuelSystemStatus(sys1 + splitComa + sys2);
				break;
			case (byte) 0x04:
				obd.setLoad((float) (a * 100 / 255));
				break;
			case (byte) 0x05:
				obd.setCoolTemperature((float) (a - 40));
				break;
			case (byte) 0x06:
				Cylinder cylinder1 = obd.getCylinders()[0];

				// cylinder1.setShortFuelCorrection(shortFuelCorrection);

				break;
			case (byte) 0x07:
				Cylinder cylinder3 = obd.getCylinders()[2];

				break;
			case (byte) 0x08:
				Cylinder cylinder2 = obd.getCylinders()[1];

				break;
			case (byte) 0x09:
				Cylinder cylinder4 = obd.getCylinders()[3];

				break;
			case (byte) 0x0A:
				obd.setFuelPipePressure((float) (a * 3));
				break;
			case (byte) 0x0B:
				obd.setInlAirPipePressure((float) a);
				break;
			case (byte) 0x0C:
				obd.setEngineSpeed((float) ab / 4);
				break;
			case (byte) 0x0D:
				obd.setSpeed((float) a);
				break;
			case (byte) 0x0E:
				obd.setFireAngle((float) (a / 2 - 64));
				break;
			case (byte) 0x0F:
				obd.setInAirtemperature((float) a - 40);
				break;
			case (byte) 0x10:
				obd.setAirTraffic((float) ab / 100);
				break;
			case (byte) 0x11:
				obd.setThrottlePosition((float) (a * 100 / 255));
				break;
			case (byte) 0x1C:
				setObdDesc(obd, a);
				break;
			case (byte) 0x1F:
				obd.setRunTime((long) ab);
				break;
			case (byte) 0x21:
				obd.setMtlDistance((long) ab);
				break;
			case (byte) 0x22:
			case (byte) 0x23:
				obd.setFuelSupplyPipePressure((float) ab * 10);
				break;
			case (byte) 0x2C:
				obd.setEGR((float) a);
				break;
			case (byte) 0x2D:
				obd.setEGRError((float) (a * 0.78125 - 100));
				break;
			case (byte) 0x2E:
				obd.setEvaporation((float) (a * 100 / 255));
				break;
			case (byte) 0x2F:
				obd.setLiquidLevelInput((float) (a * 100 / 255));
				break;
			case (byte) 0x30:
				obd.setAfterClearStart((long) a);
				break;
			case (byte) 0x31:
				obd.setAfterClearDistance((double) ab);
				break;
			case (byte) 0x32:
				obd.setEGRError((float) (a * 0.78125 - 100));
				break;
			case (byte) 0x33:
				obd.setAirPressure((float) a);
				break;
			case (byte) 0x34:
				setOxygenSensor1(obd, 0, 0, ab, cd);
				break;
			case (byte) 0x35:
				setOxygenSensor1(obd, 0, 1, ab, cd);
				break;
			case (byte) 0x36:
				setOxygenSensor1(obd, 0, 2, ab, cd);
				break;
			case (byte) 0x37:
				setOxygenSensor1(obd, 0, 3, ab, cd);
				break;
			case (byte) 0x38:
				setOxygenSensor1(obd, 1, 0, ab, cd);
				break;
			case (byte) 0x39:
				setOxygenSensor1(obd, 1, 1, ab, cd);
				break;
			case (byte) 0x3A:
				setOxygenSensor1(obd, 1, 2, ab, cd);
				break;
			case (byte) 0x3B:
				setOxygenSensor1(obd, 1, 3, ab, cd);
				break;
			case (byte) 0x3C:
				setOxygenSensor2(obd, 0, 0, ab, cd);
				break;
			case (byte) 0x3D:
				setOxygenSensor2(obd, 1, 0, ab, cd);
				break;
			case (byte) 0x3E:
				setOxygenSensor2(obd, 0, 1, ab, cd);
				break;
			case (byte) 0x3F:
				setOxygenSensor2(obd, 1, 1, ab, cd);
				break;
			case (byte) 0x42:
				obd.setControllerVoltage((float) ab / 1000);
				break;
			case (byte) 0x43:
				obd.setLoad((float) ab * 100 / 255);
				break;

			case (byte) 0x45:
				obd.setThrottlePosition((float) a * 100 / 255);
				break;
			case (byte) 0x46:
				obd.setAmbientTemperature((float) a - 40);
				break;
			case (byte) 0x47:
			case (byte) 0x48:
			case (byte) 0x49:
			case (byte) 0x4A:
			case (byte) 0x4B:
				int index = BytesConvert.bytes2Int(data[i])
						- BytesConvert.bytes2Int((byte) 0x46);
				obd.getThrottleabsolutePosition()[index] = (float) (a * 100 / 255);
				break;
			case (byte) 0x4D:
				obd.setMtlRunTime((long) ab);
				break;
			case (byte) 0x4E:
				obd.setAfterClearRuntime((long) ab);
				break;
			case (byte) 0x51:
				// obd.setFuelType(fuelType)((long)ab);
				break;
			case (byte) 0x52:
				obd.setAlcoholPercent((float) a * 100 / 255);
				break;
			case (byte) 0x53:
				obd.setVaporPressure((float) (ab * 0.005));
				break;
			case (byte) 0x54:
				obd.setVaporPressure((float) ab - 32768);
				break;
			case (byte) 0x59:
				obd.setAfterClearRuntime((long) ab);
				break;
			case (byte) 0x5B:
				obd.setBatteryPercent((float) a * 100 / 255);
				break;
			case (byte) 0x5C:
				obd.setEngineOilTemperature((float) a - 40);
				break;
			case (byte) 0x5E:
				obd.setEngineFuelRate((float) (ab * 0.05));
				break;

			}

		}

		return obd;

	}

	/**
	 * 设置传感器的当量比和电流
	 * 
	 * @param obd
	 * @param cIndex
	 * @param sIndex
	 * @param ab
	 * @param cd
	 */
	private static void setOxygenSensor1(OBD obd, int cIndex, int sIndex,
			int ab, int cd) {
		OxygenSensor sen = obd.getCylinders()[cIndex].getOxygenSensor()[sIndex];
		sen.setEquivalenceRatio((float) (ab * 0.0000305));
		sen.setVoltage((float) (cd * 0.000122));
	}

	/**
	 * 设置传感器催化剂温度
	 * 
	 * @param obd
	 * @param cIndex
	 * @param sIndex
	 * @param ab
	 * @param cd
	 */
	private static void setOxygenSensor2(OBD obd, int cIndex, int sIndex,
			int ab, int cd) {
		OxygenSensor sen = obd.getCylinders()[cIndex].getOxygenSensor()[sIndex];
		sen.setCatalystTemperature((float) (ab / 10 - 40));
	}

	private static void setObdDesc(OBD obd, int a) {
		String desc = "";
		switch (a) {
		case 1:
			desc = "OBD-II as defined by the CARB";
			break;
		case 2:
			desc = "OBD as defined by the EPA";
			break;
		case 3:
			desc = "OBD and OBD-II";
			break;
		case 4:
			desc = "OBD-I";
			break;
		case 5:
			desc = "Not OBD compliant";
			break;
		case 6:
			desc = "EOBD (Europe)";
			break;
		case 7:
			desc = "EOBD and OBD-II";
			break;
		case 8:
			desc = "EOBD and OBD";
			break;
		case 9:
			desc = "EOBD, OBD and OBD II";
			break;
		case 10:
			desc = "JOBD (Japan)";
			break;
		case 11:
			desc = "JOBD and OBD II";
			break;
		case 12:
			desc = "JOBD and EOBD";
			break;
		case 13:
			desc = "JOBD, EOBD, and OBD II";
			break;
		case 14:
			desc = "Reserved";
			break;
		case 15:
			desc = "Reserved";
			break;
		case 16:
			desc = "Reserved";
			break;
		case 17:
			desc = "Engine Manufacturer Diagnostics (EMD)";
			break;
		case 18:
			desc = "Engine Manufacturer Diagnostics Enhanced (EMD+)";
			break;
		case 19:
			desc = "Heavy Duty On-Board Diagnostics (Child/Partial) (HD OBD-C)";
			break;
		case 20:
			desc = "Heavy Duty On-Board Diagnostics (HD OBD)";
			break;
		case 21:
			desc = "World Wide Harmonized OBD (WWH OBD)";
			break;
		case 22:
			desc = "Reserved";
			break;
		case 23:
			desc = "Heavy Duty Euro OBD Stage I without NOx control (HD EOBD-I)";
			break;
		case 24:
			desc = "Heavy Duty Euro OBD Stage I with NOx control (HD EOBD-I N)";
			break;
		case 25:
			desc = "Heavy Duty Euro OBD Stage II without NOx control (HD EOBD-II)";
			break;
		case 26:
			desc = "Heavy Duty Euro OBD Stage II with NOx control (HD EOBD-II N)";
			break;
		case 27:
			desc = "Reserved";
			break;
		case 28:
			desc = "Brazil OBD Phase 1 (OBDBr-1)";
			break;
		case 29:
			desc = "Brazil OBD Phase 2 (OBDBr-2)";
			break;
		case 30:
			desc = "Korean OBD (KOBD)";
			break;
		case 31:
			desc = "India OBD I (IOBD I)";
			break;
		case 32:
			desc = "India OBD II (IOBD II)";
			break;
		case 33:
			desc = "Heavy Duty Euro OBD Stage VI (HD EOBD-IV)";
			break;
		}
		obd.setComplyStandards(desc);
	}

	private static String getFuelSystem(byte da) {
		int val = BytesConvert.bytes2Int(da);
		String sys = "";
		switch (val) {
		case 1:
			sys = "Open loop due to insufficient engine temperature";
			break;
		case 2:
			sys = "Closed loop, using oxygen sensor feedback to determine fuel mix";
			break;
		case 4:
			sys = "Open loop due to engine load OR fuel cut due to deceleration";
			break;
		case 8:
			sys = "Open loop due to system failure";
			break;
		case 16:
			sys = "Closed loop, using at least one oxygen sensor but there is a fault in the feedback system";
			break;
		}
		return sys;
	}

}
