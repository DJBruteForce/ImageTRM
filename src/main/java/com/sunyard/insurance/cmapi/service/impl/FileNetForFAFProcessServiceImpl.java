package com.sunyard.insurance.cmapi.service.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
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
import com.sunyard.insurance.cmapi.model.cmquery.RootInfo;
import com.sunyard.insurance.cmapi.service.CMProessService;
import com.sunyard.insurance.cmapi.util.BatchXmlProcess;
import com.sunyard.insurance.cmapi.util.CMCommonUtil;
import com.sunyard.insurance.common.FileExtensionFileFilter;
import com.sunyard.insurance.filenet.faf.cesupport.CEConstant;
import com.sunyard.insurance.filenet.faf.cesupport.CEService;
import com.sunyard.insurance.filenet.faf.cesupport.util.StringUtils;
import com.sunyard.insurance.util.PerSrcTable;

public class FileNetForFAFProcessServiceImpl implements CMProessService{
	private static final Logger log = Logger.getLogger(FileNetForFAFProcessServiceImpl.class);
	
	private BatchXmlProcess batchXmlProcess = new BatchXmlProcess();
	
	/**
	 * 
	 *@param batchBean
	 *@return
	 *@Description
	 *
	 */
	public String saveOrUpdateBatch(BatchBean batchBean) {
		log.debug("开始处理上传批次:" + batchBean.getBatch_id());
		//提交CE是否成功标志,0成功，否则失败
		String flag = "-70";
		//业务类型
		String appCode = "";
		//syd索引中的内部版本号
		int innerVer = 0;
		//syd索引中的批次号
		String realBatchID = "";
		//获取CM该批次的内部版本号
		int cmInnerVer = 0;
		try{
			long startTime = System.currentTimeMillis();
			//业务类型
			appCode = batchBean.getAppCode();
			//索引中的批次号
			realBatchID = batchBean.getBatch_id();
			//索引中的内部版本号
			innerVer = Integer.valueOf(batchBean.getInter_ver());
			//批次文件全路径和文件名
			List<BatchFileBean>  batchFilelist = batchBean.getBatchFileList();
			//批次扩展属性,判断是否有险种
			Map<String, String> attrMap = batchBean.getProMap();
			if(null !=attrMap.get("INS_TYPE")){
				appCode = attrMap.get("INS_TYPE");
			}
			StopWatch getInnerVerWatch = new Log4JStopWatch();
			long getInnerVerTime = System.currentTimeMillis();
			//获取CM该批次的内部版本号,xxw
			log.debug("获取CE内部版本号，realBatchID=" + realBatchID + "，appCode=" + appCode);
			cmInnerVer = CEService.getInnerVersion(realBatchID, appCode);
			log.info("获取批次["+batchBean.getBatch_id()+"]的CM内部版本号:"+cmInnerVer+" 当前内部版本号:"+innerVer+"用时："+(System.currentTimeMillis()-getInnerVerTime));
			getInnerVerWatch.stop("获取批次的版本");
			if(cmInnerVer<0){//如果批次在CM中不存在,则返回-1，存在则返回批次当前的内部版本号
				if(innerVer==1){//新增批次
						flag = CEService.submitBatch(attrMap,batchBean,false);
					 if("0".equals(flag)){//flag=0,上传成功
						log.debug("处理上传批次:"+batchBean.getBatch_id()+"新增成功，用时："+(System.currentTimeMillis()-startTime));
					}
				}else{
					log.info("批次第一次上传，内部版本号不为1！当前索引内部版本号:"+innerVer);
					return "-2";
				}
			}else {
				//批次在CM中存在
				PerSrcTable.perSrcInfo(batchBean, cmInnerVer);//SRC表不存在的情况下，补充SRC记录
				if(innerVer<=cmInnerVer){
					//如果索引中内部版本号小于或等于CM中内部版本号，则跳过
					log.info("索引中内部版本号小于等于CM中内部版本号，不处理该批次："+batchBean.getBatch_id()+"，当前索引内部版本号："+innerVer+"，CM内部版本号:"+cmInnerVer);
					String batchPath="";
					for(BatchFileBean bean : batchFilelist){
						if(bean.getFileFullPath().endsWith(".syd")){
							batchPath=bean.getFileFullPath();
							break;
						}
					}
					FileUtils.deleteDirectory(new File(batchPath).getParentFile());
					log.info("删除此批次接收到的文件成功!");
					return "-1";
				}else{
					if((cmInnerVer+1)==innerVer){//如果内部版本号连续，则更新该批次
						flag = CEService.submitBatch(attrMap,batchBean,true);
					}else{
						log.info("内部版本号不连续！当前索引内部版本号："+innerVer+"，CM内部版本号:"+cmInnerVer);
						return "-1";
					}
				}
			}
			
		}catch(Exception ex){
			log.error("上传批次存储CE失败！返回结果，batchId:"+batchBean.getBatch_id()+"，error：",ex);
		}
		return flag;
	}

	
	/**
	 * 
	 *@param appCode
	 *@param batchId
	 *@return
	 *@Description
	 *
	 */
	public boolean deleteBatch(String appCode, String batchId) {
		boolean bool = false;
		String busiNum="";//TODO
		String resultCode = CEService.deleteBatch(appCode, busiNum);
		if("0".equals(resultCode) || "1".equals(resultCode)){
			log.info("删除批次成功，批次号="+batchId);
			bool = true;
		}else if("-1".equals(resultCode)){
			log.info("删除批次失败，批次号="+batchId);
			bool = false;
		}
			return bool;
	}
	
