package com.capcare.harbor.vo;

import java.io.Serializable;

public class FenceVo implements Serializable {

	/** 圆形 */
	public static Integer TYPE_CIRCLE = 1;

	/** 矩形 */
	public static Integer TYPE_RECTANGLE = 2;

	/** 多边形 */
	public static Integer TYPE_POLYGON = 3;

	/** 路线 */
	public static Integer TYPE_LINE = 4;

	private static final long serialVersionUID = 5949172322504685326L;

	// 顶点项
	private SpotVo[] region;

	// 路线拐点项
	private PointVo[] point;

	private Integer type;// 1：圆形 2：多边形

	private Integer id;// 区域id

	private Integer property;// 区域属性

	private Integer radius;// 半径 米

	private Double[] center;// 中心点

	private String beginTime;// 开始时间

	private String endTime;// 结束时间

	private Integer maxSpeed;// 最高速度

	private Integer overSpeedTime;// 超速持续时间

	private Integer out;// 1进 2出

	// ***********************************

	public FenceVo() {
		super();
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getRadius() {
		return radius;
	}

	public void setRadius(Integer radius) {
		this.radius = radius;
	}

	public Integer getOut() {
		return out;
	}

	public void setOut(Integer out) {
		this.out = out;
	}

	public SpotVo[] getRegion() {
		return region;
	}

	public void setRegion(SpotVo[] region) {
		this.region = region;
	}

	public Double[] getCenter() {
		return center;
	}

	public void setCenter(Double[] center) {
		this.center = center;
	}

	public PointVo[] getPoint() {
		return point;
	}

	public void setPoint(PointVo[] point) {
		this.point = point;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProperty() {
		return property;
	}

	public void setProperty(Integer property) {
		this.property = property;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Integer getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(Integer maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Integer getOverSpeedTime() {
		return overSpeedTime;
	}

	public void setOverSpeedTime(Integer overSpeedTime) {
		this.overSpeedTime = overSpeedTime;
	}

}
