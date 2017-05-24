package module.util;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 字节转换工具类
 * 
 * @author XieHaiSheng
 * 
 */
public class BytesConvert {

	private static Logger logger = LoggerFactory.getLogger(BytesConvert.class);

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
//			logger.info("data[i]==="+data[i]+"j==="+j);
			out[j++] = toDigits[0x0F & data[i]];
//			logger.info("data[i]==="+data[i]+"j==="+j);

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

	public static byte[] long2Bytes(long iSource, int iArrayLen) {

		int len = iArrayLen > 8 ? 8 : iArrayLen;

		byte[] b = new byte[len];
		for (int i = 0; i < len; i++) {
			b[i] = (byte) (iSource >> 8 * (len - i - 1) & 0xFF);
		}

		return b;
	}

	public static byte[] double2Bytes(double iSourceDouble, int iArrayLen) {

		long doubleToLongBits = Double.doubleToLongBits(iSourceDouble);
		return long2Bytes(doubleToLongBits, iArrayLen);
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
	public static byte charToByte(char c) {

		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * @param objs
	 *            支持 byte,byte[],string,int,long,double,float;目前不支持list，set，map
	 *
	 * @return
	 */
	public static byte[] concatAll(byte[] first, byte[]... rest) {

		int totalLength = first.length;
		for (byte[] array : rest) {
			totalLength += array.length;
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	public static byte[] concatAll(byte[] first, List<byte[]> rest) {

		int totalLength = first.length;
		for (byte[] array : rest) {
			totalLength += array.length;
		}
		byte[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (byte[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}

	/**
	 * @函数功能: 10进制串转为BCD码
	 * @输入参数: 10进制串
	 * @输出结果: BCD码
	 */
	public static byte[] str2Bcd(String asc) {

		int len = asc.length();
		int mod = len % 2;
		if (mod != 0) {
			asc = "0" + asc;
			len = asc.length();
		}
		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}
		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;
		for (int p = 0; p < asc.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}
			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}
			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	// 将指定byte zhuan 16进制
	public String byteTohex(byte b) {
		String hex = Integer.toHexString(b & 0xFF);
		return hex;
	}

	/**
	 *java 十六进制转二进制
	 */
	 public static String hexString2binaryString(String hexString)
	    {
	        if (hexString == null)
	            return null;
	        String bString = "", tmp;
	        for (int i = 0; i < hexString.length(); i++)
	        {
	            tmp = "0000"
	                    + Integer.toBinaryString(Integer.parseInt(hexString
	                            .substring(i, i + 1), 16));
	            bString += tmp.substring(tmp.length() - 4);
	        }
	        return bString;
	    }
	
}
