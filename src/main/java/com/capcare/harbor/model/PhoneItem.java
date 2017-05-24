package com.capcare.harbor.model;

import java.io.Serializable;

public class PhoneItem implements Serializable {

	private static final long serialVersionUID = -7956801655550501865L;
	private Integer id;// 参数id
	private String phone;// 参数长度
	private String name;// 参数值

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PhoneItem() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
