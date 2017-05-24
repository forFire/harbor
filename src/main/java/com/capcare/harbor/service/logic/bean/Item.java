package com.capcare.harbor.service.logic.bean;

import java.io.Serializable;

public class Item implements Serializable {

	private static final long serialVersionUID = -7956801655550501865L;
	private Integer id;// 参数id
	private String value;// 参数值

	public Item() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
