package com.capcare.harbor.service.logic;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.capcare.harbor.protocol.Conductor_GPS;
import com.capcare.harbor.vo.BaseMessage;


@Component
@Scope("singleton")
public class ConductorGPSService implements DataService {

	private static Logger logger = LoggerFactory.getLogger(ConductorGPSService.class);


	@Resource
	private JmsTemplate jmsTemplate;


	@Override
	public void save(BaseMessage message) {
		Conductor_GPS gps = (Conductor_GPS) message.getBusinessData();
		jmsTemplate.send(gps);
		logger.info("Send to MQ : {}", message.getAct().name());
	}

}
