package com.sunyard.insurance.common;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

import com.sunyard.insurance.util.EnvironmentConfig;

public class ProjectStartListener implements ServletContextListener {
	
	private static EnvironmentConfig configReader = EnvironmentConfig.getInstance();
	private final static Log logger = LogFactory.getLog(ProjectStartListener.class);
	
	public void contextDestroyed(ServletContextEvent arg0) {
		//系统销毁初始化信息
		
	}
	
	public void contextInitialized(ServletContextEvent arg0) {
		//初始化系统参数
		logger.info("[设置系统LOG4J参数...]");
		//设置log4j配置文件所在目录
		String log4j_adress = configReader.getPropertyValue("/config.properties","CONFIG_DIR");
		DOMConfigurator.configure(log4j_adress+"/log4j.xml");
		logger.info("[设置系统LOG4J参数完成...]");
	}

}
