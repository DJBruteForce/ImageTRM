package com.sunyard.insurance.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import com.sunyard.insurance.cmapi.util.CMConnectionPool;
import com.sunyard.insurance.entity.ServerInfoBean;
import com.sunyard.insurance.entity.TRM_TASK_CONFIG;
import com.sunyard.insurance.scheduler.JobsFactory;
import com.sunyard.insurance.util.EnvironmentConfig;
import com.sunyard.insurance.webServiceImpl.DBBusiServiceImpl;

public class GlobalVar {

	private static final Logger log = Logger.getLogger(GlobalVar.class);
	private static EnvironmentConfig configReader = EnvironmentConfig.getInstance();

	public static String serverId;
	public static String tempDir;// 临时文件夹
	public static String USMURL;
	public static String CMType;//内容管理产品类型
	public static Integer CMCatchDay;//内容管理查询缓存在本地存储天数 
	public static int socketMaxThreadPool;
	public static int recvsocketbuffersize;
	public static int sendsocketbuffersize;
	public static int transBuffersize;
	public static int socketSoTimeout;// socket连接超时时间
	public static ServerInfoBean svrInfoBean;
	public static String pushModel;//推送模式1：实时2定时
	public static int pushSleepTime;//实时推送休眠时间
	public static int isNewSocketTrance;//是否使用新的传输协议1:是0:否
	public static Map<String, String> informAppMap;// 需要到达通知的业务类型MAP<appCode,1>
	public static HashMap<String, String> mimeHasMap = null; // IMETYPE哈希表对象，键为文件扩展名，值为Mime
	public static List<TRM_TASK_CONFIG> taskList = new ArrayList<TRM_TASK_CONFIG>();;// 保存排程用的
	
	/** 太平增设的缓存目录 */
//	public static String extraCacheFolder = "";
	
	/** 太平FTP开关标志变量，默认FTP开关为关闭状态 */
	public static int isFTPOpen = 0;
	
	/** 太平FTP服务器IP地址 */
	public static String FTPHost = "";
	
	/** 太平FTP服务器端口，默认端口号为21 */
	public static int FTPPort = 21;
	
	/** 太平登录FTP服务器用户名 */
	public static String FTPUsername = "";
	
	/** 太平登录FTP服务器密码 */
	public static String FTPPassword = "";
	
	/** 太平设置FTP服务器根目录对应 */
	public static String RealRootPath = "";
	
	/** 太平增设的缓存目录中存放批次的缓存周期天数 */
	public static int extraCacheDays = 0;
	
	/** 大地多个Object Store分区支持开关状态标志 */
	public static int isDynamicObjectStoreOpen = 0;
	public static boolean initConfig() {
		//初始化基础配置文件
		boolean b = GlobalVar.loadConfigFile();
		if (!b) {
			return false;
		}	
		
		// 初始化扩展名对应的MiME type信息
		if (!ConfigUtil.initMimeTypeFromSuffix()) {
			return false;
		}

		// 获取数据库节点配置信息
		svrInfoBean = GlobalVar.getSvrInfo(serverId);
		if (null == svrInfoBean) {
			log.info("数据库查询没有获取到SVR_ID=[" + serverId + "]的配置信息!");
			return false;
		}

		// 获取到达通知配置
		informAppMap = GlobalVar.getSvrInformConfig(svrInfoBean.getOrgCode());
		if (null == informAppMap) {
			log.info("数据库查询获取到SVR_ID=[" + serverId + "]的影像到达通知配置异常!");
			return false;
		}
		
		// 是否存储内容管理产品；1-是，存储在CM；0-否，本地缓存
		if (svrInfoBean.getSaveInCM().equals("1")) {
			if("IBMCM".equals(GlobalVar.CMType)){
				log.info("=====影像批次存储在 IBM CM,开始初始化IBMCM配置信息=====");	
				try {
					// 初始化CM配置信息
					if (!ConfigUtil.initCMGlobalVar()) {
						return false;
					}
					// 初始化CM连接池
					if (!CMConstant.isCMPool.equals("0")) {
						CMConnectionPool.initConnectionPool(CMConstant.CMUser, CMConstant.CMPwd);
					}
	
				} catch (Exception e) {
					log.error("初始化CM配置文件参数失败");
					return false;
				}
			}else if("FileNetCE".equals(GlobalVar.CMType)){
				log.info("=====影像批次存储在FileNetCE,开始初始化FileNetCE配置信息=====");
				try{
					// 初始化CE配置信息
					if (!ConfigUtil.initFilNetCEGlobalVar()) {
						return false;
					}
					//TODO初始化连接池
					
					
				}catch (Exception e) {
					log.error("初始化FileNetCE配置文件参数失败");
					return false;
				}
			}else if("FileNetForFAF".equals(GlobalVar.CMType)){
				log.info("=====影像批次存储在FileNetCE,开始初始化FileNetCEForFAF配置信息=====");
				try{
					// 初始化CE配置信息
					if (!ConfigUtil.initFilNetForFAFGlobalVar()) {
						return false;
					}
					//TODO初始化连接池
				}catch (Exception e) {
					log.error("初始化FileNetCE配置文件参数失败");
					return false;
				}
			}
		} else {
			log.info("==========影像批次存储在本地==========");
		}

		// 以下为初始化排程
		JobsFactory.initScheduler();

		return true;
	}
	
