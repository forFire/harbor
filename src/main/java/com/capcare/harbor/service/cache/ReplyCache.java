package com.capcare.harbor.service.cache;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.capcare.harbor.vo.BaseMessage;

/**
 * 下发命令返回值,缓存数据
 * 
 *
 */
@Component
public class ReplyCache {
	private Logger logger = LoggerFactory.getLogger(ReplyCache.class);
	private Map<String, List<Channel>> channelMap = new HashMap<String, List<Channel>>();
	private ReentrantLock lock = new ReentrantLock();
	private ReentrantLock msglock = new ReentrantLock();
	private Condition cond;
	private Condition msgcond;

	private ConcurrentLinkedQueue<BaseMessage> replyQueue = new ConcurrentLinkedQueue<BaseMessage>();
	private ConcurrentLinkedQueue<String[]> msgQueue = new ConcurrentLinkedQueue<String[]>();
	private Map<Channel, String> channelToImei = new HashMap<Channel, String>();

	public ReplyCache() {
		cond = lock.newCondition();
		msgcond = msglock.newCondition();
		start();
	}

	private void start() {
		// 下发指令的消息 打印到前端
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					while (replyQueue.isEmpty()) {
						try {
							lock.lock();
							cond.await(2000, TimeUnit.MILLISECONDS);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							lock.unlock();
						}
					}
					while (!replyQueue.isEmpty()) {
						BaseMessage baseMessage = replyQueue.poll();
						if (baseMessage != null) {
							try {
								List<Channel> channels = channelMap.get(baseMessage.getLoginDevice().getSn());
								if (channels != null) {
									for (Channel channel : channels) {
										if (channel != null) {
											if(channel.isActive()){
												channel.writeAndFlush(new TextWebSocketFrame("1--> "+baseMessage.toString()));
											}else{
												channels.remove(channel);
												channelToImei.remove(channel);
												if(channels.isEmpty()){
													channelMap.remove(baseMessage.getLoginDevice().getSn());
												}
											}
										}
									}
								}
							} catch (Exception e) {
							}
						}
					}
				}
			}
		}).start();

		// 实时日志 消息打印到前端线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					while (msgQueue.isEmpty()) {
						try {
							msglock.lock();
							msgcond.await(2000, TimeUnit.MILLISECONDS);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							msglock.unlock();
						}
					}
					while (!msgQueue.isEmpty()) {
						String[] msgs = msgQueue.poll();
						if (msgs != null) {
							try {
								List<Channel> channels = channelMap.get(msgs[0]);
								if (channels != null) {
									for (Channel channel : channels) {
										if (channel != null) {
											if(channel.isActive()){
												channel.writeAndFlush(new TextWebSocketFrame("2--> "+msgs[1]));
											}else{
												channels.remove(channel);
												channelToImei.remove(channel);
												if(channels.isEmpty()){
													channelMap.remove(msgs[0]);
												}
											}
										}
									}
								}
							} catch (Exception e) {
							}
						}
					}
				}
			}
		}).start();
	}

	/**
	 * 下发指令的回复消息
	 * 
	 * @param message
	 */
	public void addReplyMsg(BaseMessage message) {
		try {
			String sn = message.getLoginDevice().getSn();
			if (containtDevice(sn)) {
				replyQueue.add(message);
			}

			try {
				lock.lock();
				cond.signal();
			} finally {
				lock.unlock();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 实时日志消息
	 * 
	 * @param imei
	 * @param msg
	 */
	public void addMsg(String imei, String msg) {
		if (containtDevice(imei)) {
			try {
				String[] msgstr = new String[2];
				msgstr[0] = imei;
				msgstr[1] = msg;
				msgQueue.add(msgstr);
				try {
					msglock.lock();
					msgcond.signal();
				} finally {
					msglock.unlock();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addChannel(String imei, Channel c) {
		List<Channel> list = channelMap.get(imei);
		if (list == null) {
			list = new ArrayList<Channel>();
			channelMap.put(imei, list);
		}
		if (!list.contains(c)) {
			list.add(c);
			channelToImei.put(c, imei);
			logger.info("add channel imei:" + imei + ",channel id :" + c.toString() + ",size:" + list.size());
		}
	}

	public void removeChannel(Channel c) {
		String imei = channelToImei.get(c);
		channelToImei.remove(c);
		if (imei != null) {
			List<Channel> list = channelMap.get(imei);
			if (list != null) {
				list.remove(c);
				if (list.isEmpty()) {
					channelMap.remove(imei);
				}
			}
		}
	}

	private boolean containtDevice(String deviceSn) {
		return channelMap.containsKey(deviceSn);
	}
}
