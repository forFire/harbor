package com.capcare.harbor;



import javax.jms.JMSException;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.capcare.harbor.handler.WebSocketServer;
import com.capcare.harbor.util.Helper;

public class StartDeviceHarbor {

	
	
	private static final Object obj = new Object();
	private static StartDeviceHarbor harbor;

	private ClassPathXmlApplicationContext context = null;
	private Logger logger = LoggerFactory.getLogger(StartDeviceHarbor.class);

	public static StartDeviceHarbor getInstance() {

		synchronized (obj) {
			if (harbor == null)
				harbor = new StartDeviceHarbor();
		}
		return harbor;
	}

	public StartDeviceHarbor() {

		String config = "context.xml";
		String[] paths = new String[] { config };
		context = new ClassPathXmlApplicationContext(paths);
	}

	public ClassPathXmlApplicationContext getContext() {

		return context;
	}

	public void start() {
		NioSocketAcceptor acceptor = (NioSocketAcceptor) context
				.getBean("ioAcceptor");
		acceptor.getSessionConfig().setReadBufferSize(1024 * 1024);// 发送缓冲区1M
		acceptor.getSessionConfig().setReceiveBufferSize(1024 * 1024);// 接收缓冲区1M
		startWebSocket();
		connectMQ();
		// connectRDAgent ();
		bindSocket(acceptor);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				if (context != null) {
					context.close();
				}
			}
		});

	}

	/**
	 * 采集设备所有ip 端口号放入缓存中 改成新增设备的时候 放入缓存
	 */
	// @Deprecated
	// private void findDeviceIpPort() {
	// DictEquipmentService dictEquipmentService = (DictEquipmentService)
	// context.getBean("dictEquipmentService");
	// DeviceIpCache deviceIpCache = (DeviceIpCache)
	// context.getBean("deviceIpCache");
	//
	// List<DictEquipment> list = dictEquipmentService.findAll();
	// for (DictEquipment d : list) {
	// deviceIpCache.addDeviceIp(d.getCode(), d.getIp() + ":" + d.getPort());
	// }
	//
	// }

	private void connectMQ() {

		ActiveMQQueue mq = (ActiveMQQueue) context
				.getBean("upload_from_device");
		try {
			logger.info("Harbor send mq:" + mq.getQueueName());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			logger.error("[StartHarbor for mq ] : ", e);
		}

	}

	// private void connectRDAgent () {
	//
	// BeidouAgentIoHandle bean = this.context.getBean
	// (BeidouAgentIoHandle.class);
	// bean.connect ();
	// }

	private void bindSocket(NioSocketAcceptor acceptor) {

		try {
			acceptor.bind();
			logger.info("Harbor start at:"
					+ acceptor.getLocalAddress().getPort());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("[StartHarbor for bind niosocket] : ", e);
		}
	}

	// 启动websocket
	public void startWebSocket() {
		new Thread() {
			@Override
			public void run() {
				try {
					//System.out.println("-----------mina------start");
					logger.info("-----------mina------start");
					WebSocketServer webSocket = (WebSocketServer) context
							.getBean("webSocketServer");
					webSocket.run(Integer.parseInt(Helper.get("ws.port").toString()));
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	public static void main(String[] args) {

		StartDeviceHarbor instance = StartDeviceHarbor.getInstance();
		instance.start();
	}
}
