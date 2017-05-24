package com.capcare.harbor.handler.device.chengan;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.List;

import module.util.BytesConvert;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.model.SystemStatus;
import com.capcare.harbor.model.UserDeviceOperation;
import com.capcare.harbor.protocol.DeviceOperation;
import com.capcare.harbor.protocol.FireAlarm;
import com.capcare.harbor.util.DeviceStatus;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;

/**
 * decode调用该类 用于解析，封装
 */
@Component
@Scope("singleton")
public class ChenganCodeUtils {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public List<String> resolveMessage(IoBuffer in) {

		in.mark();
		int limit = in.limit();
		byte[] bytes = new byte[limit];
		logger.info("收到数据长度===================》"+limit);
		
		try {
			in.get(bytes);// io-->bytes
		} catch (BufferUnderflowException e) {
			in.reset();// 读取失败返回mark
			return null;
		}
		String hexs = BytesConvert.encodeHexStr(bytes);
		logger.info(" Chengan convert message begin ：[" + hexs + "]");
		// @@##区分 没有的话就不分
		String[] split = hexs.split("23234040");
		if (split != null) {
			List<String> ls = new ArrayList<String>();
			if (split.length == 1) {
				ls.add(this.getMessage(split[0], true, true));
			} else {
				for (int num = 0; num < split.length; num++) {
					if (num == 0)
						ls.add(this.getMessage(split[num], true, false));
					else if (num == split.length - 1)
						ls.add(this.getMessage(split[num], false, true));
					else
						ls.add(this.getMessage(split[num], false, false));
				}
			}
			return ls;
		}
		return null;
	}

	
	private String getMessage(String hexs, boolean hasHead, boolean hasEnd) {
		// 消息的第一个标志
		String head = hasHead ? hexs.substring(0, 4) : "4040";
		// 消息的最后一个标志
		String tail = hasEnd ? hexs.substring(hexs.length() - 4, hexs.length()) : "2323";

		logger.info("head==" + head + "tail=" + tail);

		if (!head.equals("4040") || !tail.equals("2323")) {
			return null;
		}
		// 去除标志位的消息
		String message = hexs.substring(hasHead ? 4 : 0, hasEnd ? (hexs.length() - 4) : hexs.length());
		logger.info(" Chengan convert message end ：[" + message + "]");
		return message;
	}

	public BaseMessage systemStatus(StringUtil str) throws Exception {
		SystemStatus systemStatus = new SystemStatus();
		System.out.println("建筑消防设施系统状态");
		// 系统类型
		String sysType = str.getStr(2);
		systemStatus.setSysType(sysType);
		System.out.println("系统类型=" + sysType);
		// 系统地址
		String sysAddress = str.getStr(2);
		systemStatus.setSysAddress(sysAddress);
		System.out.println("系统地址=" + sysAddress);
		// 系统状态
		String sysStatus = "";

		// 系统类型是1
		if (Integer.valueOf(sysType) == 1) {
			System.out.println("系统类型为主电故障！");
			// 低
			int s0 = Integer.parseInt(str.getStr(2));
			// 高
			int s1 = Integer.parseInt(str.getStr(2));

//			String binaryString0 = toString(Integer.toBinaryString(s0), 8);
//			String binaryString1 = toString(Integer.toBinaryString(s1), 8);
			
			String binaryString0 = toString(BytesConvert.hexString2binaryString(String.valueOf(s0)), 8);
			String binaryString1 = toString(BytesConvert.hexString2binaryString(String.valueOf(s1)), 8);
			
			System.out.println(binaryString1 + binaryString0);
			String sTemp = binaryString1 + binaryString0;
			byte[] b = sTemp.getBytes();
			for (int j = 0, k = 15; j < b.length; j++, k--) {
				if (b[j] == '1') {
					switch (k) {
					case 15: {
						System.out.println(15);
						break;
					}
					case 14: {
						System.out.println(14);
						break;
					}
					case 13: {
						System.out.println(13);
						break;
					}
					case 12: {
						System.out.println(12);
						break;
					}
					case 11: {
						System.out.println(11);
						break;
					}
					case 10: {
						System.out.println(10);
						break;
					}
					case 9: {
						System.out.println(9);
						break;
					}
					case 8: {
						System.out.println("主电故障！");
						sysStatus = "主电故障！";
						break;
					}
					case 7: {
						System.out.println(7);
						break;
					}
					case 6: {
						System.out.println(6);
						break;
					}
					case 5: {
						System.out.println(5);
						break;
					}
					case 4: {
						System.out.println(4);
						break;
					}
					case 3: {
						System.out.println(3);
						break;
					}
					case 2: {
						System.out.println(2);
						break;
					}
					case 1: {
						System.out.println(1);
						break;
					}
					case 0: {
						System.out.println(0);
						break;
					}
					}
				} else {

				}
			}

		}
		systemStatus.setSysStatus(sysStatus);
		// 时间标签
		// 秒分时
		String secTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String divideTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String hourTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String sdh = hourTem + divideTem + secTem;
		// 年月日
		// 日
		String dayTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		// 月
		String monthTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		// 年
		String yearTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String time = "20" + yearTem + monthTem + dayTem + sdh;

		System.out.println(time);
		systemStatus.setTime(time);
		BaseMessage ret = new BaseMessage(Act.FIREALARM, systemStatus);
		return ret;

	}

