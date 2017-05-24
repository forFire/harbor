package com.capcare.harbor.handler.device.beidou;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;

import module.util.JsonUtils;

import com.capcare.harbor.model.Instruct;
import com.capcare.harbor.model.ShortMessage;

public class ShortMessageHandle {

	private Socket s;
	private InputStream ips;
	private OutputStream ops;
	private BufferedReader brKey;
	private DataOutputStream dos;
	private BufferedReader brNet;
	
	public ShortMessageHandle() throws UnknownHostException, IOException{
		 s = new Socket("192.168.3.191", 8860);
		 ips=s.getInputStream();  
		 ops=s.getOutputStream();  
		 brKey = new BufferedReader(new StringReader(""));//键盘输入  
		 dos = new DataOutputStream(ops);  
		 brNet = new BufferedReader(new InputStreamReader(ips,"UTF-8"));  
	}
    
	public void send(String sendHandsetId, String sendContent) throws IOException {
		dos.write(formatSendContent(sendHandsetId,sendContent).getBytes("UTF-8"));
	}

	public String getContent() throws IOException {
			String[] content =brNet.readLine().split("&");
			return content[4];
	}

	public String formatSendContent(String reveiveHandsetId, String content) {
		return "&BDDHR&S&"+reveiveHandsetId+"&"+content;
	}


	public void send(Instruct instruct) throws IOException {
		String json = instruct.getContent();
		ShortMessage shortMessage = JsonUtils.str2Obj(json, ShortMessage.class);
		send(shortMessage.getReceiveHandsetId(), shortMessage.getSendContent());
	}

}
