package com.capcare.harbor.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author fyq pattern:y.M.d h:m:s S
 */
public class DateUtil {

	/**
	 * 字符转date
	 */
	public static Date strDate(String str, String pattern) throws ParseException {
		DateFormat df = new SimpleDateFormat(pattern);
		return df.parse(str);
	}

	/**
	 * 长整转date
	 */
	public Date longDate(Long num) {
		return new Date(num);
	}

	/**
	 * 时间转字符
	 */
	public static String dateStr(Date date, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(date);
	}

	/**
	 * 长整型转字符
	 */
	public static String longStr(Long num, String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);
		return df.format(num);
	}

	/**
	 * 字符转长整型
	 */
	public static Long strLong(String str, String pattern) throws ParseException {
		DateFormat df = new SimpleDateFormat(pattern);
		return df.parse(str).getTime();
	}

	/**
	 * 当前时间 方式二
	 */
	public static Date nowDate() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 前后几天的零点,昨天-1
	 */
	public static Date getYesterday(int num) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, num);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static long timeDiff(String str1,String str2) {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		long diff = 0;
		try {
			Date d1 = df.parse(str1);
			Date d2 = df.parse(str2);
			diff = d1.getTime() - d2.getTime();
			diff = diff / (1000);

		}
		catch (Exception e){

		}
		return diff;
		
	}

	public static long timediff(String str2){
		Date date = new Date();
		long diff = 0;
		diff = date.getTime() - Long.valueOf(str2);
		diff = diff / (1000);
		return diff;
	}
	
	
	public static void main(String[] args) {
		Date yesterday = getYesterday(-1);
		System.out.println(dateStr(yesterday, "yyyy.MM.dd HH:mm:ss"));
		System.out.println(yesterday.getTime());
	}
}
