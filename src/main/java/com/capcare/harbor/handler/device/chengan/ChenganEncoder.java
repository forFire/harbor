package com.capcare.harbor.handler.device.chengan;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 *未调用
 */
@Component
public class ChenganEncoder {
	private Logger log = LoggerFactory.getLogger(getClass());
	public void encode(IoSession session, String msg, ProtocolEncoderOutput out) throws Exception {
		
		log.info("ChenganEncoder.encode----------------------");
		// 分配字节缓冲区
		IoBuffer buf = IoBuffer.allocate(msg.getBytes().length);

		 byte[] byte16 = {0x40,  0x40,  0x17,  0x0,  0x1,  0x1,  0x5,
				 0x0,  0x0,  0x17,  0xa,  0xf,  0x0,  0x0,  0x0,
				 0x0,  0x0,  0x0,  0x39,  0x30,  0x0,  0x0,  0x0,
				 0x0,  0x0,  0x0,  0x3,  0x7d,  0x23,  0x23 };

		buf.put(byte16);
		buf.flip();
		out.write(buf);
	}
}
