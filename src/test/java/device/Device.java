package device;

/**
 * 
 */

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.PatternLayout;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fss
 * 
 */
public class Device {

	private IoSession session;

	private ConnectFuture connFuture;

	private NioSocketConnector connector;

	private Logger logger = LoggerFactory.getLogger(Device.class);

	private Timer timer = null;

	JFrame frame;

	JLabel label1;

	JComboBox combo1;

	JLabel label2;

	JComboBox combo2;

	JButton button1;

	JLabel label3;

	JTextArea text1;

	JLabel label4;

	JTextArea text2;

	JButton button2;

	JCheckBox cbox;

	JTextArea text3;

	JButton button3;
	
	JLabel label5;

	private static JTextArea text4;

	private double startX = 115.246963;

	private double startY = 39.058023;

	private double endX = 116.999140;

	private double endY = 40.488258;

	private int stage = 0;

	private double lng = startX;

	private double lat = startY;

	public void connect() throws Throwable {
		//String HOST = "219.142.87.104";
		//String HOST = "127.0.0.1";
		//String HOST = "211.100.96.3";
		//String HOST = "172.168.2.77";
		
		String HOST = "192.168.169.121";
		int PORT = 60004;

		int CONNECT_TIMEOUT_MILLS = 30000;

		int TIMOUT_CHECK_INTERVAL = 15000;

		connector = new NioSocketConnector();

		// 连接超时设置
		connector.setConnectTimeoutMillis(CONNECT_TIMEOUT_MILLS);
		// 连接超时检查间隔
		connector.setConnectTimeoutCheckInterval(TIMOUT_CHECK_INTERVAL);

		DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();

		filterChain.addLast("codec", new ProtocolCodecFilter(new CodecFactory()));
		// 日志过滤器
		filterChain.addLast("logger", new LoggingFilter());

		TestHandler handler = new TestHandler();
		connector.setHandler(handler);

		while (true) {
			try {
				connFuture = connector.connect(new InetSocketAddress(HOST, PORT));
				connFuture.awaitUninterruptibly();
				session = connFuture.getSession();
				break;
			} catch (RuntimeIoException e) {
				Thread.sleep(30000);
			}
		}

	}

