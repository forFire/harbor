package com.capcare.harbor.service.cache;

import module.util.JsonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.capcare.harbor.protocol.OBD;
import com.capcare.harbor.protocol.OBDError;

@Component
public class ObdCache {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final String OBD_DATA = "obd";// obd数据

	private static final String OBD_ERR = "obd_err";// obd故障数据

	public void setObd(String deviceSn, OBD obd) {
		String json = JsonUtils.obj2Str(obd);
		redisTemplate.opsForHash().put(OBD_DATA, deviceSn, json);
	}

	public OBD getObd(String deviceSn) {
		String json = (String) redisTemplate.opsForHash().get(OBD_DATA,
				deviceSn);
		if (json != null && !"".equals(json)) {
			return JsonUtils.str2Obj(json, OBD.class);
		}
		return null;
	}

	public void setObdError(String deviceSn, OBDError obderr) {
		String json = JsonUtils.obj2Str(obderr);
		redisTemplate.opsForHash().put(OBD_ERR, deviceSn, json);
	}

	public OBDError getObdError(String deviceSn) {
		String json = (String) redisTemplate.opsForHash()
				.get(OBD_ERR, deviceSn);
		if (json != null && !"".equals(json)) {
			return JsonUtils.str2Obj(json, OBDError.class);
		}
		return null;
	}
}