	// 2建筑消防设施部件运行状态
	public BaseMessage deviceStatus(StringUtil str, String primaryAddress) throws Exception {
		logger.info("建筑消防设施部件运行状态============================");
		FireAlarm fireAlarm = new FireAlarm();
		// 系统类型标志
		String sysTyp = str.getStr(2);
		logger.info("系统类型标志=====" + Integer.parseInt(sysTyp, 16));
		// fireAlarm.setSysTyp(sysTyp);
		// 系统地址
		String sysAddress = str.getStr(2);
		logger.info("系统地址=====" + Integer.parseInt(sysAddress, 16));
		// deviceStatus.setSysAddress(sysAddress);
		// 部件类型
		String componentType = str.getStr(2);
		logger.info("部件类型=====" + Integer.parseInt(componentType, 16));
		// deviceStatus.setComponentType(componentType);
//		System.out.println("部件类型=" + componentType);
		// 部件地址(探测器编码) --4字节二进制数 1 0 0 0 低字节先传
		String aa = str.getStr(2);
		String bb = str.getStr(2);
		String cc = str.getStr(2);
		String dd = str.getStr(2);
		// logger.info("a="+aa+"b="+bb+"c="+cc+"d="+dd);
		// String componentAddress =dd+cc+bb+aa;
		String str1 = String.valueOf(Integer.parseInt(dd + cc, 16));
		String str2 = String.valueOf(Integer.parseInt(bb + aa, 16));
		logger.info("部件地址(探测器编码)设备编码=" + toString((str1 + str2), 8));
		// 部件地址=源地址+部件地址
		fireAlarm.setSn(toString((primaryAddress), 6) + toString((str1 + str2), 8));
		// 部件状态 2字节 低字节先传 2
		String componetStatus = "";
		// 低
		int s0 = Integer.valueOf(str.getStr(2));
		// 高
		int s1 = Integer.valueOf(str.getStr(2));

//		String binaryString0 = toString(Integer.toBinaryString(s0), 8);
//		String binaryString1 = toString(Integer.toBinaryString(s1), 8);

		String binaryString0 = toString(BytesConvert.hexString2binaryString(String.valueOf(s0)), 8);
		String binaryString1 = toString(BytesConvert.hexString2binaryString(String.valueOf(s1)), 8);
		
		
		String sTemp = binaryString1 + binaryString0;
		// 系统类型+2进制位组装成状态
		// if (Integer.valueOf(sysTyp) == 1||Integer.valueOf(sysTyp) ==
		// 100||Integer.valueOf(sysTyp) == 101||Integer.valueOf(sysTyp) == 102)
		// {

		byte[] b = sTemp.getBytes();
		for (int j = 0, k = 15; j < b.length; j++, k--) {
			// 碰到1看看是第几位
			if (b[j] == '1') {

				switch (k) {
				case 15: {
					componetStatus = "15";
					System.out.println(15);
					break;
				}
				case 14: {
					componetStatus = "14";
					System.out.println(14);
					break;
				}
				case 13: {
					componetStatus = "13";
					System.out.println(13);
					break;
				}
				case 12: {
					componetStatus = "12";
					System.out.println(12);
					break;
				}
				case 11: {
					componetStatus = "11";
					System.out.println(11);
					break;
				}
				case 10: {
					componetStatus = "10";
					System.out.println(10);
					break;
				}
				case 9: {
					componetStatus = "9";
					System.out.println(9);
					break;
				}
				case 8: {
					componetStatus = "8";
					System.out.println(8);
					break;
				}
				case 7: {
					componetStatus = "7";
					System.out.println(7);
					break;
				}
				case 6: {
					componetStatus = "6";
					System.out.println(6);
					break;
				}
				case 5: {
					componetStatus = "5";
					System.out.println(5);
					break;
				}
				case 4: {
					componetStatus = "4";
					System.out.println(4);
					break;
				}
				case 3: {
					componetStatus = "3";
					System.out.println(3);
					break;
				}
				case 2: {
					// 保存到数据库中为0
					componetStatus = "2";
					break;
				}
				case 1: {
					componetStatus = "1";
					break;
				}
				case 0: {
					System.out.println(0);
					break;
				}
				}
			} else {

			}
		}

		// logger.info("部件类型="+componetStatus);
		// 火警保存到数据库是1 需要关联到报警类型表
		if ((Integer.parseInt(sysTyp, 16) + componetStatus).equals("11")) {
			fireAlarm.setType("in002");
		}
		// 故障保存到数据库是0
		else if ((Integer.parseInt(sysTyp, 16) + componetStatus).equals("12")) {
			fireAlarm.setType("in001");
		}
		// 故障恢复不保存到数据库
		else if ((Integer.parseInt(sysTyp, 16) + componetStatus).equals("1002")) {
			fireAlarm.setType("guzhanghuifu");
		} else {
			// 其他暂时不解析
			fireAlarm.setType("8888");
		}

		logger.info("部件状态编码=" + Integer.parseInt(sysTyp, 16) + componetStatus);
		logger.info("部件状态=" + new DeviceStatus().returnValue(Integer.parseInt(sysTyp, 16) + componetStatus));

		// 部件说明 GB18030编码 因为控制器接口，一般都不输出描述
		String componetMsg = str.getStr(62).trim();
		System.out.println("部件说明GB18030编码=" + componetMsg);
		// deviceStatus.setComponetMsg(componetMsg);
		logger.info("componetMsg======" + componetMsg);
		// 时间标签
		// 秒分时
		String secTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String divideTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String hourTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String sdh = hourTem + divideTem + secTem;
		// 年月日
		// 日
		String dayTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		// 月
		String monthTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		// 年
		String yearTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);

