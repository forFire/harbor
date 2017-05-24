package com.capcare.harbor.service.cache;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class IosTokenCache {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	/** userId对应的IOS token */
	private static final String IOS_TOKEN = "ios_token";
	

	public Set<Object> getIosTokenSet(String userId) {
		return redisTemplate.opsForSet().members(IOS_TOKEN+":"+userId);
	}

}
