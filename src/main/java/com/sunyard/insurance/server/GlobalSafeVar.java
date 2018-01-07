package com.sunyard.insurance.server;

import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.Logger;

public class GlobalSafeVar
{
  private static final Logger log = Logger.getLogger(GlobalSafeVar.class);
  public static String SAFE_FILE_PATH;
  public static String ENCODE_KEY;
  public static String CONFIG_PATH;

  public static boolean loadConfigFile()
  {
    try
    {
      CONFIG_PATH = EncodeUrl.getConfigValue("config.properties", "CONFIG_DIR");
      Properties props = EncodeUrl.getConfig("SafeConfig.properties");
      SAFE_FILE_PATH = props.getProperty("SAFE_FILE_PATH").trim();
      if ("".equals(SAFE_FILE_PATH)) {
        log.info("配置文件不存在SAFE_FILE_PATH的属性值!");
        return false;
      }
      SAFE_FILE_PATH.replace("\\", "/");

      ENCODE_KEY = props.getProperty("ENCODE_KEY").trim();
      if ("".equals(ENCODE_KEY)) {
        log.info("配置文件不存在ENCODE_KEY的属性值!");
        return false;
      }
    } catch (IOException e) {
      log.error("初始化URL安全参数失败：" + e);
    }
    return true;
  }
}