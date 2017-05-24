package com.capcare.harbor.handler;

import module.util.BytesConvert;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.capcare.harbor.util.Harbor;

/**
 * @author fyq
 */
@Component
public class TcrEncoder extends ProtocolEncoderAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(TcrEncoder.class);
	
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out){
		try {
			
//			logger.info("TcrEncoder: message=========================>" + message);
			
			String duid = session.containsAttribute("duid") ? (String) session.getAttribute("duid")
					: null;
			byte[] body = null;
			if (message instanceof String) {
				logger.info("SEND1:" + (String) message);
				body = ((String) message).getBytes(Harbor.DEFAULT_CHARSET);
			}else if(message instanceof byte[]){
				 body=(byte[]) message;
				 logger.info("SEND2:" + BytesConvert.encodeHexStr(body));
			}else{
				logger.info("Object message can't SEND:" + message+"--"+duid);
			}
			 
			if(body != null){
				IoBuffer buffer = IoBuffer.allocate(body.length, false);
				buffer.put(body);
				buffer.flip();
				out.write(buffer);
				out.flush();
				buffer.free();
			}
		} catch (Exception e) {
			logger.error("encode", e);
		}
	}
}