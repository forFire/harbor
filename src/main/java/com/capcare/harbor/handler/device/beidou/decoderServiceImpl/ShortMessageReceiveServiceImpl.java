package com.capcare.harbor.handler.device.beidou.decoderServiceImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import module.util.Tools;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.capcare.harbor.handler.device.beidou.Beidou;
import com.capcare.harbor.handler.device.beidou.decoderManage.BeidouDecoderShortMessageType;
import com.capcare.harbor.handler.device.beidou.decoderManage.BeidouDecoderType;
import com.capcare.harbor.handler.device.beidou.decoderService.BeidouDecoderService;
import com.capcare.harbor.model.ShortMessage;
import com.capcare.harbor.service.logic.ShortMessageService;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.LoginDevice;
@Component
@Scope("singleton")
public class ShortMessageReceiveServiceImpl implements BeidouDecoderService {

	private Logger log = LoggerFactory.getLogger(getClass());

	private final int MESSAGE_SIZE = 4;
	
	private final int receiveHansetId = 1;
	
	private final int sendHansetId = 2;
	
	private final int sendContent = 3;

	private final BeidouDecoderType type = BeidouDecoderType.receive_messages;
	
	@Resource
	private ShortMessageService shortMessageService;

	@Override
	public List<BaseMessage> getBaseMessage(String[] str, IoSession session) {
		log.info("receive message : {}", str);
		List<BaseMessage> ls = new ArrayList<BaseMessage>();
		if (str.length == MESSAGE_SIZE) {
			
			BaseMessage baseMessage = this.toBaseMessage(str,session);
			if (Tools.checkNotNull(baseMessage)){
				ls.add(baseMessage);
				//添加报警短报文过滤
				ShortMessage msgs = (ShortMessage)baseMessage.getBusinessData();
				String content = msgs.getSendContent();
				String sendId = msgs.getSendHandsetId();
				String receiveId = msgs.getReceiveHandsetId();
				//增加分类型处理
				if(!"".equals(content) && content != null){
					String arr[] = content.split("#");
					if(arr.length > 1){
						if(arr[0].equals(BeidouDecoderShortMessageType.chazhoubianShortMessage)){
							//处理周边查询
							shortMessageService.searchAround(sendId,receiveId,arr);
						}
						if(arr[0].equals(BeidouDecoderShortMessageType.pinganShortMessage)){
							//处理平安报和sos,可以区分优先级
							shortMessageService.dealUrgentMessages(sendId,receiveId,arr,2);
						}
						if(arr[0].equals(BeidouDecoderShortMessageType.baojingShortMessage)){
							//处理平安报和sos,可以区分优先级
							shortMessageService.dealUrgentMessages(sendId,receiveId,arr,3);
						}
					}
				}
			}
		}
		return ls;
	}

	private BaseMessage toBaseMessage(String[] str,IoSession session) {
		Object businessData = null;
		BaseMessage msg = new BaseMessage(Act.SENT_MESSAGE,businessData);
		ShortMessage shortMessage = new ShortMessage();
		shortMessage.setReceiveHandsetId(str[receiveHansetId]);
		shortMessage.setSendContent(str[sendContent]);
		shortMessage.setSendHandsetId(str[sendHansetId]);
		shortMessage.setStatus(ShortMessage.DEFAULT_SEND_SUCCESS_STATUS);
		//设置发送时间
		shortMessage.setSendDate(new Date());
		//判断短消息类型
		if(str[sendHansetId].length() == 6 && str[receiveHansetId].length() == 6){
			shortMessage.setType(1);
			if(str[sendContent].startsWith(BeidouDecoderShortMessageType.chazhoubianShortMessage)){
				if(str[sendHansetId] == null){
					shortMessage.setStatus(0);
				}
				shortMessage.setStatus(1);
			}else{
				shortMessage.setStatus(1);
			}
		}
		if(str[sendHansetId].length() == 6 &&str[receiveHansetId].length() == 11){
			shortMessage.setType(2);
		}
		if(str[sendHansetId].length() == 11 &&str[receiveHansetId].length() == 6){
			shortMessage.setType(3);
		}
		LoginDevice loginDevice = new LoginDevice ();
        loginDevice.setDt (new Beidou ());
//        loginDevice.setSn (session.getAttribute("duid").toString());
        loginDevice.setSn (shortMessage.getReceiveHandsetId());
        msg.setLoginDevice (loginDevice);
		msg.setBusinessData(shortMessage);
		return msg;
	}
	

	@Override
	public BeidouDecoderType getDecoderType() {
		return type;
	}
	
}
