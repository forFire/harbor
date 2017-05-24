package com.capcare.harbor.handler.device.leader6920;

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
public class MessDecoder {
	private Logger log = LoggerFactory.getLogger(getClass());
	@Resource
	private LeaderCodeUtils codeUtils;

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
	
	//事件应答信息 源地址和目的地址原样交换位置
	String retPrimaryAddress = "";
	String retDestinationAddress = "";
	
	String dataUnitLength = "";
	String orderByte = "";
	
	// 校验码
	private String lastTime = "";

	// 每当有数据到达时，IOService会先调用底层IO接口读取数据，封装成IoBuffer，之后以事件的形式通知上层代码，从而将Java
	// NIO的同步IO接口转化成了异步IO。
	public List<BaseMessage> decode(IoSession session, IoBuffer in) throws Exception {
		
		// 校验数据包
		boolean check = checkInBytes(in);
		
		if (!check) {
			return null;
		}
		List<String> messagels = codeUtils.resolveMessage(in);
		
		if (messagels == null) {
			log.info("校验不通过---messagels===null");
			return null;
		}
		
		List<BaseMessage> ls = new ArrayList<BaseMessage>();
		for (String message : messagels) {
			// 解析
			List<BaseMessage> baseMessageLists = getMessage(session, in, message);

			LoginDevice ld = new LoginDevice();
			ld.setDt(new Leader());

			for (BaseMessage baseMsg : baseMessageLists) {
				if (baseMsg != null) {
					baseMsg.setLoginDevice(ld);
					ls.add(baseMsg);
				}
			}

		}
		return ls;
	}

