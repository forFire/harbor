package com.capcare.harbor.handler.device.mt90;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import com.capcare.harbor.vo.InstructVo;
import com.capcare.harbor.vo.InstructType;

public class MT90Encoder {

	private static final String beginTag = "@@";

	private static final char separator = ',';

	private static final char star = '*';
	
	private static byte PACK_TAG = 0x41;
	
	private static final String finish = "\r\n";
	
	private static DecimalFormat decimalFormat = new DecimalFormat("0.000000");
	
	/**
	 * 将服务器端指令 转换为 为设备需要的格式
	 * 
	 * @param session
	 * @param protocol
	 * @return
	 * @throws Exception
	 */
	public static List<String> encodeInstruct(IoSession session, InstructVo protocol) throws Exception {
		Map<String, Object> cmdMap = protocol.getCmdMap();

		InstructType instructType = protocol.getInstructType();
		String deviceSn = protocol.getDeviceSn();
		String downStr = null;
		String[] params = null;
		List<String> downList = new LinkedList<String>(); 
		
		switch (instructType) {
		case SetTickInterval:// 设置上传时间间隔
			int interval = (Integer) cmdMap.get("interval");
			int timesLimit = 0;
			if (cmdMap.containsKey("times_limit")){
				timesLimit = (Integer) cmdMap.get("times_limit");
			}
			params = new String[]{String.valueOf(interval), String.valueOf(timesLimit)};
			downStr = MT90Encoder.encode(deviceSn, InstructType.SetTickInterval, params);
			downList.add(downStr);
			break;

		case SetFence:// 设置围栏
			String fenceId = "1";
			int type = (Integer) cmdMap.get("type");
			if (type == 1) {// 圆形
				
				Double[] center = (Double[]) cmdMap.get("center");


				double lat = center[1];
				double lng = center[0];
				long radius = (Integer) cmdMap.get("radius");
				Integer condition = (Integer) cmdMap.get("condition");
				int in = 0;
				int out = 1;
				if (condition != null && condition == 2) {
					in = 1;
				}
				params = new String[]{fenceId, decimalFormat.format(lat),decimalFormat.format(lng),
						String.valueOf(radius),String.valueOf(in), String.valueOf(out)};
				downStr = MT90Encoder.encode(deviceSn, InstructType.SetFence, params);
				downList.add(downStr);
			}

			break;
		case SetSpeed://超速报警
			int spdSwitch = (Integer) cmdMap.get("switch");
			int max = 0;
			if (spdSwitch == 1){
				max = (Integer) cmdMap.get("max");
			}

			params = new String[]{String.valueOf(max)};
			downStr = MT90Encoder.encode(deviceSn, InstructType.SetSpeed, params);
			downList.add(downStr);
			break;
		case SetSos://设置管理人号码
			String[] nums = (String[]) cmdMap.get("nums");
			params = new String[]{nums[0], nums[1], nums[2]};
			downStr = MT90Encoder.encode(deviceSn, InstructType.SetSos, params);
			downList.add(downStr);
			break;
		case Restore://恢复出厂设置
			downStr = MT90Encoder.encode(deviceSn, InstructType.Restore, params);
			downList.add(downStr);
			break;

		default:
			break;
		}

		return downList;
	}

	/**
	 * 将服务器端指令 格式化 为设备需要的格式
	 * @param imei
	 * @param instructType
	 * @param params
	 * @return
	 */
	private static String encode(String imei, InstructType instructType,String[] params){

		char mark = (char) PACK_TAG;
		byte packMark = (byte) (PACK_TAG + 1);
		if (packMark > 0x7A)
			packMark = 0x41;
		PACK_TAG = packMark;

		String content = null;
		StringBuilder sb = new StringBuilder();
		if(params != null && params.length > 0){
			int index = 1;
			for(String param : params){
				sb.append(param);
				if(index < params.length){
					sb.append(separator);
				}
				index++;
			}
		}
		content = sb.toString();
		int conLen = content == null ? 0 : content.length() + 1;
		int length = 1 + imei.length() + 1 + 3 + conLen + 5;

		sb = new StringBuilder();
		sb.append(beginTag);
		sb.append(mark);
		sb.append(length);
		sb.append(separator);
		sb.append(imei);
		sb.append(separator);
		sb.append(InstructMapping.mapToDevice(instructType));
		
		if (content != null && !content.equals("")){
			sb.append(separator + content);
		}
		sb.append(star);

		byte[] added = sb.toString().getBytes();
		int temp = 0;
		for (byte b : added) {
			short ub = (short) (b & 0xFF);
			temp += ub;
		}
		byte check = (byte) temp;
		String hex = Integer.toHexString(check & 0xFF);
		if (hex.length() == 1)
			hex = '0' + hex;
		sb.append(hex.toUpperCase());
		sb.append(finish);

		return sb.toString();

	}
}
