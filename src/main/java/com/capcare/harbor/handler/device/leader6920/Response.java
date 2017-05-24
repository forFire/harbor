package com.capcare.harbor.handler.device.leader6920;

public class Response {

	private byte[] resultData;

	private String protocolVersion;
	
	Response(String protocolVersion){
		this.protocolVersion = protocolVersion;	
	}
	
	
//  注册请求：40 40 01 00 01 01 1F 09 13 0F 0B 10 06 05 04 03 02 01 00 00 00 00 00 00 00 00 FE 7B 23 23
//	注册应答：40 40 01 00 01 01 31 09 19 15 11 16 00 00 00 00 00 00 06 05 04 03 02 01 00 00 FE A5 23 23
	public byte[] tobytes1() {
		
//		byte[] byte16 = { 0x40, 0x40 ,  0x01 , 0x00 , 0x01 , 0x01 , 0x31 , 0x09 , 0x19 , 0x15 , 0x11 , 0x16 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 
//				, 0x00 , 0x06 , 0x05 , 0x04 , 0x03 , 0x02 , 0x01 , 0x00 , 0x00 , (byte) 0xFE , (byte) 0xA5 , 0x23 , 0x23 };
//		
		byte[] byte16 = { 0x40,  0x40,  0x01,  0x00,  0x01,  0x00,  0x31,  0x09,  0x19,  0x15,  0x11,  0x16,  0x00,  0x00,  0x00,  0x00,  0x00,  0x00,  0x01,  0x00,  0x00,  0x00,  0x00,  0x00,  0x00,  0x00,  (byte) 0xFE,  (byte) 0xA5,  0x23,  0x23};
		return byte16;
		
	}
	
	
	public byte[] tobytes() {
		// 应答包 40 40 01 00 01 01 30 45 14 31 10 16 00 00 00 00 00 00 06 05 04 03
		//       02 01 00 00 FE F6 23 23
//		byte[] byte16 = { 0x40 ,  0x40,  0x02 ,  0x00 ,  0x01 ,  0x01 ,  0x17 ,  0x40 ,  0x09 ,  0x16 ,  0x11 ,  0x16 ,  0x00 ,  
//				0x00 ,  0x00 ,  0x00 ,  0x00 ,  0x00 ,  0x06 ,  0x05 ,  0x04 ,  0x03 ,  0x02 ,  0x01 ,  0x00 ,  0x00 ,  0x03 ,  (byte) 0xB9 ,  0x23 ,  0x23};
//		byte[] byte16 = { 0x40,  0x40 ,  0x02,  0x00,  0x01 ,  0x01 ,  0x17 ,  0x40 ,  0x09 ,  0x16 ,  0x11 ,  0x16 ,  0x00 ,  0x00 ,  0x00 ,  0x00 ,  0x00 ,  0x00 ,  0x06 ,  0x05 ,  0x04 ,  0x03 ,  0x02 ,  0x01 ,  0x00 ,  0x00 ,  0x03 ,  (byte) 0xB9 ,  0x23 ,  0x23};
		byte[] byte16 = { 0x40, 0x40, 0x02, 0x00, 0x01, 0x00, 0x17, 0x40, 0x09, 0x16, 0x11, 0x16, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x03, (byte) 0xa5, 0x23, 0x23};
		return byte16;
		
	}

	
//	心跳应答---ff + 校验码
//	
	
//心跳应答包
	public byte[] tobytes3() {
		byte[] byte16 = {0x40, 0x40, 0x02 , 0x00 , 0x01 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x01 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , 0x00 , (byte) 0xff , 0x07 , 0x23 , 0x23};
		return byte16;
	}

	
	
//	public String tobytes() {
//		
//		String str="404002000101174009161116000000000000010000000000000003B92323";
//
//		return str;
//		
//	}
//	
//	public String tobytes1() {
//		String str="4040010001013109191511160000000000000100000000000000FEA52323";
//		return str;
//		
//	}
	
	public byte[] getResultData() {
		return this.resultData;
	}

	public void setResultData(byte[] resultData) {
		this.resultData = resultData;
	}


	public String getProtocolVersion() {
		return protocolVersion;
	}


	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	
	
}
