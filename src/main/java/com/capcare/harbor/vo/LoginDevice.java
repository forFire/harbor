package com.capcare.harbor.vo;

import com.capcare.harbor.handler.device.DeviceType;

public class LoginDevice {
	
	private DeviceType dt;
	private String sn;
	
	public DeviceType getDt() {
		return dt;
	}
	public void setDt(DeviceType dt) {
		this.dt = dt;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	
	@Override
	public String toString() {
		return "LoginDevice[dt=" + dt.getCode() + ", sn=" + sn+ "]";
	}
	
}
