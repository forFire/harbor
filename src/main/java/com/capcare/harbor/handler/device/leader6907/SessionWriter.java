package com.capcare.harbor.handler.device.leader6907;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class SessionWriter {
	
	private Queue<Object> queue = new LinkedBlockingQueue<Object>();
	
	public void add(Object msg){
		if(!queue.contains(msg)){
			queue.add(msg);
		}
	}
	
	public void addAll(List < ? > list){
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
	
	

}
