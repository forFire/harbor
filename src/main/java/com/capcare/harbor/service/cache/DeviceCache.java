package com.capcare.harbor.service.cache;

import module.util.JsonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.capcare.harbor.model.Device;

@Component
public class DeviceCache {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final String ONLINE_DEVICE = "online_device";
		
	private static final String DEVICE = "device";
	
	private static final String DEVICE_FEE_SMS = "device_fee_sms";
	
	private static final String NEED_ISSUE = "need_issue";

	public void addOnlineDevice(String deviceSn) {
		redisTemplate.opsForSet().add(ONLINE_DEVICE, deviceSn);
	}
	
	public void removeOnlineDevice(String deviceSn) {
		redisTemplate.opsForSet().remove(ONLINE_DEVICE, deviceSn);
	}
	
	public void setDevice(String deviceSn, Device device) {
		String json = JsonUtils.obj2Str(device);
		redisTemplate.opsForHash().put(DEVICE, deviceSn, json);
	}
	public Device getDevice(String deviceSn) {
		String json = (String)redisTemplate.opsForHash().get(DEVICE, deviceSn);
		if(json != null && !"".equals(json)){
			return JsonUtils.str2Obj(json, Device.class);
		}
		return null;
	}
	
	public void setFeeSmsTime(String deviceSn, Long sendTime) {
		redisTemplate.opsForHash().put(DEVICE_FEE_SMS, deviceSn, String.valueOf(sendTime));
	}
	public Long getFeeSmsTime(String deviceSn) {
		Object res = redisTemplate.opsForHash().get(DEVICE_FEE_SMS, deviceSn);	
		if(res != null){
			return Long.valueOf((String)res);
		}
		return null;
	}
	
	public void setNeedIssue(String deviceSn) {
		redisTemplate.opsForSet().add(NEED_ISSUE, deviceSn);
	}
	
	public boolean getNeedIssue(String deviceSn) {
		if(deviceSn != null){
			return redisTemplate.opsForSet().isMember(NEED_ISSUE, deviceSn);
		}else{
			return false;
		}
		
	}
	
	public void delNeedIssue(String deviceSn) {
		redisTemplate.opsForSet().remove(NEED_ISSUE, deviceSn);
	}
}
