package com.capcare.harbor.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import module.orm.IdEntity;

@Entity
@Table(name = "us_feecheck")
public class FeeCheck extends IdEntity implements Serializable {
	private static final long serialVersionUID = 5009763852240883481L;

	@Column(name = "f_device_sn")
	private String deviceSn;
	
	@Column(name = "f_content")
	private String content;
	
	@Column(name = "f_receive")
	private String receiveTime;

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(String receiveTime) {
		this.receiveTime = receiveTime;
	}

	
}
