package com.syberhub.toolbox;

public class ToolBoxConfig {
//	private static Properties prop;
//	static {
//		InputStream is = null;
//		try {
//			prop = new Properties();
//			is = ClassLoader.class.getResourceAsStream("/application.properties");
//			prop.load(is);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	public static String getProperty(String key) {
		return System.getenv(key); // for heroku
		// return prop.getProperty(key); // local system
	}

	public static boolean getBooleanProperty(String key) {
		return Boolean.TRUE.toString().equalsIgnoreCase(getProperty(key));
	}
}
