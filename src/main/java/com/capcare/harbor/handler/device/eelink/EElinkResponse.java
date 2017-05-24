package com.capcare.harbor.handler.device.eelink;


public class EElinkResponse {
	
	private byte[] head=new EELink().getBegin();
	private byte act;
	private byte[] datalength;
	private byte[] sequnce;
	private byte[] sn=new byte[8];
	private byte lanage;
	
	public byte[] getHead() {
		return head;
	}
	public void setHead(byte[] head) {
		this.head = head;
	}
	public byte getAct() {
		return act;
	}
	public void setAct(byte act) {
		this.act = act;
	}
	public byte[] getDatalength() {
		return datalength;
	}
	public void setDatalength(byte[] datalength) {
		this.datalength = datalength;
	}
	public byte[] getSequnce() {
		return sequnce;
	}
	public void setSequnce(byte[] sequnce) {
		this.sequnce = sequnce;
	}
	
	public byte[] getSn() {
		return sn;
	}
	
	public void setSn(byte[] sn) {
		for(int i=0;i<8;i++){
			this.sn[i] = sn[i];
		}
	}
	public byte getLanage() {
		return lanage;
	}
	public void setLanage(byte lanage) {
		this.lanage = lanage;
	}
	
	public byte[] tobytes(){
		byte[] ret=new byte[7];
		ret[0]=head[0];
		ret[1]=head[1];
		ret[2]=act;
		ret[3]=datalength[0];
		ret[4]=datalength[1];
		ret[5]=sequnce[0];
		ret[6]=sequnce[1];
		
		return ret;
	}
	
}
