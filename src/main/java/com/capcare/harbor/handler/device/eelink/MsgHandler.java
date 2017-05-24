package com.capcare.harbor.handler.device.eelink;

import org.apache.mina.core.session.IoSession;

import com.capcare.harbor.handler.MessageHandler;
import com.capcare.harbor.handler.SessionWriter;
import com.capcare.harbor.vo.BaseMessage;

public class MsgHandler extends MessageHandler {
	
	
	public void afterProcess(BaseMessage message, IoSession session) {
		
		switch(message.getAct()){
			case LOGIN:
			case HEART_BEAT:
			case ALARM:
			case OBD:
			case OBD_ALARM:
				EElinkResponse response=(EElinkResponse)message.getResponse();

				SessionWriter sessionWriter = (SessionWriter)session.getAttribute("cap_session_writer");
				if(sessionWriter == null){
					sessionWriter = new SessionWriter();
				}
				sessionWriter.add(response.tobytes());
				session.setAttribute("cap_session_writer", sessionWriter);
				break;
			default:
				break;
		}
	}
}
