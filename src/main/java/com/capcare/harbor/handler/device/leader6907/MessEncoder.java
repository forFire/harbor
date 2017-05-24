package com.capcare.harbor.handler.device.leader6907;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

/**
 * 编码器
 * @author zf
 */
public class MessEncoder implements MessageEncoder<String> {
	/**
	 * 编码器未做任何处理
	 * @param session
	 * @param msg
	 * @param out
	 * @throws Exception
	 *             String 转 数组
	 */
	public void encode(IoSession session, String msg, ProtocolEncoderOutput out) throws Exception {
		
//		System.out.println("msg.getBytes().length=="+msg.getBytes().length);
		
		// 分配字节缓冲区
		IoBuffer buf = IoBuffer.allocate(222);
			 
		byte[] byte16 = {(byte)0x20}; 
		 
		buf.put(byte16);
		buf.flip();
		out.write(buf);
	}

//	/**
//	 * 十六进制字符串转化为byte数组
//	 *
//	 */
//	public static final byte[] hex2byte(String hex) throws IllegalArgumentException {
//
//		System.out.println("hex==============>" + hex);
//		char[] arr = hex.toCharArray();
//		byte[] b = new byte[hex.length() / 2];
//		for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
//			// 2
//			String swap = "" + arr[i++] + arr[i];
//
//			// System.out.println("swap===>"+swap);
//
//			b[j] = (byte) ((arr[i++] + arr[i]));
//
//			// 4040c711521210f13103930000000000030022110010002000000000000000000000000000000001210f1310472323
//
//			// int byteint = Integer.parseInt(swap, 16) & 0xFF;
//
//			// b[j] = new Integer(byteint).byteValue();
//
//			System.out.println(b[j] & 0xFF);
//
//		}
//		return b;
//	}

}
