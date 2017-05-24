package com.capcare.harbor.service.logic;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.capcare.harbor.protocol.OBD;
import com.capcare.harbor.protocol.OBDError;
import com.capcare.harbor.service.cache.ObdCache;
import com.capcare.harbor.vo.BaseMessage;

@Component("OBDService")
public class OBDService implements DataService {

    private static Logger logger = LoggerFactory.getLogger (OBDService.class);

    @Resource
    private JmsTemplate   jmsTemplate;

    @Resource
    private ObdCache      obdCache;

    public void saveJms (BaseMessage message) {

        Object data = message.getBusinessData ();
        if (data instanceof OBD) {
            OBD obd = (OBD) data;
            // 数据合法性校验
            logger.info ("obdData:" + obd.toString ());
            jmsTemplate.send (obd);
        }
        else if (data instanceof OBDError) {
            logger.info ("OBDError..." + data.toString ());
            // OBDError error=(OBDError)data;
            // for(String code:error.getCodes()){
            // Alarm alarm = new Alarm();
            // alarm.setTime(error.getReceive());
            // alarm.setSystime(error.getSystime());
            // alarm.setType(AlarmType.OBDERROR.getNum());
            // alarm.setInfo(code);
            // jmsTemplate.send(alarm);
            // logger.info("OBDError to alarm:"+alarm.toString());
            // }

        }
    }

    @Override
    public void save (BaseMessage message) {

        Object data = message.getBusinessData ();
        if (data instanceof OBD) {
            OBD obd = (OBD) data;

            Float engineSpeed = obd.getEngineSpeed ();
            Float speed = obd.getSpeed ();
            Float load = obd.getLoad ();
            String complyStandards = obd.getComplyStandards ();
            Float coolTemperature = obd.getCoolTemperature ();

            if (engineSpeed == null && speed == null && load == null && complyStandards == null
                    && coolTemperature == null) {
                return;
            }

            // 数据合法性校验
            logger.info ("obdData:" + obd.toString ());
            OBD obdCached = obdCache.getObd (obd.getDeviceSn ());
            if (obdCached == null || obdCached.getReceive () < obd.getReceive ())
                obdCache.setObd (obd.getDeviceSn (), obd);
        }
        else if (data instanceof OBDError) {
            logger.info ("OBDError...");
            OBDError error = (OBDError) data;
            obdCache.setObdError (error.getDeviceSn (), error);
        }
    }

}
