package module.util;

public class UsConstants {

	/**
	 * 抖动 间隔 1000 * 60 * 10分
	 */
	public static final int INTERVAL = 600000;

	/**
	 * 抖动 阈值
	 */
	public static final Double THRESHOLD = 0.000001;

	/**
	 * token过期时间 1000*60*30分=1200000 1000*60*60*24*7天=604800000
	 */
	public static final long EXPIRE = 604800000;

	public static final String DEVICE_REGISTERED = "设备已注册";

	public static final String DEVICE_FENCE_EXIST = "设备已有围栏";

	public static final String USER_NOTDEVICE = "用户无此设备";

	public static final String USER_NOTFENCE = "用户无此围栏";

	public static final String USER_REGISTERED = "用户名已注册";

	public static final String EMAIL_REGISTERED = "邮箱已注册";

	public static final String PHONE_REGISTERED = "电话号码已注册";

	public static final String THIRD_REGISTERED = "第三方账号已被注册";

	public static final String THIRD_NULL = "第三方账号不存在";

	public static final String USER_NULL = "用户不存在";

	public static final String USER_PW_ERROR = "密码错误";

	public static final String CLIENT_COUNT = "count_client";
	
	public static final String DEVICE_COUNT = "count_device";
}
