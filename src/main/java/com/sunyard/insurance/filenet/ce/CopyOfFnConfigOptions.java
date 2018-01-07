package com.sunyard.insurance.filenet.ce;

import java.util.ResourceBundle;

import com.sunyard.insurance.util.EnvironmentConfig;

public class CopyOfFnConfigOptions {
	public static final String DEFAULT_JAAS_STANZA = "FileNetP8";
	private static final ResourceBundle CONFIG_BUNDLE = ResourceBundle.getBundle("FileNetCEConfig");
	private static EnvironmentConfig configReader = EnvironmentConfig.getInstance();
	
	public static String getJaasConfigFile() {
		return getString("JAAS_ConfigFile");
	}

	public static String getJndiFactory() {
		return getString("JNDI_Factory");
	}

	public static String getWaspLocation() {
		String WASP_Location = configReader.getPropertyValue("/FileNetCEConfig.properties", "WASP_Location");
		if(null == WASP_Location){
			return "";
		}else{
			return WASP_Location.trim();
		}
		
	}

	public static String getContentEngineUrl() {
		return getString("ContentEngineURL");
	}

	public static String getObjectStoreName() {
		return getString("ObjectStoreName");
	}

	public static String getHistoryObjectStore() {
		return getString("HistoryObjectStore");
	}

	public static String getUsername() {
		return getString("Username");
	}

	public static String getPassword() {
		return getString("Password");
	}

	public static String getFldClsName() {
		return getString("FldClsName");
	}

	public static String getDocClsName() {
		return getString("DocClsName");
	}

	private static String getString(String key) {
		return CONFIG_BUNDLE.getString(key);
	}

}
