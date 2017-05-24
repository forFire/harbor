package com.capcare.harbor.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Helper {
	private static Properties prop;
	private static Logger logger = Logger.getLogger(Helper.class);
	private static InputStream inputStream;
	
	static {
		try {
			inputStream = Helper.class
					.getResourceAsStream("/application.properties");
			prop = new Properties();
			prop.load(inputStream);
			
		} catch (Exception ex) {
			try {
				if (inputStream!=null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			ex.printStackTrace();
			logger.error(ex, ex);
		}
	}
	public static String get(String key) {
		return (String) prop.getProperty(key);
	}
}
