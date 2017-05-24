package com.capcare.harbor.handler.device.m2616.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capcare.harbor.handler.device.m2616.M2616;
import com.capcare.harbor.handler.device.m2616.exception.NoIndexException;
import com.capcare.harbor.util.CharsetKey;
import com.capcare.harbor.vo.BaseMessage;
import com.capcare.harbor.vo.BaseMessage.Act;
import com.capcare.harbor.vo.LoginDevice;

/**
 * @author fyq
 */
public class GetUtil {

	private static final Logger log = LoggerFactory.getLogger(GetUtil.class);

	public static String getString(IoBuffer in) throws CharacterCodingException {
		int index = in.indexOf(DecoderKey.M2616_BEGIN);
		if (index < 0) {
			in.reset();
			throw new NoIndexException();
		}
		int position = in.position();
		String rs = in.getString(index - position, CharsetKey.DEFAULT_DECODER);
		in.skip(1);
		return rs;
	}

	public static int getInt(IoBuffer in) {
		int index = in.indexOf(DecoderKey.M2616_BEGIN);
		if (index < 0) {
			in.reset();
			throw new NoIndexException();
		}
		int position = in.position();
		int rs = in.getInt(index - position);
		in.skip(1);
		return rs;
	}

	public static float getFloat(IoBuffer in) {
		int index = in.indexOf(DecoderKey.M2616_BEGIN);
		if (index < 0) {
			in.reset();
			throw new NoIndexException();
		}
		int position = in.position();
		float rs = in.getFloat(index - position);
		in.skip(1);
		return rs;
	}

	public static void checkTail(IoBuffer in) {
		int index = in.indexOf(DecoderKey.M2616_BEGIN);
		if (index < 0) {
			throw new NoIndexException();
		}
		in.skip(1);
	}

	public static void checkHead(IoBuffer in) throws Exception{
		String symbol = in.getString(1, CharsetKey.DEFAULT_DECODER);
		if (!"#".equals(symbol)) {
			throw new Exception("invalid begin");
		}
	}

	public static String checkMsg(IoBuffer in) {
		//parseSms(in);
		in.mark();
		byte[] bytes = new byte[in.remaining()];
		in.get(bytes);
		
		String rsInStr = new String(bytes,Charset.forName("UTF-8"));
		log.info("From_M2616:[" + rsInStr+"]");	

		if(rsInStr.indexOf(DecoderKey.M2616_BEGIN) != -1){
			in.reset();
			return null;
		}				
		return rsInStr;
	}
	
	public static String parseAck(IoBuffer in) {
		in.mark();
		byte[] bytes = new byte[in.remaining()];
		in.get(bytes);
		String rsInStr = new String(bytes, CharsetKey.DEFAULT_CHARSET);
		if(rsInStr.indexOf(DecoderKey.M2616_BEGIN) != -1){
			return null;
		}
		return rsInStr;
	}

	public static boolean isNumber(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher matcher = pattern.matcher(str);
		if (!matcher.matches())
			return false;
		return true;
	}
	
	public static String getConfigHead(String message) {
		String ver = "Ver";
		String imei = "Imei";
		String user = "User";
		String apn = "Apn";
		String acc = "Acc";
		String speed = "Speed";
		String fence = "Fence";
		String configMessage = "Type:";
		String[] parts = message.split("\n");
		int type = 0;
		if (parts.length < 3)
			return configMessage + type + "\n";
		if (parts[0].contains(imei) && parts[1].contains(user)) {
			if (parts[2].contains(ver)) {
				type = 600;
			}else if (parts[2].contains(apn)) {
				type = 620;
			} else if (parts[2].contains(acc)) {
				type = 630;
			} else if (parts[2].contains(speed)) {
				type = 640;
			} else if (parts[2].contains(fence)) {
				type = 650;
			}

		}
		return configMessage + type + "\n";
	}
	
	public static BaseMessage parseSms(IoBuffer in){
		in.mark();
		byte[] bytes = new byte[in.remaining()];
		in.get(bytes);

		String start = new String(new byte[]{bytes[0],bytes[1],bytes[2],bytes[3],bytes[4]});
		
		String msg = null;
		String sn = null;
		
		//宠物
		if("#360#".equals(start)){
			byte[] dest = new byte[15];
			System.arraycopy(bytes, 5, dest, 0, 15);
			sn = new String(dest);
			
			byte[] sms = new byte[bytes.length-23];
			System.arraycopy(bytes, 21, sms, 0, bytes.length-23);
			
			//宠物：不需反转，GBK
			try {
				msg = new String(sms,"GBK");
			} catch (UnsupportedEncodingException e) {
				log.error("", e);
			}
		}
		//个人
		else if("#361#".equals(start)){
			byte[] dest = new byte[15];
			System.arraycopy(bytes, 5, dest, 0, 15);
			sn = new String(dest);
			
			byte[] sms = new byte[bytes.length-23];
			System.arraycopy(bytes, 21, sms, 0, bytes.length-23);
			for(int i=0;i<sms.length;i+=2){
				byte tmp = sms[i];
				sms[i] = sms[i+1];
				sms[i+1] = tmp;
			}

			try {
				msg = new String(sms,"Unicode");
			} catch (UnsupportedEncodingException e) {
				log.error("", e);
			}
		}
		if(msg != null){
			BaseMessage message=new BaseMessage(Act.FEE_CHECK,msg);
			LoginDevice ld=new LoginDevice();
			ld.setDt(new M2616());
			ld.setSn(sn);
			message.setLoginDevice(ld);
			return message;
		}else{
			in.reset();
			return null;
		}
	}
	
	public static String unicodeDecode(String strText) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        char c;
        while (i < strText.length()) {
            c = strText.charAt(i);
            if (c == '\\' && (i + 1) != strText.length() && strText.charAt(i + 1) == 'u') {
                sb.append((char) Integer.parseInt(strText.substring(i + 2, i + 6), 16));
                i += 6;
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
 }
}
