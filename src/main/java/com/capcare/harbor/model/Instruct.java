package com.capcare.harbor.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author fyq
 */
@Entity
@Table(name = "us_instruct")
public class Instruct implements Serializable {

	private static final long serialVersionUID = -8164031615167124852L;

	@Id
	@Column(name = "f_id")
	private String id;

	@Column(name = "f_device_sn")
	private String deviceSn;

	@Column(name = "f_type")
	private Integer type;

	// 是否已回复 0未回复 1回复
	@Column(name = "f_reply")
	private Integer reply;

	@Column(name = "f_content")
	private String content;

	// 原来参数 回滚使用
	@Column(name = "f_original")
	private String original;

	// 回滚次数 回滚使用
	@Column(name = "f_num")
	private Integer num = 3;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getReply() {
		return reply;
	}

	public void setReply(Integer reply) {
		this.reply = reply;
	}

	public String getOriginal() {
		return original;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "Instruct [id=" + id + ", deviceSn=" + deviceSn + ", type=" + type + ", reply="
				+ reply + ", content=" + content + ", original=" + original + ", num=" + num + "]";
	}

}