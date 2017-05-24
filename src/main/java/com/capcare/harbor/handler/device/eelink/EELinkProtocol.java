package com.capcare.harbor.handler.device.eelink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import module.util.BytesConvert;
import module.util.JsonUtils;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capcare.harbor.handler.device.DeviceProtocol;
import com.capcare.harbor.handler.device.m2616.M2616Encoder;
import com.capcare.harbor.service.logic.InstructService;
import com.capcare.harbor.util.Harbor;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.InstructVo;

public class EELinkProtocol implements DeviceProtocol {

	private static Logger logger = LoggerFactory
			.getLogger(EELinkProtocol.class);

	public boolean decode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) {
		String deviceSn = session.containsAttribute("duid") ? (String) session
				.getAttribute("duid") : null;
		BaseMessage message = EElinkDecoder.decode(in, deviceSn);
		if (message != null) {
			out.write(message);
			return true;
		} else {
			return false;
		}

	}

	public Object encode(IoSession session, Object protocol) {
		String str_instruct = null;
		int instruct_type = 0;
		if (protocol instanceof InstructVo) {// 下发指令
			InstructVo instruct = (InstructVo) protocol;
			Map<String, Object> cmdMap = instruct.getCmdMap();
			instruct_type = instruct.getInstructType().getNum();
			switch (instruct.getInstructType()) {
			case SetSpeed:
				String min = String.valueOf(cmdMap.get("min"));
				String max = String.valueOf(cmdMap.get("max"));
				str_instruct = "SPEED," + min + "," + max;
				break;
			case SetTickInterval:
				str_instruct = "TIMER,"
						+ String.valueOf((Integer) cmdMap.get("interval"));
				break;
			case SetFence:

				str_instruct = "FENCE,";
				int condition = 1;// 进围栏 2出围栏
				if (cmdMap.get("condition") != null) {
					condition = (Integer) cmdMap.get("condition");
				}

				int type = 1;// 围栏类型，默认为圆形
				if (cmdMap.get("type") != null) {
					type = (Integer) cmdMap.get("type");
				}
				if (type == 1) {// 圆形
					Double[] center = (Double[]) cmdMap.get("center");
					String lat = null;
					String lng = null;
					if (center != null) {
						lat = String.valueOf(center[1]);
						lng = String.valueOf(center[0]);
					} else {
						logger.error("fence instruct error:lost center,sn="
								+ instruct.getDeviceSn());
					}
					String radius = null;
					if (cmdMap.get("radius") != null) {
						radius = String.valueOf((Integer) cmdMap.get("radius"));
					} else {
						logger.error("fence instruct error:lost radius,sn="
								+ instruct.getDeviceSn());
					}
					switch (condition) {
					case 1:// 入
						str_instruct += "5,IR," + lng + "," + lat + ","
								+ radius;
						break;
					case 2:
						str_instruct += "4,OR," + lng + "," + lat + ","
								+ radius;
						break;
					}

				} else if (type == 2) {// 矩形
					String region = ((String) cmdMap.get("region")).replaceAll(
							"lat=", "\"lat\":").replaceAll("lng=", "\"lng\":");
					List<Map<Double, Double>> list = JsonUtils.json2Obj(region,
							ArrayList.class);
					if (list != null) {
						Map<Double, Double> point1 = list.get(3);
						Map<Double, Double> point2 = list.get(1);
						String lng1 = String.valueOf(point1.get("lng"));
						String lat1 = String.valueOf(point1.get("lat"));
						String lng2 = String.valueOf(point2.get("lng"));
						String lat2 = String.valueOf(point2.get("lat"));
						switch (condition) {
						case 1:// 入
							str_instruct += "2,IS," + lng1 + "," + lat1 + ","
									+ lng2 + "," + lat2;
							break;
						case 2:
							str_instruct += "1,OS," + lng1 + "," + lat1 + ","
									+ lng2 + "," + lat2;
							break;
						}
					} else {
						logger.error("fence instruct error:lost points,sn="
								+ instruct.getDeviceSn());
					}

				}
				break;
			case SetFenceSwitch:// 围栏报警开关
				int fenceSwitch = (Integer) cmdMap.get("switch");
				if (fenceSwitch == 0) {
					str_instruct = "FENCE,0";
				}

				break;
			case Reboot:// 重启
				str_instruct = "RESET";
				break;
			case Restore:// 恢复出厂设置
				str_instruct = "FACTORY";
				break;
			case ClearOBDError:// 清除OBD故障码
				str_instruct = "OBD,15";
				break;
			default:
				logger.error("不支持的指令:" + instruct.getInstructType().name());
				break;
			}
		}
		if (str_instruct != null) {
			str_instruct += "#";
		}

		if (str_instruct == null) {
			return null;
		}

		return getBytes(instruct_type,
				str_instruct.getBytes(Harbor.DEFAULT_CHARSET));

		// return str_instruct;
	}

	public Object getBytes(int instruct_type, byte[] instruct) {

		int len = instruct.length;
		byte[] plen = BytesConvert.int2Bytes(7 + len, 2);
		byte[] server = BytesConvert.int2Bytes(instruct_type, 4);
		byte ret[] = new byte[12 + len];

		ret[0] = 0x67; // 信息头
		ret[1] = 0x67;

		ret[2] = (byte) 0x80; // 协议号

		ret[3] = plen[0]; // 包长度
		ret[4] = plen[1];

		ret[5] = 0x00;// 序列号
		ret[6] = 0x01;

		ret[7] = 0x01; // 信息标示

		ret[8] = server[0]; // 服务器标示
		ret[9] = server[1];
		ret[10] = server[2];
		ret[11] = server[3];

		for (int i = 0; i < len; i++) {
			ret[12 + i] = instruct[i];
		}

		return ret;
	}

}
