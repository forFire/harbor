package com.capcare.harbor.service.cache;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserCache {
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	private static final String USER_PHONE = "phone";
	private static final String USER_PHONE_MQ = "phone_mq";
	private static final String BACKEND = "backend";
	
	public Set<Object> getOnlinePhone(String userId, String appName){
		return redisTemplate.opsForSet().members(USER_PHONE+":"+userId+":"+appName);		
	}
	
	public Set<Object> getBackendPhone(String userId, String appName){
		return redisTemplate.opsForSet().members(BACKEND+":"+userId+":"+appName);
	}
	
	public String getPhoneMq(String userId, String appName, String duid){
		return (String)redisTemplate.opsForHash().get(USER_PHONE_MQ, userId+":"+appName+":"+duid);	
	}
}
