package com.sunyard.insurance.filenet.ce.common;

import java.util.ResourceBundle;

public class IcmConfigOptions {

	private static final ResourceBundle CONFIG_BUNDLE = ResourceBundle.getBundle("config");
	
	public static String getEndPoint() {
		return getString("EndPoint");
	}
	
	private static String getString(String key) {
		return CONFIG_BUNDLE.getString(key);
	}
	
}
