package com.capcare.harbor.handler.device.m2616;

import java.util.HashMap;
import java.util.Map;

import com.capcare.harbor.protocol.AlarmType;

/**
 * 设备告警类型  <----> 凯步告警类型   映射关系
 * @author capcare
 *
 */
public class AlarmMapping {

	private static Map<String,AlarmType> map = new HashMap<String,AlarmType>();
	
	static{
		//map.put("SPDLO", AlarmType.SPDLO);
		//map.put("BAT", AlarmType.BAT);
/*		map.put("EPD", AlarmType.EPD);
		map.put("VIB", AlarmType.VIB);
		map.put("SOS", AlarmType.SOS);
		map.put("SPDHI", AlarmType.SPDHI);
		map.put("LPD", AlarmType.PowerLow);
		map.put("BNDIN", AlarmType.BNDIN);
		map.put("BNDOUT", AlarmType.BNDOUT);*/
		map.put("COVER", AlarmType.COVER);
		map.put("PRESSURE", AlarmType.PRESSURE);
		map.put("SLANT", AlarmType.SLANT);
		map.put("LPWRCE", AlarmType.LPWRCE);
		map.put("LPWRPE", AlarmType.LPWRPE);
		map.put("OVERTIME", AlarmType.OVERTIME);
	}
	
	protected static AlarmType getMappedAlarm(String orig_alarm){
		return map.get(orig_alarm);
	}
}
