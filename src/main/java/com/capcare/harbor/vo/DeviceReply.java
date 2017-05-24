package com.capcare.harbor.vo;

public class DeviceReply{
	
	private String deviceSn;
	
	private InstructType instructType;

	private boolean success;

	public InstructType getInstructType() {
		return instructType;
	}

	public void setInstructType(InstructType instructType) {
		this.instructType = instructType;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getDeviceSn() {
		return deviceSn;
	}

	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Reply[");
		sb.append("sn=");
		sb.append(getDeviceSn());
		sb.append(",type=");
		sb.append(getInstructType().getInfo());
		sb.append(",result=");
		sb.append(isSuccess());
		sb.append("]");
		return sb.toString();
	}
}
