/**
 * 
 */
package com.capcare.harbor.service.back;

import javax.annotation.Resource;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.SessionManager;
import com.capcare.harbor.handler.SessionWriter;
import com.capcare.harbor.service.logic.InstructService;

/**
 * @author fss
 * 
 */
@Component
public class JmsConsumer implements MessageListener {

	@Resource
	private SessionManager sessionManager;

	@Resource
	private InstructService instructService;

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			TextMessage msg = (TextMessage) message;
			try {
				String text = msg.getText();
				if (text != null && !"".equals(text)) {
					String[] str = text.split(",");
					IoSession session = sessionManager.getSession(str[0]);
					if (session == null) {
						instructService.sendReplyMessage2Back("设备"+str[0]+"不在线，请稍后再试");
						return;
					}
					SessionWriter sessionWriter = (SessionWriter) session
							.getAttribute("cap_session_writer");
					if (sessionWriter == null) {
						sessionWriter = new SessionWriter();
					}
					sessionWriter.add(str[1]);
					session.setAttribute("cap_session_writer", sessionWriter);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
