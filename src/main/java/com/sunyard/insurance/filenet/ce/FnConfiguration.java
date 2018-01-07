package com.sunyard.insurance.filenet.ce;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;
/**
 * 
  * @Title FnConfiguration.java
  * @Package com.sunyard.insurance.filenet.ce
  * @Description FileNet 连接配置文件
  * @author Administrator
  * @time 2012-11-30 下午01:22:50  
  * @version 1.0
 */
public class FnConfiguration {

	private static Logger logger = Logger.getLogger(FnConfiguration.class);

    /**
     * Sets JVM parameters for login.
     */
    public static void configurate() {
    	try {
    		// Set the "wasp.location" parameter.
            // Only web services transport need to set this.
    		String waspLocation = FnConfigOptions.getWaspLocation();
    		if (waspLocation != null && !waspLocation.equals("")) {
    			URL waspLocationURL = FnConfiguration.class.getResource(waspLocation);
    			String waspLocationPath = toFilePath(waspLocationURL);
    			if (logger.isDebugEnabled()) {
    				logger.debug("Use WebSservice transport.");
    				logger.debug("wasp config file = " + waspLocationPath);
    			}
    			System.setProperty("wasp.location", waspLocationPath);
    		}

            // Setup jaas configuration parameter.
            String jaasConfigFile = FnConfigOptions.getJaasConfigFile();
            URL jaasConfigURL = FnConfiguration.class.getResource(jaasConfigFile);
            String jaasConfigFilePath = jaasConfigURL.getPath();
            if (logger.isDebugEnabled())
                logger.debug("jaas config file = " + jaasConfigFilePath);
            System.setProperty("java.security.auth.login.config",jaasConfigFilePath);

            // Setup JNDI configurations.
            String jndiFactory = FnConfigOptions.getJndiFactory();
            System.setProperty("java.naming.factory.initial", jndiFactory);

            // Set the CE_URL for authentication.
            String ceUrl = FnConfigOptions.getContentEngineUrl();
            System.setProperty("filenet.pe.bootstrap.ceuri", ceUrl);

            if (logger.isDebugEnabled()) {
            	logger.debug("wasp.location = " + System.getProperty("wasp.location"));
                logger.debug("java.security.auth.login.config = " + System.getProperty("java.security.auth.login.config"));
                logger.debug("java.naming.factory.initial = "  + System.getProperty("java.naming.factory.initial"));
                logger.debug("filenet.pe.bootstrap.ceuri = "  + System.getProperty("filenet.pe.bootstrap.ceuri"));
            }
		} catch (Throwable t) {
			logger.error("FnConfiguration.configurate() error.", t);
		}
    }

	/**
	 * Converts a file url to real file path without encoding.
	 * 
	 * @param url The url of the file.
	 * @return The real file path without encoding. If the url is not a file url,
	 *         return <code>null</code>.
	 */
	private static String toFilePath(URL url) {
		if (url == null || !url.getProtocol().equals("file")) {
			return null;
		} else {
			String filename = url.getFile().replace('/', File.separatorChar);
			int position = 0;
			while ((position = filename.indexOf('%', position)) >= 0) {
				if (position + 2 < filename.length()) {
					String hexStr = filename.substring(position + 1, position + 3);
					char ch = (char) Integer.parseInt(hexStr, 16);
					filename = filename.substring(0, position) + ch + filename.substring(position + 3);
				}
			}
			return new File(filename).getPath();
		}
	}

}
