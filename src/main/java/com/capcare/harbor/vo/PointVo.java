package com.capcare.harbor.vo;

import java.io.Serializable;

public class PointVo implements Serializable {

	private static final long serialVersionUID = 7129153746826875594L;

	private Integer id;// 拐点id
	private Integer segmentId; // 路段ID

	private Integer width;// 路段宽度
	private Integer prop;// 路段属性

	private Integer min;// 路段行驶过短阈值
	private Integer max;// 路段行驶过长阈值

	// 路段最高速度
	private Integer maxSpeed;
	// 路段超速持续时间
	private Integer overSpeedTime;

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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(Integer segmentId) {
		this.segmentId = segmentId;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getProp() {
		return prop;
	}

	public void setProp(Integer prop) {
		this.prop = prop;
	}

	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public Integer getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(Integer maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Integer getOverSpeedTime() {
		return overSpeedTime;
	}

	public void setOverSpeedTime(Integer overSpeedTime) {
		this.overSpeedTime = overSpeedTime;
	}

	@Override
	public String toString() {
		return "PointVo [lng=" + lng + ", lat=" + lat + "]";
	}

}
