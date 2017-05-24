package com.capcare.harbor.handler.device.chengan;

import java.nio.BufferUnderflowException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import module.util.BytesConvert;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.capcare.harbor.protocol.EquipmentTime;
import com.capcare.harbor.service.cache.RoomLastTimeCache;
import com.capcare.harbor.util.DateUtil;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.LoginDevice;

/**
 * 解析，封装，过滤
 * @author zf
 */
@Component
public class ChenganDecoder {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Resource
	private ChenganCodeUtils codeUtils;
	
	@Autowired
	RoomLastTimeCache roomLastTimeCache;
	
	String messType = "";
	// 协议版本号
	String protocolVersion = "";
	String yearTem = "";
	String monthTem = "";
	String dayTem = "";
	String hourTem = "";
	String divideTem = "";
	String secTem = "";
	String primaryAddress = "";
	String destinationAddress = "";
	String dataUnitLength = "";
	String orderByte = "";
	List<BaseMessage>	rets = new ArrayList<BaseMessage>();
	//校验码
	private String checkCode = "";
	private String lastTime ="";
	//每当有数据到达时，IOService会先调用底层IO接口读取数据，封装成IoBuffer，之后以事件的形式通知上层代码，从而将Java NIO的同步IO接口转化成了异步IO。
	public List<BaseMessage> decode(IoSession session, IoBuffer in) throws Exception {
		// 校验数据包
		boolean check = checkInBytes(in);
		if (!check) {
			return null;
		}
		List<String> messagels = codeUtils.resolveMessage(in);
		if (messagels == null) {
			log.info("messagels===null");
			return null;
		}
		List<BaseMessage> ls = new ArrayList<BaseMessage>();
		for (String message : messagels) {
			log.info("message=================" + message);
			// 校验和
//			checkCode = checkMessage(message.substring(message.length()-2,message.length()));
			log.info("校验和=================" + checkCode);
			//解析
			List<BaseMessage>	baseMessageLists = getMessage(session, in, message);
			// 对所有的消息返回的都是只有3确认和6否认 这2种应
			ChenganResponse response = new ChenganResponse();// 应答消息
			LoginDevice ld = new LoginDevice();
			ld.setDt(new Chengan());
			response.setFlag(true);
			
			if(baseMessageLists !=null){
				
				for(BaseMessage baseMsg  : baseMessageLists){
					
					if(baseMsg != null){
						baseMsg.setLoginDevice(ld);
						baseMsg.setResponse(response);
						if (baseMsg != null) {
							ls.add(baseMsg);
						}
					}
				}
			}
			
		}
		return ls;
	}
	
	
	private List<BaseMessage> getMessage(IoSession session, IoBuffer in, String mesage) {
		
		if (mesage == null || mesage.length() <= 0) {
			return null;
		}
		StringUtil su = new StringUtil(mesage);
		// 业务流水号
		try {
			messType = su.getStr(4);
			log.info("业务流水号=" + messType);
			System.out.println("业务流水号=" + Integer.parseInt(messType,16));
			// 协议版本号
			protocolVersion = su.getStr(4);
			System.out.println("协议版本号=" + Integer.parseInt(protocolVersion,16));
			// 时间标签 18 16 15 1 3 16
			// 秒分时
			secTem = toString(String.valueOf(Integer.parseInt(su.getStr(2),16)),2);
			divideTem = toString(String.valueOf(Integer.parseInt(su.getStr(2),16)),2);
			hourTem = toString(String.valueOf(Integer.parseInt(su.getStr(2),16)),2);
			
			String sdh = hourTem + divideTem + secTem;
			// 年月日
			// 日
			dayTem = toString(String.valueOf(Integer.parseInt(su.getStr(2),16)),2);
			// 月
			monthTem = toString(String.valueOf(Integer.parseInt(su.getStr(2),16)),2);
			// 年
			yearTem = toString(String.valueOf(Integer.parseInt(su.getStr(2),16)),2);
			
			String ymd = "20" + yearTem + monthTem + dayTem;
			
			log.info("时间标签===>" + ymd + sdh); 
			
			lastTime = ymd + sdh;
			
			// 源地址 如果是JK8000发送的， 那就是JK8000的用户编码 如果是中心发送的报文，可忽略     例如 ：500000000000--->000080
			String s1 = toString(su.getStr(2),2);
			String s2 = toString(su.getStr(2),2);
			String s3 = toString(su.getStr(2),2);
			String s4 = toString(su.getStr(2),2);
			String s5 = toString(su.getStr(2),2);
			String s6 = toString(su.getStr(2),2);
			primaryAddress = s6+s5+s4+s3+s2+s1;
			primaryAddress = String.valueOf(Integer.parseInt(primaryAddress,16));
			
			log.info("源地址 =" + toString((primaryAddress),6));
			// 目的地址
			String s11 = toString(su.getStr(2),2);
			String s12 = toString(su.getStr(2),2);
			String s13 = toString(su.getStr(2),2);
			String s14 = toString(su.getStr(2),2);
			String s15 = toString(su.getStr(2),2);
			String s16 = toString(su.getStr(2),2);
			
			destinationAddress =s16+s15+s14+s13+s12+s11;
			destinationAddress = String.valueOf(Integer.parseInt(destinationAddress,16));
			log.info("目的地址=" + destinationAddress);
			// 应用数据单元长度
			String dataUnitLength0 = su.getStr(2);
			String dataUnitLength1 = su.getStr(2);
			
			dataUnitLength = dataUnitLength1+dataUnitLength0;
			// 命令字节
			orderByte =  su.getStr(2);
			log.info("命令字节=" + orderByte);
			// 命令字节 命令字节是2 表示发送数据
			if (Integer.valueOf(orderByte).intValue() == 2) {
				// ----------------------------应用数据单元内容解析-------------------------------------
				rets = transDataUnitStr(su.getStr(Integer.parseInt(dataUnitLength,16)*2),primaryAddress);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 结束符"##"
		// endCode = strMess.readInteger2("L2H");
		return rets;
		

	}
	// 应用数据单元内容解析
	private List<BaseMessage> transDataUnitStr(String strT,String primaryAddress) {
		List<BaseMessage> lists = new ArrayList<BaseMessage>();
		BaseMessage ret = null;
		StringUtil str = new StringUtil(strT);
		try {
			// 类型标志
			int type = Integer.parseInt(str.getStr(2),16);
			log.info("类型标志=" + type);
			// 信息对象数目
			int mesNum = Integer.valueOf(str.getStr(2)).intValue();
			System.out.println("信息对象数目==" + mesNum);
			log.info("信息对象数目==" + mesNum);
			for (int i = 0; i < mesNum; i++) {
				
				switch (type) {
					// 建筑消防设施系统状态--4字节
					case 1:
						ret = codeUtils.systemStatus(str);
						break;
					// 2建筑消防设施部件运行状态 40字节---火警和故障  故障恢复
					case 2:
						ret = codeUtils.deviceStatus(str,primaryAddress);
						break;
					// 建筑消防设施操作信息 复位
					case 4:
						ret = codeUtils.devicOperation(str,primaryAddress);
						break;
//					// 用户信息传输装置运行状态 8位2进制---主电故障
					case 21:
						ret = codeUtils.userOperationStatus(str);
						break;
//					// 用户信息传输装置操作信息 8位2进制 自检故障
					case 24:
						ret = codeUtils.userOperation(str);
						break;
					default:
						log.info("连接测试---default----");
						//心跳每十分钟把时间和编码存到redis
						String str1 = roomLastTimeCache.getEquipmentTime(toString((primaryAddress),6));
						log.info("str1----"+str1);
						Date date = new Date();
						
						if (str1 == null || str1 == "" || str1.length() == 0) {
							log.info("str1 == null str1.length() == 0===================================================");
//							roomLastTimeCache.setEquipmentTime(toString((primaryAddress),6),lastTime);
							roomLastTimeCache.setEquipmentTime(toString((primaryAddress), 6), String.valueOf(date.getTime()));
							ret =  new BaseMessage(Act.FIREALARM, new EquipmentTime());
						}else{
//							
							long l = DateUtil.timediff(str1);
							log.info("时间差====================================================>"+l);
							//5 6分钟发一次，前台面根据10分钟判断是否断线  2:小于1000 防止 时间传错 突然很大的数保存到数据库 
							if( l > 500 && l<1000 ){
//								roomLastTimeCache.setEquipmentTime(toString((primaryAddress),6),lastTime);
								roomLastTimeCache.setEquipmentTime(toString((primaryAddress), 6), String.valueOf(date.getTime()));
								EquipmentTime equipmentTime = new EquipmentTime();
								equipmentTime.setEquipmentCode(toString((primaryAddress),6));
//								equipmentTime.setTime(lastTime);
								equipmentTime.setTime(DateUtil.dateStr(new Date(), "yyyyMMddHHmmss"));
								
								//通过logic修改数据库中控室时间，判断城安设备是否有心跳
								ret = new BaseMessage(Act.FIREALARM, equipmentTime);
							}else if (l > 1000 || l < 0) {
								// 防止时间跑乱 时间差为负数修改不回来
								log.info("时间差过大或小于0，时间重置为当前时间====================================================>"+l);
								roomLastTimeCache.setEquipmentTime(toString((primaryAddress), 6),  String.valueOf(date.getTime()));
							} else {
								ret = new BaseMessage(Act.FIREALARM, new EquipmentTime());
							}
							
						}
						break;
					}

				lists.add(ret);
				
			}

		} catch (Exception e) {
			log.error("解析失败");
			e.printStackTrace();
		}
		return lists;
	}

	/**
	 * 补齐长度
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

	/**
	 * @param mesage
	 * @param sub 传过来的校验码 把3到27和应用数据单元的值全部加起来，然后转成2进制，然后留8位 转整数
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean checkMessage(String mesage, String sub) {
		
		StringUtil su = new StringUtil(mesage);
		int subMsg = 0;
		
		for (int i = 0; i < mesage.length()/2; i++) {
			int a = Integer.parseInt(su.getStr(2),16);
			subMsg += a;
			
			//大余255去掉多余值
			if(subMsg > 255){
				String s = Integer.toBinaryString(subMsg);
				if(s.length() > 8){
					s = s.substring(s.length() - 8, s.length());
				}
				// 二进制字符串转成整数
				subMsg = Integer.parseInt(s, 2);
			}
			
		}
		
		String s = Integer.toBinaryString(subMsg);
		s = s.substring(s.length() - 8, s.length());
		// 二进制字符串转成整数
		subMsg = Integer.parseInt(s, 2);
		String subMsgS = Integer.toHexString(subMsg);
		log.info("subMsgS=" + subMsgS + "sub=" + sub);
		if (toString(subMsgS,2).equalsIgnoreCase(sub)) {
			return true;
		}
		return false;
	}

	private boolean checkInBytes(IoBuffer in) {
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
		log.info(" chengan receive msg：[" + BytesConvert.encodeHexStr(bytes) + "]");
		return true;
	}

}
