package com.capcare.harbor.service.logic;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.capcare.harbor.dao.FeeCheckDao;
import com.capcare.harbor.model.FeeCheck;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.LoginDevice;

@Component
public class FeeCheckService implements DataService{

	private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Resource
	private FeeCheckDao feeCheckDao;
	
	@Override
	public void save(BaseMessage message) {
		String sms = (String) message.getBusinessData();
		LoginDevice ld = message.getLoginDevice();
		
		FeeCheck feeCheck = new FeeCheck();
		feeCheck.setDeviceSn(ld.getSn());
		feeCheck.setContent(sms);
		
		Date d = new Date();
		feeCheck.setReceiveTime(format.format(d));
		feeCheckDao.save(feeCheck);
	}
}
