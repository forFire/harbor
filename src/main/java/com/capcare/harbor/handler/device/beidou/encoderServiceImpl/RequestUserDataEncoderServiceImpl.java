package com.capcare.harbor.handler.device.beidou.encoderServiceImpl;

import java.util.Map;

import module.util.Tools;

import org.apache.mina.core.session.IoSession;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.beidou.BeidouEncoder;
import com.capcare.harbor.handler.device.beidou.encoderManage.BeidouEncoderType;
import com.capcare.harbor.handler.device.beidou.encoderService.BeidouEncoderService;
import com.capcare.harbor.handler.device.m2616.util.DecoderKey;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.InstructVo;

@Component
@Scope("singleton")
public class RequestUserDataEncoderServiceImpl implements BeidouEncoderService {

    private InstructType type      = InstructType.RequestUserData;

    private final BeidouEncoderType SENT_TYPE = BeidouEncoderType.request_userDatas;

    @Override
    public InstructType getInstructType () {

        return type;
    }

    @Override
    public Object getBaseMessage (InstructVo protocol, IoSession session) {

        Map <String, Object> cmdMap = protocol.getCmdMap ();
        String content = Tools.toString (cmdMap.get ("content"));
        String toSn = Tools.toString (cmdMap.get ("toSn"));

        StringBuffer sbf = new StringBuffer ();
        sbf.append (DecoderKey.BEIDOU_BEGIN).append (BeidouEncoder.ENCODER_STR).append (DecoderKey.BEIDOU_BEGIN)
                .append (SENT_TYPE);
        return sbf.toString ();
    }

}
