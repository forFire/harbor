package com.capcare.harbor.model;

import java.io.Serializable;

import com.capcare.harbor.vo.SpotVo;

public class Fence implements Serializable {

	/** 圆1 */
	public static Integer TYPE_CIRCLE = 1;

	/** 多边型2 */
	public static Integer TYPE_POLYGON = 2;

	private static final long serialVersionUID = 5949172322504685326L;

	private SpotVo[] region;

	private Integer type;// 1：圆形 2：多边形

	private Integer radius;// 半径 米

	private Double[] center;// 中心点

	private Integer out;// 1进 2出

	// ***********************************

	public Fence() {
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

}
