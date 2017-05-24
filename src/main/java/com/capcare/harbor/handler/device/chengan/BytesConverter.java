package com.capcare.harbor.handler.device.chengan;

import java.io.UnsupportedEncodingException;
import java.util.BitSet;

/**
 * 字节转换工具类
 */
public class BytesConverter {

	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	/**
	 * @param b
	 *            高位在前
	 * @return
	 */
	public static int bytes2Int(byte... b) {

		int mask = 0xff;
		int temp = 0;
		int n = 0;
		int length = b.length > 4 ? 4 : b.length;
		for (int i = 0; i < length; i++) {
			n <<= 8;
			temp = b[i] & mask;
			n |= temp;
		}
		return n;
	}

	public static long bytes2long(byte... b) {

		long temp = 0;
		long res = 0;
		int length = b.length > 8 ? 8 : b.length;
		for (int i = 0; i < length; i++) {
			res <<= 8;
			temp = b[i] & 0xff;
			res |= temp;
		}
		return res;
	}

	public static BitSet fromByte(byte... b) {

		BitSet bits = new BitSet(b.length * 8);
		int index = 0;
		// 从低位开始
		for (int j = b.length - 1; j > -1; j--) {
			for (int i = 0; i < 8; i++) {
				bits.set(index, (b[j] & 1) == 1);
				b[j] >>= 1;
				index++;
			}
		}

		return bits;
	}

