package com.capcare.harbor.handler.device.m2616;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capcare.harbor.handler.TcrIoHandler;
import com.capcare.harbor.handler.device.DeviceProtocol;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.InstructVo;

public class M2616Protocol implements DeviceProtocol{
	@Override
	public boolean decode(IoSession session, IoBuffer in,ProtocolDecoderOutput out) {
		BaseMessage message=M2616Decoder.decode(session, in);
		if(message!=null){
			out.write(message);
			return true;
		}else{
			return false;
		}
	}

	
	@Override
	public Object encode(IoSession session, Object protocol) {
		try {
			return M2616Encoder.encodeInstruct(session, (InstructVo) protocol);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	

}
