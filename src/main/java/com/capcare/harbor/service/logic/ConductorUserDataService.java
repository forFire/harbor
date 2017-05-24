package com.capcare.harbor.service.logic;


import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.capcare.harbor.protocol.Conductor_UserData;
import com.capcare.harbor.vo.BaseMessage;


@Component
@Scope("singleton")
public class ConductorUserDataService implements DataService {

	private static Logger logger = LoggerFactory.getLogger(ConductorUserDataService.class);

	@Resource
	private JmsTemplate jmsTemplate;



	@Override
	public void save(BaseMessage message) {
		Conductor_UserData userData = (Conductor_UserData) message.getBusinessData();
		jmsTemplate.send(userData);
		logger.info("Send to MQ : {}", message.getAct().name());
	}

}
