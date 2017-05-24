package com.capcare.harbor.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_device")
public class Device extends IdEntity implements Serializable {

	@Column(name = "f_name")
	private String name;
	@Column(name = "f_type")
	private String type;
	@Column(name = "f_sn")
	private String sn;
	@Column(name = "f_model")
	private String model;
	@Column(name = "f_system")
	private String system;
	@Column(name = "f_position")
	private String position;
	@Column(name = "f_maker")
	private String maker;
	@Column(name = "f_layer")
	private Integer layer;
	@Column(name = "f_floor_id")
	private Integer floorId;
	@Column(name = "f_control_room_code")
	private String roomCode;
	@Column(name = "f_org_id")
	private Integer orgId;
	@Column(name = "f_input_time")
	private Date inputTime;
	@Column(name = "f_lng")
	private Double lng;
	@Column(name = "f_lat")
	private Double lat;
	@Column(name = "f_max")
	private Double max;
	@Column(name = "f_min")
	private Double min;
	@Column(name = "f_flag")
	private Integer flag;
	@Column(name = "f_tick")
	private Integer tick;//单位秒
	

	public Device() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public Integer getLayer() {
		return layer;
	}

	public void setLayer(Integer layer) {
		this.layer = layer;
	}

	public Integer getFloorId() {
		return floorId;
	}

	public void setFloorId(Integer floorId) {
		this.floorId = floorId;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public Date getInputTime() {
		return inputTime;
	}

	public void setInputTime(Date inputTime) {
		this.inputTime = inputTime;
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

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public Integer getTick() {
		return tick;
	}

	public void setTick(Integer tick) {
		this.tick = tick;
	}
	
}
