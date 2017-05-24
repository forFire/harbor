package com.capcare.harbor.handler;

import javax.annotation.Resource;

import module.util.BytesConvert;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.DeviceHandler;
import com.capcare.harbor.handler.device.DeviceType;

/**
 * @author zf
 */
@Component
public class TcrDecoder extends CumulativeProtocolDecoder {

	private static Logger logger = LoggerFactory.getLogger(TcrDecoder.class);

	@Resource
	private DeviceHandler deviceHandler;

	/*
	 * 第一步：接收客户端消息
	 */

	/**
	 * decode函数将IoBuffer in对象中的内容累积添加到
	 * 内部的buffer上，并调用doDecode方法。，如果doDecode方法返回false且内部累积buffer中的数据再次可用时，
	 * doDecode方法会被再次调用，直到doDecode方法返回true;
	 */
	@Override
	public boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
		try {

			in.mark();// 标记当前位置，以便reset
			DeviceType deviceType = (DeviceType) session.getAttribute("deviceType");
			byte first = in.get();
			in.reset();// 返回mark的位置
			
			int limit1 = in.limit();//容量大小 
			logger.error("容量大小 limit====================>：[" + limit1 + "]");
			
//			 byte[] bytes1 = new byte[limit1];
//			 int limit1 = in.limit();//容量大小 byte[] bytes1 = new byte[limit1];
//			 in.get(bytes1);//The byte[] that will contain the read bytes
//			String inStr = BytesConvert.encodeHexStr(bytes1);// bytes转16进制------------》传过来就是16进制byte[]
//			logger.info("收到消息（十六进制）：[" + inStr + "]");

			// logger.error("deviceType==================>"+deviceType);

			if (deviceType == null) {
				deviceType = deviceHandler.getDeviceType(first);// 取出@  配置文件headMapping 中
				if (deviceType == null && deviceHandler.getHeadMaxLength() > 1) { // deviceHandler.getHeadMaxLength()
																					// 取出@@
					byte[] head = new byte[deviceHandler.getHeadMaxLength()];
					head[0] = first;
					for (int i = 1; i < head.length; i++) {
						first = in.get();
						head[i] = first;
						deviceType = deviceHandler.getDeviceType(head);
						if (deviceType != null) {
							break;
						}
					}
					if (deviceType == null) {
						logger.error("无法识别的消息头：[" + new String(head) + "]");
					}
				}

				if (deviceType == null) {
					in.reset();
					int limit = in.limit();
					
					
					
					byte[] bytes = new byte[limit];
					in.get(bytes);
					logger.error("无法识别的消息：[" + new String(bytes) + "]");
					session.close(true);
					return false;
				}
			}

			in.reset();// 返回mark的位置
			session.setAttribute("deviceType", deviceType);
			logger.info("传入消防协议，准备处理！");
			//deviceType---已通过配置文件headMapping转成device_leader  6920跳转到LeaderProtocol
			return deviceHandler.getDeviceProtocol(deviceType).decode(session, in, out);

		} catch (Exception e) {
			logger.error("doDecode", e);
		}
		return false;
	}

}
