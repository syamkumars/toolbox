package com.syberhub.toolbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ToolBoxConfig {
	private static Properties prop;
	static {
		InputStream is = null;
		try {
			prop = new Properties();
			is = ClassLoader.class.getResourceAsStream("/application.properties");
			prop.load(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getProperty(String key) {
//		return System.getenv(key);
		return prop.getProperty(key);
	}

	public static boolean getBooleanProperty(String key) {
		return Boolean.TRUE.toString().equalsIgnoreCase(getProperty(key));
	}
}
