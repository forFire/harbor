package com.capcare.harbor.handler.device.beidou.encoderServiceImpl;

import java.net.URLEncoder;
import java.util.Map;

import module.util.Tools;

import org.apache.mina.core.session.IoSession;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.beidou.BeidouEncoder;
import com.capcare.harbor.handler.device.beidou.encoderService.BeidouEncoderService;
import com.capcare.harbor.handler.device.m2616.util.DecoderKey;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.InstructVo;

@Component
@Scope("singleton")
public class SentMessageEncoderServiceImpl implements BeidouEncoderService {

    private InstructType type      = InstructType.SentMessage;

    private final String SENT_TYPE = "S";

    @Override
    public InstructType getInstructType () {

        return type;
    }

    @Override
    public Object getBaseMessage (InstructVo protocol, IoSession session) {

        Map <String, Object> cmdMap = protocol.getCmdMap ();
        String content = Tools.toString (cmdMap.get ("content"));
        String toSn = Tools.toString (cmdMap.get ("toSn"));
        //TODO 多个短报文怎么发送? 现在：心跳一次发一条
        StringBuffer sbf = new StringBuffer ();
        sbf.append (DecoderKey.BEIDOU_BEGIN).append (BeidouEncoder.ENCODER_STR).append (DecoderKey.BEIDOU_BEGIN)
                .append (SENT_TYPE).append (DecoderKey.BEIDOU_BEGIN).append (toSn).append (DecoderKey.BEIDOU_BEGIN)
                .append (URLEncoder.encode(content));
        return sbf.toString ();
    }

}
