package com.capcare.harbor.handler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.DeviceType;
import com.capcare.harbor.service.cache.DeviceCache;
import com.capcare.harbor.service.cache.PositionCache;
import com.capcare.harbor.service.cache.ReplyCache;
import com.capcare.harbor.service.logic.AlarmService;
import com.capcare.harbor.service.logic.DataService;
import com.capcare.harbor.service.logic.FireAlarmService;
import com.capcare.harbor.service.logic.InstructService;
import com.capcare.harbor.service.logic.PositionService;
import com.capcare.harbor.service.logic.ShortMessageService;
import com.capcare.harbor.util.Helper;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.DeviceReply;

@Component
public class TcrIoHandler implements IoHandler {

	private static Logger logger = LoggerFactory.getLogger(TcrIoHandler.class);

	public static final Long offline_time_car = Long.valueOf(Helper
			.get("offline_time_car"));
	public static final Long offline_time_person = Long.valueOf(Helper
			.get("offline_time_person"));

	@Resource
	private SessionManager sessionManager;

	@Resource
	private PositionService positionService;

	@Resource
	private InstructService instructService;
	
	@Resource
	private FireAlarmService fireAlarmService;

	@Resource
    private AlarmService   alarmService;
	
	@Resource
	private PositionCache positionCache;

	@Resource(name = "msgHandler")
	private Map<String, MessageHandler> msgHandler;

	@Resource(name = "dataService")
	private Map<String, DataService> dataService;
	
	@Resource
	private ShortMessageService messageService;
	
	@Resource
	private DeviceCache deviceCache;
	
	@Resource
	private ReplyCache replyCache;
	
	@Override
	public void messageReceived(IoSession session, Object message) {

		if (message instanceof BaseMessage) {
			BaseMessage act = (BaseMessage) message;
//			logger.info("Received:" + act.toString());
			// 不同协议自己处理部分
			DeviceType deviceType = act.getLoginDevice().getDt();
			String deviceSn = act.getLoginDevice().getSn();

			MessageHandler mh = msgHandler.get(deviceType.getCode());
			if (mh != null) {
				mh.preProcess(act, session);
			}
			try {
				replyCache.addMsg(deviceSn, act.toString());
			} catch (Exception e) {
				logger.error("web socket send msg error:"+e.toString());
			}
			
			session.setAttribute("last_received", System.currentTimeMillis());// 收到数据的最后时间

//			logger.info("收到数据的最后时间===========>" + System.currentTimeMillis());
			
			//屏蔽设备登录验证
			if (deviceSn != null && !"".equals(deviceSn)) {
				sessionManager.checkin(act.getLoginDevice().getSn(), session);
			}

//			logger.info("act.getAct()============>" + act.getAct());
			
			switch (act.getAct()) {
			
				case REPLY:
					DeviceReply deviceReply = (DeviceReply) act.getBusinessData();
					instructService.dictateConfirm(deviceSn,
							deviceReply.isSuccess(), deviceReply.getInstructType());
					replyCache.addReplyMsg(act);
					break;
	
				case CONFIG_REPLY:
					String msg = (String) act.getBusinessData();
					instructService.sendReplyMessage2Back(msg);
					replyCache.addReplyMsg(act);
					break;
					
				default:
					
					DataService ds = dataService.get(act.getAct().name());
					if (ds != null) {
						ds.save(act);
					} else {
						
						logger.error("不支持的业务类型:" + act.getAct().name());
					}
				break;
			}
			
			
			/*
			 * 第四步：封装返回的消息
			 */
			
			//信息处理完成之后返回主机信息
			if (mh != null) {
				mh.afterProcess(act, session);
			}
			
			SessionWriter sessionWriter = (SessionWriter) session.getAttribute("cap_session_writer");
//			logger.info("sessionWriter==========================>"+sessionWriter);
//			if (sessionWriter == null) {
//				sessionWriter = new SessionWriter();
//			}
//			session.setAttribute("cap_session_writer", sessionWriter);
			
			
			if (deviceCache.getNeedIssue(deviceSn)) {// 授时下发
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Calendar now = Calendar.getInstance();
				now.add(Calendar.HOUR, -8);
				Date date = now.getTime();
				String dateString = formatter.format(date);
				// #705#2016-7-5 23:35:0#8.0#0000##
				StringBuilder content = new StringBuilder();
				content.append("#705");
				content.append("#" + dateString);
				content.append("#8.0#0000##");
				deviceCache.delNeedIssue(deviceSn);
				
				logger.info("授时下发---------------------->"+deviceSn+"内容-------->"+content.toString());
//				session.write(content.toString());
				sessionWriter.add(content.toString());
//				Object o =  sessionWriter.poll();
//				logger.info("o--------------------->"+o);
//				o--------------------->#705#2016-10-13 10:37:42#8.0#0000##
				
			}
			
			//屏蔽原指令下发
			if (deviceSn != null && !"".equals(deviceSn)) {
				if (positionCache.isSetInstruct(deviceSn)) {
					instructService.pushInstruct(deviceSn, session);
				}
			}
			
//			//增加sessionWriter判断处理
//			if(messageService.writerMap.get("rd_writer") == null){
//				messageService.putMessages();
//			}
		} else {
			logger.error("不支持的业务数据:" + message.getClass().getName());
		}
	}

