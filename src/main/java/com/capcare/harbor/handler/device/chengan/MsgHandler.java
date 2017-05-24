package com.capcare.harbor.handler.device.chengan;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capcare.harbor.handler.MessageHandler;
import com.capcare.harbor.handler.SessionWriter;
import com.capcare.harbor.vo.BaseMessage;

public class MsgHandler extends MessageHandler {
	private Logger log = LoggerFactory.getLogger(getClass());
	/**
	 *回复消息 
	 */
	public void afterProcess(BaseMessage message, IoSession session) {
		log.info("MsgHandler.afterProcess------------------");
//		switch(message.getAct()){
//			case FIREALARM:
				ChenganResponse response=(ChenganResponse)message.getResponse();
				SessionWriter sessionWriter = (SessionWriter)session.getAttribute("cap_session_writer");
				if(sessionWriter == null){
					sessionWriter = new SessionWriter();
				}
				sessionWriter.add(response.tobytes(response.isFlag()));
				session.setAttribute("cap_session_writer", sessionWriter);
//				break;
//			default:
//				break;
//		}
	}
}