	private List<BaseMessage>  getMessage(IoSession session, IoBuffer in, String mesage) {
		
		BaseMessage ret = null;
		
		if(mesage == null || mesage.length() <= 0){
			return null;
		}
		
		StringUtil su = new StringUtil(mesage);
		
		// 业务流水号
		try {
			
			messType = su.getStr(4);
			// 协议版本号
			protocolVersion =  su.getStr(2)  + su.getStr(2);
			System.out.println("协议版本号======>" + protocolVersion);
			// 秒分时
			secTem = toString(String.valueOf(Integer.parseInt(su.getStr(2), 16)), 2);
			divideTem = toString(String.valueOf(Integer.parseInt(su.getStr(2), 16)), 2);
			hourTem = toString(String.valueOf(Integer.parseInt(su.getStr(2), 16)), 2);
			String sdh = hourTem + divideTem + secTem;
			// 年月日
			// 日
			dayTem = toString(String.valueOf(Integer.parseInt(su.getStr(2), 16)), 2);
			// 月
			monthTem = toString(String.valueOf(Integer.parseInt(su.getStr(2), 16)), 2);
			// 年
			yearTem = toString(String.valueOf(Integer.parseInt(su.getStr(2), 16)), 2);
			String ymd = "20" + yearTem + monthTem + dayTem;
			lastTime = ymd + sdh;
			
			String s1 = toString(su.getStr(2),2);
			String s2 = toString(su.getStr(2),2);
			String s3 = toString(su.getStr(2),2);
			String s4 = toString(su.getStr(2),2);
			String s5 = toString(su.getStr(2),2);
			String s6 = toString(su.getStr(2),2);
			primaryAddress = s6+s5+s4+s3+s2+s1;
//			primaryAddress = String.valueOf(Integer.parseInt(primaryAddress,16));
			
			log.info("源地址 =" + toString((primaryAddress),12));
			
			// 目的地址
			String s11 = toString(su.getStr(2),2);
			String s12 = toString(su.getStr(2),2);
			String s13 = toString(su.getStr(2),2);
			String s14 = toString(su.getStr(2),2);
			String s15 = toString(su.getStr(2),2);
			String s16 = toString(su.getStr(2),2);
			
			destinationAddress =s16+s15+s14+s13+s12+s11;
//			destinationAddress = String.valueOf(Integer.parseInt(destinationAddress,16));
			
			//返回串
			retPrimaryAddress = s1+s2+s3+s4+s5+s6;
			retDestinationAddress = s11+s12+s13+s14+s15+s16;
			
			// 应用数据单元长度
			String dataUnitLength0 = su.getStr(2);
			String dataUnitLength1 = su.getStr(2);

			dataUnitLength = dataUnitLength1 + dataUnitLength0;
			
			// 命令字节
			orderByte = su.getStr(2);
			
			log.info("命令字节=" + orderByte);
			
			// 命令字节 命令字节是2 表示发送数据
			//回复信息 业务流水号 版本号一致 源地址目的地址转换 校验码
			
			if (orderByte.equalsIgnoreCase("2") || orderByte.equalsIgnoreCase("02")) {
				List<BaseMessage> rets = new ArrayList<BaseMessage>();
				// ----------------------------应用数据单元内容解析-------------------------------------
				log.info("源地址--------------------------------------->" + primaryAddress);
				rets = transDataUnitStr(su.getStr(Integer.parseInt(dataUnitLength, 16) * 2), primaryAddress);
				return rets;
			}

			
			// 注册包-----单点登录 同一个设备id 登录2次---回复确认和否认
			if ("FE".equalsIgnoreCase(orderByte)) {
				List<BaseMessage> rets = new ArrayList<BaseMessage>();
				log.info("6920连接到服务器需要先注册服务器回复注册应答---------------------------------------!");
				ret = new BaseMessage(Act.LEADER_ZHUCE, null);
				rets.add(ret);
				return rets;
			}

			// 心跳
			if ("FF".equalsIgnoreCase(orderByte)) {
				
				List<BaseMessage> rets = new ArrayList<BaseMessage>();
				Date date = new Date();
				
				log.info("接收心跳信息---------------");
				// 心跳每十分钟把时间和编码存到redis
				String str1 = roomLastTimeCache.getEquipmentTime(toString((primaryAddress), 12));
				log.info("str1----" + str1);
				
				if (str1 == null || str1 == "" || str1.length() == 0) {
					roomLastTimeCache.setEquipmentTime(toString((primaryAddress), 12), String.valueOf(date.getTime()));
					ret = new BaseMessage(Act.LEADER_XINTIAO, new EquipmentTime());
				} else {
					//
					long l = DateUtil.timediff(str1);
					log.info("时间差====================================================>" + l);
					// 8分钟发一次，前台面根据10分钟判断是否断线 2:小于1000 防止 时间传错 突然很大的数保存到数据库
					if (l > 480 && l < 1000) {
						roomLastTimeCache.setEquipmentTime(toString((primaryAddress), 12),  String.valueOf(date.getTime()));
						EquipmentTime equipmentTime = new EquipmentTime();
						equipmentTime.setEquipmentCode(toString((primaryAddress), 12));
						
						equipmentTime.setTime(DateUtil.dateStr(new Date(), "yyyyMMddHHmmss"));
						
						ret = new BaseMessage(Act.LEADER_XINTIAO, equipmentTime);
					} else if (l > 1000 || l < 0) {
						// 防止时间跑乱 时间差为负数修改不回来
						log.info("时间差过大或小于0，时间重置为当前时间====================================================>"+l);
						roomLastTimeCache.setEquipmentTime(toString((primaryAddress), 12),  String.valueOf(date.getTime()));
					} else {
						ret = new BaseMessage(Act.LEADER_XINTIAO, new EquipmentTime());
					}
				}
				
				if (ret != null) {
					rets.add(ret);
					return rets;
				}
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// 结束符"##"
		// endCode = strMess.readInteger2("L2H");
		return null;

	}

	// 应用数据单元内容解析
	private List<BaseMessage> transDataUnitStr(String strT, String primaryAddress) {
		List<BaseMessage> lists = new ArrayList<BaseMessage>();
		BaseMessage ret = null;
		StringUtil str = new StringUtil(strT);
		try {
			// 类型标志
			int type = Integer.parseInt(str.getStr(2), 16);
			// 信息对象数目一次发送多少事件
			int mesNum = Integer.valueOf(str.getStr(2)).intValue();

			for (int i = 0; i < mesNum; i++) {

				switch (type) {
				// 建筑消防设施系统状态--4字节
				case 1:
//					ret = codeUtils.systemStatus(str);
					break;
				// 2建筑消防设施部件运行状态 40字节---火警和故障 故障恢复
				case 2:
					ret = codeUtils.deviceStatus(str, primaryAddress, lastTime);
					break;
				// 建筑消防设施操作信息 复位
				case 4:
					log.info("建筑消防设施操作信息 复位--------------");
					ret = codeUtils.devicOperation(str, primaryAddress);
					break;
				// // 用户信息传输装置运行状态 8位2进制---主电故障
				case 21:
					log.info("用户信息传输装置运行状态__系统不需要，直接跳过去");
//					ret = codeUtils.userOperationStatus(str);
					break;
				// // 用户信息传输装置操作信息 8位2进制 自检故障
				case 24:
					log.info("用户信息传输装置（6920）操作信息（复位 自检 手报等）---------");
//					ret = codeUtils.userOperation(str);
					break;
				default:
					log.info("default-------------------------");
					break;
				}
				
				//所有的事件（包括现在未解析的静音手动报警等）都需要应答否则会重新发送
				//回复信息 业务流水号 版本号一致 源地址目的地址转换 校验码
				String ret1 ="4040"+messType+protocolVersion+"000000000000"+retDestinationAddress+retPrimaryAddress+"000003"+toString(check(messType+protocolVersion+"000000000000"+destinationAddress+primaryAddress+"03"),2) +"2323";
				if(ret == null){
					ret = new BaseMessage(Act.LEADER, "");
				}
				ret.setResponse(hexStr2ByteArray(ret1));
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

	/**
	 * @param mesage
	 * @param sub
	 *            传过来的校验码 把3到27和应用数据单元的值全部加起来，然后转成2进制，然后留8位 转整数
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean checkMessage(String mesage, String sub) {

		StringUtil su = new StringUtil(mesage);
		int subMsg = 0;

		for (int i = 0; i < mesage.length() / 2; i++) {
			int a = Integer.parseInt(su.getStr(2), 16);
			subMsg += a;
		}

		String s = Integer.toBinaryString(subMsg);
		s = s.substring(s.length() - 8, s.length());
		// 二进制字符串转成整数
		subMsg = Integer.parseInt(s, 2);
		String subMsgS = Integer.toHexString(subMsg);
		log.info("subMsgS=" + subMsgS + "sub=" + sub);
		if (toString(subMsgS, 2).equalsIgnoreCase(sub)) {
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
		log.info(" receive msg：[" + BytesConvert.encodeHexStr(bytes) + "]");
		return true;
	}

	
	
	public String check(String mesage){
//		String mesage ="0e000100000000000000000000000000010000000000000003";		
		StringUtil su = new StringUtil(mesage);
			int subMsg = 0;
			for (int i = 0; i < mesage.length()/2; i++) {
				int a = Integer.parseInt(su.getStr(2),16);
				subMsg += a;
			}
//			subMsg= 44;
//			System.out.println(subMsg);
			String s = Integer.toBinaryString(subMsg);
//			System.out.println(s);
			if(s.length() > 8){
				s = s.substring(s.length() - 8, s.length());
			}
			// 二进制字符串转成整数
			subMsg = Integer.parseInt(s, 2);
			String subMsgS = Integer.toHexString(subMsg);
			return subMsgS;
	}
	
	
	public static byte[] hexStr2ByteArray(String hexString) {  
	    hexString = hexString.toLowerCase();  
	    final byte[] byteArray = new byte[hexString.length() / 2];  
	    int k = 0;  
	    for (int i = 0; i < byteArray.length; i++) {  
	                    //因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先  
	                    //将hex 转换成byte   "&" 操作为了防止负数的自动扩展  
	                    // hex转换成byte 其实只占用了4位，然后把高位进行右移四位  
	                    // 然后“|”操作  低四位 就能得到 两个 16进制数转换成一个byte.  
	                    //  
	        byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);  
	        byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);  
	        byteArray[i] = (byte) (high << 4 | low);  
	        k += 2;  
	    }  
	    return byteArray;  
	}  
	  
	  /** 
	   * 16进制字符串转换成byte数组 
	   * @param 16进制字符串 
	   * @return 转换后的byte数组 
	   */  
	    public static byte[] hex2Byte(String hex) {
	    	
		    String digital = "0123456789ABCDEF";  
		    char[] hex2char = hex.toCharArray();  
		    byte[] bytes = new byte[hex.length() / 2];  
		    int temp;  
		    for (int i = 0; i < bytes.length; i++) {  
		    // 其实和上面的函数是一样的 multiple 16 就是右移4位 这样就成了高4位了  
		    // 然后和低四位相加， 相当于 位操作"|"   
		    //相加后的数字 进行 位 "&" 操作 防止负数的自动扩展. {0xff byte最大表示数}  
		        temp = digital.indexOf(hex2char[2 * i]) * 16;  
		        temp += digital.indexOf(hex2char[2 * i + 1]);  
		        bytes[i] = (byte) (temp & 0xff);  
		    }  
		    return bytes;  
		    
	}  
}
