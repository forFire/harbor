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
import com.capcare.harbor.protocol.Conductor_Status;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.LoginDevice;

@Component
@Scope("singleton")
public class CommandStatusDecoderServiceImpl implements BeidouDecoderService {

    private final BeidouDecoderType type             = BeidouDecoderType.command_status;

    private Logger                  log              = LoggerFactory.getLogger (getClass ());
    
    private final int MESSAGE_SIZE = 11;
    private final int accMode = 0;
    private final int BEAN1 = 1;
    private final int BEAN2 = 2;
    private final int BEAN3 = 3;
    private final int BEAN4 = 4;
    private final int BEAN5 = 5;
    private final int BEAN6 = 6;
    private final int BEAN7 = 7;
    private final int BEAN8 = 8;
    private final int BEAN9 = 9;
    private final int BEAN10 = 10;
    
    

    @Override
    public List <BaseMessage> getBaseMessage (String [] str, IoSession session) {
    	
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
    public BeidouDecoderType getDecoderType () {

        return type;
    }
    
    /**
     * @param str
     * @return
     */
    public BaseMessage toBaseMessage(String[] str,IoSession session){
    	
    	Object businessData = null;
		BaseMessage msg = new BaseMessage(Act.STATUS, businessData);
		Conductor_Status status = new Conductor_Status();
		status.setAccMode(str[accMode]);
		status.setBean1(str[BEAN1]);
		status.setBean2(str[BEAN2]);
		status.setBean3(str[BEAN3]);
		status.setBean4(str[BEAN4]);
		status.setBean5(str[BEAN5]);
		status.setBean6(str[BEAN6]);
		status.setBean7(str[BEAN7]);
		status.setBean8(str[BEAN8]);
		status.setBean9(str[BEAN9]);
		status.setBean10(str[BEAN10]);
		
		msg.setBusinessData(status);
		LoginDevice loginDevice = new LoginDevice();
		loginDevice.setDt(new Beidou());
		log.info("session  sn : {}", session.toString());
		log.info("session  sn : {}", session.getAttribute("duid"));
		loginDevice.setSn(session.getAttribute("duid").toString());
		msg.setLoginDevice(loginDevice);
		return msg;
    }

}
