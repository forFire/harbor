package com.capcare.harbor.handler.device.leader6907;

import java.net.InetSocketAddress;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.demux.DemuxingProtocolCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.capcare.harbor.service.cache.DeviceIpCache;
import com.capcare.harbor.service.cache.RoomLastTimeCache;


/**
 * 此设备由于不稳定，容易丢数据，暂未投入使用
 */
@Deprecated
@Component
public class MainExecutor implements Runnable {

	// private static Logger logger =
	// LoggerFactory.getLogger(MainExecutor.class);

	@Autowired
	private TaskExecutor taskExecutor;

	@Autowired
	RoomLastTimeCache roomLastTimeCache;
	@Resource
	private JmsTemplate jmsTemplate;
	@Autowired
	private LeaderClientHander leaderClientHander;
	@Autowired
	DeviceIpCache deviceIpCache;

	public MainExecutor() {
	}

	@Override
	public void run() {

//		// 1从缓存中读取设备ip和port列表 遍历
//		@SuppressWarnings("unchecked")
//		Map<String, String> map = (Map<String, String>) deviceIpCache.getAll();
//		if (map != null) {
//			for (String key : map.keySet()) {
//				// 2 放入线程池
//				taskExecutor.execute(new MessagePrinterTask(map.get(key)));
//			}
//		}

	}

//	private class MessagePrinterTask implements Runnable {
//
//		private String ip;
//		private String port;
//
//		public MessagePrinterTask(String str) {
//			if (str != null && str.contains(":")) {
//				this.ip = str.split(":")[0];
//				this.port = str.split(":")[1];
//			}
//		}

		// 3执行线程
//		public void run() {
//			// 4服务器端轮训客户端
//			if (ip != null && port != null) {
//				callClient(ip, port);
//			}
//		}
//	}

	// 5轮训客户端
//	private void callClient(String ip, String port) {
//
//		System.out.println("ip====>" + ip + "port=====>" + port);
//
//		final IoConnector connector = new NioSocketConnector();
//		DemuxingProtocolCodecFactory pcf = new DemuxingProtocolCodecFactory();
//		// 自定义编码器
//		pcf.addMessageEncoder(String.class, new MessEncoder());
//		// 自定义解码器
//		pcf.addMessageDecoder(new MessDecoder());
//		ProtocolCodecFilter codec = new ProtocolCodecFilter(pcf);
//		connector.getFilterChain().addLast("codec", codec);// 指定编码过滤器
//		connector.setHandler(leaderClientHander);
//		ConnectFuture connectFuture = connector.connect(new InetSocketAddress(ip, Integer.parseInt(port)));
//
//		// 等待建立连接
//		connectFuture.awaitUninterruptibly();
//		
//		if  (connectFuture.isDone()) {      
//            
//			if  (!connectFuture.isConnected()) {  //若在指定时间内没连接成功，则抛出异常       
//                
//				connector.dispose();    //不关闭的话会运行一段时间后抛出，too many open files异常，导致无法连接       
//                
//           }     
//		}
//		
//
//	}

	/**
	 * 补齐长度
	 * 
	 * @param value
	 * @param size
	 * @return
	 */
//	public static String toString(String value, int size) {
//		// String s = "00000000000000000000000000000000000000000000000000" +
//		// value;
//		// return s.substring(s.length() - 8, s.length());
//		// value ="233123000";
//		while (value.length() < size) {
//			value = "0" + value;
//		}
//
//		if (value.length() > size) {
//			value = value.substring(0, size);
//		}
//		return value;
//	}

//	public TaskExecutor getTaskExecutor() {
//		return taskExecutor;
//	}
//
//	public void setTaskExecutor(TaskExecutor taskExecutor) {
//		this.taskExecutor = taskExecutor;
//	}
//
//	public DeviceIpCache getDeviceIpCache() {
//		return deviceIpCache;
//	}
//
//	public void setDeviceIpCache(DeviceIpCache deviceIpCache) {
//		this.deviceIpCache = deviceIpCache;
//	}

}