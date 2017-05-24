package com.capcare.harbor.handler.device.eelink;

import com.capcare.harbor.handler.device.DeviceType;

public class EELink extends DeviceType {

	public EELink(){
		super("EELINK", "EELINK", new byte[]{0x67,0x67});
	}
}
