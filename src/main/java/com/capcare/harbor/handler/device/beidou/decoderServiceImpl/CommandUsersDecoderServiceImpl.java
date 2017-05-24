package com.capcare.harbor.handler.device.beidou.decoderServiceImpl;

import java.util.ArrayList;
import java.util.List;

import module.util.Tools;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.beidou.Beidou;
import com.capcare.harbor.handler.device.beidou.decoderManage.BeidouDecoderType;
import com.capcare.harbor.handler.device.beidou.decoderService.BeidouDecoderService;
import com.capcare.harbor.protocol.Conductor_UserData;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.LoginDevice;

@Component
@Scope("singleton")
public class CommandUsersDecoderServiceImpl implements BeidouDecoderService {

	private final BeidouDecoderType type = BeidouDecoderType.command_users;

	private Logger log = LoggerFactory.getLogger(getClass());

	private final int MESSAGE_SIZE = 9;
	private final int accMode = 0;
	private final int CONDUCTOR_SN = 1; // 指挥机sn
	private final int CONDUCTOR_ADDR = 2; // 指挥机地址
	private final int FREQUENTNESS = 3;
	private final int CONDUCTOR_TYPE = 4;
	private final int CONDUCTOR_LEVEL = 5;
	private final int ENCRYPTION = 6;
	private final int CONDUCTOR_BEAN = 7;
	private final int USER_NO = 8;

	@Override
	public List<BaseMessage> getBaseMessage(String[] str, IoSession session) {

		log.info("receive message : {}", str);
		List<BaseMessage> ls = new ArrayList<BaseMessage>();
		if (str.length == MESSAGE_SIZE) {
			BaseMessage baseMessage = this.toBaseMessage(str,session);
			if (Tools.checkNotNull(baseMessage))
				ls.add(baseMessage);
		}
		return ls;
	}

	@Override
	public BeidouDecoderType getDecoderType() {

		return type;
	}

	public BaseMessage toBaseMessage(String[] str,IoSession session) {

		Object businessData = null;
		BaseMessage msg = new BaseMessage(Act.USERDATA, businessData);
		Conductor_UserData userData = new Conductor_UserData();
		userData.setAccMode(str[accMode]);
		userData.setConductor_sn(str[CONDUCTOR_SN]);
		userData.setConductor_addr(str[CONDUCTOR_ADDR]);
		userData.setFrequentness(str[FREQUENTNESS]);
		userData.setConductor_type(str[CONDUCTOR_TYPE]);
		userData.setConductor_level(str[CONDUCTOR_LEVEL]);
		userData.setEncryption(str[ENCRYPTION]);
		userData.setConductor_bean(str[CONDUCTOR_BEAN]);
		userData.setUser_no(str[USER_NO]);
		msg.setBusinessData(userData);
		LoginDevice loginDevice = new LoginDevice();
		loginDevice.setDt(new Beidou());
		loginDevice.setSn(session.getAttribute("duid").toString());
		msg.setLoginDevice(loginDevice);
		return msg;
	}
}