	/**
	 * 
	 *@param queryStr
	 *@param basePath
	 *@param queryFlag
	 *@return
	 *@Description
	 *
	 */
	public String queryBatchForECM(String queryStr, String basePath,String queryFlag,int queryType) {
		//计数
		int num = 0;
		//默认返回XML串；返回给SunECM端的报文，xxw
		String resultStr =  "<results><resCode>-1</resCode></results>";
		//查询返回对象
		Results results = new Results();
		
		try{
			log.debug("开始查询："+queryStr);
			//请求数据对象
			RootInfo rootInfo = batchXmlProcess.readCMQueryXml(queryStr);
			//业务类型
			String appCode = rootInfo.getAppCode();
			//扩展属性结合
			List<AttrInfo> attrsList = rootInfo.getAttrs();
			//判断是否存在险种大类，如果存在则将appCode赋值为险种大类，将险种大类作为项类型
			if(attrsList!=null && attrsList.size()>0){
				for(int i=0;i<attrsList.size();i++){
					if(attrsList.get(i).getAttrCode().equals("INS_TYPE")){
						//如果存在险种大类,则把险种大类作为业务类型;不存在的话原业务类型代码appCode作为业务类型，xxw
						String insType = attrsList.get(i).getAttrValue();
						if(insType!=null && !insType.equals("")){
							appCode = insType;
						}
					}
					
					//以下这么干均因为前台后台定义属性名称不一致，需要转换
					if(attrsList.get(i).getAttrCode().equals("BatchID")){
						attrsList.get(i).setAttrCode("BATCH_ID");
					}
					
					if(attrsList.get(i).getAttrCode().equals("BUSI_NO")){
						attrsList.get(i).setAttrCode("BUSI_NUM");
					}
				}
			}
			//查询FileNetForFAF
			StopWatch queryWatch = new Log4JStopWatch();
			long queryTime = System.currentTimeMillis();
			//去CM查询批次
			results = CEService.queryBatch(basePath,attrsList,appCode);
			log.info("查询批次"+queryStr+"，用时："+(System.currentTimeMillis()-queryTime));
			queryWatch.stop("查询批次");
			if(!results.getResCode().equals("1")){
				//查询CM失败
				results.setResultList(null);
			}
			
			//---------------------------------------------
			if("TRM".equals(queryFlag)){//TRM内部查询
				String xmlPath = results.getResultList().get(0).getXmlPath();
				resultStr = xmlPath.substring(xmlPath.indexOf("=")+1);
			}else if("ECM".equals(queryFlag)){//ECM的查询
				resultStr = batchXmlProcess.resultsToXml(results);//返回报文
			}
		}catch(Exception ex){
			log.error("FileNetForFAF查询失败，queryStr:"+queryStr+"，error：",ex);
		}
		
		return resultStr;
	}
	
