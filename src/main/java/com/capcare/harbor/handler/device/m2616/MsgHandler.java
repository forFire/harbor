package com.capcare.harbor.handler.device.m2616;

import javax.annotation.Resource;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capcare.harbor.handler.MessageHandler;
import com.capcare.harbor.handler.SessionWriter;
import com.capcare.harbor.handler.TcrIoHandler;
import com.capcare.harbor.model.Device;
import com.capcare.harbor.service.cache.DeviceCache;
import com.capcare.harbor.util.Helper;
import com.capcare.harbor.vo.BaseMessage;

public class MsgHandler extends MessageHandler {

	@Resource
	private DeviceCache deviceCache;
	
	private static Logger logger = LoggerFactory.getLogger(MsgHandler.class);
	
	private static final Long fee_check_interval = Long.valueOf(Helper
			.get("fee_check_interval"));
	
	public void afterProcess(BaseMessage act, IoSession session) {

		// 设备类型
		Object dtype = session.getAttribute("type");
		int type = 0;
		if (dtype != null) {
			type = (Integer) dtype;
		}
		logger.info("------type--------"+type+"act.getAct()===>"+act.getAct());
		
		//除去"人"外的所有其他设备
		switch (act.getAct()) {
		case ALARM:
			String downStr = null;
			downStr = M2616Encoder.getClearAlarmCmd();
			
			logger.info("------downStr--------"+downStr);
			
			if (downStr != null && !"".equals(downStr)) {
				SessionWriter sessionWriter = (SessionWriter) session.getAttribute("cap_session_writer");
				logger.info("------sessionWriter--------"+sessionWriter);
				if (sessionWriter == null) {
					sessionWriter = new SessionWriter();
				}
				sessionWriter.add(downStr);
				session.setAttribute("cap_session_writer", sessionWriter);
			}else if(type == 3){
				session.write("OK");
			}
			break;
		case POSITION:

			SessionWriter sessionWriter = (SessionWriter) session.getAttribute("cap_session_writer");
			if (sessionWriter == null) {
				sessionWriter = new SessionWriter();
			}
			session.setAttribute("cap_session_writer", sessionWriter);
			
			
			if(type == 3){
				session.write("OK");
			}
			//sendFeeCheckCmd(session);
			break;
		default:
			break;
		}
	}
	
	private void sendFeeCheckCmd(IoSession session){
		String deviceSn = (String)session.getAttribute("duid");
		if(deviceSn == null){
			return;
		}
		
		Device device = deviceCache.getDevice(deviceSn);
		
		/*if(device == null || 
				device.getEnableFeeCheck() == null ||device.getEnableFeeCheck() != 1 || 
				device.getFeeCheckCmd() == null || "".equals(device.getFeeCheckCmd()) || 
				device.getFeeCheckNo() == null || "".equals(device.getFeeCheckNo())){
			return;
		}*/

		Object last_send_time = session.getAttribute("fee_check_cmd_last_send_time");
		if(last_send_time == null){
			last_send_time = deviceCache.getFeeSmsTime(deviceSn);
			session.setAttribute("fee_check_cmd_last_send_time", last_send_time);
		}
		
		long currtime = System.currentTimeMillis();
		//未发过查询短信，或者间隔大于3天
		if(last_send_time == null || (currtime - (Long)last_send_time)>fee_check_interval){
			
			String msg = null;
			/*if(device.getType() == 1 || device.getType() == 3){
				msg = "#360#"+device.getFeeCheckNo()+"#"+device.getFeeCheckCmd()+"#0000##";
			}else{
				msg = "*HQ,"+deviceSn+",360,"+device.getFeeCheckNo()+","+device.getFeeCheckCmd()+",0000#";
			}*/
			SessionWriter sessionWriter = (SessionWriter) session.getAttribute("cap_session_writer");
			if (sessionWriter == null) {
				sessionWriter = new SessionWriter();
			}
			sessionWriter.add(msg);
			session.setAttribute("cap_session_writer", sessionWriter);
			
			session.setAttribute("fee_check_cmd_last_send_time", currtime);
			deviceCache.setFeeSmsTime(deviceSn, currtime);
		}
	}

}
