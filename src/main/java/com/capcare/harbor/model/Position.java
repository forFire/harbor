package com.capcare.harbor.model;

import java.io.Serializable;
import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.core.MessageCreator;


public class Position implements MessageCreator, Serializable {

	/**
     * 
     */
    private static final long serialVersionUID = -6772629502776952672L;

    /** 报警标志 */
	private Integer alarmType = 0;

	/** 状态 */
	private Integer status;

	private Integer lng = 0;

	private Integer lat = 0;

	/** 高程 */
	private Integer height;

	/** 速度 米/秒 */
	private Integer speed;

	/** 方向 */
	private Integer direction;

	/** 时间 */
	private String time;

    // 位置附加项列表
    private HashMap<String ,PositionExtra> extra;

	// ************************************************

	public Position() {
		super();
	}

	public Integer getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(Integer alarmType) {
		this.alarmType = alarmType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getLng() {
		return lng;
	}

	public void setLng(Integer lng) {
		this.lng = lng;
	}

	public Integer getLat() {
		return lat;
	}

	public void setLat(Integer lat) {
		this.lat = lat;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

    public HashMap<String ,PositionExtra> getExtra() {
        return extra;
    }

    public void setExtra(HashMap<String ,PositionExtra> extra) {
        this.extra = extra;
    }

	@Override
	public Message createMessage(Session session) throws JMSException {
		ObjectMessage objectMessage = session.createObjectMessage(this);
		return objectMessage;
	}

}
