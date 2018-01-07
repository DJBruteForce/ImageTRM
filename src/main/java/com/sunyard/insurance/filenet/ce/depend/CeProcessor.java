package com.sunyard.insurance.filenet.ce.depend;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.FolderSet;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.property.Properties;
import com.ibm.filenet.helper.ce.ObjectStoreProvider;
import com.ibm.filenet.helper.ce.util.UserContextUtils;
import com.sunyard.insurance.cmapi.model.AttrInfo;
import com.sunyard.insurance.cmapi.model.cmquery.BatchVer;
import com.sunyard.insurance.cmapi.model.cmquery.InnerVer;
import com.sunyard.insurance.cmapi.model.cmquery.Result;
import com.sunyard.insurance.cmapi.model.cmquery.Results;
import com.sunyard.insurance.common.FileExtensionFileFilter;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.entity.B_OBJECT_STORE;
import com.sunyard.insurance.filenet.ce.FnConfigOptions;
import com.sunyard.insurance.filenet.ce.common.constant.CeConstant;
import com.sunyard.insurance.filenet.ce.common.constant.CeMsg;
import com.sunyard.insurance.filenet.ce.common.constant.CeProperty;
import com.sunyard.insurance.filenet.ce.common.constant.CeSql;
import com.sunyard.insurance.filenet.ce.model.BatchBean;
import com.sunyard.insurance.filenet.ce.model.DocBean;
import com.sunyard.insurance.filenet.ce.model.PageBean;
import com.sunyard.insurance.filenet.ce.model.PageExtBean;
import com.sunyard.insurance.filenet.ce.model.PropBean;
import com.sunyard.insurance.filenet.ce.proxy.CeBatchProxy;
import com.sunyard.insurance.filenet.ce.proxy.CeProxy;
import com.sunyard.insurance.filenet.ce.util.FileUtil;
import com.sunyard.insurance.filenet.ce.util.StringUtil;
import com.sunyard.insurance.util.DateUtil;
import com.sunyard.insurance.util.NumberUtil;
import com.sunyard.insurance.webService.DBBusiService;
import com.sunyard.insurance.webServiceImpl.DBBusiServiceImpl;


public class CeProcessor {
	
	private static final Logger log = Logger.getLogger(CeProcessor.class);
	private ExecPool pool = new ExecPool();
	private DBBusiService dbService = new DBBusiServiceImpl();
	public CeProcessor() {
		super();
	}
	
	/**
	 * 
	 *@Description 
	 *获取CE内部版本
	 *@param batchId
	 *@return
	 *@throws Exception
	 */
	public int getInnerVersion(String batchId, String appCode, String busiDate) throws Exception {
		int innerVer = 0;
		String sql = null;
		long startTime = System.currentTimeMillis();
		try {
			CeProxy proxy = null;
			if(GlobalVar.isDynamicObjectStoreOpen == 1) {
				String objectStoreName = "";
				B_OBJECT_STORE objectStore = dbService.findObjectStoreByAppCodeAndBusiDate(appCode, busiDate);
				if(objectStore == null) {
					objectStore = dbService.findObjectStoreByAppCodeAndBusiDate("default", busiDate);
				}
				
				if(objectStore != null) {
					objectStoreName = objectStore.getObjectStoreName();
					proxy = CeProxy.authIn(objectStoreName);
					log.info("获取内部版本号，连接的ObjectStoreName为：" + objectStoreName);
				} else {
					proxy = CeProxy.authIn();
					log.info("获取内部版本号，连接的ObjectStoreName为FileNetCEConfig.properties文件中配置的名称");
				}
			} else {
				proxy = CeProxy.authIn();
				log.info("获取内部版本号，连接的ObjectStoreName为FileNetCEConfig.properties文件中配置的名称");
			}
			sql = CeMsg.getMessage(CeSql.QUERY_INNER_VER, batchId);
			List<Properties> propsList = proxy.searchProperties(sql);
			if (propsList == null || propsList.isEmpty()) {
				innerVer = -1;
			} else {
				Properties currentProps = propsList.get(0);
				String innerVerStr = String.valueOf(currentProps.getStringValue(CeProperty.INNER_VER));
				innerVer = Integer.parseInt(innerVerStr);
			}
		} catch (Exception e) {
			throw new Exception(CeMsg.getMessage(CeMsg.GET_INNER_VERSION_ERR, sql), e);
		} finally {
			CeProxy.authOut();
		}

		log.debug("CE获取批次["+batchId+"]内部版本号耗时:"+(System.currentTimeMillis()- startTime));
		return innerVer;
	}
	
