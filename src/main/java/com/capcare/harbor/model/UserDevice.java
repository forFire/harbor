package com.capcare.harbor.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import module.orm.IdEntity;

/**
 * @author fyq
 */
@Entity
@Table(name = "us_user_device")
public class UserDevice extends IdEntity implements Serializable {

	private static final long serialVersionUID = 6332438155575669134L;

	@Column(name = "f_user_id")
	private Long userId;

	@Column(name = "f_device_sn")
	private String deviceSn;

	// ************************************************

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	public UserDevice(Long userId, String deviceSn) {
		super();
		this.userId = userId;
		this.deviceSn = deviceSn;
	}

	public UserDevice() {
		super();
	}

}
