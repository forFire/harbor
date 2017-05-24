package com.capcare.harbor.handler.device.leader6920;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capcare.harbor.handler.MessageHandler;
import com.capcare.harbor.handler.SessionWriter;
import com.capcare.harbor.vo.BaseMessage;

public class MsgHandler extends MessageHandler {
	private Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * MsgHandler.afterProcess()
	 */
	public void afterProcess(BaseMessage message, IoSession session) {
		log.info("MsgHandler.afterProcess--业务处理之后回复到客户机信息----------------");
		switch (message.getAct()) {
		
		case LEADER:
			
			log.info("message:"+message.getResponse());
//			Response response = (Response) message.getResponse();
			SessionWriter sessionWriter = (SessionWriter) session.getAttribute("cap_session_writer");
			if (sessionWriter == null) {
				sessionWriter = new SessionWriter();
			}
			sessionWriter.add(message.getResponse());
			session.setAttribute("cap_session_writer", sessionWriter);
			break;
			
		case LEADER_ZHUCE:
			Response response1 = (Response) message.getResponse();
			SessionWriter sessionWriter1 = (SessionWriter) session.getAttribute("cap_session_writer");
			if (sessionWriter1 == null) {
				sessionWriter1 = new SessionWriter();
			}
			if(response1 == null){
				response1 = new Response("12");
			}
			sessionWriter1.add(response1.tobytes1());
			session.setAttribute("cap_session_writer", sessionWriter1);
			break;
		
		case LEADER_XINTIAO:
			Response response2 = (Response) message.getResponse();
			SessionWriter sessionWriter2 = (SessionWriter) session.getAttribute("cap_session_writer");
			if (sessionWriter2 == null) {
				sessionWriter2 = new SessionWriter();
			}
			
			if(response2 == null){
				response2 = new Response("12");
			}
			sessionWriter2.add(response2.tobytes3());
			session.setAttribute("cap_session_writer", sessionWriter2);
			break;
			
		default:
			break;
		}
	}
}
