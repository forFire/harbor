package com.capcare.harbor.handler.device;

import java.util.Map;

public class DeviceHandler {
	
    private  static Map<String,DeviceType> headMapping=null;
	
	private  static  Map<DeviceType,DeviceProtocol> processMapping=null;
	
	private static int headMax=0;//协议标示最大长度
	
	public  DeviceType getDeviceType(byte... deviceType){
		return headMapping.get(new String(deviceType).trim());
	}
	

	public static void setHeadMapping(Map<String, DeviceType> headMapping) {
		DeviceHandler.headMapping = headMapping;
	}


	public static void setProcessMapping(Map<DeviceType, DeviceProtocol> processMapping) {
		DeviceHandler.processMapping = processMapping;
	}


	public int getHeadMaxLength(){
		if(headMax<1){
			for(String head:headMapping.keySet()){
				DeviceType dt=headMapping.get(head);
				if(dt.getBegin().length>headMax){
					headMax=dt.getBegin().length;
				}
			}
		}
		return headMax;
	}
	
	
	public  DeviceProtocol getDeviceProtocol(DeviceType deviceType){
		DeviceProtocol ret=null;
		ret=processMapping.get(deviceType);
		return ret;
	}
	
	
	
	public static void main(String[] args) {
		byte[] tt=new byte[4];
		tt[0]=(byte)0x67;
		tt[1]=(byte)0x67;
		System.out.println(new String(tt));
		byte[] tt1=new byte[1];
		tt1[0]=(byte)0x64;
		System.out.println(new String(tt1));
	}
	
}
