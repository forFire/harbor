package com.capcare.harbor.handler.device.beidou.decoderServiceImpl;

import java.util.ArrayList;
import java.util.List;

import module.util.DateUtil;
import module.util.Tools;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.beidou.Beidou;
import com.capcare.harbor.handler.device.beidou.decoderManage.BeidouDecoderType;
import com.capcare.harbor.handler.device.beidou.decoderService.BeidouDecoderService;
import com.capcare.harbor.protocol.Position;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.LoginDevice;

@Component
@Scope("singleton")
public class PositionDecoderServiceImpl implements BeidouDecoderService {

    private final BeidouDecoderType type             = BeidouDecoderType.receive_locations;

    private Logger                  log              = LoggerFactory.getLogger (getClass ());

    private final int               MESSAGE_SIZE     = 6;

    private final int               accMode          = 0;
    private final int               SN_NO            = 1;//协议中称为地址
    private final int               POSITION_TIME_NO = 2;
    private final int               LNG_NO           = 3;
    private final int               LAT_NO           = 4;
    private final int               ELEVATION        = 5;
    private final String            MODE             = "V";

    @Override
    public List <BaseMessage> getBaseMessage (String [] str, IoSession session) {

        log.info ("receive message : {}", str);
        List <BaseMessage> ls = new ArrayList <BaseMessage> ();
        if (str.length == MESSAGE_SIZE) {
           BaseMessage baseMessage = this.toBaseMessage (str,session);
           if(Tools.checkNotNull (baseMessage))
               ls.add (baseMessage);
        }
        return ls;
    }

    @Override
    public BeidouDecoderType getDecoderType () {

        return type;
    }

    public BaseMessage toBaseMessage (String [] strs,IoSession session) {
        Object businessData = null;
        BaseMessage msg = new BaseMessage (Act.POSITION, businessData);
        Position position = new Position ();
        
        position.setAccMode (accMode);
        position.setDeviceSn (strs [SN_NO]);
        try {
			position.setReceive(DateUtil.strLong(strs [POSITION_TIME_NO], "yyyy-MM-dd HH:mm:ss"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        position.setLng (Tools.toDouble (strs [LNG_NO]));
        position.setLat (Tools.toDouble (strs [LAT_NO]));
        position.setInfo(strs[ELEVATION]);//备用字段info用来存放高程
        
        position.setMode (MODE);//mode中放入高程
        int mode433 = 0;
        position.setMode433 (mode433);
        position.setCell ("");
        
        msg.setBusinessData (position);
        LoginDevice loginDevice = new LoginDevice ();
        loginDevice.setDt (new Beidou ());
        loginDevice.setSn (session.getAttribute("duid").toString());
        msg.setLoginDevice (loginDevice);
        return msg;
    }
}
