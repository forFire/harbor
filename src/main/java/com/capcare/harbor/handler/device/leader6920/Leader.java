package com.capcare.harbor.handler.device.leader6920;

import com.capcare.harbor.handler.device.DeviceType;

public class Leader extends DeviceType{
	
	public Leader(){
		
		super("LEADER", "LEADER", new byte[]{0x40,0x40});
	}
}
