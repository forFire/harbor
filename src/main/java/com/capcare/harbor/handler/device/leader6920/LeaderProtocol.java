package com.capcare.harbor.handler.device.leader6920;

import java.util.List;

import javax.annotation.Resource;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.DeviceProtocol;
import com.capcare.harbor.vo.BaseMessage;

@Component
@Scope("singleton")
public class LeaderProtocol implements DeviceProtocol {

    private Logger log = LoggerFactory.getLogger (getClass ());

    @Resource
    private MessDecoder decoder;

    @Resource
    private MessEncoder encoder;
	
	/*
	 * 第二步：跳转到消息解码
	 */
    @Override
    public boolean decode (IoSession session, IoBuffer in, ProtocolDecoderOutput out) {

        log.debug ("protocol decode");
        List<BaseMessage> decodes = null;
		try {
			
			/*
			 * 第三步：对消息解码封装
			 */
			decodes = decoder.decode (session, in);
		} catch (Exception e) {
			e.printStackTrace();
		}
        if (decodes != null && decodes.size () > 0) {
            for (BaseMessage message : decodes) {

                if (message != null) {
                    out.write (message);
                }
                else {
                    continue;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Object encode (IoSession session, Object protocol) {
        log.debug ("protocol encode--------------------------");
//        try {
//            return this.encoder.encodeInstruct (session, (InstructVo) protocol);
//        }
//        catch (Exception e) {
//            log.error ("beidou protocol encode error : ", e);
//        }
        return null;
    }

}
