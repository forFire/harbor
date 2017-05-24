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
import com.capcare.harbor.protocol.Conductor_GPS;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.LoginDevice;

@Component
@Scope("singleton")
public class CommandGPSDecoderServiceImpl implements BeidouDecoderService {

    private final BeidouDecoderType type             = BeidouDecoderType.command_GPS;

    private Logger                  log              = LoggerFactory.getLogger (getClass ());
    
    private final int MESSAGE_SIZE = 2;
    private final int accMode = 0;
    private final int SATELLITE_NO = 1;
    

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

    public BaseMessage toBaseMessage(String[] str,IoSession session){
    	
    	Object businessData = null;
		BaseMessage msg = new BaseMessage(Act.GPS, businessData);
		Conductor_GPS gps = new Conductor_GPS();
		gps.setAccMode(str[accMode]);
		gps.setSatellite_no(str[SATELLITE_NO]);
		
		msg.setBusinessData(gps);
		LoginDevice loginDevice = new LoginDevice();
		loginDevice.setDt(new Beidou());
		loginDevice.setSn(session.getAttribute("duid").toString());
		msg.setLoginDevice(loginDevice);
		return msg;
    }
}
