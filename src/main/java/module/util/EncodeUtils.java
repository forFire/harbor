package module.util;

import org.apache.commons.codec.binary.Base64;

public class EncodeUtils {

	/**
	 * 把BASE64字符串译转为字节数组
	 */
	public static final byte[] decode(String input) {
		return Base64.decodeBase64(input);
	}

	/**
	 * 把字节数组转为BASE64字符串
	 */
	public static final String encode(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	public static void main(String[] args) {
		String input = "abcdefg";
		byte[] byteArray = EncodeUtils.decode(input);
		System.out.println(byteArray.length);
		System.out.println(EncodeUtils.encode(byteArray));// 关注此结果
	}
}
