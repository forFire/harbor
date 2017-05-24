package com.capcare.harbor.util;


public class JobUtils {

    public JobUtils () {

        // TODO Auto-generated constructor stub
    }
    
    /**
     * 通过经纬度计算两点之间的距离
     */
    private static double EARTH_RADIUS = 6378137;
    private static double rad(double d)
    {
       return d * Math.PI / 180.0;
    }
    
    /*
     * 返回值单位为米
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2)
    {
       double radLat1 = rad(lat1);
       double radLat2 = rad(lat2);
       double a = radLat1 - radLat2;
       double b = rad(lng1) - rad(lng2);
       double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) + 
        Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));
       s = s * EARTH_RADIUS;
       s = Math.round(s * 10000) / 10000;
       return s;
    }
    
    /*
     * 
     */
    public static Double[] getLatLngArr(String arrStr){
    	String latLngStr = arrStr.substring(arrStr.indexOf("(")+1, arrStr.indexOf(")"));
    	String[] str = latLngStr.split(","); 
    	Double[] doubleArr = {Double.parseDouble(str[0]),Double.parseDouble(str[1])};
    	return doubleArr;
    }
    
//    public static void main(String[] args) {
//    	String s = "(34.32,43243.342321)";
//		System.out.println(JobUtils.getLatLngStr(s));
//	}
}
