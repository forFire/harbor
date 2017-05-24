package com.capcare.harbor.handler;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class SessionWriter {
//	private static Logger logger = LoggerFactory.getLogger(SessionWriter.class);
	private Queue<Object> queue = new LinkedBlockingQueue<Object>();
	
	public void add(Object msg){
		if(!queue.contains(msg)){
			queue.add(msg);
		}
		
	}
	
	public void addAll(List list){
		for(Object obj:list){
			add(obj);
		}
	}
	
	public Object poll(){
		
		return queue.poll();
	}

	public Queue<Object> getQueue() {
		return queue;
	}

	public void setQueue(Queue<Object> queue) {
		this.queue = queue;
	}
	
	public int size(){
		return this.queue.size();
	}

}