	@Override
	public void sessionClosed(IoSession session) {
		
		sessionManager.checkout(session);
//      屏蔽掉设备离线判断
		logger.info("IP:" + session.getRemoteAddress().toString() + "断开连接");
		
		session.close(true);
//		session.getService().dispose();
		
		
		positionService.deviceOffLine((String) session.getAttribute("duid"));

	}
	
	@SuppressWarnings("unchecked")
	@Override
	// 当一个消息被(IoSession#write)发送出去后调用；
	public void messageSent(IoSession session, Object message) {
		((AbstractIoSession) session).getProcessor().flush(session);
	}

	public void closeConn(IoSession session) {

		// 最后请求时间
		long last_received = (Long) session.getAttribute("last_received");
		long currTime = System.currentTimeMillis();

		// 设备类型
		Object dtype = session.getAttribute("type");
		if (dtype == null) {
			session.close(true);
			return;
		}
		int type = (Integer) dtype;

		long offline_time = offline_time_car;
		switch (type) {
		case 1:
			offline_time = offline_time_car;
			break;
		case 3:
			offline_time = offline_time_person;
			break;
		default:
			;
		}

		// 关闭session
		if ((currTime - last_received) > offline_time) {
			session.close(true);
			//设备离线、
//			String sn = (String)session.getAttribute("duid");
//			Device device = deviceCache.getDevice(sn);
			
		}
	}

	/*
	 * 第五步：发送消息到客户端
	 */
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		
//		logger.info("IP:" + session.getRemoteAddress().toString() + "发送数据");
		
		if (status.equals(IdleStatus.WRITER_IDLE)) {
//			logger.info(session.getId() + ":WRITER IDLE");
			
			SessionWriter sessionWriter = (SessionWriter) session.getAttribute("cap_session_writer");
			
			if (sessionWriter != null) {
				Object msg = sessionWriter.poll();
				
				if (msg != null) {
//					logger.info("发送 = " + msg + " = 到指挥机！");
					session.write(msg);
				}
			}
			return;
		}
//
//		if (status.equals(IdleStatus.BOTH_IDLE)) {
//			String duid = (String) session.getAttribute("duid");
//			logger.info("BOTH_IDLE,sn="+duid);
//			// 关闭连接，并且设置设备下线
//			closeConn(session);
//		}
		
		
//		if(messageService.writerMap.get("normalQueue")==null){
//			messageService.putMessages();
//		}
//		if(messageService.writerMap.get("sosQueue").size() == 0){
//			messageService.putAppointMessages(BeidouDecoderShortMessageType.sosPriority);
//		}
//		if(messageService.writerMap.get("pinganQueue").size() == 0){
//			messageService.putAppointMessages(BeidouDecoderShortMessageType.safePriority);
//		}
//		if(messageService.writerMap.get("normalQueue").size() == 0){
//			messageService.putMessages();
//		}
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) {
		logger.info("-------------exceptionCaught!");
//		session.close(true);
	}

	@Override
	public void sessionCreated(IoSession session) {
		logger.info("IP:" + session.getRemoteAddress().toString() + "创建了连接");
//		logger.info("new session created!");
		session.setAttribute("last_received", System.currentTimeMillis());// 收到数据的最后时间

		// 设置IoSession闲置时间，参数单位是秒
		session.getConfig().setIdleTime(IdleStatus.READER_IDLE, 10);
	}

	@Override
	public void sessionOpened(IoSession session) {
	}

}
