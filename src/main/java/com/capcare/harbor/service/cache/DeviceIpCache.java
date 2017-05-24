package com.capcare.harbor.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;



/**
 *采集设备ip 端口放缓存，harbor轮训用 
 */
@Component
public class DeviceIpCache {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final String DEVICE_IP_PORT = "device_ip_port";
		
//	public void addDeviceIp(String code,String ipPort) {
//		redisTemplate.opsForHash().put(DEVICE_IP_PORT, code,ipPort);
//		
//	}
	
	public Object getAll() {
		return redisTemplate.opsForHash().entries(DEVICE_IP_PORT);
	}
	
	
//	public void delDeviceIp(String code) {
//		redisTemplate.opsForHash().delete(DEVICE_IP_PORT, code);
//	}
}