	/**
	 * 
	 *@param queryStr
	 *@param basePath
	 *@param queryFlag
	 *@return
	 *@Description
	 *
	 */
	public String queryBatchForECM_old(String queryStr, String basePath,String queryFlag) {
		//计数
		int num = 0;
		//默认返回XML串；返回给SunECM端的报文，xxw
		String resultStr =  "<results><resCode>-1</resCode></results>";
		//查询返回对象
		Results results = new Results();
		//唯一的ID
		String uniqueID = java.util.UUID.randomUUID().toString();
		//查询临时存放路径
		String queryZipPath = CEConstant.queryZipFolder+File.separator+uniqueID;
		File queryZipFolder = new File(queryZipPath);
		if(!queryZipFolder.exists()){
			queryZipFolder.mkdirs();
		}
		try{
			log.debug("开始查询："+queryStr);
			long startTime = System.currentTimeMillis();
			//请求数据对象
			RootInfo rootInfo = batchXmlProcess.readCMQueryXml(queryStr);
			//业务类型
			String appCode = rootInfo.getAppCode();
			//扩展属性结合
			List<AttrInfo> attrsList = rootInfo.getAttrs();
			//判断是否存在险种大类，如果存在则将appCode赋值为险种大类，将险种大类作为项类型
			if(attrsList!=null && attrsList.size()>0){
				for(int i=0;i<attrsList.size();i++){
					if(attrsList.get(i).getAttrCode().equals("INS_TYPE")){
						//如果存在险种大类,则把险种大类作为业务类型;不存在的话原业务类型代码appCode作为业务类型，xxw
						String insType = attrsList.get(i).getAttrValue();
						if(insType!=null && !insType.equals("")){
							appCode = insType;
						}
						break;
					}
				}
			}
			//查询CM
			StopWatch queryWatch = new Log4JStopWatch();
			long queryTime = System.currentTimeMillis();
			//去CM查询批次
			results = CEService.queryBatch_old(basePath,attrsList,appCode,queryZipPath);
			log.info("查询批次"+queryStr+"，用时："+(System.currentTimeMillis()-queryTime));
			queryWatch.stop("查询批次");
			
			String bCachePath="";//批次缓存路径
			if(results.getResCode().equals("1")){//查询成功，xxw
				File[] batchFiles = queryZipFolder.listFiles();//列出查询临时缓存下的批次文件
				List<Result> resultList = results.getResultList();
				num = resultList.size();
				//循环遍历查询临时缓存的批次文件
				for(int k=0;k<batchFiles.length;k++){
					String batchID = batchFiles[k].getName();
					File batchFile = batchFiles[k];
					String batchPath = batchFile.getPath();
					//获取查询缓存路径
					String batchCachePath = StringUtils.getQueryCacheFolder(batchID);
					StopWatch zzWatch = new Log4JStopWatch();
					long zzTime = System.currentTimeMillis();
					StopWatch hqWatch = new Log4JStopWatch();
					long hqTime = System.currentTimeMillis();
					
					FileFilter sydFilter = new FileExtensionFileFilter("syd");       
				    File[] sydFiles = batchFile.listFiles(sydFilter);//查询临时缓存的所有syd文件，xxw
				    int[] batchVers = new int[sydFiles.length];//存放syd所有外部版本数，xxw
				    for(int m=0;m<sydFiles.length;m++){
				    	String fileName = sydFiles[m].getName();
				    	int start = fileName.indexOf("_");
				    	int end = fileName.indexOf(".");
				    	int batchVer = Integer.parseInt(fileName.substring(start+1,end));
				    	batchVers[m] = batchVer;
				    }
				    log.info("获取版本"+queryStr+"，用时:"+(System.currentTimeMillis()-hqTime));
					hqWatch.stop("获取版本");
					
				    //排序批次版本
				    StopWatch sortWatch = new Log4JStopWatch();
				    long Time = System.currentTimeMillis();
				    CMCommonUtil.bubbleSort(batchVers,"desc");//对查询临时缓存的所有syd版本倒序排序，xxw
				    log.info("批次版本排序"+queryStr+"，用时："+(System.currentTimeMillis()-Time));
				    sortWatch.stop("批次排序");
				    
				    //组装批次
				    List<BatchVer> batchVerList = new ArrayList<BatchVer>();
				    for(int n=0;n<batchVers.length;n++){
				    	BatchVer batchVerInfo = new BatchVer();
				    	batchVerInfo.setValue(Integer.toString(batchVers[n]));
				    	batchVerList.add(batchVerInfo);
				    }
				    
				    //组装数据
				    StopWatch zzDataWatch = new Log4JStopWatch();
				    long zzDataTime = System.currentTimeMillis();
				    //获取查询临时缓存下最大版本的syd文件，xxw
				    File maxVerXml = new File(batchPath+File.separator+batchID+"_"+batchVers[0]+".syd");
				    //解析这个最大版本的syd文件,获取批次信息,xxw
				    ImgXmlInfo imgXmlInfo = batchXmlProcess.readImgXml(maxVerXml,batchID);
				    String busiNo = imgXmlInfo.getBusiNo();
				    int innerVer = imgXmlInfo.getInnerVer(); //syd的内部版本号,xxw
				    Result result = new Result();
				    /*************（返回报文中添加内部版本号）**********************/
				    List<InnerVer> innerVerList = new ArrayList<InnerVer>();
				    InnerVer innerVerInfo = new InnerVer();
				    innerVerInfo.setValue(innerVer+"");
				    innerVerList.add(innerVerInfo);
				    result.setInnerVers(innerVerList);
				    /********************************************************/
				    result.setBatchID(batchID);
				    result.setAppCode(appCode);
				    result.setBusiNo(busiNo);
				    result.setId(Integer.toString(num));
				    num++;
				    result.setXmlPath(basePath+"servlet/GetImage?filename="+batchCachePath);
				    result.setBatchVers(batchVerList);
				    resultList.add(result);
				    log.info("组装数据"+queryStr+"，用时："+(System.currentTimeMillis()-zzDataTime));
				    zzDataWatch.stop("组装数据");
				    zzWatch.stop("组装批次");
				    log.info("组装批次"+queryStr+"，用时："+(System.currentTimeMillis()-zzTime));
				    
				    //拷贝SYD文件
				    long cutTime = System.currentTimeMillis();
					StopWatch cutWatch = new Log4JStopWatch();
					File[] files = batchFile.listFiles();
					//列出查询临时缓存路径下的所有syd文件，如：C:\scic\scanFolder\CM\query\zip\120810131906812021
					for(int j=0;j<files.length;j++){
						String fileName = files[j].getName();
						int index = fileName.lastIndexOf(".");
						String suffix = fileName.substring(index+1);
						if(!suffix.equalsIgnoreCase("zip")){
							//不是ZIP文件，移动SYD文件
							File destFile = new File(batchCachePath+File.separator+fileName);
							if(destFile.exists()) {
								destFile.delete();
							}
							//源文件
							File srcFile = new File(files[j].getAbsolutePath());
							FileUtils.moveFile(srcFile, destFile);
						}
					}
					cutWatch.stop("移动SYD文件");
					log.info("覆盖文件"+queryStr+"，用时："+(System.currentTimeMillis()-cutTime));
					
				}
				results.setResultList(resultList);
				log.info("查询成功，queryStr:"+queryStr+"，用时："+(System.currentTimeMillis()-startTime));
				//-----------------------------------------
				//TRM内部查询 而且缓存中没有
				String batch_id = resultList.get(0).getBatchID();//获取批次号
				bCachePath = StringUtils.getQueryCacheFolder(batch_id);//查询缓存路径
			}else{
				//查询CM失败
				results.setResultList(null);
			}
			//---------------------------------------------
			if("TRM".equals(queryFlag)){//TRM内部查询
				resultStr = bCachePath; 
			}else if("ECM".equals(queryFlag)){//ECM的查询
				resultStr = batchXmlProcess.resultsToXml(results);//返回报文
			}
		}catch(Exception ex){
			log.error("CM查询失败，queryStr:"+queryStr+"，error：",ex);
		}finally{
			try {
				FileUtils.deleteDirectory(new File(queryZipPath));
			} catch (IOException e) {
				log.error("删除临时文件夹["+queryZipPath+"]数据失败！",e);
			}
		}
		
		return resultStr;
	}

	/**
	 * 
	 *@param map
	 *@param queryFlag
	 *@return
	 *@Description
	 *
	 */
	public String queryBatchForTRM(Map<String, String> map, String queryFlag) {
		log.info("SunTRM开始查询FileNet CE影像批次...");
		String queryCachePath="";
		//查询xml字符串
		String queryStr = batchXmlProcess.plamoCMXML(map);
		log.info("FileNetForFAFProcessServiceImpl queryBatchForTRM():"+queryStr);
		queryCachePath = this.queryBatchForECM(queryStr,"",queryFlag,0);
		if("<results><resCode>-1</resCode></results>".equals(queryCachePath)){
			return "";
		}else{
			return queryCachePath;
		}
	}

	
}
