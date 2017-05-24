package com.capcare.harbor.handler.device;


/**
 * 设备(协议)类型
 * @author capcare
 *
 */
public abstract class DeviceType {
	/**M2616("M2616", "M2616", (byte)'#'),
	MT90("MT90", "MT90", (byte)'$'),
	EELINK("EELINK", "EELINK", new byte[]{0x67,0x67});
	**/
	private String code;
	
	private String info;

	private byte[] begin;
	
	
	protected DeviceType(String code, String info, byte begin) {
		this.code = code;
		this.info = info;
		this.begin = new byte[]{begin};
	}
	
	protected DeviceType(String code, String info, byte[] begin) {
		this.code = code;
		this.info = info;
		this.begin = begin;
	}

	
	public String getCode() {
		return this.code;
	}

	public String getInfo() {
		return this.info;
	}
	
	public byte[] getBegin(){
		return this.begin;
	}
	
	
}