	public static boolean loadConfigFile() {
		//获取配置文件所在目录
		String configPath = configReader.getPropertyValue("/config.properties","CONFIG_DIR");
		// 读取配置文件TRMConfig.properties
		String trmConfigPath = configPath+"/TRMConfig.properties";
		
		serverId = configReader.getPropertyValue(trmConfigPath,"serverId").trim();
		if ("".equals(serverId)) {
			log.info("配置文件不存在serverId的属性值!");
			return false;
		}

		tempDir = configReader.getPropertyValue(trmConfigPath,
				"tempDir").trim();

		if ("".equals(tempDir)) {
			log.info("配置文件不存在tempDir的属性值!");
			return false;
		}

		USMURL = configReader.getPropertyValue(trmConfigPath, "USMURL").trim();
		if ("".equals(USMURL)) {
			log.info("配置文件不存在USMURL的属性值!");
			return false;
		}

		socketMaxThreadPool = Integer.parseInt(configReader.getPropertyValue(
				trmConfigPath, "socketMaxThreadPool").trim());

		if ("".equals(socketMaxThreadPool)) {
			log.info("配置文件不存在socketMaxThreadPool的属性值!");
			return false;
		}

		recvsocketbuffersize = Integer.parseInt(configReader.getPropertyValue(
				trmConfigPath, "recvsocketbuffersize").trim());

		if ("".equals(recvsocketbuffersize)) {
			log.info("配置文件不存在recvsocketbuffersize的属性值!");
			return false;
		}

		sendsocketbuffersize = Integer.parseInt(configReader.getPropertyValue(
				trmConfigPath, "sendsocketbuffersize").trim());

		if ("".equals(sendsocketbuffersize)) {
			log.info("配置文件不存在sendsocketbuffersize的属性值!");
			return false;
		}

		transBuffersize = Integer.parseInt(configReader.getPropertyValue(
				trmConfigPath, "transBuffersize").trim());

		if ("".equals(transBuffersize)) {
			log.info("配置文件不存在transBuffersize的属性值!");
			return false;
		}

		socketSoTimeout = Integer.parseInt(configReader.getPropertyValue(
				trmConfigPath, "socketSoTimeout").trim());

		if ("".equals(socketSoTimeout)) {
			log.info("配置文件不存在socketSoTimeout的属性值!");
			return false;
		}
		
		pushModel = configReader.getPropertyValue(trmConfigPath, "pushModel").trim();
		if ("".equals(pushModel)) {
			log.info("配置文件不存在pushModel的属性值!");
			return false;
		}
		
		pushSleepTime = Integer.parseInt(configReader.getPropertyValue(trmConfigPath, "pushSleepTime").trim());
		if ("".equals(pushSleepTime)) {
			log.info("配置文件不存在pushSleepTime的属性值!");
			return false;
		}
		
		CMType = configReader.getPropertyValue(trmConfigPath, "CMType").trim();
		if ("".equals(CMType)) {
			log.info("配置文件不存在内容管理产品类型CMType的属性值，请查看配置文件TRMConfig.properties!");
			return false;
		}
		
		CMCatchDay = Integer.parseInt(configReader.getPropertyValue(trmConfigPath, "CMCatchDay").trim());
		if ("".equals(CMCatchDay) || 0==CMCatchDay) {
			log.info("配置文件不存在CMCatchDay的属性值，请查看配置文件TRMConfig.properties!");
			return false;
		}
		
		isNewSocketTrance = Integer.parseInt(configReader.getPropertyValue(trmConfigPath, "isNewSocketTrance").trim());
		if ("".equals(isNewSocketTrance)) {
			log.info("配置文件不存在isNewSocketTrance的属性值，请查看配置文件TRMConfig.properties!");
			return false;
		}
		
		isFTPOpen = Integer.parseInt(configReader.getPropertyValue(trmConfigPath, "isFTPOpen"));
		log.info("FTP开关状态：" + isFTPOpen + " (1:打开;0:关闭)");
		
		if(isFTPOpen == 1) {
			// 如果FTP开关打开，则读取登录FTP服务器的IP信息和用户信息
//			extraCacheFolder = configReader.getPropertyValue(trmConfigPath, "extraCacheFolder");
//			if("".equals(extraCacheFolder)) {
//				log.info("配置文件不存在增设的自定义缓存目录extraCacheFolder的属性值，请查看配置文件TRMConfig.properties!");
//				return false;
//			} else {
//				log.info("增设的自定义缓存目录路径：" + extraCacheFolder);
//			}
			
			FTPHost = configReader.getPropertyValue(trmConfigPath, "FTPHost");
			if ("".equals(FTPHost)) {
				log.info("配置文件FTP服务器IP地址为空，请查看配置文件TRMConfig.properties!");
				return false;
			} else {
				log.info("配置文件FTP服务器IP地址为：" + FTPHost);
			}
			
			String ftpPort = configReader.getPropertyValue(trmConfigPath, "FTPPort");
			if("".equals(ftpPort)) {
				log.info("配置文件FTP服务器端口为空，请查看配置文件TRMConfig.properties!");
				return false;
			} else {
				FTPPort = Integer.parseInt(ftpPort);
				log.info("配置文件FTP服务器端口为：" + FTPPort);
			}
			
			FTPUsername = configReader.getPropertyValue(trmConfigPath, "FTPUsername");
			if("".equals(FTPUsername)) {
				log.info("配置文件登录FTP服务器用户名为空，请查看配置文件TRMConfig.properties!");
				return false;
			} else {
				log.info("配置文件登录FTP服务器用户名为：" + FTPUsername);
			}
			
			FTPPassword = configReader.getPropertyValue(trmConfigPath, "FTPPassword");
			if("".equals(FTPPassword)) {
				log.info("配置文件登录FTP服务器密码为空，请查看配置文件TRMConfig.properties!");
				return false;
			} else {
				log.info("配置文件登录FTP服务器密码为：" + FTPPassword);
			}
			
			RealRootPath = configReader.getPropertyValue(trmConfigPath, "RealRootPath");
			if("".equals(RealRootPath)) {
				log.info("配置文件FTP服务器根目录实际路径为空，请查看配置文件TRMConfig.properties!");
				return false;
			} else {
				log.info("配置文件FTP服务器根目录实际路径为：" + RealRootPath);
			}
			
			extraCacheDays = Integer.parseInt(configReader.getPropertyValue(trmConfigPath, "extraCacheDays"));
			if(extraCacheDays == 0) {
				log.info("配置文件增设的缓存目录批次缓存天数为0天(禁止配置0天)，请查看配置文件TRMConfig.properties!");
				return false;
			} else {
				log.info("配置文件增设的缓存目录批次缓存天数：" + extraCacheDays);
			}
		}
		
		isDynamicObjectStoreOpen = Integer.parseInt(configReader.getPropertyValue(trmConfigPath, "isDynamicObjectStoreOpen"));
		log.info("动态Object Store开关状态：" + isDynamicObjectStoreOpen + " (1:打开;0:关闭)");
		
		return true;
	}

	/**
	 *@Description 获取服务器节点基本信息
	 *@param svrId
	 *@return
	 */
	public static ServerInfoBean getSvrInfo(String svrId) {
		return new DBBusiServiceImpl().getSvrInfo(svrId);
	}

	/**
	 * 
	 *@Description 获取节点影像到达通知配置
	 *@param orgCode
	 *@return
	 */
	public static Map<String, String> getSvrInformConfig(String orgCode) {
		List<String> appList = new DBBusiServiceImpl().findAllInformConfig(orgCode);
		Map<String, String> map = new HashMap<String, String>();
		if(null != appList) {
			for (int i = 0; i < appList.size(); i++) {
				map.put(appList.get(i), "1");
			}
		}
		return map;
	}
	
	
}
