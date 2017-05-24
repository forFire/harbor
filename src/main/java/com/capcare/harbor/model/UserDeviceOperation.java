/**
 * 
 */
package com.capcare.harbor.model;

/**
 * @author zf
 *
 */
public class UserDeviceOperation {

	// 操作标志
	private String operationFlag;
	// 操作员编码
	private	String operatorCode;

	//时间
	private String time;
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getOperationFlag() {
		return operationFlag;
	}

	public void setOperationFlag(String operationFlag) {
		this.operationFlag = operationFlag;
	}

	public String getOperatorCode() {
		return operatorCode;
	}

	public void setOperatorCode(String operatorCode) {
		this.operatorCode = operatorCode;
	}
	
	
	
	
	
}
