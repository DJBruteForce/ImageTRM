package com.sunyard.insurance.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpingContextUtil implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpingContextUtil.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return SpingContextUtil.applicationContext;
	}

	public static Object getBean(String name) throws BeansException {
		return SpingContextUtil.applicationContext.getBean(name);
	}

}
