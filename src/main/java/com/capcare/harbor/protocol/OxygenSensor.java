package com.capcare.harbor.protocol;

import java.io.Serializable;

/**
 * 氧传感器
 * @author XieHaiSheng
 *
 */
public class OxygenSensor implements  Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9060001688851305136L;
	private Float voltage ;//电压 V
	private int position;//位置
	private Float shortFuelCorrection;//短期燃油修正
	private Float catalystTemperature;//催化剂温度
	private Float electricity ;//电流  mA
	private Float equivalenceRatio;//当量比
	
	public Float getVoltage() {
		return voltage;
	}
	public void setVoltage(Float voltage) {
		this.voltage = voltage;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public Float getShortFuelCorrection() {
		return shortFuelCorrection;
	}
	public void setShortFuelCorrection(Float shortFuelCorrection) {
		this.shortFuelCorrection = shortFuelCorrection;
	}
	public Float getCatalystTemperature() {
		return catalystTemperature;
	}
	public void setCatalystTemperature(Float catalystTemperature) {
		this.catalystTemperature = catalystTemperature;
	}
	public Float getElectricity() {
		return electricity;
	}
	public void setElectricity(Float electricity) {
		this.electricity = electricity;
	}
	public Float getEquivalenceRatio() {
		return equivalenceRatio;
	}
	public void setEquivalenceRatio(Float equivalenceRatio) {
		this.equivalenceRatio = equivalenceRatio;
	}
	
	
}
