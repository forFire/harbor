package com.capcare.harbor.handler.device.t808;

import org.apache.mina.core.session.IoSession;

import com.capcare.harbor.handler.MessageHandler;
import com.capcare.harbor.handler.SessionWriter;
import com.capcare.harbor.vo.BaseMessage;

public class MsgHandler extends MessageHandler {

    public void afterProcess (BaseMessage message, IoSession session) {

        switch (message.getAct ()) {
        case AUTH:
        case LOGIN:
        case HEART_BEAT:
        case ALARM:
        case OBD:
        case OBD_ALARM:
        default:
            T808Response response = (T808Response) message.getResponse ();

            SessionWriter sessionWriter = (SessionWriter) session.getAttribute ("cap_session_writer");
            if (sessionWriter == null) {
                sessionWriter = new SessionWriter ();
            }
            byte [] tobytes = response.tobytes ();
            if (tobytes == null)
                break;
            sessionWriter.add (tobytes);
            session.setAttribute ("cap_session_writer", sessionWriter);
            break;
        }
    }
}
