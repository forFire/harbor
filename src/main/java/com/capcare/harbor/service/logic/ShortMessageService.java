package com.capcare.harbor.service.logic;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.capcare.harbor.dao.ShortMessageDao;
import com.capcare.harbor.dao.SpotDao;
import com.capcare.harbor.handler.SessionWriter;
import com.capcare.harbor.handler.device.beidou.decoderManage.BeidouDecoderShortMessageType;
import com.capcare.harbor.model.Chat;
import com.capcare.harbor.model.ShortMessage;
import com.capcare.harbor.protocol.Position;
import com.capcare.harbor.service.cache.PositionCache;
import com.capcare.harbor.util.JobUtils;
import com.capcare.harbor.vo.BaseMessage;

@Component
public class ShortMessageService implements DataService{
	
	private static Logger logger = LoggerFactory.getLogger(ShortMessageService.class);
	
	public static Map<String ,SessionWriter>  writerMap = new HashMap<String,SessionWriter>();

	@Resource
	private JmsTemplate jmsTemplate;
	@Resource
	private ShortMessageDao shortMessageDao;
	@Resource
	private SpotDao spotDao;
	@Resource
	private PositionCache positionCache;
	
	
	
	
	public void save(ShortMessage shortMessage) {
		shortMessageDao.save(shortMessage);
	}

	public ShortMessage findById(long id) {
		return shortMessageDao.get(id);
	}

	public void delete(long id) {
		shortMessageDao.delete(id);
	}

	public List<ShortMessage> findNotSendSuccess(String receiveHandsetId) {
		List<ShortMessage> notSendSuccesses = shortMessageDao.findNotSendSuccess(receiveHandsetId);
		return notSendSuccesses;
	}
	
	public ShortMessage findNotSendSuccessById(Long messageId){
		List<ShortMessage> smgList = shortMessageDao.findNotSendSuccessById(messageId);
		ShortMessage smg = null;
		if(smgList.size() > 0){
			smg = shortMessageDao.findNotSendSuccessById(messageId).get(0);
		}
		return smg;
	}
	
	public void putMessages(){
		List<ShortMessage> notSendMessages = shortMessageDao.findAllNotSendMessages();
		SessionWriter normalQueue = new SessionWriter();
		normalQueue.addAll(notSendMessages);
		writerMap.put("normalQueue", normalQueue);
	}
	
	/*
	 * 将要发送的消息按优先级放到队列中  sos报警 3，平安报 2，普通短消息 1
	 */
	public void putAppointMessages(int priority){
		List<ShortMessage> notSendMessages = shortMessageDao.findNotSendAppointMessages(priority);
		SessionWriter normalQueue = new SessionWriter();
		SessionWriter pinganQueue = new SessionWriter();
		SessionWriter sosQueue = new SessionWriter();
		switch(priority){
		case 3:
			sosQueue.addAll(notSendMessages);
			writerMap.put("sosQueue", sosQueue);
			break;
		case 2:
			pinganQueue.addAll(notSendMessages);
			writerMap.put("pinganQueue", pinganQueue);
			break;
		case 1:
			normalQueue.addAll(notSendMessages);
			writerMap.put("normalQueue", normalQueue);
			break;
		}
		
	}
	
	

	@Override
	public void save(BaseMessage message) {
		ShortMessage shortMessage = (ShortMessage) message.getBusinessData();
		if(shortMessage.getSendContent().length() > 0){//增加短报文长度限制
//			try {
//				byte[] by = shortMessage.getSendContent().getBytes("GB2312");
//				if(BytesConvert.encodeHexStr(by).length() > 70){
//					logger.info(BytesConvert.encodeHexStr(by));
//					logger.info("短报文内容超过长度限制，不发送", BytesConvert.encodeHexStr(by));
//					return;
//				}
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
		}
		sendJMSofShortMessage(shortMessage);
	}

	private void sendJMSofShortMessage(ShortMessage shortMessage) {
		//如果是北斗to北斗，不发到
		if(shortMessage.getType() ==3){
			return;
		}
		jmsTemplate.send(shortMessage);
	}

    public void saveOrUpdate (com.capcare.harbor.model.ShortMessage shortMessage) {
        shortMessageDao.update(shortMessage);
    }
    
    //处理周边查询
    public void searchAround(String sendId,String receiveId,String[] arr){
    	
    	if(arr.length >= 3){
    		String latLng = arr[1];
    		String range = arr[2];
    		Double[] doubleArr = JobUtils.getLatLngArr(latLng);
    		Double lng = doubleArr[0];
    		Double lat = doubleArr[1];
    		
    		List<Object> smList = spotDao.findbyDistinctId();
    		if(smList != null){
    			for(int i=0;i<smList.size();i++){
    				Position position = positionCache.getPosition((String)smList.get(i));
    				if(position!=null){
    					Double distance = JobUtils.getDistance(lat, lng, position.getLat(), position.getLng());
    					//如果计算出的距离小于传入的查询范围，则生成短报文放入表中
    					if(distance <= Double.parseDouble(range)){
    						ShortMessage shortMessage = new ShortMessage();
    						shortMessage.setReceiveHandsetId(sendId);
    						shortMessage.setSendHandsetId(receiveId);
    						shortMessage.setSendContent(BeidouDecoderShortMessageType.chazhoubianShortMessage
    								+ "#(" + position.getLng() + "," + position.getLat() + ")#" + position.getDeviceSn());
    						shortMessage.setType(1);
    						shortMessage.setStatus(0);
    						shortMessage.setSendDate(new Date());
    						this.saveOrUpdate(shortMessage);
    					}
    				}
    			}
    		}
    		
    	}
    }
    
    //处理平安报或sos
    public void dealUrgentMessages(String sendId,String receiveId,String[] arr, int priority){
    	String mq = "from_device_harbor_rd_msg";
    	if(arr.length >= 4){
//    		String latLngStr = arr[1];
    		String phones = arr[2];
    		String content = arr[3];
    		String[] phoneArr = phones.split("\\+");
    		if(phoneArr.length > 0){
    			for(int i=0;i<phoneArr.length;i++){
    				ShortMessage smess = new ShortMessage();
    				smess.setSendHandsetId(sendId);
    				smess.setReceiveHandsetId(phoneArr[i]);
    				smess.setSendContent(content);
    				smess.setSendDate(new Date());
    				smess.setStatus(1);
    				smess.setType(2);
    				smess.setPriority(priority);
    				this.saveOrUpdate(smess);
    				//紧急短报文，不存库，存入mq
    				Chat chat = new Chat();
    				chat.setUserId(sendId);
    				chat.setFriendId(receiveId);
    				chat.setTime(new Date());
    				chat.setContent(arr[3]);
    				jmsTemplate.send(mq, chat);
    			}
    		}
    		
    	}else{
    		logger.info("协议格式错误！");
    	}
    }
    
}
