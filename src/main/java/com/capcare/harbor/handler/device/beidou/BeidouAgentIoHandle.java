package com.capcare.harbor.handler.device.beidou;

import java.net.InetSocketAddress;

import javax.annotation.Resource;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.TcrCodecFactory;
import com.capcare.harbor.handler.TcrIoHandler;
import com.capcare.harbor.handler.device.beidou.messageSentJob.MessageSentTask;
import com.capcare.harbor.handler.device.m2616.util.DecoderKey;

@Component
public class BeidouAgentIoHandle extends TcrIoHandler {

    private Logger             log      = LoggerFactory.getLogger (getClass ());

    private NioSocketConnector connector;
//    private String             hostname = "192.168.3.191";
    private String             hostname = "127.0.0.1";
//    private int                port     = 8860;
    private int                port     = 60000;
    
    
    private String             DUID     = "161865";

    @Resource
    TcrCodecFactory            tcrCodecFactory;

    public BeidouAgentIoHandle () {

        log.info ("client");
        connector = new NioSocketConnector ();
        connector.setHandler (this);

    }

    public void connect () {

        DefaultIoFilterChainBuilder chain = connector.getFilterChain ();
        chain.addLast ("logger", new LoggingFilter ());
        chain.addLast ("codec", new ProtocolCodecFilter (tcrCodecFactory));
        connector.connect (new InetSocketAddress (hostname, port));

    }

    public void sessionIdle (IoSession session, IdleStatus status) {
    	MessageSentTask.session = session;
        super.sessionIdle (session, status);
        session.setAttribute ("duid", DUID);
        String heartbeat_userData = DecoderKey.BEIDOU_BEGIN + "BDDHR" + DecoderKey.BEIDOU_BEGIN + "U";
        String heartbeat_status = DecoderKey.BEIDOU_BEGIN + "BDDHR" + DecoderKey.BEIDOU_BEGIN + "B";
        String heartbeat_GPS = DecoderKey.BEIDOU_BEGIN + "BDDHR" + DecoderKey.BEIDOU_BEGIN + "G";
        String heartbeat = DecoderKey.BEIDOU_BEGIN + "BDDHR" + DecoderKey.BEIDOU_BEGIN + "B";
        
//        if (status == IdleStatus.READER_IDLE) {
            //            log.info ("heartbeat_userData : {}", heartbeat_userData);
            //            log.info ("heartbeat_status : {}", heartbeat_status);
            //            log.info ("heartbeat_GPS : {}", heartbeat_GPS);
//            暂时不要求指挥机上传数据
//            session.write (heartbeat_userData);
//            session.write (heartbeat_status);
//            session.write (heartbeat_GPS);
//        	session.write ("@ConTest");
//        	session.write (heartbeat);
//        }
        
    }

}
