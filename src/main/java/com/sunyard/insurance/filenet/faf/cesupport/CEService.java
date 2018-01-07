package com.sunyard.insurance.filenet.faf.cesupport;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.batch.bean.BatchFileBean;
import com.sunyard.insurance.cmapi.model.AttrInfo;
import com.sunyard.insurance.cmapi.model.ImgXmlInfo;
import com.sunyard.insurance.cmapi.model.cmquery.BatchVer;
import com.sunyard.insurance.cmapi.model.cmquery.InnerVer;
import com.sunyard.insurance.cmapi.model.cmquery.Result;
import com.sunyard.insurance.cmapi.model.cmquery.Results;
import com.sunyard.insurance.cmapi.util.BatchXmlProcess;
import com.sunyard.insurance.cmapi.util.CMCommonUtil;
import com.sunyard.insurance.common.CMConstant;
import com.sunyard.insurance.common.FileExtensionFileFilter;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.filenet.ce.depend.ExecPool;
import com.sunyard.insurance.filenet.ce.depend.ReadDocCall;
import com.sunyard.insurance.filenet.faf.cesupport.entity.CEConnBean;
import com.sunyard.insurance.filenet.faf.cesupport.entity.CEContent;
import com.sunyard.insurance.filenet.faf.cesupport.entity.CEContentSrcType;
import com.sunyard.insurance.filenet.faf.cesupport.entity.CEDocument;
import com.sunyard.insurance.filenet.faf.cesupport.util.FileUtil;
import com.sunyard.insurance.util.DateUtil;


public class CEService {
	private static final Logger log = Logger.getLogger(CEService.class);
	
	private static final String MARK = new String("$xxx");
	
	private static final String UPDATE_BATCH_START_LOG = new String("Update batch start, batchId=$xxx, appCode=$xxx.");
	
	private static final String UPDATE_BATCH_END_LOG = new String("Update batch end, batchId=$xxx, appCode=$xxx, flag=$xxx.");
	
	private static final String CREATE_BATCH_START_LOG = new String("Create batch start, batchId=$xxx, appCode=$xxx.");
	
	private static final String CREATE_BATCH_END_LOG = new String("Create batch end, batchId=$xxx, appCode=$xxx, flag=$xxx.");
	
	private static final String GET_ERRMSG_FAILDED_MSG = new String("Get error message failded, message=$xxx, i=$xxx, pos=$xxx, replace=$xxx.");
	
	private static final String GET_INNER_VERSION_FAILDED_MSG = new String("Get batch's inner version failded, batchId=$xxx.");
	
	private static final String SUBMIT_BATCH_FAILDED_MSG = new String("Submit batch failded, batchId=$xxx.");
	
	private static final String UPDATE_BATCH_ITEM_FAILDED_MSG = new String("Update batch failded, batchId=$xxx, appCode=$xxx, busiNo=$xxx, batchFolder=$xxx, innerVersion=$xxx.");
	
	private static final String CREATE_BATCH_ITEM_FAILDED_MSG = new String("Create batch failded, batchId=$xxx, appCode=$xxx, busiNo=$xxx, batchFolder=$xxx, innerVersion=$xxx.");
	
	private static final String QUERY_BATCH_FAILDED_MSG = new String("Query batch failded, appCode=$xxx, tempDir=$xxx.");
	
	private static final String DELETE_BATCH_FAILDED_MSG = new String("Delete batch failded, appCode=$xxx, busiNum=$xxx.");
	