	/**
	 * 
	 *@Description 
	 *提交批次
	 *批次提交是否成功标志，0成功，否则失败
	 *@param docBean
	 *@param batchId
	 *@param batchPath
	 *@return
	 */
	public String createBatch(DocBean docBean, String batchId, String batchPath) {
		String flag = "0";
		CeBatchProxy proxy = null;
		String batchFldId = null;
		long startTime = System.currentTimeMillis();
		try {
			TimeCounter tc = TimeCounter.record();
			if(GlobalVar.isDynamicObjectStoreOpen == 1) {
				String objectStoreName = "";
				String appCode = docBean.getBatchBean().getAppCode();
				String busiDate = docBean.getBatchBean().getBusi_date();
				B_OBJECT_STORE objectStore = dbService.findObjectStoreByAppCodeAndBusiDate(appCode, busiDate);
				if(objectStore == null) {
					objectStore = dbService.findObjectStoreByAppCodeAndBusiDate("default", busiDate);
				}
				if(objectStore != null) {
					objectStoreName = objectStore.getObjectStoreName();
					proxy = CeBatchProxy.authIn(objectStoreName);
					log.info("createBatch，连接的ObjectStoreName为：" + objectStoreName);
				} else {
					proxy = CeBatchProxy.authIn();
					log.info("createBatch，连接的ObjectStoreName为FileNetCEConfig.properties文件中配置的名称");
				}
			} else {
				proxy = CeBatchProxy.authIn();
				log.info("createBatch，连接的ObjectStoreName为FileNetCEConfig.properties文件中配置的名称");
			}
			log.info("耗时阶段1(CeBatchProxy.authIn())："+(System.currentTimeMillis()-startTime));
			String gdFlag = CeConstant.NOT_ARCHIVE;
			String docClass = FnConfigOptions.getDocClsName();
			BatchBean batchBean = docBean.getBatchBean();
			List<PageBean> pageList = docBean.getPageList();
			List<PropBean> propList = docBean.getPropList();
			Map<String, String> nodeMap = docBean.getNodeMap();
			Map<String, Object> fldProps = getFldProps(batchBean, propList);//影像存储文件夹 批次的属性=值的map
			fldProps.put(CeProperty.GD_FLAG, gdFlag);
			
			startTime = System.currentTimeMillis();
			
			batchFldId = createBatchFolder(proxy, fldProps);//创建批次存储文件夹
			String sydSrcPath = batchPath + '/' + docBean.getSydFileName(); 
			String sydSrcName = docBean.getSydFileName();//syd文件名称
			ContentElementList sydContentList = getDocContents(sydSrcPath);//创建文档内容到CE
			proxy.createDocument(docClass, sydSrcName, sydContentList, null, batchFldId);//创建批次的document
			log.info("耗时阶段2："+(System.currentTimeMillis()-startTime));
			
			startTime = System.currentTimeMillis();
			for (PageBean pageBean : pageList) {
				String pageId = pageBean.getPageId();
				String pageSrcUrl = batchPath + '/' + pageBean.getPageUrl();
				String thumSrcUrl = batchPath + '/' + pageBean.getThumUrl();
				ContentElementList pageContentList = getDocContents(pageSrcUrl, thumSrcUrl);//创建文档内容到CE
				Map<String, Object> docProps = getDocProps(pageBean, nodeMap);
				docProps.put(CeProperty.BUSI_NO, batchBean.getBusiNo());
				proxy.createDocument(docClass, pageId, pageContentList, docProps, batchFldId);//创建批次的document
			}
			log.info("耗时阶段3："+(System.currentTimeMillis()-startTime));
			startTime = System.currentTimeMillis();
			proxy.updateBatch();
			log.info("耗时阶段4："+(System.currentTimeMillis()-startTime));
			log.debug(CeMsg.getMessage(CeMsg.OPERATE_EXHAUST_TIME_MSG, "", "processor.createBatch()", tc.exhaust()));
		} catch (Exception e) {
			log.error(CeMsg.getMessage(CeMsg.CREATE_BATCH_ERR, batchId, batchPath), e);
			if (proxy != null && batchFldId != null) {
				proxy.deleteFolder(batchFldId);
			}
			return CeConstant.CM_IMG_CMPROCESS_CM_OPERATION_ERROR;
		} finally {
			CeBatchProxy.authOut();
			log.info("耗时阶段5："+(System.currentTimeMillis()-startTime));
		}
		return flag;
	}
	/**
	 * 
	 *@Description 
	 *更新批次
	 *批次提交是否成功标志，0成功，否则失败
	 *@param docBean
	 *@param batchId
	 *@param batchPath
	 *@return
	 */
	public String updateBatch(DocBean docBean, String batchId, String batchPath) {
		String flag = "0";
		CeBatchProxy proxy = null;
		List<String> updateDocList = new ArrayList<String>();
		try {
			TimeCounter tc = TimeCounter.record();
			if(GlobalVar.isDynamicObjectStoreOpen == 1) {
				String objectStoreName = "";
				String busiDate = (docBean.getBatchBean()).getBusi_date();
				String appCode = (docBean.getBatchBean()).getAppCode();
				B_OBJECT_STORE objectStore = dbService.findObjectStoreByAppCodeAndBusiDate(appCode, busiDate);
				if(objectStore == null) {
					objectStore = dbService.findObjectStoreByAppCodeAndBusiDate("default", busiDate);
				}
				
				if(objectStore != null) {
					objectStoreName = objectStore.getObjectStoreName();
					proxy = CeBatchProxy.authIn(objectStoreName);
					log.info("updateBatch，连接的ObjectStoreName为：" + objectStoreName);
				} else {
					proxy = CeBatchProxy.authIn();
					log.info("updateBatch，连接的ObjectStoreName为FileNetCEConfig.properties文件中配置的名称");
				}
			} else {
				proxy = CeBatchProxy.authIn();
				log.info("updateBatch，连接的ObjectStoreName为FileNetCEConfig.properties文件中配置的名称");
			}
			
			String docClass = FnConfigOptions.getDocClsName();
			BatchBean batchBean = docBean.getBatchBean();
			List<PageBean> pageList = docBean.getPageList();
			List<PropBean> propList = docBean.getPropList();
			Map<String, String> nodeMap = docBean.getNodeMap();
			Map<String, Object> fldProps = getFldProps(batchBean, propList);

			String fldSql = CeMsg.getMessage(CeSql.QUERY_BATCH_FOLDER, batchId);
			List<Folder> fldList = proxy.searchFolders(fldSql);
			String batchFldId = fldList.get(0).get_Id().toString();
			proxy.updateFolder(batchFldId, fldProps);

			String sydSrcPath = batchPath + '/' + docBean.getSydFileName();
			String sydSrcName = docBean.getSydFileName();
			ContentElementList sydContentList = getDocContents(sydSrcPath);
			if (sydContentList != null && !sydContentList.isEmpty()) {
				String sydSql = CeMsg.getMessage(CeSql.QUERY_DOCUMENT, sydSrcName);
				List<Document> sydDocList = proxy.searchDocuments(sydSql);
				if (sydDocList != null && sydDocList.size() > 0) {
					String sydDocId = sydDocList.get(0).get_Id().toString();
					proxy.checkOut(sydDocId);//检出
					//TODO
					proxy.updateDocument(sydDocId, sydContentList, null);//更新后检入
					updateDocList.add(sydDocId);
				} else {
					proxy.createDocument(docClass, sydSrcName, sydContentList,null, batchFldId);//创建后检入
				}
			}

			for (PageBean pageBean : pageList) {
				String pageId = pageBean.getPageId();
				String pageSrcUrl = batchPath + '/' + pageBean.getPageUrl();
				String thumSrcUrl = batchPath + '/' + pageBean.getThumUrl();
				ContentElementList pageContentList = getDocContents(pageSrcUrl, thumSrcUrl);
				if (pageContentList == null || pageContentList.isEmpty()) {
					continue;
				}
				
				Map<String, Object> docProps = getDocProps(pageBean, nodeMap);
				docProps.put(CeProperty.BUSI_NO, batchBean.getBusiNo());

				String docSql = CeMsg.getMessage(CeSql.QUERY_DOCUMENT, pageId);
				List<Document> pageDocList = proxy.searchDocuments(docSql);
				if (pageDocList != null && pageDocList.size() > 0) {
					String pageDocId = pageDocList.get(0).get_Id().toString();
					proxy.checkOut(pageDocId);
					//TODO 超过5天取消检出 ，即检入后再检出，然后更新操作
					proxy.updateDocument(pageDocId, pageContentList, docProps);//更新后检入
					updateDocList.add(pageDocId);
				} else {
					proxy.createDocument(docClass, pageId, pageContentList, docProps, batchFldId);//创建后检入
				}
			}
			proxy.updateBatch();
			log.debug(CeMsg.getMessage(CeMsg.OPERATE_EXHAUST_TIME_MSG, "", "processor.updateBatch()", tc.exhaust()));
		} catch (Exception e) {
			log.error(CeMsg.getMessage(CeMsg.UPDATE_BATCH_ERR, batchId, batchPath), e);
			if (proxy != null && !updateDocList.isEmpty()) {
				for (String updateDocId : updateDocList) {
					proxy.cancelCheckOut(updateDocId);//取消检出
				}
			}
			return CeConstant.CM_IMG_CMPROCESS_CM_OPERATION_ERROR;
		} finally {
			CeBatchProxy.authOut();
		}
		return flag;
	}
	
