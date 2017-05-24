package com.capcare.harbor.model;

import java.io.Serializable;

public class PositionExtra implements Serializable {

	private static final long serialVersionUID = -7956801655550501865L;
	private Integer id;// 参数id
	private Integer length;// 长度
	private String value;// 参数值

	public PositionExtra() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
