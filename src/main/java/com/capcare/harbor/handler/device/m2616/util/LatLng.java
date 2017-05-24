package com.capcare.harbor.handler.device.m2616.util;

import java.math.BigDecimal;

/**
 * @author fyq
 */
public class LatLng {

	/** 11627.7347 */
	public static double num(String num) {
		double numDouble = Double.parseDouble(num) / 100;// 116.277347
		int zs = (int) Math.floor(numDouble);// 116
		double xs = (numDouble - zs) * 100;// 27.7347,000000006
		double xsRes = xs / 60;// 0.46224500000001
		return round(zs + xsRes, 6);// 116.462245
	}

	public static double round(double dout, int place) {
		BigDecimal bd = new BigDecimal(dout);
		bd = bd.setScale(place, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
}
