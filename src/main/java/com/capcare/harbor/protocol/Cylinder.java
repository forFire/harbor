package com.capcare.harbor.protocol;

import java.io.Serializable;

/**
 * 缸
 * @author XieHaiSheng
 *
 */
public class Cylinder implements  Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7124272745520017685L;
	private Float shortFuelCorrection;//短期燃油修正 %
	private Float secondShortFuelCorrection;//二级传感器短期燃油修正 %
	private Float longFuelCorrection;//长期燃油修正 %
	private OxygenSensor[]  oxygenSensor=new OxygenSensor[]{new OxygenSensor(),new OxygenSensor(),new OxygenSensor(),new OxygenSensor()};//氧传感器
	
	public Float getShortFuelCorrection() {
		return shortFuelCorrection;
	}
	public void setShortFuelCorrection(Float shortFuelCorrection) {
		this.shortFuelCorrection = shortFuelCorrection;
	}
	public Float getSecondShortFuelCorrection() {
		return secondShortFuelCorrection;
	}
	public void setSecondShortFuelCorrection(Float secondShortFuelCorrection) {
		this.secondShortFuelCorrection = secondShortFuelCorrection;
	}
	public Float getLongFuelCorrection() {
		return longFuelCorrection;
	}
	public void setLongFuelCorrection(Float longFuelCorrection) {
		this.longFuelCorrection = longFuelCorrection;
	}
	public OxygenSensor[] getOxygenSensor() {
		return oxygenSensor;
	}
	public void setOxygenSensor(OxygenSensor[] oxygenSensor) {
		this.oxygenSensor = oxygenSensor;
	}
	
	
}
