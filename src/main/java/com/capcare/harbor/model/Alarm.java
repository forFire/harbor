package com.capcare.harbor.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "b_fire_alarm")
public class Alarm extends IdEntity implements Serializable {

	@Column(name = "f_sn")
	private String sn;
	@Column(name = "f_alarm_time")
	private Date alarmTime;
	@Column(name = "f_create_time")
	private Date createTime;
	@Column(name = "f_type")
	private String type;
	@Column(name = "f_status_person")
	private Integer statusPerson;
	@Column(name = "f_status_system")
	private Integer statusSystem;
	@Column(name = "f_status")
	private Integer status;
	
	public Alarm() {

	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Date getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(Date alarmTime) {
		this.alarmTime = alarmTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getStatusPerson() {
		return statusPerson;
	}

	public void setStatusPerson(Integer statusPerson) {
		this.statusPerson = statusPerson;
	}

	public Integer getStatusSystem() {
		return statusSystem;
	}

	public void setStatusSystem(Integer statusSystem) {
		this.statusSystem = statusSystem;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
}
