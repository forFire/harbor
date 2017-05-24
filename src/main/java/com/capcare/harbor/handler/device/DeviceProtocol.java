package com.capcare.harbor.handler.device;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/**
 * 硬件协议解析
 * @author XieHaiSheng
 *
 */
public interface DeviceProtocol {
	
	/**
	 * 解析字节到对象，并out write对象
	 * @param session
	 * @param in
	 * @param out
	 * @return
	 * 成功返回true 失败返回false
	 */
	boolean decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out);
	
	Object encode(IoSession session, Object protocol);
}
