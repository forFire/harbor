package com.capcare.harbor.util;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class Harbor {

	/** UTF-8 */
	public static String DEFAULT_CHARSET_NAME = "UTF-8";

	/** UTF-8 */
	public static Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_CHARSET_NAME);

	/** session_Id */
	public static String SESSION_ID = "session_Id";

	/** token */
	public static String TOKEN = "token";

	/** user_id */
	public static String USER_ID = "user_id";

	/** duid */
	public static String DUID = "duid";

	/** protocol */
	public static String PROTOCOL = "protocol";

	/** token前缀:usr_tk_ */
	public static String TOKEN_PREFIX = "usr_tk_";

	/** MT90设备流量统计 */
	public static Map<String, Integer> TOTAL_FLOW_MT90 = new HashMap<String, Integer>();

	/** 0x41 --- 0x7A */
	public static byte PACK_TAG = 0x41;
	
	/** M2616设备流量统计 */
	public static Map<String, Integer> TOTAL_FLOW_M2616 = new HashMap<String, Integer>();

	/** 出围栏报警 */
	public static int ALARM_OUT = 1;
	
	/** 进围栏报警 */
	public static int ALARM_IN = 2;
	
	/** 低电报警 */
	public static int ALARM_LOW = 3;
	
	/** 超速报警 */
	public static int ALARM_SPDHI = 4;
	
	/** SOS报警 */
	public static int ALARM_SOS = 5;
	
	/** 振动报警 */
	public static int ALARM_VIB = 6;
	
	/** 外电报警 */
	public static int ALARM_EPD = 7;
	
	/** 防盗报警 */
	public static int ALARM_BAT = 8;
	
	/** 低速报警 */
	public static int ALARM_SPDLO = 9;
	
	/** cmd标签 */
	public static String CMD_TAG = "cmd";
	
	/** 设备sn*/
	public static String UP_SN = "device_sn";
	
	/** 时间 */
	public static String UP_TIME = "time";
	
	/** 经度 */
	public static String UP_LNG = "lng";
	
	/** 纬度 */
	public static String UP_LAT = "lat";
	
	/** 速度 */
	public static String UP_SPEED = "speed";
	
	/** 方向 */
	public static String UP_DIRECTION = "direction";
	
	/** 模式：GPS/AGPS */
	public static String UP_MODE = "mode";
	
	/** 流量*/
	public static String UP_FLOW = "flow";
	
	/** 电量*/
	public static String UP_BATTERY = "battery";
	
	/** 类型*/
	public static String UP_TYPE = "type";
	
	/** 相关信息*/
	public static String UP_INFO = "info";
	
	/** 操作*/
	public static String UP_OPERATE = "operate";
	
	/** 结果*/
	public static String UP_RESULT = "result";
}
