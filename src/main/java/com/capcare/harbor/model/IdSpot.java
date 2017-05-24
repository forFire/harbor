package com.capcare.harbor.model;

import javax.persistence.Column;

public class IdSpot implements java.io.Serializable {

	private static final long serialVersionUID = -4039289320002077046L;

	@Column(name = "f_receive")
	private Long receive = 0L;

	@Column(name = "f_device_sn", length = 28)
	private String deviceSn;

	public Long getReceive() {
		return receive;
	}

	public void setReceive(Long receive) {
		this.receive = receive;
	}

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IdSpot) {
			IdSpot pk = (IdSpot) obj;
			if (this.deviceSn.equals(pk.deviceSn) && this.receive.equals(pk.receive)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
