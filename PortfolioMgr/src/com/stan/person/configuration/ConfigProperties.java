package com.stan.person.configuration;

import java.io.FileInputStream;
import java.util.Properties;

public class ConfigProperties {
	private static Properties properties = null;
	public static void initProperties() {
		try {
			properties = new Properties();
			properties.load(new FileInputStream(System.getProperty("user.dir") + "/resources/"+"config.properties"));
		} catch (Exception e) {
			System.out.println("config.properties not found...using defaults");
		}
	}
	public static String getProperty(String pName, String defaultValue) {
		if (properties == null)
			initProperties();
		return properties.getProperty(pName, defaultValue);
	}

}
