package com.capcare.harbor.handler.device.mt90;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.capcare.harbor.handler.device.DeviceProtocol;
import com.capcare.harbor.handler.device.m2616.M2616Encoder;
import com.capcare.harbor.vo.InstructVo;

public class MT90Protocol implements DeviceProtocol {
	
	@Override
	public boolean decode(IoSession session, IoBuffer in,ProtocolDecoderOutput out) {
		 return MT90Decoder.decode(session, in,out);
	}
	
	@Override
	public Object encode(IoSession session, Object protocol) {
		
		try {
			return MT90Encoder.encodeInstruct(session, (InstructVo) protocol);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
