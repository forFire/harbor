package com.capcare.harbor.handler.device.chengan;

public class ChenganResponse {

	private byte[] resultData;
	private boolean flag;

	public byte[] tobytes(boolean flag) {
		// 回复确认消息 命令字节和校验码
		if (flag == true) {
			byte[] byte16 = {0x40,  0x40,  0x17,  0x0,  0x1,  0x1,  0x5,
					 0x0,  0x0,  0x17,  0xa,  0xf,  0x0,  0x0,  0x0,
					 0x0,  0x0,  0x0,  0x39,  0x30,  0x0,  0x0,  0x0,
					 0x0,  0x0,  0x0,  0x3,  0x7d,  0x23,  0x23 };
			return byte16;

		// 回复否认消息
		} else {
			byte[] byte16 = {0x40,  0x40,  0x17,  0x0,  0x1,  0x1,  0x5,
					 0x0,  0x0,  0x17,  0xa,  0xf,  0x0,  0x0,  0x0,
					 0x0,  0x0,  0x0,  0x39,  0x30,  0x0,  0x0,  0x0,
					 0x0,  0x0,  0x0,  0x6,  0x7d,  0x23,  0x23 };
			return byte16;
		}

	}
	
	public byte[] getResultData() {
		return resultData;
	}

	public void setResultData(byte[] resultData) {
		this.resultData = resultData;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

}