package com.capcare.harbor.handler.device.beidou.decoderService;

import java.util.List;

import org.apache.mina.core.session.IoSession;

import com.capcare.harbor.handler.device.beidou.decoderManage.BeidouDecoderType;
import com.capcare.harbor.vo.BaseMessage;

public interface BeidouDecoderService {

    public List <BaseMessage> getBaseMessage (String [] str, IoSession session);

    public BeidouDecoderType getDecoderType ();
}
