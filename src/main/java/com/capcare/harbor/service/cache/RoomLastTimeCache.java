package com.capcare.harbor.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


/**
 *采集设备最后连接时间 
 */
@Component
public class RoomLastTimeCache {
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	private static final String EQUIP_LAST_TIME = "equip_last_time";
	
	
	public void setEquipmentTime(String equipCode, String date) {
		redisTemplate.opsForHash().put(EQUIP_LAST_TIME, equipCode, date);
	}

	public String getEquipmentTime(String equipCode) {
		Object obj = redisTemplate.opsForHash().get(EQUIP_LAST_TIME, equipCode);
		if (obj != null) {
			return obj.toString();
		} else {
			return null;
		}

	}
	
	
}
