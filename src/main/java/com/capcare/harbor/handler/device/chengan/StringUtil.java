package com.capcare.harbor.handler.device.chengan;

/**
 * @author zf
 */
public class StringUtil {
//	int position = in.position();
	private int pos = 0;
	private String str ="" ;
	
	
	public StringUtil (String str ){
		this.str = str;
	};
	
	
	public String getStr(int len){
		return str.substring(pos,pos = pos+len);
	}
	
	
	
}