	private static final String NOT_ARCHIVE = new String("0");
	
	
	/**
	 * 获取批次在CE的内部版本号
	 * @param BATCH_ID
	 * @param APP_CODE
	 * @return
	 * @throws Exception
	 */
	public static int getInnerVersion(String BATCH_ID, String APP_CODE) throws Exception {
		CEManager manager = null;
		CEConnBean connBean = null;
		int innerVer = 0;
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT d.INNER_VER FROM Document d");
		sql.append(" WHERE d.IsCurrentVersion=TRUE AND d.BATCH_ID='").append(BATCH_ID).append("'");
		try {
			manager = CEManager.getInstance();
			connBean = CEManager.getConnBean();
			List<Map<String, Object>> propsList = manager.searchCEProperties(connBean, sql.toString());
			if (propsList == null || propsList.size() <= 0) {
				innerVer = -1;
			} else {
				Map<String, Object> currentProps = propsList.get(0);
				String innerVerStr = String.valueOf(currentProps.get("INNER_VER"));
				innerVer = Integer.parseInt(innerVerStr);
			}
		} catch (Exception e) {
			throw new Exception(getErrMessage(GET_INNER_VERSION_FAILDED_MSG, BATCH_ID), e);
		}
		return innerVer;
	}
	
	/**
	 * 提交批次
	 * @param attrList
	 * @param BATCH_ID
	 * @param APP_CODE
	 * @param BUSI_NO
	 * @param SRC_NAME 路径
	 * @param INNER_VER
	 * @param isExist 是否是新增
	 * @return
	 */
	public static String submitBatch(Map<String, String> attrMap,BatchBean batchBean,boolean isExist) {
			// 批次提交是否成功标志，0成功，否则失败
			String flag = "0";
			String batchPath="";
			for(BatchFileBean bean : batchBean.getBatchFileList()){
				if(bean.getFileFullPath().endsWith(".syd")){
					batchPath=new File(bean.getFileFullPath()).getParent();
					break;
				}
			}
		try {
			if (isExist) {
				// 修改批次
				log.debug(getErrMessage(UPDATE_BATCH_START_LOG, batchBean.getBatch_id(), batchBean.getAppCode()));
				StopWatch watch = new Log4JStopWatch();
				flag = updateBatchItem(attrMap, batchBean,batchPath);
				log.debug(getErrMessage(UPDATE_BATCH_END_LOG, batchBean.getBatch_id(), batchBean.getAppCode(), flag));
				watch.stop("update CEDocument");
			} else {
				// 新增批次
				log.debug(getErrMessage(CREATE_BATCH_START_LOG, batchBean.getBatch_id(), batchBean.getAppCode()));
				StopWatch watch = new Log4JStopWatch();
				flag = createBatchItem(attrMap, batchBean,batchPath);
				log.debug(getErrMessage(CREATE_BATCH_END_LOG, batchBean.getBatch_id(), batchBean.getAppCode(), flag));
				watch.stop("create CEDocument");
			}
		} catch (Exception e) {
			log.error(getErrMessage(SUBMIT_BATCH_FAILDED_MSG, batchBean.getBatch_id()), e);
			return CMConstant.CM_IMG_CMPROCESS_CM_OPERATION_ERROR;
		}
		return flag;
	}
	
