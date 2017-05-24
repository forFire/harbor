package com.capcare.harbor.handler.device.beidou.messageSentJob;

import java.io.UnsupportedEncodingException;

import javax.annotation.Resource;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capcare.harbor.handler.SessionWriter;
import com.capcare.harbor.handler.device.beidou.BeidouEncoder;
import com.capcare.harbor.handler.device.m2616.util.DecoderKey;
import com.capcare.harbor.model.ShortMessage;
import com.capcare.harbor.service.logic.ShortMessageService;


public class MessageSentTask {
	
	
	public static IoSession session;
	
	private static Logger logger = LoggerFactory.getLogger(MessageSentTask.class);
	
	@Resource
	private ShortMessageService messageService;
	
	@Resource
	private IoAcceptor ioAcceptor;
	
	public void doWork() throws UnsupportedEncodingException{
		
		SessionWriter normalQueue = ShortMessageService.writerMap.get("normalQueue");
	
		if(session != null){
//			if (normalQueue != null) {
//			//poll一条信息的策略
//			 Object msg = normalQueue.poll();
//				if (msg != null) {
//					ShortMessage message = (ShortMessage)msg;
//					logger.info("发送 = " + message.toString() + " = 到指挥机！");
//					StringBuffer sbf = new StringBuffer ();
//			        sbf.append (DecoderKey.BEIDOU_BEGIN).append (BeidouEncoder.ENCODER_STR).append (DecoderKey.BEIDOU_BEGIN)
//			                .append ("S").append (DecoderKey.BEIDOU_BEGIN).append (message.getReceiveHandsetId()).append (DecoderKey.BEIDOU_BEGIN)
//			        		.append (message.getSendContent());//需要根据返回的不同短报文类型做判断
//					session.write(sbf.toString().getBytes("GB2312"));
//					logger.info("message send,reseive handset is:" + message.getReceiveHandsetId());
//					//改变短消息在数据库中的标志位
//					ShortMessage targetMsg = messageService.findNotSendSuccessById(message.getId());
//					if(targetMsg != null){
//						targetMsg.setStatus(1);
//						messageService.saveOrUpdate(targetMsg);
//					}
//				}
//			}
		}
	}
	
	/*
	 * 优先级 sosQueue > pinganQueue > normalQueue
	 */
	public Object getMsg(SessionWriter sosQueue,SessionWriter pinganQueue,SessionWriter normalQueue){
		
		Object msg = null;
		if(sosQueue.size() > 0){
			msg = sosQueue.poll();
		}else if(pinganQueue.size() > 0){
			msg = pinganQueue.poll();
		}else if(normalQueue.size() > 0){
			msg = normalQueue.poll();
		}
		return msg;
	}
}
