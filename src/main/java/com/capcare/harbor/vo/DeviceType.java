package com.capcare.harbor.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备类型
 * @author capcare
 *
 */
public enum DeviceType {
	M2616("M2616", "M2616", (byte)'#'),
	MT90("MT90", "MT90", (byte)'$');
	
	private String code;

	private String info;

	private byte begin;
	
	private static Map<Byte, DeviceType> map = new HashMap<Byte, DeviceType>();
	static{
		for (DeviceType type  : DeviceType.values()) {
			map.put(type.getBegin(), type);
		}
	}

	private DeviceType(String code, String info, byte begin) {
		this.code = code;
		this.info = info;
		this.begin = begin;
	}

	public static DeviceType getByBegin(byte begin){
		return map.get(begin);
	}
	
	public String getCode() {
		return this.code;
	}

	public String getInfo() {
		return this.info;
	}
	
	public byte getBegin(){
		return this.begin;
	}
	
}