	/**
	 * 查询批次
	 * @param basePath
	 * @param attrList
	 * @param appCode
	 * @param tempDir
	 * @return
	 */
	public static Results queryBatch(String basePath, List<AttrInfo> attrList, String appCode) {
		Results results = new Results();
		List<Result> resultList = new ArrayList<Result>();
		int num = 0;
		StringBuilder searchSql = new StringBuilder();
		searchSql.append("SELECT d.BATCH_ID,d.BUSI_NUM, d.INNER_VER, d.ContentElements").append(" FROM Document d WHERE d.IsCurrentVersion=TRUE")
				.append(" AND d.APP_CODE='").append(appCode).append("'");
		
		if (attrList != null && attrList.size() > 0) {
			for (int i = 0; i < attrList.size(); i++) {
				AttrInfo info = attrList.get(i);
				searchSql.append(" AND d.").append(info.getAttrCode()).append("='").append(info.getAttrValue()).append("'");
			}
		}
		
		CEManager manager = null;
		CEConnBean ceConn = null;
		try {
			manager = CEManager.getInstance();
			ceConn = CEManager.getConnBean();
			List<CEDocument> ceDocList = manager.searchCEDocuments(ceConn, searchSql.toString());
			if (ceDocList == null || ceDocList.size() <= 0) {
				// 集合为空，CE中无指定批次或批次状态为忙，返回错误
				log.info("批次查询，查询条件："+searchSql.toString()+"，集合为空，CE中无指定批次");
				results.setResCode(CMConstant.CM_IMG_CMPORCESS_QUERY_RESULT_ISNULL);
				return results;
			} else if (ceDocList.size() > CEConstant.maxResultNum) {
				//查询返回的结果集数量太大，精确查询条件
				log.error("批次查询，查询条件："+searchSql.toString()+"，返回的结果集数量太大,超过了10条");
				results.setResCode(CMConstant.CM_IMG_CMPROCESS_QUERY_RESULT_SET_TOO_LARGE);
				return results;
			}
			
			for (int i = 0; i < ceDocList.size(); i++) {
				CEDocument ceDoc = ceDocList.get(i);
				//获取CE中批次号
				String batchID = String.valueOf(ceDoc.getProperties().get(CEConstant.BATCH_ID));
				//获取CE中的业务号
				String busiNo = String.valueOf(ceDoc.getProperties().get(CEConstant.BUSI_NO));
				//获取CE中的内部版本号
				String innerVer = String.valueOf(ceDoc.getProperties().get(CEConstant.INNER_VER));
				
				//批次在各缓存目录下最高版本的存储情况
				String CM_CACHE_DIR = GlobalVar.tempDir+File.separator+"CM_CACHE";
				
				Map<Integer,String> batchVerPath = new HashMap<Integer,String>();
				for(int k=0;k<GlobalVar.CMCatchDay;k++) {
					StringBuilder catchDirStr = new StringBuilder();
					catchDirStr.append(CM_CACHE_DIR+File.separator);
					catchDirStr.append(DateUtil.getDateBefAft(k, "yyyyMMdd")+File.separator);
					catchDirStr.append(batchID.substring(0,1)+File.separator+batchID.substring(1,2)+File.separator);
					catchDirStr.append(batchID);
					File catchDirFile = new File(catchDirStr.toString());
					
					int maxVerNum = 0;
					if(catchDirFile.exists()) {
						FileFilter sydFilterCache = new FileExtensionFileFilter("syd");
						//列出该缓存目录下所有的syd文件
					    File[] sydFilesCache = catchDirFile.listFiles(sydFilterCache);
					    if(sydFilesCache.length!=0){
						    for(int m=0;m<sydFilesCache.length;m++){
						    	String fileName = sydFilesCache[m].getName();
						    	int start = fileName.indexOf("_");
						    	int end = fileName.indexOf(".");
						    	int sydVer = Integer.parseInt(fileName.substring(start+1,end));
						    	if(sydVer>maxVerNum) {
						    		maxVerNum = sydVer;
						    	}
						    }
					    }
					}
					
					//将结果记录到MAP
					if(Integer.parseInt(innerVer)==maxVerNum) {
						batchVerPath.put(maxVerNum, catchDirStr.toString());
						break;
					}else if((maxVerNum>0) && (null == batchVerPath.get(maxVerNum))) {
						batchVerPath.put(maxVerNum, catchDirStr.toString());
					}
				}
				
				
				if(null != batchVerPath.get(Integer.parseInt(innerVer))) {
					//本地缓存中有最大版本的批次文件
					String batchCatchDir = batchVerPath.get(Integer.parseInt(innerVer));
					
					List<BatchVer> batchVerList = new ArrayList<BatchVer>();//组装批次
				    Result result = new Result();
				    /******************（返回报文添加内部版本号）****************/
				    List<InnerVer> innerVerList = new ArrayList<InnerVer>();
				    InnerVer innerVerInfo = new InnerVer();
				    innerVerInfo.setValue(innerVer+"");
				    innerVerList.add(innerVerInfo);
				    result.setInnerVers(innerVerList);
				    /********************************************************/
				    for(int ver = Integer.parseInt(innerVer);ver>0;ver--) {
				    	BatchVer batchVerInfo = new BatchVer();
				    	batchVerInfo.setValue(""+ver);
				    	batchVerList.add(batchVerInfo);
				    }
				    //组装数据
				    result.setBatchID(batchID);
				    result.setAppCode(appCode);
				    result.setBusiNo(busiNo);
				    result.setId(Integer.toString(num));
				    num++;
				    result.setXmlPath(basePath+"servlet/GetImage?filename="+batchCatchDir);
				    result.setBatchVers(batchVerList);
				    resultList.add(result);
				    continue;
				}else {
					//本地缓存中没有最大版本的批次文件
					String batchCatchDir;
					int catchMaxVer = 0;
					for (Iterator<Integer> batchVerIter = batchVerPath.keySet().iterator(); batchVerIter.hasNext();) {
						Integer key = batchVerIter.next();
						if(key>catchMaxVer) {
							catchMaxVer = key;
						}
					}
					if(catchMaxVer == 0) {
						//表示缓存中没有任何版本的缓存文件，需要向CE重新下载,定义缓存目录
						StringBuilder strNew = new StringBuilder();
						strNew.append(CM_CACHE_DIR+File.separator);
						strNew.append(DateUtil.getDateStrCompact()+File.separator);
						strNew.append(batchID.substring(0,1)+File.separator+batchID.substring(1,2)+File.separator);
						strNew.append(batchID);
						batchCatchDir = strNew.toString();
						
					} else {
						//表示缓存中有部分版本的文件，只需增量下载文件到已有目录即可
						batchCatchDir = batchVerPath.get(catchMaxVer);
					}
					
					//下载文件,先判断文件夹是否需要创建
					File batchCatchFile = new File(batchCatchDir);
					if(!batchCatchFile.exists()) {
						batchCatchFile.mkdirs();
					}
					List<CEContent> contentList = ceDoc.getContentList();
					if (contentList == null || contentList.size() <= 0) {
						log.error("批次查询失败！查询条件："+searchSql.toString()+"，Error：指定批次不存在文档部件属性");
						results.setResCode(CMConstant.CM_IMG_CMPROCESS_ATTRIBUTE_NOT_EXIST_ERROR);
					} else {
						List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
						for (int j = 0; j < contentList.size(); j++) {
							CEContent content = contentList.get(j);
							InputStream in = content.getContentInput();
							String sourceID = content.getContentName();
							String filePath = batchCatchDir+File.separator+sourceID;
							File file = new File(filePath);
//							FileUtil.writeFileWithInputStream(in, file, false);//文件存在就覆盖
							
							ReadDocCall call = new ReadDocCall(in, file);
							tasks.add(call);
						}
						if (tasks != null && !tasks.isEmpty()) {
							ExecPool pool = new ExecPool();
							pool.execute(tasks);
						}
						
					}
					
					//下载完成，拼接返回结果
					List<BatchVer> batchVerList = new ArrayList<BatchVer>();//组装批次
				    Result result = new Result();
				    /******************（返回报文添加内部版本号）****************/
				    List<InnerVer> innerVerList = new ArrayList<InnerVer>();
				    InnerVer innerVerInfo = new InnerVer();
				    innerVerInfo.setValue(innerVer+"");
				    innerVerList.add(innerVerInfo);
				    result.setInnerVers(innerVerList);
				    /********************************************************/
				    for(int ver = Integer.parseInt(innerVer);ver>0;ver--) {
				    	BatchVer batchVerInfo = new BatchVer();
				    	batchVerInfo.setValue(""+ver);
				    	batchVerList.add(batchVerInfo);
				    }
				    //组装数据
				    result.setBatchID(batchID);
				    result.setAppCode(appCode);
				    result.setBusiNo(busiNo);
				    result.setId(Integer.toString(num));
				    num++;
				    result.setXmlPath(basePath+"servlet/GetImage?filename="+batchCatchDir);
				    result.setBatchVers(batchVerList);
				    resultList.add(result);
				    
				}
				
			}
			
			results.setResCode("1");
			results.setResultList(resultList);
		} catch (Exception e) {
			log.error("FileNetCEFAF查询过程发生异常，查询报文："+searchSql.toString(),e);
			results.setResCode(CMConstant.CM_IMG_CMPORCESS_QUERY_FAIL_ERROR);
			return results;
		} finally {
			manager.authOutCEngine();
		}
		return results;
	}
	