	/**
	 * 
	 * @param basePath
	 * @param attrList
	 * @param appCode
	 * @param busiDate
	 * @param queryType 1表示只查询syd文件
	 * @return
	 */
	public Results queryBatchs(String basePath, List<AttrInfo> attrList, String appCode, String busiDate,int queryType) {
		long startTime = System.currentTimeMillis();
		TimeCounter queryBatch = TimeCounter.record();
		Results results = new Results();
		results.setResultList(new ArrayList<Result>());
		int num = 0;
		StringBuilder searchSql = new StringBuilder();
		searchSql.append(CeSql.QUERY_BATCHS).append(" WHERE f.[APP_CODE]='").append(appCode).append("'");
		if (attrList != null && attrList.size() > 0) {
			for (int i = 0; i < attrList.size(); i++) {
				AttrInfo info = attrList.get(i);
				if ("START_DATE".equals(info.getAttrCode())) {
					searchSql.append(" AND f.[").append(CeProperty.CREATE_DATE).append("]>='").append(info.getAttrValue()).append(" 00:00:00'");
				} else if ("END_DATE".equals(info.getAttrCode())) {
					searchSql.append(" AND f.[").append(CeProperty.CREATE_DATE).append("]<='").append(info.getAttrValue()).append(" 23:59:59'");
				} else if ("BatchID".equals(info.getAttrCode())) {
					searchSql.append(" AND f.[").append("BATCH_ID").append("]='").append(info.getAttrValue()).append("'");
				} else {
					searchSql.append(" AND f.[").append(info.getAttrCode()).append("]='").append(info.getAttrValue()).append("'");
				}
			}
		}
		String sql = searchSql.toString();
		try {
			try {
				TimeCounter authIn = TimeCounter.record();
				CeProxy proxy = null;
				if(GlobalVar.isDynamicObjectStoreOpen == 1) {
					String objectStoreName = "";
					B_OBJECT_STORE objectStore = dbService.findObjectStoreByAppCodeAndBusiDate(appCode, busiDate);
					if(objectStore == null) {
						objectStore = dbService.findObjectStoreByAppCodeAndBusiDate("default", busiDate);
					}
					
					if(objectStore != null) {
						objectStoreName = objectStore.getObjectStoreName();
						proxy = CeProxy.authIn(objectStoreName);
						log.info("queryBatchs，连接的ObjectStoreName为：" + objectStoreName);
					} else {
						proxy = CeProxy.authIn();
						log.info("queryBatchs，连接的ObjectStoreName为FileNetCEConfig.properties文件中配置的名称");
					}
				} else {
					proxy = CeProxy.authIn();
					log.info("queryBatchs，连接的ObjectStoreName为FileNetCEConfig.properties文件中配置的名称");
				}
				log.debug(CeMsg.getMessage(CeMsg.OPERATE_EXHAUST_TIME_MSG, sql, "proxy.authIn()", authIn.exhaust()));
				List<Folder> fldList = proxy.searchFolders(sql);
				if (fldList == null || fldList.isEmpty()) {
					// 集合为空，CE中无指定批次或批次状态为忙，返回错误
					log.warn(CeMsg.getMessage(CeMsg.QUERY_BATCH_NOT_EXISTS_MSG,  sql));
					results.setResCode(CeConstant.CM_IMG_CMPORCESS_QUERY_RESULT_ISNULL);
					return results;
				} else if (fldList.size() > CeConstant.maxResultNum) {
					// 查询返回的结果集数量太大，精确查询条件
					log.warn(CeMsg.getMessage(CeMsg.QUERY_BATCH_RESULT_OVERFLOW_MSG, fldList.size(),CeConstant.maxResultNum, sql));
					results.setResCode(CeConstant.CM_IMG_CMPROCESS_QUERY_RESULT_SET_TOO_LARGE);
					return results;
				}
				for (int i = 0; i < fldList.size(); i++) {
					Folder fld = fldList.get(i);
					Properties props = fld.getProperties();
					String batchID = props.getStringValue(CeProperty.BATCH_ID);
					String innerVer = props.getStringValue(CeProperty.INNER_VER);
					
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
					    result.setBusiNo("");//这里先空着，因为SQl没有查询该字段
					    result.setId(Integer.toString(num));
					    num++;
					    result.setXmlPath(basePath+"servlet/GetImage?filename="+batchCatchDir);
					    result.setBatchVers(batchVerList);
					    results.getResultList().add(result);
					    continue;
					}else {
						//queryType==1只下载SYD文件
						if(queryType==1) {
							String sydPathRoot = GlobalVar.tempDir + File.separator + "scan"+ File.separator + DateUtil.getDateStrCompact()
			        		+ File.separator+NumberUtil.getRandomNum(10)
			        		+ File.separator+NumberUtil.getRandomNum(10);
							String sydPath = "";
							DocumentSet docSet = fld.get_ContainedDocuments();
							if (docSet == null || docSet.isEmpty()) {
								log.error(CeMsg.getMessage(CeMsg.QUERY_BATCH_WITHOUT_DOC_MSG, sql));
								results.setResCode(CeConstant.CM_IMG_CMPROCESS_ATTRIBUTE_NOT_EXIST_ERROR);
								continue;
							}
							for (Iterator<?> it = docSet.iterator(); it.hasNext();) {
								Document doc = (Document) it.next();
								ContentElementList contentList = doc.get_ContentElements();
								for (int j = 0; j < contentList.size(); j++) {
									ContentTransfer content = (ContentTransfer) contentList.get(j);
									InputStream in = content.accessContentStream();
									String sourceID = content.get_RetrievalName();
									if(("_"+innerVer+".syd").equalsIgnoreCase(sourceID)) {
										sydPath = sydPathRoot+File.separator+sourceID;
										File file = new File(sydPath);
										ReadDocCall call = new ReadDocCall(in, file);
										call.call();
										log.debug("CE查询只下载syd文件请求,批次号["+batchID+"]最高版本["+innerVer+"]syd存放路径["+sydPath+"]");
									}
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
						    result.setBusiNo("");//这里先空着，因为SQl没有查询该字段
						    result.setId(Integer.toString(num));
						    num++;
						    result.setXmlPath(basePath+"servlet/GetImage?filename="+sydPath);
						    result.setBatchVers(batchVerList);
						    results.getResultList().add(result);
						    continue;
						}
						
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
						
						DocumentSet docSet = fld.get_ContainedDocuments();
						if (docSet == null || docSet.isEmpty()) {
							log.error(CeMsg.getMessage(CeMsg.QUERY_BATCH_WITHOUT_DOC_MSG, sql));
							results.setResCode(CeConstant.CM_IMG_CMPROCESS_ATTRIBUTE_NOT_EXIST_ERROR);
							continue;
						}
						List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
						for (Iterator<?> it = docSet.iterator(); it.hasNext();) {
							Document doc = (Document) it.next();
							ContentElementList contentList = doc.get_ContentElements();
							for (int j = 0; j < contentList.size(); j++) {
								ContentTransfer content = (ContentTransfer) contentList.get(j);
								InputStream in = content.accessContentStream();
								String sourceID = content.get_RetrievalName();
								String filePath = batchCatchDir+File.separator+sourceID;
								File file = new File(filePath);
								ReadDocCall call = new ReadDocCall(in, file);
								tasks.add(call);
							}
							
						}
						
						if (tasks != null && !tasks.isEmpty()) {
							ExecPool pool = new ExecPool();
							pool.execute(tasks);
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
					    result.setBusiNo("");//这里先空着，因为SQl没有查询该字段
					    result.setId(Integer.toString(num));
					    num++;
					    result.setXmlPath(basePath+"servlet/GetImage?filename="+batchCatchDir);
					    result.setBatchVers(batchVerList);
					    results.getResultList().add(result);
					    
					}
					
				}
			} finally {
				CeProxy.authOut();
			}
			results.setResCode("1");//成功结果,xxw
			log.debug("CE["+sql+"]查询耗时:"+(System.currentTimeMillis()- startTime));
			return results;
		} catch (Exception e) {
			log.error(CeMsg.getMessage(CeMsg.QUERY_BATCH_ERR,  sql), e);
			results.setResCode(CeConstant.CM_IMG_CMPORCESS_QUERY_FAIL_ERROR);
			return results;
		} finally {
			log.debug(CeMsg.getMessage(CeMsg.OPERATE_EXHAUST_TIME_MSG, sql,"processor.queryBatch()", queryBatch.exhaust()));
		}
		
	}
	/**
	 * 
	 *@Description 
	 *判断批次是否存在
	 *@param attrList
	 *@param appCode
	 *@return
	 */
	public int existsBatch(List<AttrInfo> attrList, String appCode, String busiDate) {
		StringBuilder searchSql = new StringBuilder();
		searchSql.append(CeSql.QUERY_BATCHS).append(" WHERE f.APP_CODE='").append(appCode).append("'");
		if (attrList != null && attrList.size() > 0) {
			for (int i = 0; i < attrList.size(); i++) {
				AttrInfo info = attrList.get(i);
				searchSql.append(" AND f.").append(info.getAttrCode()).append("='").append(info.getAttrValue()).append("'");
			}
		}
		String sql = searchSql.toString();
		try {
			CeProxy proxy = null;
			if(GlobalVar.isDynamicObjectStoreOpen == 1) {
				String objectStoreName = "";
				B_OBJECT_STORE objectStore = dbService.findObjectStoreByAppCodeAndBusiDate(appCode, busiDate);
				if(objectStore == null) {
					objectStore = dbService.findObjectStoreByAppCodeAndBusiDate("default", busiDate);
				}
				
				if(objectStore != null) {
					objectStoreName = objectStore.getObjectStoreName();
					proxy = CeProxy.authIn(objectStoreName);
					log.info("判断批次是否存在，连接的ObjectStoreName为：" + objectStoreName);
				} else {
					proxy = CeProxy.authIn();
					log.info("判断批次是否存在，连接的ObjectStoreName为FileNetCEConfig.properties文件中配置的名称");
				}
			} else {
				proxy = CeProxy.authIn();
				log.info("判断批次是否存在，连接的ObjectStoreName为FileNetCEConfig.properties文件中配置的名称");
			}
			List<Folder> fldList = proxy.searchFolders(sql);
			if (fldList != null && fldList.size() > 0) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			log.error(CeMsg.getMessage(CeMsg.EXISTS_BATCH_ERR, searchSql.toString()), e);
			return -1;
		} finally {
			CeProxy.authOut();
		}
	}
	
	/**
	 * 
	 *@Description 
	 *创建文件夹类
	 *@param superClassName
	 *@param className
	 *@throws Exception
	 */
	public void createFolderClass(String superClassName, String className) throws Exception {
		String fldClass = FnConfigOptions.getFldClsName();
		try {
			CeProxy proxy = CeProxy.authIn();
			if ("0".equals(superClassName)) {
				superClassName = fldClass;
			}
			proxy.createClass(superClassName, className);
			log.info(CeMsg.getMessage(CeMsg.CREATE_FOLDER_CLASS_MSG, superClassName, className));
		} catch (Exception e) {
			throw e;
		} finally {
			CeProxy.authOut();
		}
	}
	/**
	 * 
	 *@Description 
	 *创建属性
	 *@param className
	 *@param properties
	 *@throws Exception
	 */
	public void createFolderProperties(String className, String... properties) throws Exception {
		try {
			CeProxy proxy = CeProxy.authIn();
			for (String propertyName : properties) {
				proxy.createProperty(className, propertyName);
				log.info(CeMsg.getMessage(CeMsg.CREATE_FOLDER_PROPERT_MSG, className, propertyName));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			CeProxy.authOut();
		}
	}
	
	
	/**
	 * 
	 *@Description 
	 *按产品大类删除影像
	 *@param insType
	 */
	public void deleteInsuranceFolder(String insType) {
		final int size = 50;
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>(size);
		while (true) {
			try {
				ObjectStoreProvider provider = new ObjectStoreProvider();
				Folder folder = provider.fetchFolder(insType);
				FolderSet folderSet = folder.get_SubFolders();
				if (folderSet != null && !folderSet.isEmpty()) {
					for (Iterator<?> it = folderSet.iterator(); it.hasNext();) {
						Folder fld = (Folder) it.next();
						String fldID = fld.get_Id().toString();
						DelFldCall task = new DelFldCall(fldID);
						tasks.add(task);
						if (tasks.size() == size) {
							pool.execute(tasks);
							tasks.clear();
						}
					}
				} else {
					folder.delete();
					folder.save(RefreshMode.REFRESH);
					break;
				}
			} catch (Exception e) {
				log.error("", e);
				break;
			} finally {
				UserContextUtils.popSubject();
			}
		}
	}
	/**
	 * 
	 *@Description 
	 *
	 *@param className
	 *@param properties
	 *@throws Exception
	 */
	public void deleteFolderProperties(String className, String... properties) throws Exception {
		try {
			CeProxy proxy = CeProxy.authIn();
			for (String propertyName : properties) {
				proxy.deleteProperty(className, propertyName);
				log.info(CeMsg.getMessage(
				CeMsg.DELETE_FOLDER_PROPERTY_MSG, className,
				propertyName));
			}
				} catch (Exception e) {
					throw e;
				} finally {
					CeProxy.authOut();
				}
	}
	/**
	 * 
	 *@Description 
	 *
	 *@param className
	 *@throws Exception
	 */
	public void deleteFolderClass(String className) throws Exception {
		try {
			CeProxy proxy = CeProxy.authIn();
			proxy.deleteClass(className);
			log.info(CeMsg.getMessage(CeMsg.DELETE_FOLDER_CLASS_MSG, className));
		} catch (Exception e) {
			throw e;
		} finally {
			CeProxy.authOut();
		}
	}
	/**
	 * 
	 *@Description 
	 *创建文档内容到CE
	 *@param paths
	 *@return
	 *@throws Exception
	 */
	@SuppressWarnings("unchecked")
	private ContentElementList getDocContents(String... paths) throws Exception {
		if (paths == null) {
			return null;
		}
		ContentElementList contentList = Factory.ContentElement.createList();
		for (String path : paths) {
			File file = new File(path);
			if (!FileUtil.isFile(file)) {
				continue;
			}
			String fileSrc = file.getAbsolutePath();
			String fileName = file.getName();
			String suffixName = FileUtil.getFileSuffix(file);
			String mimeType = GlobalVar.mimeHasMap.get(suffixName.toLowerCase());
			if (!StringUtil.isValue(mimeType)) {
				mimeType = CeConstant.DEFAULT_DOCTYPE;
			}
			ContentTransfer ct = CeProxy.createContentTransfer(fileSrc, fileName, mimeType);
			contentList.add(ct);
		}
		return contentList;
	}
	/**
	 * 
	 *@Description 
	 *
	 *@param batchBean
	 *@param propList
	 *@return 文件夹类属性Map
	 *{MOD_USER=admin, BATCH_ID=009d763474fb43f594f24f8550488b62,...}
	 *{属性=值,...}
	 */
	private Map<String, Object> getFldProps(BatchBean batchBean, List<PropBean> propList) {
		Map<String, Object> fldProps = new HashMap<String, Object>();
		String CREATE_DATE = batchBean.getCreateDate();
		String MOD_DATE = batchBean.getModDate();
		
//		if (StringUtil.isValue(CREATE_DATE)) {
//			CREATE_DATE = DateUtil.formatDateTime(CREATE_DATE,"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
//		}
//		
//		if (StringUtil.isValue(MOD_DATE)) {
//			MOD_DATE = DateUtil.formatDateTime(MOD_DATE,"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
//		}
		
		fldProps.put(CeProperty.BATCH_ID, batchBean.getBatchId());
		fldProps.put(CeProperty.APP_CODE, batchBean.getAppCode());
		fldProps.put(CeProperty.BUSI_NO, batchBean.getBusiNo());
		fldProps.put(CeProperty.INNER_VER, batchBean.getInterVer());
		fldProps.put(CeProperty.CREATE_DATE, CREATE_DATE);
		fldProps.put(CeProperty.CREATE_USER, batchBean.getCreateUser());
		fldProps.put(CeProperty.MOD_DATE, MOD_DATE);
		fldProps.put(CeProperty.MOD_USER, batchBean.getModUser());
		for (PropBean propBean : propList) {
			fldProps.put(propBean.getCode(), propBean.getValue());
		}
		return fldProps;
	}
	
	
	/**
	 * 
	 *@Description 
	 *
	 *@param pageBean
	 *@param nodeMap
	 *@return 文档类属性Map
	 * *{MOD_USER=admin, BATCH_ID=009d763474fb43f594f24f8550488b62,...}
	 *{属性=值,...}
	 */
	private Map<String, Object> getDocProps(PageBean pageBean, Map<String, String> nodeMap) {
		Map<String, Object> docProps = new HashMap<String, Object>();
		String CREATE_TIME = pageBean.getCreateTime();
		String MODIFY_TIME = pageBean.getModifyTime();
		
//		if (StringUtil.isValue(CREATE_TIME)) {
//			CREATE_TIME = DateUtil.formatDateTime(CREATE_TIME, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
//		}
//		if (StringUtil.isValue(MODIFY_TIME)) {
//			MODIFY_TIME = DateUtil.formatDateTime(MODIFY_TIME, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
//		}
		
		docProps.put(CeProperty.CREATE_USER, pageBean.getCreateUser());
		docProps.put(CeProperty.CREATE_TIME, CREATE_TIME);
		docProps.put(CeProperty.IMG_TYPE, nodeMap.get(pageBean.getPageId()));
		docProps.put(CeProperty.MODIFY_TIME, MODIFY_TIME);
		docProps.put(CeProperty.MODIFY_USER, pageBean.getModifyUser());
		docProps.put(CeProperty.PAGE_CRC, pageBean.getPageCrc());
		docProps.put(CeProperty.PAGE_DESC, pageBean.getPageDesc());
		docProps.put(CeProperty.PAGE_ENCRYPT, pageBean.getPageEncrypt());
		docProps.put(CeProperty.PAGE_FORMAT, pageBean.getPageFormat());
		docProps.put(CeProperty.UPLOAD_ORG, pageBean.getUploadOrg());
		docProps.put(CeProperty.PAGE_URL, pageBean.getPageUrl());
		docProps.put(CeProperty.THUM_URL, pageBean.getThumUrl());
		
		//上汽个性化开始
		for (PageExtBean pageExt : pageBean.getExtList()) {
			if ("DEF_INDEX".equals(pageExt.getId())
					|| "ID_CARD".equals(pageExt.getId())
					|| "CP_LIENCE".equals(pageExt.getId())
					|| "BK_BICKER".equals(pageExt.getId())
					|| "AD_BICKER".equals(pageExt.getId())
					|| "IS_BICKER".equals(pageExt.getId())
					|| "AC_BICKER".equals(pageExt.getId())
					|| "QR_BICKER".equals(pageExt.getId())) {
				docProps.put(pageExt.getId(), pageExt.getValue());
			}
		}
		//上汽个性化结束
		
		return docProps;
	}
	/**
	 * 
	 *@Description 
	 *创建批次存储文件夹
	 *@param proxy
	 *@param fldProps
	 *@return
	 */
	@SuppressWarnings("unused")
	private String createBatchFolder_bak(CeBatchProxy proxy, Map<String, Object> fldProps) {
		String fldClass = FnConfigOptions.getFldClsName();//IMG_SRC_FLD
		String batchId = (String) fldProps.get(CeProperty.BATCH_ID);
		String appCode = (String) fldProps.get(CeProperty.APP_CODE);
		String insType = (String) fldProps.get(CeProperty.INS_TYPE);
		
		String fldId = null;
		Folder folder = null;
		String fldCls = appCode;//存储类文件夹
		
		String appFldPath = CeConstant.ROOT_FOLDER + '/' + appCode;
		Folder appFld = proxy.fetchFolderByPath(appFldPath);
		if (appFld == null) {
			Folder rootFld = proxy.fetchFolderByPath(CeConstant.ROOT_FOLDER);
			fldId = rootFld.get_Id().toString();
			folder = proxy.createFolder(appCode, fldId, fldClass, null);
			proxy.updateBatch();
			fldId = folder.get_Id().toString();
		} else {
			fldId = appFld.get_Id().toString();
		}
		
		if (StringUtil.isValue(insType)) {//有产品大类
			String insFldPath = appFldPath + '/' + insType;
			Folder insFld = proxy.fetchFolderByPath(insFldPath);
			if (insFld == null) {
				folder = proxy.createFolder(insType, fldId, fldClass, null);
				proxy.updateBatch();
				fldId = folder.get_Id().toString();
			} else {
				fldId = insFld.get_Id().toString();
			}
			fldCls = insType;
		}
		
		folder = proxy.createFolder(batchId, fldId, fldCls, fldProps);
		proxy.updateBatch();
		
		return folder.get_Id().toString();
	}
	
	/**
	 * 
	 *@Description 
	 *创建批次存储文件夹
	 *@param proxy
	 *@param fldProps
	 *@return
	 */
	@SuppressWarnings("unused")
	private String createBatchFolder_bak2(CeBatchProxy proxy, Map<String, Object> fldProps) {	
		String fldClass = FnConfigOptions.getFldClsName();//IMG_SRC_FLD
		String batchId = (String) fldProps.get(CeProperty.BATCH_ID);
		String appCode = (String) fldProps.get(CeProperty.APP_CODE);
		String insType = (String) fldProps.get(CeProperty.INS_TYPE);
		
		String fldId = null;
		Folder folder = null;
		String fldCls = appCode;//存储类文件夹
		String appFldPath = CeConstant.ROOT_FOLDER + '/' + appCode;
		Folder appFld = proxy.fetchFolderByPath(appFldPath);
		if (appFld == null) {
			Folder rootFld = proxy.fetchFolderByPath(CeConstant.ROOT_FOLDER);
			fldId = rootFld.get_Id().toString();
			folder = proxy.createFolder(appCode, fldId, fldClass, null);
			proxy.updateBatch();
			fldId = folder.get_Id().toString();
		} else {
			fldId = appFld.get_Id().toString();
		}
		
		if (StringUtil.isValue(insType)) {//有产品大类
			String insFldPath = appFldPath + '/' + insType;
			Folder insFld = proxy.fetchFolderByPath(insFldPath);
			if (insFld == null) {
				folder = proxy.createFolder(insType, fldId, fldClass, null);
				proxy.updateBatch();
				fldId = folder.get_Id().toString();
			} else {
				fldId = insFld.get_Id().toString();
			}
			fldCls = insType;
		}
		
		//存储路径为app_code+batch_id的第一位+batch_id的第二位
		Folder FldBatch_01 = proxy.fetchFolderByPath(appFldPath+'/'+batchId.substring(0, 2));
		if (FldBatch_01 == null) {
			folder = proxy.createFolder(batchId.substring(0, 2), fldId, fldClass, null);
			proxy.updateBatch();
			fldId = folder.get_Id().toString();
		}else {
			fldId = FldBatch_01.get_Id().toString();
		}
		
		Folder FldBatch_02 = proxy.fetchFolderByPath(appFldPath+'/'+batchId.substring(0, 2)+'/'+batchId.substring(2, 4));
		if (FldBatch_02 == null) {
			folder = proxy.createFolder(batchId.substring(2, 4), fldId, fldClass, null);
			proxy.updateBatch();
			fldId = folder.get_Id().toString();
		}else {
			fldId = FldBatch_02.get_Id().toString();
		}
		
		folder = proxy.createFolder(batchId, fldId, fldCls, fldProps);
		proxy.updateBatch();
		
		return folder.get_Id().toString();
	}
	
	
	/**
	 * 
	 *@Description 
	 *创建批次存储文件夹
	 *@param proxy
	 *@param fldProps
	 *@return
	 */
	private String createBatchFolder(CeBatchProxy proxy, Map<String, Object> fldProps) {	
		String fldClass = FnConfigOptions.getFldClsName();//IMG_SRC_FLD
		String batchId = (String) fldProps.get(CeProperty.BATCH_ID);
		String appCode = (String) fldProps.get(CeProperty.APP_CODE);
		String insType = (String) fldProps.get(CeProperty.INS_TYPE);
		
		String fldId = null;
		Folder folder = null;
		String appFldPath = CeConstant.ROOT_FOLDER + '/' + appCode;
		Folder appFld = proxy.fetchFolderByPath(appFldPath);
		if (appFld == null) {
			Folder rootFld = proxy.fetchFolderByPath(CeConstant.ROOT_FOLDER);
			fldId = rootFld.get_Id().toString();
			folder = proxy.createFolder(appCode, fldId, fldClass, null);
			proxy.updateBatch();
			fldId = folder.get_Id().toString();
		} else {
			fldId = appFld.get_Id().toString();
		}
		String fldCls = appCode;//存储类文件夹类,默认是appCode
		String batchSavePath = "";
		
		if (StringUtil.isValue(insType)) {
			//有产品大类
			String insFldPath = appFldPath + '/' + insType;
			Folder insFld = proxy.fetchFolderByPath(insFldPath);
			if (insFld == null) {
				folder = proxy.createFolder(insType, fldId, fldClass, null);
				proxy.updateBatch();
				fldId = folder.get_Id().toString();
			} else {
				fldId = insFld.get_Id().toString();
			}
			
			//创建下级文件夹
			Folder FldBatch_01 = proxy.fetchFolderByPath(insFldPath+'/'+batchId.substring(0, 2));
			if (FldBatch_01 == null) {
				folder = proxy.createFolder(batchId.substring(0, 2), fldId, fldClass, null);
				proxy.updateBatch();
				fldId = folder.get_Id().toString();
			}else {
				fldId = FldBatch_01.get_Id().toString();
			}
			
			batchSavePath = insFldPath+'/'+batchId.substring(0, 2)+'/'+batchId.substring(2, 4);
			Folder FldBatch_02 = proxy.fetchFolderByPath(batchSavePath);
			if (FldBatch_02 == null) {
				folder = proxy.createFolder(batchId.substring(2, 4), fldId, fldClass, null);
				proxy.updateBatch();
				fldId = folder.get_Id().toString();
			}else {
				fldId = FldBatch_02.get_Id().toString();
			}
			
			
			fldCls = insType;
		} else {
			//没有产品大类
			//创建下级文件夹
			Folder FldBatch_01 = proxy.fetchFolderByPath(appFldPath+'/'+batchId.substring(0, 2));
			if (FldBatch_01 == null) {
				folder = proxy.createFolder(batchId.substring(0, 2), fldId, fldClass, null);
				proxy.updateBatch();
				fldId = folder.get_Id().toString();
			}else {
				fldId = FldBatch_01.get_Id().toString();
			}
			
			batchSavePath = appFldPath+'/'+batchId.substring(0, 2)+'/'+batchId.substring(2, 4);
			Folder FldBatch_02 = proxy.fetchFolderByPath(batchSavePath);
			if (FldBatch_02 == null) {
				folder = proxy.createFolder(batchId.substring(2, 4), fldId, fldClass, null);
				proxy.updateBatch();
				fldId = folder.get_Id().toString();
			}else {
				fldId = FldBatch_02.get_Id().toString();
			}
		}
		
		//防止之前已经创建了存储文件夹
		Folder batchSaveFolder = proxy.fetchFolderByPath(batchSavePath+'/'+batchId);
		if(null==batchSaveFolder) {
			folder = proxy.createFolder(batchId, fldId, fldCls, fldProps);
			proxy.updateBatch();
			return folder.get_Id().toString();
		} else {
			proxy.updateFolder(batchSaveFolder.get_Id().toString(), fldProps);
			proxy.updateBatch();
			return batchSaveFolder.get_Id().toString();
		}
		
	}
	
}
