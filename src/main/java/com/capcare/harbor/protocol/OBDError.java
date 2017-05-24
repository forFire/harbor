package com.capcare.harbor.protocol;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;

public class OBDError {
	
	private String deviceSn;
	
	private Long receive = 0L;
	// 后台处理时间
	private Long systime = 0L;
	
    private Set<String> codes=new HashSet();
    
    public void add(String code){
    	codes.add(code);
    }
    
	
	public Set<String> getCodes() {
		return codes;
	}



	public String getDeviceSn() {
		return deviceSn;
	}
	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	
	public Long getReceive() {
		return receive;
	}


	public void setReceive(Long receive) {
		this.receive = receive;
	}


	public Long getSystime() {
		return systime;
	}


	public void setSystime(Long systime) {
		this.systime = systime;
	}


	@Override
	public String toString() {
		return "OBDError [deviceSn=" + deviceSn + ", codes=" + codes + "]";
	}

}