	/**
	 * 查询批次
	 * @param basePath
	 * @param attrList
	 * @param appCode
	 * @param tempDir
	 * @return
	 */
	public static Results queryBatch_old(String basePath, List<AttrInfo> attrList, String appCode, String tempDir) {
		Results results = new Results();
		List<Result> resultList = new ArrayList<Result>();
		BatchXmlProcess batchXmlProcess = new BatchXmlProcess();
		int num = 0;
		StringBuilder searchSql = new StringBuilder();
		searchSql.append("SELECT d.BATCH_ID, d.INNER_VER, d.ContentElements").append(" FROM Document d WHERE d.IsCurrentVersion=TRUE")
				.append(" AND d.APP_CODE='").append(appCode).append("'");
		
		if (attrList != null && attrList.size() > 0) {
			for (int i = 0; i < attrList.size(); i++) {
				AttrInfo info = attrList.get(i);
				searchSql.append(" AND d.").append(info.getAttrCode()).append("='").append(info.getAttrValue()).append("'");
			}
		}
		
		CEManager manager = null;
		CEConnBean ceConn = null;
		try {
			manager = CEManager.getInstance();
			ceConn = CEManager.getConnBean();
			List<CEDocument> ceDocList = manager.searchCEDocuments(ceConn, searchSql.toString());
			if (ceDocList == null || ceDocList.size() <= 0) {
				// 集合为空，CE中无指定批次或批次状态为忙，返回错误
				log.info("批次查询，查询条件："+searchSql.toString()+"，集合为空，CE中无指定批次");
				results.setResCode(CMConstant.CM_IMG_CMPORCESS_QUERY_RESULT_ISNULL);
				return results;
			} else if (ceDocList.size() > CEConstant.maxResultNum) {
				//查询返回的结果集数量太大，精确查询条件
				log.error("批次查询，查询条件："+searchSql.toString()+"，返回的结果集数量太大,超过了10条");
				results.setResCode(CMConstant.CM_IMG_CMPROCESS_QUERY_RESULT_SET_TOO_LARGE);
				return results;
			}
			
			for (int i = 0; i < ceDocList.size(); i++) {
				CEDocument ceDoc = ceDocList.get(i);
				StopWatch Watch = new Log4JStopWatch();
				//获取CM中批次号
				String batchID = String.valueOf(ceDoc.getProperties().get(CEConstant.BATCH_ID));
				//获取CM中的内部版本号
				String innerVer = String.valueOf(ceDoc.getProperties().get(CEConstant.INNER_VER));
				//判断该批次在缓存中是否存在，且是否和CM中的内部版本号一致，如果一致则直接用缓存中的批次组装数据
				String batchCachePath = CEConstant.queryCacheFolder+File.separator+batchID;
				File batchCacheFile = new File(batchCachePath);
				if(batchCacheFile.exists()){
					FileFilter sydFilterCache = new FileExtensionFileFilter("syd");       
				    File[] sydFilesCache = batchCacheFile.listFiles(sydFilterCache);
				    if(sydFilesCache.length!=0){
				    	int[] batchVersCahce = new int[sydFilesCache.length];
					    for(int m=0;m<sydFilesCache.length;m++){
					    	String fileName = sydFilesCache[m].getName();
					    	int start = fileName.indexOf("_");
					    	int end = fileName.indexOf(".");
					    	int batchVer = Integer.parseInt(fileName.substring(start+1,end));
					    	batchVersCahce[m] = batchVer;
					    }
					    //排序批次号
					    CMCommonUtil.bubbleSort(batchVersCahce,"desc");
					    File maxVerXmlCache = new File(batchCachePath+File.separator+batchID+"_"+batchVersCahce[0]+".syd");
					    ImgXmlInfo imgXmlInfoCache = batchXmlProcess.readImgXml(maxVerXmlCache,batchID);
					    int innerVerCache = imgXmlInfoCache.getInnerVer();
					    if(innerVerCache==Integer.parseInt(innerVer)){
					    	//组装批次
						    List<BatchVer> batchVerList = new ArrayList<BatchVer>();
						    Result result = new Result();
						    for(int n=0;n<batchVersCahce.length;n++){
						    	BatchVer batchVerInfo = new BatchVer();
						    	batchVerInfo.setValue(Integer.toString(batchVersCahce[n]));
						    	batchVerList.add(batchVerInfo);
						    }
						    //组装数据
						    String busiNo = imgXmlInfoCache.getBusiNo();
						    result.setBatchID(batchID);
						    result.setAppCode(appCode);
						    result.setBusiNo(busiNo);
						    result.setId(Integer.toString(num));
						    num++;
						    result.setXmlPath(basePath+"servlet/GetImage?filename="+CEConstant.queryCacheFolder+File.separator+batchID);
						    result.setBatchVers(batchVerList);
						    resultList.add(result);
						    continue;
					    }
				    }
				}
				
				//组装完后的批次临时存放路径
				String dir = tempDir+File.separator+batchID;
				File tempFile = new File(dir);
				if(!tempFile.exists()){
					tempFile.mkdirs();
				}
				
				List<CEContent> contentList = ceDoc.getContentList();
				if (contentList == null || contentList.size() <= 0) {
					log.error("批次查询失败！查询条件："+searchSql.toString()+"，Error：指定批次不存在文档部件属性");
					results.setResCode(CMConstant.CM_IMG_CMPROCESS_ATTRIBUTE_NOT_EXIST_ERROR);
					return results;
				}
				
				for (int j = 0; j < contentList.size(); j++) {
					CEContent content = contentList.get(j);
					StopWatch imgWatch = new Log4JStopWatch();
					InputStream in = content.getContentInput();
					String sourceID = content.getContentName();
					String filePath = dir+File.separator+sourceID;
					File file = new File(filePath);
					FileUtil.writeFileWithInputStream(in, file, false);//文件存在就覆盖
					imgWatch.stop("获取部件*");
				}
				Watch.stop("获取文档*");
			}
			results.setResCode("1");
			results.setResultList(resultList);
		} catch (Exception e) {
			log.error(getErrMessage(QUERY_BATCH_FAILDED_MSG, appCode, tempDir), e);
			results.setResCode(CMConstant.CM_IMG_CMPORCESS_QUERY_FAIL_ERROR);
			return results;
		} finally {
			manager.authOutCEngine();
		}
		return results;
	}
	
	
	/**
	 * 新增批次
	 * @param attrList
	 * @param BATCH_ID
	 * @param APP_CODE
	 * @param BUSI_NO
	 * @param SRC_NAME
	 * @param INNER_VER
	 * @return
	 * @throws Exception
	 */
	private static String createBatchItem(Map<String, String> attrMap,BatchBean batchBean,String SRC_NAME) throws Exception {
		StringBuilder docTitle = new StringBuilder();
		docTitle.append(batchBean.getBusi_no()).append("_").append(batchBean.getAppCode()).toString();
		String GD_FLAG = NOT_ARCHIVE;
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(CEConstant.BATCH_ID, batchBean.getBatch_id());
		properties.put(CEConstant.APP_CODE, batchBean.getAppCode());
		properties.put(CEConstant.BUSI_NO, batchBean.getBusi_no());
		properties.put(CEConstant.INNER_VER, batchBean.getInter_ver());
		properties.put(CEConstant.SRC_NAME, SRC_NAME);
		properties.put(CEConstant.GD_FLAG, GD_FLAG);
		properties.put(CEConstant.DOC_TITLE, docTitle.toString());
		
		if(null!=attrMap){
			Set<String>  keySet =  attrMap.keySet();
			for(Iterator<String> iter = keySet.iterator();iter.hasNext();){
				String mapKey = (String) iter.next();
				//这两个KEY名称的字段的排除之外
				if(!(mapKey.toUpperCase().equals("BUSI_NO") || mapKey.toUpperCase().equals("BUSI_NUM"))) {
					properties.put(mapKey, attrMap.get(mapKey));
				}
			}
		}
		
		List<CEContent> contentList = new ArrayList<CEContent>();
		for (int i = 0; i < batchBean.getBatchFileList().size(); i++) {
			BatchFileBean fileBean = batchBean.getBatchFileList().get(i);
			// 取得文件名
			String fileName = fileBean.getFileName();
			// 取得文件的后缀名
			String suffixName = FileUtil.getFileSuffix(fileBean.getFileName());
			// 取得文件的MIME TYPE
			String mimeType = GlobalVar.mimeHasMap.get(suffixName);
			if (mimeType == null || "".equals(mimeType)) {
				mimeType = CEConstant.DEFAULT_DOCTYPE;
			}

			CEContent ceContent = new CEContent(CEContentSrcType.TRANSFER,fileName, mimeType, new FileInputStream(fileBean.getFileFullPath()));
			contentList.add(ceContent);
		}
		
		CEManager manager = null;
		CEConnBean ceConn = null;
		try {
			manager = CEManager.getInstance();
			ceConn = CEManager.getConnBean();
			CEDocument ceDoc = new CEDocument(CEConfig.DOCUMENT_CLASS,CEConstant.DEFAULT_DOCTYPE, CEConfig.MAIN_FOLDER, batchBean.getBatch_id(), properties, contentList);
			manager.createCEDocument(ceConn, ceDoc);//创建CEDocment
		} catch (Exception e) {
			log.error(getErrMessage(CREATE_BATCH_ITEM_FAILDED_MSG, batchBean.getBatch_id(), batchBean.getAppCode(), batchBean.getBusi_no(),SRC_NAME, batchBean.getInter_ver()), e);
			throw e;
		}
		    return "0";
	}
	/**
	 * 修改批次
	 * @param attrList
	 * @param BATCH_ID
	 * @param APP_CODE
	 * @param BUSI_NO
	 * @param SRC_NAME
	 * @param INNER_VER
	 * @return
	 * @throws Exception
	 */
	private static String updateBatchItem(Map<String, String> attrMap,BatchBean batchBean, String SRC_NAME) throws Exception {
		StringBuilder DOC_TITLE = new StringBuilder();
		DOC_TITLE.append(batchBean.getBusi_no()).append("_").append(batchBean.getAppCode()).toString();//DocumentTitle+BUSI_NO+APP_CODE
		String GD_FLAG = NOT_ARCHIVE;
		
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(CEConstant.BATCH_ID, batchBean.getBatch_id());
		properties.put(CEConstant.APP_CODE, batchBean.getAppCode());
		properties.put(CEConstant.BUSI_NO, batchBean.getBusi_no());
		properties.put(CEConstant.INNER_VER, batchBean.getInter_ver());
		properties.put(CEConstant.SRC_NAME, SRC_NAME);
		properties.put(CEConstant.GD_FLAG, GD_FLAG);
		properties.put(CEConstant.DOC_TITLE, DOC_TITLE.toString());
		if(null != attrMap){
			Set<String>  keySet =  attrMap.keySet();
			for(Iterator<String> iter = keySet.iterator();iter.hasNext();){
				String mapKey = (String) iter.next();
				//这两个KEY名称的字段的排除之外
				if(!(mapKey.toUpperCase().equals("BUSI_NO") || mapKey.toUpperCase().equals("BUSI_NUM"))) {
					properties.put(mapKey, attrMap.get(mapKey));
				}
			}
		}
		
		Set<String> imgSet = new HashSet<String>();
		List<CEContent> contentList = new ArrayList<CEContent>();
		for (int i = 0; i < batchBean.getBatchFileList().size(); i++) {
			BatchFileBean fileBean = batchBean.getBatchFileList().get(i);
			// 取得文件名
			String fileName = fileBean.getFileName();
			// 取得文件的后缀名
			String suffixName = FileUtil.getFileSuffix(fileBean.getFileName());
			// 取得文件的MIME TYPE
			String mimeType = GlobalVar.mimeHasMap.get(suffixName);
			if (mimeType == null || "".equals(mimeType)) {
				mimeType = CEConstant.DEFAULT_DOCTYPE;
			}
			CEContent ceContent = new CEContent(CEContentSrcType.TRANSFER,fileName, mimeType, new FileInputStream(fileBean.getFileFullPath()));
			contentList.add(ceContent);
			imgSet.add(fileName);
		}

		StringBuilder searchSql = new StringBuilder();
		searchSql.append("SELECT d.BATCH_ID, d.ContentElements FROM Document d");
		searchSql.append(" WHERE d.IsCurrentVersion=TRUE AND d.BATCH_ID='").append(batchBean.getBatch_id()).append("'");
		
		StringBuilder modifySql = new StringBuilder();
		modifySql.append("SELECT d.Reservation FROM Document d");
		modifySql.append(" WHERE d.IsCurrentVersion=TRUE AND d.BATCH_ID='").append(batchBean.getBatch_id()).append("'");
		
		CEManager manager = null;
		CEConnBean ceConn = null;
		try {
			manager = CEManager.getInstance();
			ceConn = CEManager.getConnBean();
			List<CEDocument> ceDocList = manager.searchCEDocuments(ceConn, searchSql.toString());
			CEDocument currentDoc = ceDocList.get(0);
			List<CEContent> curContentList = currentDoc.getContentList();
			for(int i = curContentList.size() - 1; i >= 0; i--) {
				CEContent cec = curContentList.get(i);
				String contentName = cec.getContentName();
				if(imgSet.contains(contentName)) {//
					curContentList.remove(i);
				}
			}
			contentList.addAll(curContentList);
			CEDocument ceDoc = new CEDocument(CEConfig.DOCUMENT_CLASS,CEConstant.DEFAULT_DOCTYPE, CEConfig.MAIN_FOLDER,batchBean.getBatch_id(), properties, contentList);
			manager.modifyCEDocument(ceConn, ceDoc, modifySql.toString());//修改CEDocment
		} catch (Exception e) {
			log.error(getErrMessage(UPDATE_BATCH_ITEM_FAILDED_MSG, batchBean.getBatch_id(), batchBean.getAppCode(), batchBean.getBusi_no(),SRC_NAME, batchBean.getInter_ver()), e);
			throw e;
		}
		return "0";
	}

	
//---------------------------------------------------------------------------------------------------------
	private static String getErrMessage(String message, Object... args) {
		StringBuilder msg = new StringBuilder();
		msg.append(message);
		if (args != null && args.length > 0) {
			for (int i = 0, pos = 0; i < args.length; i++) {
				String str = String.valueOf(args[i]);
				int start = msg.indexOf(MARK, pos);
				int end = start + MARK.length();
				if (start == -1 || end >= msg.length()) {
					throw new RuntimeException(getErrMessage(GET_ERRMSG_FAILDED_MSG, message, i, pos, str));
				} else {
					msg.replace(start, end, str);
					pos = start + str.length();
				}
			}
		}
		return msg.toString();
	}
	
