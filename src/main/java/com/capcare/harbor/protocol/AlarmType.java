package com.capcare.harbor.protocol;



/**
 * @author capcare
 */
public enum AlarmType {
	
	/** 未识别报警  */
	UNDIFINE(-1, "未识别报警"),
	
	
	LPWRPE(2, "水压设备低电"),
	
	COVER(3, "遮挡报警"),
	
	PRESSURE(4, "水压异常报警"),
	
	SLANT(5, "倾斜（移动）报警"),

	LPWRCE(7, "井盖设备低电"),
	
	OVERTIME(8,"蓝牙异常");
	
	
	
	private int num;

	private String info;


	private AlarmType(int num, String info) {
		this.num = num;
		this.info = info;
	}

	public int getNum() {
		return this.num;
	}

	public String getInfo() {
		return this.info;
	}

	public static AlarmType getByNum(int num){
		AlarmType[] types = AlarmType.values();
		for(AlarmType type : types){
			if(type.getNum() == num){
				return type;
			}
		}
		return null;
	}
}
