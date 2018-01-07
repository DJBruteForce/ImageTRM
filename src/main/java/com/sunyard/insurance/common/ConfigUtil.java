package com.sunyard.insurance.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.sunyard.insurance.filenet.ce.common.constant.CeConstant;
import com.sunyard.insurance.filenet.faf.cesupport.CEConstant;
import com.sunyard.insurance.util.EnvironmentConfig;

/**
 * 
 * @Title ConfigUtil.java
 * @Package com.sunyard.insurance.common
 * @Description 解析初始化信息配置文件工具类
 * 
 * @time 2012-8-7 下午02:36:13 @author xxw
 * @version 1.0 -------------------------------------------------------
 */
public class ConfigUtil {
	private static final Logger log = Logger.getLogger(ConfigUtil.class);
	private static EnvironmentConfig configReader = EnvironmentConfig.getInstance();

	/**
	 * 
	 *@Description
	 * 初始化IBMCM配置信息
	 *@return
	 */
	public static boolean initCMGlobalVar() {
		//获取配置文件所在目录
		String configPath = configReader.getPropertyValue("/config.properties","CONFIG_DIR");
		//读取配置文件CMConfig.properties
		String cmConfigPath = configPath+"/CMConfig.properties";
				
		CMConstant.CMUser = configReader.getPropertyValue(cmConfigPath, "CMUser").trim();
		if ("".equals(CMConstant.CMUser)) {
			log.info("配置文件不存在CMUser的属性值!");
			return false;
		}
		CMConstant.CMPwd = configReader.getPropertyValue(cmConfigPath, "CMPwd").trim();
		if ("".equals(CMConstant.CMPwd)) {
			log.info("配置文件不存在CMPwd的属性值!");
			return false;
		}
		CMConstant.RMN = configReader.getPropertyValue(cmConfigPath, "RMN").trim();
		if ("".equals(CMConstant.RMN)) {
			log.info("配置文件不存在RMN的属性值!");
			return false;
		}
		CMConstant.initPool = Integer.valueOf(configReader.getPropertyValue(cmConfigPath, "initPool").trim());
		if ("".equals(CMConstant.initPool)) {
			log.info("配置文件不存在CMPassword的属性值!");
			return false;
		}
		CMConstant.minPool = Integer.valueOf(configReader.getPropertyValue(cmConfigPath, "minPool").trim());
		if ("".equals(CMConstant.minPool)) {
			log.info("配置文件不存在minPool的属性值!");
			return false;
		}
		CMConstant.maxPool = Integer.valueOf(configReader.getPropertyValue(cmConfigPath, "maxPool").trim());
		if ("".equals(CMConstant.maxPool)) {
			log.info("配置文件不存在maxPool的属性值!");
			return false;
		}
		// ------------------------------------------------------------
		CMConstant.attrSubLen = Integer.valueOf(configReader.getPropertyValue(cmConfigPath, "attrSubLen").trim());
		if ("".equals(CMConstant.attrSubLen)) {
			log.info("配置文件不存在attrSubLen的属性值!");
			return false;
		}
		CMConstant.isCMPool = configReader.getPropertyValue(cmConfigPath, "isCMPool").trim();
		if ("".equals(CMConstant.isCMPool)) {
			log.info("配置文件不存在isCMPool的属性值!");
			return false;
		}
		CMConstant.imageThreadNum = Integer.valueOf(configReader.getPropertyValue(cmConfigPath, "imageThreadNum").trim()) ;
		if ("".equals(CMConstant.imageThreadNum)) {
			log.info("配置文件不存在imageThreadNum的属性值!");
			return false;
		}
		
		//-----------------------------------------------------------------
		//批次CM查询缓存
		CMConstant.queryCacheFolder=GlobalVar.tempDir+File.separator+"CM_CACHE";
		
		//------------------------------------------------------------------
		log.info("********************读取CM配置信息****************");
		log.info("内容存储管理产品类型#CMType:" + GlobalVar.CMType);
		log.info("CMUser:" + CMConstant.CMUser);
		log.info("CMPwd:" + CMConstant.CMPwd);
		log.info("Resource Manage Name:" + CMConstant.RMN);
		log.info("initPool:" + CMConstant.initPool);
		log.info("maxPool:" + CMConstant.maxPool);
		log.info("minPool:" + CMConstant.minPool);
		log.info("属性字段截取长度#attrSubLen:" + CMConstant.attrSubLen);
		log.info("CM获取部件线程数#imageThreadNum:" + CMConstant.imageThreadNum);
		log.info("是否使用CM连接池 (0：不使用 ;1：使用 )#isCMPool:" + CMConstant.isCMPool);
		log.info("批次CM查询缓存路径:" + CMConstant.queryCacheFolder);
		log.info("**************************************************");
		return true;
	}