	/**
	 * 删除批次
	 * @param appCode
	 * @param busiNum
	 * @return
	 */
	public static String deleteBatch(String appCode, String busiNum) {
		CEManager manager = null;
		CEConnBean connBean = null;
		String resultCode = "1";
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT d.BATCH_ID FROM Document d WITH INCLUDESUBCLASSES");
		sql.append(" WHERE d.IsCurrentVersion=TRUE AND d.APP_CODE='");
		sql.append(appCode).append("' AND d.BUSI_NUM='");
		sql.append(busiNum).append("'");
		try {
			manager = CEManager.getInstance();
			connBean = CEManager.getConnBean();
			List<Map<String, Object>> propsList = manager.searchCEProperties(connBean, sql.toString());
			if (propsList == null || propsList.size() <= 0) {
				resultCode = "0";
			} else {
				for (int i = 0; i < propsList.size(); i++) {
					Map<String, Object> props = propsList.get(i);
					String batchId = String.valueOf(props.get("BATCH_ID"));
					String path = CEConfig.MAIN_FOLDER + "/" + batchId;
					manager.deleteFolderByPath(connBean, path);
				}
			}
		} catch (Exception e) {
			log.error(getErrMessage(DELETE_BATCH_FAILDED_MSG, appCode, busiNum), e);
			resultCode = "-1";
		}
		    return resultCode;
	}
	
}
