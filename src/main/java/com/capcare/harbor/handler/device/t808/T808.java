package com.capcare.harbor.handler.device.t808;

import com.capcare.harbor.handler.device.DeviceType;



public class T808 extends DeviceType {

	public T808(){
		super("T808", "T808", new byte[]{0x7e});
	}
}
