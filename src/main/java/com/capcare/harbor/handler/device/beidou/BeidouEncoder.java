package com.capcare.harbor.handler.device.beidou;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.beidou.encoderManage.BeidouEncoderFactory;
import com.capcare.harbor.handler.device.beidou.encoderService.BeidouEncoderService;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.InstructVo;

@Component
@Scope("singleton")
public class BeidouEncoder {

    private Logger               log         = LoggerFactory.getLogger (getClass ());

    public static String         ENCODER_STR = "BDDHR";

    @Resource
    private BeidouEncoderFactory encoderFactory;

    public Object encodeInstruct (IoSession session, InstructVo protocol) throws Exception {

        log.info ("encoder : {}", "");

        Map <String, Object> cmdMap = protocol.getCmdMap ();
        InstructType instructType = protocol.getInstructType ();
        String deviceSn = protocol.getDeviceSn ();

        BeidouEncoderService service = encoderFactory.getService (protocol.getInstructType ());
        
        Object content = service.getBaseMessage (protocol, session);

        log.info ("encode:{}",content);
        return service.getBaseMessage (protocol, session);

    }
}