		String time = "20" + yearTem + monthTem + dayTem + sdh;

		fireAlarm.setAlarmTime(time);
		// Calendar c = Calendar.getInstance();
		// fireAlarm.setCreateTime(c.getTime().toString());
		BaseMessage ret = new BaseMessage(Act.FIREALARM, fireAlarm);
		logger.info("----------------------------------");
		return ret;
	}

	// 建筑消防设施操作信息
	public BaseMessage devicOperation(StringUtil str, String primaryAddress) throws Exception {
		System.out.println("建筑消防设施操作信息");
		DeviceOperation deviceOperation = new DeviceOperation();

		// 源地址--- 查询复位哪些设备（设备编码==源地址+部件地址）
		deviceOperation.setPrimaryAddress(toString((primaryAddress), 6));

		// 系统类型标志
		String sysType = str.getStr(2);
		deviceOperation.setSysType(String.valueOf(Integer.parseInt(sysType)));
		logger.info("系统类型标志========>" + String.valueOf(Integer.parseInt(sysType)));
		// 系统地址
		String sysAddress = str.getStr(2);
		deviceOperation.setSysAddress(sysAddress);
		logger.info("系统地址========>" + sysAddress);

		// 操作标志
		int s13 = Integer.parseInt(str.getStr(2));
//		String operationFlag = toString(Integer.toBinaryString(s13), 8);
		String operationFlag = toString(BytesConvert.hexString2binaryString(String.valueOf(s13)), 8);
		
		byte[] b = operationFlag.getBytes();
		for (int j = 0, k = 7; j < b.length; j++, k--) {
			if (b[j] == '1') {
				switch (k) {
				case 7: {
					operationFlag = "7";
					System.out.println(7);
					break;
				}
				case 6: {
					operationFlag = "6";
					System.out.println(6);
					break;
				}
				case 5: {
					operationFlag = "5";
					System.out.println(5);
					break;
				}

				case 4: {
					operationFlag = "4";
					System.out.println(4);
					break;
				}
				case 3: {
					operationFlag = "3";
					System.out.println(3);
					break;
				}
				case 2: {
					operationFlag = "2";
					System.out.println(2);
					break;
				}
				case 1: {
					operationFlag = "1";
					System.out.println(1);
					break;
				}
				case 0: {
					operationFlag = "0";
					System.out.println("复位标志");
					logger.info("复位标志");
					break;
				}
				}
			}
		}
		deviceOperation.setOperationFlag(operationFlag);
		// 操作员编码
		String operatorCode = str.getStr(2);
		deviceOperation.setOperatorCode(operatorCode);
		logger.info("操作员编码=====>" + operatorCode);

		// 时间标签
		// 秒分时
		String secTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String divideTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String hourTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String sdh = hourTem + divideTem + secTem;
		// 年月日
		// 日
		String dayTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		// 月
		String monthTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		// 年
		String yearTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);

		String time = "20" + yearTem + monthTem + dayTem + sdh;
		logger.info("时间标签=====>" + time);
		deviceOperation.setTime(time);
		BaseMessage ret = new BaseMessage(Act.FIREALARM, deviceOperation);
		logger.info("系统类型标志=" + sysType + "源地址=" + deviceOperation.getPrimaryAddress() + "系统地址=" + sysAddress + "操作标志=" + operationFlag + "操作员编码=" + operatorCode);
		return ret;

	}

	// 用户信息传输装置运行状态 8位2进制
	public BaseMessage userOperationStatus(StringUtil str) throws Exception {

		System.out.println("用户信息传输装置运行状态");
		int s12 = Integer.parseInt(str.getStr(2));
		// 主电故障
//		String mainElectricFault = toString(Integer.toBinaryString(s12), 8);
		
		String mainElectricFault = toString(BytesConvert.hexString2binaryString(String.valueOf(s12)), 8);
//		String binaryString1 = toString(BytesConvert.hexString2binaryString(String.valueOf(s1)), 8);
		
		byte[] b2 = mainElectricFault.getBytes();
		for (int j = 0, k = 7; j < b2.length; j++, k--) {
			if (b2[j] == '1') {
				switch (k) {
				case 7: {
					mainElectricFault = "7";
					System.out.println(7);
					break;
				}
				case 6: {
					mainElectricFault = "6";
					System.out.println(6);
					break;
				}
				case 5: {
					mainElectricFault = "5";
					System.out.println(5);
					break;
				}

				case 4: {
					mainElectricFault = "4";
					System.out.println(4);
					break;
				}
				case 3: {
					mainElectricFault = "3";
					System.out.println("主电故障");
					break;
				}
				case 2: {
					mainElectricFault = "2";
					System.out.println(2);
					break;
				}
				case 1: {
					mainElectricFault = "1";
					System.out.println(1);
					break;
				}
				case 0: {
					mainElectricFault = "0";
					System.out.println(0);
					break;
				}
				}
			}
		}

		// 时间标签
		// 秒分时
		String secTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String divideTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String hourTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String sdh = hourTem + divideTem + secTem;
		// 年月日
		// 日
		String dayTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		// 月
		String monthTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		// 年
		String yearTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);

		String time = "20" + yearTem + monthTem + dayTem + sdh;

		logger.info("时间标签=====" + time);

		logger.info("装置运行状态===" + mainElectricFault);
		// deviceOperation.setTime(time);

		BaseMessage ret = new BaseMessage(Act.FIREALARM, mainElectricFault);

		return ret;

	}

	// 用户信息传输装置操作信息 8位2进制
	public BaseMessage userOperation(StringUtil str) throws Exception {
		System.out.println("用户信息传输装置操作信息");
		UserDeviceOperation userDeviceOperation = new UserDeviceOperation();
		String operationFlag = "";

		// 自检
		int s11 = Integer.parseInt(str.getStr(2));
//		String selfChecking = toString(Integer.toBinaryString(s11), 8);
		
		String selfChecking = toString(BytesConvert.hexString2binaryString(String.valueOf(s11)), 8);
		
		byte[] b1 = selfChecking.getBytes();
		for (int j = 0, k = 7; j < b1.length; j++, k--) {
			if (b1[j] == '1') {
				switch (k) {
				case 7: {
					operationFlag = "7";
					System.out.println(7);
					break;
				}
				case 6: {
					operationFlag = "6";
					System.out.println(6);
					break;
				}
				case 5: {
					operationFlag = "5";
					System.out.println(5);
					break;
				}

				case 4: {
					operationFlag = "4";
					System.out.println("自检");
					operationFlag = "自检";
					break;
				}
				case 3: {
					operationFlag = "3";
					operationFlag = "警情清楚";
					System.out.println(3);
					break;
				}
				case 2: {
					operationFlag = "2";
					operationFlag = "手动报警";
					System.out.println(2);
					break;
				}
				case 1: {
					operationFlag = "1";
					// operationFlag = "消音";
					System.out.println(1);
					break;
				}
				case 0: {
					operationFlag = "0";
					// operationFlag = "复位";
					System.out.println(0);
					break;
				}
				}
			}
		}

		logger.info("用户信息传输装置操作信息==" + operationFlag);
		userDeviceOperation.setOperationFlag(operationFlag);
		// 操作员编码
		String operatorCode = str.getStr(2);
		userDeviceOperation.setOperatorCode(operatorCode);

		// 时间标签
		// 秒分时
		String secTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String divideTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String hourTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		String sdh = hourTem + divideTem + secTem;
		// 年月日
		// 日
		String dayTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		// 月
		String monthTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);
		// 年
		String yearTem = toString(String.valueOf(Integer.parseInt(str.getStr(2), 16)), 2);

		String time = "20" + yearTem + monthTem + dayTem + sdh;

		System.out.println("time============================." + time);
		// deviceOperation.setTime(time);
		System.out.println("操作员编码=" + operatorCode);
		BaseMessage ret = new BaseMessage(Act.FIREALARM, userDeviceOperation);
		return ret;

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

	// 把一位数字转为两位数字
	@SuppressWarnings("unused")
	private static String replenishStr(byte bytes) {
		String str = Byte.toString(bytes);
		if (str.length() < 2 && !"".equals(str)) {
			return "0" + str;
		} else {
			return str;
		}
	}

	public static byte[] toRsBytes(byte[] ret) {

		int count7e = 0;
		for (int num = 0; num < ret.length - 1; num++) {// 校验码
			ret[ret.length - 1] ^= ret[num];
			if (ret[num] == 0x7E || ret[num] == 0x7D)
				count7e++;
		}

		// 转码量计算
		if (ret[ret.length - 1] == 0x7E || ret[ret.length - 1] == 0x7D)
			count7e++;

		byte[] rsbt = new byte[2 + count7e + ret.length];
		rsbt[0] = 0x7E;
		int num = 1;
		for (byte bt : ret) {// 转码
			if (bt == 0x7E) {
				rsbt[num++] = 0x7D;
				rsbt[num++] = 0x02;
			} else if (bt == 0x7D) {
				rsbt[num++] = 0x7D;
				rsbt[num++] = 0x01;
			} else {
				rsbt[num++] = bt;
			}
		}
		rsbt[rsbt.length - 1] = 0x7E;
		return rsbt;
	}

}
