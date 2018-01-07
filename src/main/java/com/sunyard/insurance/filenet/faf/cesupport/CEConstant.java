package com.sunyard.insurance.filenet.faf.cesupport;

import java.io.File;

public class CEConstant {

	public static final String DOC_TITLE = new String("DocumentTitle");
	
	public static final String BATCH_ID = new String("BATCH_ID");
	
	public static final String BUSI_NO = new String("BUSI_NUM");
	
	public static final String APP_CODE = new String("APP_CODE");
	
	public static final String SRC_NAME = new String("SRC_NAME");
	
	public static final String INNER_VER = new String("INNER_VER");
	
	public static final String GD_FLAG = new String("GD_FLAG");
	
	public static final String DEFAULT_DOCTYPE = new String("text/plain");
	
	public static int maxResultNum = 10;// CE查询最大条数
	
	public static int imageThreadNum = 35;//wzl add  CE多线程下载文件线程数
	
	public static String queryZipFolder = File.separator+"CM"+File.separator+"query"+File.separator+"zip";// 查询临时存放路径
	
	public static String queryCacheFolder = File.separator+"CM"+File.separator+"query"+File.separator+"cache";// 批次查询缓存路径
}
