package com.capcare.harbor.protocol;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

public class OBD  implements MessageCreator, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1568661040063332566L;
	private Long systime = 0L;
	private String deviceSn;
	private Long receive = 0L;
	
	private Float load;//负荷计算 %
	
	private Float coolTemperature;//发动机冷却温度 摄氏度
	private Float  inAirtemperature;//进气温度   摄氏度
	private Float ambientTemperature;//环境温度
	private Float engineOilTemperature;//机油温度
	
	
	private Cylinder[] cylinders=new Cylinder[4];//缸组
	
	private Float[] longFuelCorrection;//长期燃油修正 %
	
	private Float  fuelPipePressure;//燃油分配管压力   kpa
	private Float  fuelSupplyPipePressure;//燃油分供管压力   kpa(相对大气压)
	private Float  inlAirPipePressure;//进气管压力   kpa
	private Float vaporPressure;//蒸汽压
	private Float airPressure;//大气压
	
	private Float  engineSpeed;//发动机转速  rpm
	private Float  speed;  //车速  km/h
	
	private Float  fireAngle;//点火提前角  °
	private Float engineFuelRate;//发动机燃油率

	
	private Float  airTraffic;//空气流量  g/s
	
	private Float[]  throttleabsolutePosition=new Float[6];// 节气门绝对位置ABCDEF
	
	private Long  runTime;//运转时间 s
	private Long  mtlDistance;//故障灯后的行驶距离  km
	private Long  mtlRunTime;//故障灯后的运转时间  minute
	private Float  EGR;//指令的  %
	private Float  EGRError;//故障  %
	private Float evaporation;//蒸发净化 %
	private Float liquidLevelInput;//液位输入 %
	
	private Long afterClearStart;//清楚故障码后的暖机次数
	private Double afterClearDistance;//清楚故障码后的行驶距离  km
	private Long afterClearRuntime;//清楚故障码后的运转时间   minute
	
	
	
	private Float controllerVoltage;//电瓶电压  V
	
	private Float  throttlePosition;// 节气门绝对位置
	
	private String frozenFaultCode;//冻结故障码
	
	private String fuelSystemStatus;//燃油系统状态

	private  Short fuelType;//燃料类型
	private  Float alcoholPercent;//酒精占比 %
	private  Float batteryPercent;//电池剩余电量 %
	
	private Float  fuelOutAngle;//燃油喷射点  °
	
	private Float torque;//实际发动机扭矩  %
	
	private Float fuleCostPerHour;//每小时油耗 L/H
	private Float fuleCostHundred;//每100km油耗 L/100km
	private String leftFule;//剩余油量
	private Double totalDistance;//行驶里程  km
	
	
	private String  complyStandards; //车辆转配OBD的类型
	
	
	public Float getLoad() {
		return load;
	}
	public void setLoad(Float load) {
		this.load = load;
	}
	public Float getCoolTemperature() {
		return coolTemperature;
	}
	public void setCoolTemperature(Float coolTemperature) {
		this.coolTemperature = coolTemperature;
	}
	public Cylinder[] getCylinders() {
		return cylinders;
	}
	public void setCylinders(Cylinder[] cylinders) {
		this.cylinders = cylinders;
	}
	public Float[] getLongFuelCorrection() {
		return longFuelCorrection;
	}
	public void setLongFuelCorrection(Float[] longFuelCorrection) {
		this.longFuelCorrection = longFuelCorrection;
	}
	public Float getFuelPipePressure() {
		return fuelPipePressure;
	}
	public void setFuelPipePressure(Float fuelPipePressure) {
		this.fuelPipePressure = fuelPipePressure;
	}
	public Float getInlAirPipePressure() {
		return inlAirPipePressure;
	}
	public void setInlAirPipePressure(Float inlAirPipePressure) {
		this.inlAirPipePressure = inlAirPipePressure;
	}
	public Float getEngineSpeed() {
		return engineSpeed;
	}
	public void setEngineSpeed(Float engineSpeed) {
		this.engineSpeed = engineSpeed;
	}
	public Float getSpeed() {
		return speed;
	}
	public void setSpeed(Float speed) {
		this.speed = speed;
	}
	public Float getFireAngle() {
		return fireAngle;
	}
	public void setFireAngle(Float fireAngle) {
		this.fireAngle = fireAngle;
	}
	public Float getInAirtemperature() {
		return inAirtemperature;
	}
	public void setInAirtemperature(Float inAirtemperature) {
		this.inAirtemperature = inAirtemperature;
	}
	public Float getAirTraffic() {
		return airTraffic;
	}
	public void setAirTraffic(Float airTraffic) {
		this.airTraffic = airTraffic;
	}
	public Float[] getThrottleabsolutePosition() {
		return throttleabsolutePosition;
	}
	public void setThrottleabsolutePosition(Float[] throttleabsolutePosition) {
		this.throttleabsolutePosition = throttleabsolutePosition;
	}
	public Long getRunTime() {
		return runTime;
	}
	public void setRunTime(Long runTime) {
		this.runTime = runTime;
	}
	public Long getMtlDistance() {
		return mtlDistance;
	}
	public void setMtlDistance(Long mtlDistance) {
		this.mtlDistance = mtlDistance;
	}
	public Long getMtlRunTime() {
		return mtlRunTime;
	}
	public void setMtlRunTime(Long mtlRunTime) {
		this.mtlRunTime = mtlRunTime;
	}
	public Float getEGR() {
		return EGR;
	}
	public void setEGR(Float eGR) {
		EGR = eGR;
	}
	public Float getEGRError() {
		return EGRError;
	}
	public void setEGRError(Float eGRError) {
		EGRError = eGRError;
	}
	public Float getEvaporation() {
		return evaporation;
	}
	public void setEvaporation(Float evaporation) {
		this.evaporation = evaporation;
	}
	public Float getLiquidLevelInput() {
		return liquidLevelInput;
	}
	public void setLiquidLevelInput(Float liquidLevelInput) {
		this.liquidLevelInput = liquidLevelInput;
	}
	public Long getAfterClearStart() {
		return afterClearStart;
	}
	public void setAfterClearStart(Long afterClearStart) {
		this.afterClearStart = afterClearStart;
	}
	public Double getAfterClearDistance() {
		return afterClearDistance;
	}
	
	public void setAfterClearDistance(Double afterClearDistance) {
		this.afterClearDistance = afterClearDistance;
	}
	public Float getVaporPressure() {
		return vaporPressure;
	}
	public void setVaporPressure(Float vaporPressure) {
		this.vaporPressure = vaporPressure;
	}
	public Float getAirPressure() {
		return airPressure;
	}
	public void setAirPressure(Float airPressure) {
		this.airPressure = airPressure;
	}
	public Float getControllerVoltage() {
		return controllerVoltage;
	}
	public void setControllerVoltage(Float controllerVoltage) {
		this.controllerVoltage = controllerVoltage;
	}
	
	public Float getThrottlePosition() {
		return throttlePosition;
	}
	public void setThrottlePosition(Float throttlePosition) {
		this.throttlePosition = throttlePosition;
	}
	public Float getAmbientTemperature() {
		return ambientTemperature;
	}
	public void setAmbientTemperature(Float ambientTemperature) {
		this.ambientTemperature = ambientTemperature;
	}
	public Short getFuelType() {
		return fuelType;
	}
	public void setFuelType(Short fuelType) {
		this.fuelType = fuelType;
	}
	public Float getAlcoholPercent() {
		return alcoholPercent;
	}
	public void setAlcoholPercent(Float alcoholPercent) {
		this.alcoholPercent = alcoholPercent;
	}
	public Float getBatteryPercent() {
		return batteryPercent;
	}
	public void setBatteryPercent(Float batteryPercent) {
		this.batteryPercent = batteryPercent;
	}
	public Float getEngineOilTemperature() {
		return engineOilTemperature;
	}
	public void setEngineOilTemperature(Float engineOilTemperature) {
		this.engineOilTemperature = engineOilTemperature;
	}
	public Float getFuelOutAngle() {
		return fuelOutAngle;
	}
	public void setFuelOutAngle(Float fuelOutAngle) {
		this.fuelOutAngle = fuelOutAngle;
	}
	public Float getTorque() {
		return torque;
	}
	public void setTorque(Float torque) {
		this.torque = torque;
	}
	public Float getFuleCostPerHour() {
		return fuleCostPerHour;
	}
	public void setFuleCostPerHour(Float fuleCostPerHour) {
		this.fuleCostPerHour = fuleCostPerHour;
	}
	public Float getFuleCostHundred() {
		return fuleCostHundred;
	}
	public void setFuleCostHundred(Float fuleCostHundred) {
		this.fuleCostHundred = fuleCostHundred;
	}
	public String getLeftFule() {
		return leftFule;
	}
	public void setLeftFule(String leftFule) {
		this.leftFule = leftFule;
	}
	
	public Long getSystime() {
		return systime;
	}
	public void setSystime(Long systime) {
		this.systime = systime;
	}
	public Long getReceive() {
		return receive;
	}
	public void setReceive(Long receive) {
		this.receive = receive;
	}
	
	public Double getTotalDistance() {
		return totalDistance;
	}
	public void setTotalDistance(Double totalDistance) {
		this.totalDistance = totalDistance;
	}
	public Float getFuelSupplyPipePressure() {
		return fuelSupplyPipePressure;
	}
	public void setFuelSupplyPipePressure(Float fuelSupplyPipePressure) {
		this.fuelSupplyPipePressure = fuelSupplyPipePressure;
	}
	
	public Long getAfterClearRuntime() {
		return afterClearRuntime;
	}
	public void setAfterClearRuntime(Long afterClearRuntime) {
		this.afterClearRuntime = afterClearRuntime;
	}
	
	public String getDeviceSn() {
		return deviceSn;
	}
	public void setDeviceSn(String deviceSn) {
		this.deviceSn = deviceSn;
	}
	public Float getEngineFuelRate() {
		return engineFuelRate;
	}
	public void setEngineFuelRate(Float engineFuelRate) {
		this.engineFuelRate = engineFuelRate;
	}
	
	public String getComplyStandards() {
		return complyStandards;
	}
	public void setComplyStandards(String complyStandards) {
		this.complyStandards = complyStandards;
	}
	
	public String getFrozenFaultCode() {
		return frozenFaultCode;
	}
	public void setFrozenFaultCode(String frozenFaultCode) {
		this.frozenFaultCode = frozenFaultCode;
	}
	
	public String getFuelSystemStatus() {
		return fuelSystemStatus;
	}
	public void setFuelSystemStatus(String fuelSystemStatus) {
		this.fuelSystemStatus = fuelSystemStatus;
	}
	
	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}
	@Override
	public String toString() {
		return "OBD [receive=" + receive + ",complyStandards="+complyStandards+",engineFuelRate="+engineFuelRate+" load=" + load + "%, speed="
				+ speed + "km/h, engineSpeed="+engineSpeed+"rpm, coolTemperature="+coolTemperature+"°C, controllerVoltage="+controllerVoltage+"V]";
	}
	
	
	
}
