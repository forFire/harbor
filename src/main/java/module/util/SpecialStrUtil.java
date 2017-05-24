package module.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class SpecialStrUtil {

	/**
	 * 使用MD5对Message散列加密
	 * 
	 * @param input
	 * @return
	 */
	public static String getMd5(String inputMessage) {
		if (inputMessage == null) {
			return null;
		}
		try {
			// Create MessageDigest object for MD5
			MessageDigest digest = MessageDigest.getInstance("MD5");
			// Update input message in message digest
			digest.update(inputMessage.toUpperCase().getBytes());
			// Converts message digest value in base 16 (hex)
			byte[] byteArray = digest.digest();
			StringBuffer md5StrBuff = new StringBuffer();
			for (int i = 0; i < byteArray.length; i++) {
				if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
					md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
				else
					md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
			}
			return md5StrBuff.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 生成32位UUID
	 * 
	 * @return String
	 */
	public static String getUuid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().toUpperCase();
	}

	/**
	 * token过期时间
	 */
	// 1000 * 60秒 * 30分钟=1200000
	// 1000 * 60秒 * 60分钟 * 24小时 * 7天=604800000
	public static final long EXPIRE = 604800000;

}
