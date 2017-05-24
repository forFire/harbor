package com.capcare.harbor.vo;

import java.io.Serializable;

public class SpotVo implements Serializable {

	private static final long serialVersionUID = 7129153746826875594L;

	private Double lng;

	private Double lat;

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	@Override
	public String toString() {
		return "SpotVo [lng=" + lng + ", lat=" + lat + "]";
	}

}
