package com.capcare.harbor.service.cache;

import module.util.JsonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.capcare.harbor.protocol.Alarm;
import com.capcare.harbor.protocol.Position;

@Component
public class PositionCache {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	private static final String POSITION = "position";
	
	private static final String LAST_ALARMS = "last_alarm";
	
	private static final String DEVICE_ALARM = "device_alarm";
		
	private static final String INSTRUCT = "instruct";
	
	public void setInstruct(String deviceSn) {
        redisTemplate.opsForSet().add(INSTRUCT, deviceSn);
    }
	
	public boolean isSetInstruct(String deviceSn) {
		return redisTemplate.opsForSet().isMember(INSTRUCT, deviceSn);
	}
	
	public void removeInstruct(String deviceSn) {
		redisTemplate.opsForSet().remove(INSTRUCT, deviceSn);
	}
	
	public Position getPosition(String deviceSn) {
		String json = (String)redisTemplate.opsForHash().get(POSITION, deviceSn);
		if(json != null && !"".equals(json)){
			return JsonUtils.str2Obj(json, Position.class);
		}
		return null;
	}

	public void setPosition(String deviceSn, Position position) {
		String json = JsonUtils.obj2Str(position);
		redisTemplate.opsForHash().put(POSITION, deviceSn, json);
	}
	
	public Alarm getLastAlarm(String deviceSn) {
		String json = (String)redisTemplate.opsForHash().get(LAST_ALARMS, deviceSn);
		if(json != null && !"".equals(json)){
			return JsonUtils.str2Obj(json, Alarm.class);
		}
		return null;
	}
	
	public void setLastAlarm(String deviceSn, Alarm lastAlarm) {
		redisTemplate.opsForHash().put(LAST_ALARMS, deviceSn, JsonUtils.obj2Str(lastAlarm));
	}
	
	public void addAlarmId(String deviceSn, String alarmId) {
		redisTemplate.opsForSet().add(DEVICE_ALARM+":"+deviceSn, alarmId);
	}
	
}
