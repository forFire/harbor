package com.capcare.harbor.protocol;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;

/*
 * 当前位置 最新状态 点
 */
public class Position implements MessageCreator, Serializable {

    private static final long serialVersionUID = -7222178759207809761L;
    private Long              id;
    /** 接收时间 */
    private Long              receive          = 0L;

    // 后台处理时间
    private Long              systime          = 0L;

    private Double            lng              = 116.4985916018D;

    private Double            lat              = 39.9798650053D;

    /** 停留时间 秒 */
    private Integer           stayed           = 0;

    /** 1:在线 2:离线 3:注销 4:过期 5:服务停止 */
    private Integer           status           = 2;

    /** 速度 米/秒 */
    private Double            speed            = 0D;

    /** 方向 */
    private Float             direction        = 0F;

    /** 流量 */
    private Float             flow             = 0F;

    /** 电量 */
    private Integer           battery          = 0;

    /** 定位方式,如:AGPS:V GPS:A */
    private String            mode;

    /** <ACC端口>的有效状态（1：已点火；0：未点火） */
    private int               accMode;

    /** 当天告警数 */
    private Integer           alarm            = 0;

    private String            deviceSn;

    private Long              stamp;
    //基站数据
    private String            cell;
    /**
     * 备用字段----
     */
    private String            info;

    private Integer           steps            = 0;

    private int               mode433;

    private boolean flag;
    
    
    // ************************************************

    public Long getReceive () {

        return receive;
    }

    public int getMode433 () {

        return mode433;
    }

    public void setMode433 (int mode433) {

        this.mode433 = mode433;
    }

    public String getCell () {

        return cell;
    }

    public void setCell (String cell) {

        this.cell = cell;
    }

    public int getAccMode () {

        return accMode;
    }

    public void setAccMode (int accMode) {

        this.accMode = accMode;
    }

    public Long getId () {

        return id;
    }

    public void setId (Long id) {

        this.id = id;
    }

    public void setReceive (Long receive) {

        this.receive = receive;
    }

    public String getInfo () {

        return info;
    }

    public void setInfo (String info) {

        this.info = info;
    }

    public Double getSpeed () {

        return speed;
    }

    public void setSpeed (Double speed) {

        this.speed = speed;
    }

    public Float getDirection () {

        return direction;
    }

    public void setDirection (Float direction) {

        this.direction = direction;
    }

    public Double getLng () {

        return lng;
    }

    public void setLng (Double lng) {

        this.lng = lng;
    }

    public Double getLat () {

        return lat;
    }

    public void setLat (Double lat) {

        this.lat = lat;
    }

    public String getDeviceSn () {

        return deviceSn;
    }

    public void setDeviceSn (String deviceSn) {

        this.deviceSn = deviceSn;
    }

    public Integer getStayed () {

        return stayed;
    }

    public void setStayed (Integer stayed) {

        this.stayed = stayed;
    }

    public Integer getStatus () {

        return status;
    }

    public void setStatus (Integer status) {

        this.status = status;
    }

    public Position (String deviceSn) {

        super ();
        this.deviceSn = deviceSn;
    }

    public Position () {

        super ();
    }

    public String getMode () {

        return mode;
    }

    public void setMode (String mode) {

        this.mode = mode;
    }

    public Float getFlow () {

        return flow;
    }

    public void setFlow (Float flow) {

        this.flow = flow;
    }

    public Integer getBattery () {

        return battery;
    }

    public void setBattery (Integer battery) {

        this.battery = battery;
    }

    public Integer getAlarm () {

        return alarm;
    }

    public void setAlarm (Integer alarm) {

        this.alarm = alarm;
    }

    public Long getSystime () {

        return systime;
    }

    public void setSystime (Long systime) {

        this.systime = systime;
    }

    public Long getStamp () {

        return stamp;
    }

    public void setStamp (Long stamp) {

        this.stamp = stamp;
    }

    public Integer getSteps () {

        return steps;
    }

    public void setSteps (Integer steps) {

        this.steps = steps;
    }

    @Override
    public Message createMessage (Session session) throws JMSException {

        ObjectMessage objectMessage = session.createObjectMessage (this);
        return objectMessage;
    }

    @Override
    public String toString () {

        StringBuilder sb = new StringBuilder ();
        sb.append ("Position[");
        sb.append ("sn=");
        sb.append (getDeviceSn ());
        sb.append (",time=");
        sb.append (getReceive ());
        sb.append (",lng=");
        sb.append (getLng ());
        sb.append (",lat=");
        sb.append (getLat ());
        sb.append (",speed=");
        sb.append (getSpeed ());
        sb.append (",direction=");
        sb.append (getDirection ());
        sb.append (",mode=");
        sb.append (getMode ());
        sb.append (",acc=");
        sb.append (getAccMode ());
        sb.append (",433=");
        sb.append (getMode433 ());
        sb.append (",battery=");
        sb.append (getBattery ());
        sb.append (",steps=");
        sb.append (getSteps ());
        sb.append (",cell=");
        sb.append (getCell ());
        sb.append ("]");
        return sb.toString ();
    }

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}
    
    
}
