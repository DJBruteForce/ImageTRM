package com.sunyard.insurance.util;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ClassUtil {
	
	private static final Logger loger = Logger.getLogger(ClassUtil.class);
	private static EnvironmentConfig configReader = EnvironmentConfig.getInstance();
	public static Syd2BatchBean syd2BatchBean = null;
	public static SydFileReader sydFileReader = null;
	
	//获取SYD解析类
	@SuppressWarnings("unchecked")
	public static Syd2BatchBean getSydUtilClass() throws Exception {
		//获取配置文件所在目录
		String configPath = configReader.getPropertyValue("/config.properties","CONFIG_DIR");
		String className = "";
		if(null == syd2BatchBean){
			try {
				InputStream in = null;
				SAXReader saxReader = new SAXReader();
//				in = ClassUtil.class.getResourceAsStream("/ServicesConfig.xml");
				in = new FileInputStream(configPath+"/ServicesConfig.xml");
				Document doc = saxReader.read(in);
				Element root = doc.getRootElement();
				className = root.selectSingleNode("//sydService/classname").getText();
				Class<Syd2BatchBean> sydClass = (Class<Syd2BatchBean>) Class.forName(className);
				syd2BatchBean = sydClass.newInstance();
				loger.info("反射生成Syd2BatchBean解析类成功!");
			} catch (Exception e) {
				loger.error("反射SYD解析类异常["+className+"]!",e);
				throw e;
			}
		}
		return syd2BatchBean;
	}
	
	//获取sydFileReader类的实现
	@SuppressWarnings("unchecked")
	public static SydFileReader getSydFileReaderClass()throws Exception {
		//获取配置文件所在目录
		String configPath = configReader.getPropertyValue("/config.properties","CONFIG_DIR");
		String className = "";
		if(null == sydFileReader){
			try {
				InputStream in = null;
				SAXReader saxReader = new SAXReader();
				in = new FileInputStream(configPath+"/ServicesConfig.xml");
				Document doc = saxReader.read(in);
				Element root = doc.getRootElement();
				className = root.selectSingleNode("//sydReaderService/classname").getText();
				Class<SydFileReader> sydFileReaderClass = (Class<SydFileReader>) Class.forName(className);
				sydFileReader = sydFileReaderClass.newInstance();
				loger.info("反射生成SydFileReader解析类成功!");
			} catch (Exception e) {
				loger.error("反射SydFileReader解析类异常["+className+"]!",e);
				throw e;
			}
		}
		return sydFileReader;
	}
	
	/**
	 *@Description 
	 *
	 *@param args
	 */
	public static void main(String[] args) {
		try {
			ClassUtil.getSydUtilClass().getBatchBean("F:/基线版本SYD.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