	/**
	 * 发送消息
	 * 
	 * @param msg
	 */
	public void sendMessage(Object msg) {
		if (session != null)
			session.write(msg);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 设置Log4j
		DailyRollingFileAppender appender = new DailyRollingFileAppender();
		appender.setFile("device" + ".log");
		appender.setDatePattern("'.'yyyy-MM-dd");
		appender.setName("ServerAppender");

		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%d{dd MMM yyyy HH:mm:ss,SSS} %-4r [%t] %-5p %c %x - %m%n");
		appender.setLayout(layout);
		appender.activateOptions();

		BasicConfigurator.configure(appender);

		final Device client = new Device();
		try {
			client.connect();
			client.show();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public static void displayMsg(String msg) {
		text4.setText(msg);
		System.out.println("已设置");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void show() {
		frame = new JFrame("设备模拟器");
		Font f = new Font("la", 0, 18);
		label1 = new JLabel("设备:");
		label1.setFont(f);
		combo1 = new JComboBox();
		combo1.addItem("013226008748994-MT90");
		combo1.addItem("013227000048508-MT90");
		combo1.addItem("013226008485167-MT90");
		combo1.addItem("354188046621178-OBD");
		combo1.addItem("354188046621186-OBD");
		combo1.addItem("354188046621187-OBD");
		combo1.addItem("012345678900002-OBD");
		combo1.addItem("012345678900001-OBD");
		combo1.addItem("660105827535166-OBD");
		combo1.addItem("660105827535177-OBD");

		label2 = new JLabel("消息类型：");
		label2.setFont(f);
		combo2 = new JComboBox();
		combo2.addItem("位置");
		combo2.addItem("进围栏报警");
		combo2.addItem("出围栏报警");
		combo2.addItem("超速报警");
		combo2.addItem("低电报警");
		combo2.setSelectedItem("位置");
		label3 = new JLabel("经度：");
		label3.setFont(f);
		text1 = new JTextArea("116.498705", 1, 10);
		text1.setFont(f);
		text1.setLineWrap(false);
		text1.setColumns(10);
		label4 = new JLabel("纬度：");
		label4.setFont(f);
		text2 = new JTextArea("39.980038", 1, 10);
		text2.setFont(f);
		text2.setLineWrap(false);
		button1 = new JButton("send");
		button2 = new JButton("自动模拟");
		cbox = new JCheckBox();
		text3 = new JTextArea("text", 1, 30);
		text3.setFont(f);
		text3.setLineWrap(false);
		button3 = new JButton("发送文本");
		button3.setEnabled(false);
		label5 = new JLabel("收到消息:");
		label5.setFont(f); 
		text4 = new JTextArea("收到...", 1, 30);
		text4.setFont(f);
		text4.setLineWrap(false);
		// 为一般按钮添加动作监听器
		final List<String> imei = new LinkedList<String>();
		final List<Integer> alarm = new LinkedList<Integer>();
		imei.add("013226008748994-MT90");
		alarm.add(35);

		combo1.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED) {
					imei.clear();
					imei.add(e.getItem().toString());
					try {
						session.close(true);
						connect();
					} catch (Throwable e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});

		combo2.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED) {
					alarm.clear();
					String s = e.getItem().toString();
					if (s.equals("进围栏报警")) {
						alarm.add(20);
					} else if (s.equals("出围栏报警")) {
						alarm.add(21);
					} else if (s.equals("超速报警")) {
						alarm.add(19);
					} else if (s.equals("低电报警")) {
						alarm.add(17);
					} else if (s.equals("位置")) {
						alarm.add(35);
					}
				}
			}
		});

		button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				String snTxt = imei.get(0);
				int type = alarm.get(0);
				String lngStr = "0";
				if (!text1.getText().equals(""))
					lngStr = text1.getText();
				String latStr = "0";
				if (!text2.getText().equals(""))
					latStr = text2.getText();
				double lng = 0;
				double lat = 0;
				try {
					lng = Double.parseDouble(lngStr);
					lat = Double.parseDouble(latStr);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String devType = snTxt.substring(16, snTxt.length());
				String sn = snTxt.substring(0, 15);
				sendMessage(MessageUtils.getMessage(devType, sn, type, lng, lat));
				 System.out.println(MessageUtils.getMessage(devType, sn, type,
				 lng, lat));

			}
		});

		button2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				// TODO Auto-generated method stub
				stage = 0;
				String snTxt = imei.get(0);

				final String devType = snTxt.substring(16, snTxt.length());
				final String sn = snTxt.substring(0, 15);

				if (button2.getText().equals("自动模拟")) {
					button2.setText("停止模拟");
					if (timer != null) {
						timer.cancel();
						timer.purge();
					}
					timer = new Timer();
					timer.scheduleAtFixedRate(new TimerTask() {

						@Override
						public void run() {
							if (stage == 0) {
								lat += 0.000005;

								if (Math.abs(lat - endY) < 0.000005) {
									stage++;
									lat = endY;
								}
							} else if (stage == 1) {
								lng += 0.000005;

								if (Math.abs(lng - endX) < 0.000005) {
									stage++;
									lng = endX;
								}
							} else if (stage == 2) {
								lat -= 0.000005;
								if (Math.abs(lng - endX) < 0.000005) {
									stage++;
									lat = startY;
								}
							} else if (stage == 3) {
								lng -= 0.000005;
								if (Math.abs(lng - startX) < 0.000005) {
									stage = 0;
									lng = startX;
								}
							}

							// stage ++;
							System.out.println(MessageUtils.getMessage(devType, sn, 35, lng, lat));
							sendMessage(MessageUtils.getMessage(devType, sn, 35, lng, lat));
						}
					}, 0, 30000);
				} else {
					if (timer != null) {
						timer.cancel();
						timer.purge();
					}
					button2.setText("自动模拟");
				}
			}
		});

		cbox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent ce) {
				if (cbox.isSelected()) {
					button3.setEnabled(true);
				} else {
					button3.setEnabled(false);
				}

			}
		});

		button3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String msg = null;
				String str = text3.getText();
				if(str.startsWith("$$"))
					 msg = str.substring(0, str.length()-1) + "\r\n";
				if (msg != null && !msg.equals(""))
					sendMessage(msg);

			}
		});

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new java.awt.FlowLayout());

		frame.getContentPane().add(label1);
		frame.getContentPane().add(combo1);
		frame.getContentPane().add(label2);
		frame.getContentPane().add(combo2);
		frame.getContentPane().add(label3);
		frame.getContentPane().add(text1);
		frame.getContentPane().add(label4);
		frame.getContentPane().add(text2);
		frame.getContentPane().add(button1);
		frame.getContentPane().add(button2);
		frame.getContentPane().add(cbox);
		frame.getContentPane().add(text3);
		frame.getContentPane().add(button3);
		frame.getContentPane().add(label5);
		frame.getContentPane().add(text4);
		frame.setSize(530, 400);
		frame.setVisible(true);

	}
}
