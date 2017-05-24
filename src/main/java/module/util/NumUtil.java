package module.util;

import java.math.BigDecimal;

public class NumUtil {

	public static double round(double dout, int place) {
		BigDecimal bd = new BigDecimal(dout);
		bd = bd.setScale(place, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	public static float round(float dout, int place) {
		BigDecimal bd = new BigDecimal(dout);
		bd = bd.setScale(place, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

	public static void main(String[] args) {
		System.out.println(round(0.5714285714285714, 2));
	}
}
