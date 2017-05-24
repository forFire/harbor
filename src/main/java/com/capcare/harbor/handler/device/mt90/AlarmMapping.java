package com.capcare.harbor.handler.device.mt90;

import java.util.HashMap;
import java.util.Map;

import com.capcare.harbor.protocol.AlarmType;

/**
 * 设备告警类型  <----> 凯步告警类型   映射关系
 * @author capcare
 *
 */
public class AlarmMapping {

	private static Map<Integer,AlarmType> map = new HashMap<Integer,AlarmType>();
	
	static{
		/*map.put(17, AlarmType.PowerLow);
		map.put(19, AlarmType.SPDHI);
		map.put(20, AlarmType.BNDIN);
		map.put(21, AlarmType.BNDOUT);*/
	}
	
	protected static AlarmType getMappedAlarm(int orig_alarm){
		return map.get(orig_alarm);
	}
}
