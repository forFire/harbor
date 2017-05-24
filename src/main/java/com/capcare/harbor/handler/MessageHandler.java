package com.capcare.harbor.handler;

import org.apache.mina.core.session.IoSession;

import com.capcare.harbor.vo.BaseMessage;

public class MessageHandler {
	
	public void preProcess(BaseMessage act,IoSession session){
//		System.out.println("-----1----------------");
	}
	
	public void afterProcess(BaseMessage act,IoSession session){
//		System.out.println("-----3----------------");
	}
	
}
