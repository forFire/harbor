package com.capcare.harbor.handler.device.beidou.encoderService;

import java.util.List;

import org.apache.mina.core.session.IoSession;

import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.InstructType;
import com.capcare.harbor.vo.InstructVo;

public interface BeidouEncoderService {

    public Object getBaseMessage (InstructVo protocol, IoSession session);

    public InstructType getInstructType ();
}
