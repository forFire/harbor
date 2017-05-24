package com.capcare.harbor.handler.device.leader6907;

import java.nio.BufferUnderflowException;

import module.util.BytesConvert;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

/**
 *解码
 *@author zf 
 */
public class MessDecoder implements MessageDecoder {
	public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
			return MessageDecoderResult.OK;//读取数据判断当前数据包是否可进行decode，返回MessageDecoderResult.OK
	}

	public MessageDecoderResult decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
			try {
				in.mark();
				int limit = in.limit();
				byte[] bytes = new byte[limit];
				try {
					in.get(bytes);// io-->bytes
				} catch (BufferUnderflowException e) {
					in.reset();// 读取失败返回mark
					return null;
				}
				
				String hexs = BytesConvert.encodeHexStr(bytes);
//				log.info("接收到的消息转16进制===》"+hexs);
				in.position(in.limit());//在return之前加上in.position(in.limit());把指针复位.
				// 调用推送数据接口
//				session.write(tm.carRegisterMes());
				out.write(hexs);
				return MessageDecoderResult.OK;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return MessageDecoderResult.OK;
	}

	public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
		
		
	}


}
