package com.capcare.harbor.service.logic;

import com.capcare.harbor.vo.BaseMessage;

public interface DataService {
	
	/**
	 * 保存数据
	 * @param message
	 */
	public void save(BaseMessage message);
	
}
