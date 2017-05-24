package com.capcare.harbor.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * 位置 点
 */
@Entity
@Table(name = "us_spot")
@IdClass(value = IdSpot.class)
public class Spot implements Serializable {

	private static final long serialVersionUID = -3261367063305632658L;

	// 上传时间
	@Id
	@Column(name = "f_receive")
	private Long receive = 0L;

	// 后台处理时间
	@Column(name = "f_systime")
	private Long systime = 0L;

	@Column(name = "f_lng")
	private Double lng = 0D;

	@Column(name = "f_lat")
	private Double lat = 0D;

	// 停留时间 秒
	@Column(name = "f_stayed")
	private Integer stayed;

	// 与上个点的里程
	@Column(name = "f_distance")
	private Double distance = 0D;

	// 速度
	@Column(name = "f_speed")
	private Double speed = 0D;

	// 方向
	@Column(name = "f_direction")
	private Float direction = 0F;

	// 定位方式,如:AGPS:V GPS:A
	@Column(name = "f_mode", length = 1)
	private String mode;

	@Id
	@Column(name = "f_device_sn", length = 28)
	private String deviceSn;

	/**
	 * 备用字段----
	 */
	@Column(name = "f_info")
	private String info;
	
	@Column(name = "f_cell")
	private String cell;
	
	@Column(name = "f_acc")
	private int accMode;
	
	@Column(name = "f_rfid")
	private int mode433;

	// *******************************************

	public String getInfo() {
		return info;
	}

	public String getCell() {
		return cell;
	}

	public void setCell(String cell) {
		this.cell = cell;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Long getReceive() {
		return receive;
	}

	public void setReceive(Long receive) {
		this.receive = receive;
	}

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

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	public Integer getStayed() {
		return stayed;
	}

	public void setStayed(Integer stayed) {
		this.stayed = stayed;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Float getDirection() {
		return direction;
	}

	public void setDirection(Float direction) {
		this.direction = direction;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public Long getSystime() {
		return systime;
	}

	public void setSystime(Long systime) {
		this.systime = systime;
	}

	public int getAccMode() {
		return accMode;
	}

	public void setAccMode(int accMode) {
		this.accMode = accMode;
	}

	public int getMode433() {
		return mode433;
	}

	public void setMode433(int mode433) {
		this.mode433 = mode433;
	}
	
}
