package com.capcare.harbor.model;

public class SpeetInstructVo {

	private Integer open;

	private Integer min;

	private Integer max;

	public Integer getOpen() {
		return open;
	}

	public void setOpen(Integer open) {
		this.open = open;
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

	public SpeetInstructVo(Integer open, Integer min, Integer max) {
		super();
		this.open = open;
		this.min = min;
		this.max = max;
	}

	public SpeetInstructVo() {
		super();
	}

	@Override
	public String toString() {
		return "SpeetInstructVo [open=" + open + ", min=" + min + ", max=" + max + "]";
	}

}