	/**
	 * 
	 *@Description 
	 *初始化FillNetCE配置信息
	 *@return
	 */
	public static boolean initFilNetCEGlobalVar() {
		CeConstant.maxResultNum = Integer.valueOf(configReader.getPropertyValue("/com/sunyard/insurance/filenet/ce/config/CEconfig.properties", "maxResultNum").trim()) ;
		if ("".equals(CeConstant.maxResultNum)) {
			log.info("配置文件不存在maxResultNum的属性值!");
			return false;
		}
		//------------------------------------------------------------------
		log.info("********************读取FileNet CE配置信息****************");
		log.info("批次查询缓存路径:" + GlobalVar.tempDir+File.separator+"CM_CACHE");
		log.info("********************************************************");
		return true;
	}
	/**
	 * 
	 *@Description 
	 *FAF
	 *@return
	 */
	public static boolean initFilNetForFAFGlobalVar() {
		CEConstant.maxResultNum = Integer.valueOf(configReader.getPropertyValue("/com/sunyard/insurance/filenet/faf/config/config.properties", "maxResultNum").trim()) ;
		if ("".equals(CEConstant.maxResultNum)) {
			log.info("配置文件不存在maxResultNum的属性值!");
			return false;
		}
		//-----------------------------------------------------------------
		//批次查询临时缓存路径
		CEConstant.queryZipFolder=GlobalVar.tempDir+CEConstant.queryZipFolder;
		//批次查询缓存
		CEConstant.queryCacheFolder=GlobalVar.tempDir+CEConstant.queryCacheFolder;
		
		//------------------------------------------------------------------
		log.info("********************读取FileNet For FAF配置信息****************");
		log.info("********************************************************");
		return true;
	}
	/**
	 *初始化扩展名对应的MiME type信息
	 */
	public static boolean initMimeTypeFromSuffix() {
		HashMap<String, String> mimeHm = new HashMap<String, String>();
		//获取配置文件所在目录
		String configPath = configReader.getPropertyValue("/config.properties","CONFIG_DIR");
		InputStream in = null;
		SAXBuilder builder = new SAXBuilder();
		try {
//			in = ConfigUtil.class.getResourceAsStream("/mime-config.xml");
			in = new FileInputStream(configPath+"/mime-config.xml");
			Document doc = builder.build(in);
			Element root = doc.getRootElement();
			List<?> mimeMapList = root.getChildren("mime-mapping");	
			Iterator<?> iter = mimeMapList.iterator();
			String extension = null;
			String mimeType = null;
			while (iter.hasNext()) {
				Element mimeMappElem = (Element) iter.next();
				extension = mimeMappElem.getChildText("extension").trim();
				mimeType = mimeMappElem.getChildText("mime-type").trim();
				mimeHm.put(extension, mimeType);
			}
			GlobalVar.mimeHasMap = mimeHm;
			log.info("成功初始化扩展名对应的MIME TYPE信息");
			return true;
		} catch (Exception e) {
			log.error("初始化扩展名对应的Mime type名称信息失败，原因为：", e);
			return false;
		} finally {
			try {
				if (null != in) {
					in.close();
					in = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
