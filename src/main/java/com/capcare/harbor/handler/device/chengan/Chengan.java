package com.capcare.harbor.handler.device.chengan;

import com.capcare.harbor.handler.device.DeviceType;

public class Chengan extends DeviceType{
	
	public Chengan(){
		
		super("CHENGAN", "CHENGAN", new byte[]{0x40,0x40});
	}
}