	public static void printBitSet(BitSet bs) {

		StringBuffer buf = new StringBuffer();
		buf.append("[\n");
		for (int i = 0; i < bs.size(); i++) {
			if (i < bs.size() - 1) {
				buf.append(bs.get(i) + ",");
			} else {
				buf.append(bs.get(i));
			}
			if ((i + 1) % 8 == 0 && i != 0) {
				buf.append("\n");
			}
		}
		buf.append("]");
		System.out.println(buf.toString());
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 * 
	 * @param data
	 *            byte[]
	 * @param toDigits
	 *            用于控制输出的char[]
	 * @return 十六进制String
	 */
	public static String encodeHexStr(byte... data) {

		return new String(encodeHex(data, DIGITS_UPPER));
	}

	/**
	 * 将字节数组转换为十六进制字符数组
	 * 
	 * @param data
	 *            byte[]
	 * @param toDigits
	 *            用于控制输出的char[]
	 * @return 十六进制char[]
	 */
	public static char[] encodeHex(byte[] data, char[] toDigits) {

		int l = data.length;
		char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return out;
	}

	public static String toFullBinaryString(int num) {

		char[] chs = new char[Integer.SIZE];
		for (int i = 0; i < Integer.SIZE; i++) {
			chs[Integer.SIZE - 1 - i] = (char) (((num >> i) & 1) + '0');
		}
		return new String(chs);
	}

	// 浮点到字节转换
	public static byte[] doubleToByte(double d) {

		byte[] b = new byte[8];
		long l = Double.doubleToLongBits(d);
		for (int i = 0; i < b.length; i++) {
			b[i] = new Long(l).byteValue();
			l = l >> 8;

		}
		return b;
	}

	public static byte[] longToBytes(long l) {

		byte[] b = new byte[8];
		b = java.lang.Long.toString(l).getBytes();
		return b;
	}

	// 整数到字节数组的转换
	public static byte[] intToByte(int number) {

		int temp = number;
		byte[] b = new byte[4];
		for (int i = b.length - 1; i > -1; i--) {
			b[i] = new Integer(temp & 0xff).byteValue(); // 将最高位保存在最低位
			temp = temp >> 8; // 向右移8位
		}
		return b;
	}

	public static String toFullBinaryString(long num) {

		char[] chs = new char[Long.SIZE];
		for (int i = 0; i < Long.SIZE; i++) {
			chs[Long.SIZE - 1 - i] = (char) (((num >> i) & 1) + '0');
		}
		return new String(chs);
	}

	/**
	 * int转byte数组,整型的高字节位存储字节数组的低位
	 * 
	 * @param iSource
	 *            源数据
	 * @param iArrayLen
	 *            数组大小
	 * @return
	 */
	public static byte[] int2Bytes(int iSource, int iArrayLen) {

		int len = iArrayLen > 4 ? 4 : iArrayLen;

		byte[] b = new byte[len];
		for (int i = 0; i < len; i++) {
			b[i] = (byte) (iSource >> 8 * (len - i - 1) & 0xFF);
		}

		return b;
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {

		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {

		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String bytes2HexString(String s) {
		byte[] b = s.getBytes();
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "gb2312");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	public static String getUTCTime(Long time) {
		final java.util.Calendar cal = java.util.Calendar.getInstance();
		cal.setTimeInMillis(time);
		return Long.toHexString(cal.getTimeInMillis() / 1000);
	}

	public static String writeInteger(int i, int j) throws Exception {
		String value = "";
		value = toHexInt(i).toUpperCase();
		while (value.length() < j) {
			value = "0" + value;
		}
		return "" + value;
	}

	public static String writeString(String str, int i) throws UnsupportedEncodingException {
		String value = "";
		value = toHexString(str).toUpperCase();
		while (value.length() < i) {
			value = value + "0";
		}
		return "" + value;
	}

	public static String writeLong(long i, int j) throws Exception {
		String value = "";
		value = toHexLong(i).toUpperCase();
		while (value.length() < j) {
			value = "0" + value;
		}
		return "" + value;
	}

	public static String writeDouble(double i, int j) throws Exception {
		String value = "";
		value = toHexDouble(i).toUpperCase();
		while (value.length() < j) {
			value = "0" + value;
		}
		return "" + value;
	}

	// Double转16进制
	public static String toHexDouble(double s) {
		String str = Double.toHexString(s);
		return str;
	}

	// 整数转16进制
	public static String toHexInt(int s) {
		String str = Integer.toHexString(s);
		return str;
	}

	// long转16进制
	public static String toHexLong(long s) {
		String str = Long.toHexString(s);
		return str;
	}

	// 字符串转16进制
	public static String toHexString(String str) throws UnsupportedEncodingException {
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes("gbk");
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString();
	}

	/**
	 * 
	 * 3.5 尾标识 尾标识为字符 0x5d。 数据内容进行转义判断，转义规则如下:
	 * a) 若数据内容中有出现字符 0x5b 的，需替换为字符 0x5a紧跟字符 0x01；
	 * b) 若数据内容中有出现字符 0x5a 的，需替换为字符 0x5a 紧跟字符 0x02；
	 * c) 若数据内容中有出现字符 0x5d 的，需替换为字符 0x5e 紧跟字符 0x01；
	 * d) 若数据内容中有出现字符 0x5e 的，需替换为字符 0x5e 紧跟字符0x02。
	 */
	public static String converString(String str) {
		if(str.contains("5a")){
			str = str.replaceAll("5a", "5a02");
		}
		if(str.contains("5e")){
			str = str.replaceAll("5e", "5e02");
		}
		if(str.contains("5b")){
			str = str.replaceAll("5b", "5a01");
		}
		if(str.contains("5d")){
			str = str.replaceAll("5d", "5e01");
		}
		
		return str;
	}

	public static void main(String[] args) {
		// System.out.println(printBitSet(fromByte(0x20)));
		// System.out.println(bytes2Int((byte) 0xFF));
		// int status = BytesConvert.bytes2Int((byte) 0x00, (byte) 0x01);
		// System.out.println(status);
		// String binstr = toFullBinaryString(status);
		// System.out.println(binstr);// 0354188046625120

		// byte[] b = int2Bytes(2147483647, 4);
		// for (int i = 0; i < b.length; i++) {
		// System.out.println(i + "," + b[i]);
		// }
		
//		5b000000b20000000112000000047901020f0000000000bea94e3937305431000000000000000000000000000212030000004a01001d0307e00e1e2f06f120ab02605dbf0000000000000000000300000000000000000000d3835d
//		String s ="5b000000b20000000112000000047901020f0000000000bea9513246533632000000000000000000000000000212030000004a01001d0307e00e392e06f1959f02615dsdfasdfad";
//		System.out.println(converString(s));
//		String bytes2HexString = bytes2HexString("我");
//		System.out.println(bytes2HexString);
//		byte[] bytes = bytes2HexString.getBytes();
//		System.out.println(bytes.length);
		
		String s = "AA";
		
		System.out.println(Integer.parseInt(s, 16));
		
		
	}

}
