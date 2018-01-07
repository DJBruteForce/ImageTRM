package com.sunyard.insurance.cmapi.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.filenet.api.core.Folder;
import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.batch.bean.BatchFileBean;
import com.sunyard.insurance.cmapi.model.AttrInfo;
import com.sunyard.insurance.cmapi.model.cmquery.Results;
import com.sunyard.insurance.cmapi.model.cmquery.RootInfo;
import com.sunyard.insurance.cmapi.service.CMProessService;
import com.sunyard.insurance.cmapi.util.BatchXmlProcess;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.filenet.ce.common.constant.CeMsg;
import com.sunyard.insurance.filenet.ce.common.constant.CeSql;
import com.sunyard.insurance.filenet.ce.depend.CeProcessor;
import com.sunyard.insurance.filenet.ce.depend.TimeCounter;
import com.sunyard.insurance.filenet.ce.model.DocBean;
import com.sunyard.insurance.filenet.ce.proxy.CeProxy;
import com.sunyard.insurance.filenet.ce.util.StringUtil;
import com.sunyard.insurance.util.ClassUtil;
import com.sunyard.insurance.util.PerSrcTable;

/**
 * 
  * @Title FileNetCEProessServiceImpl.java
  * @Package com.sunyard.insurance.cmapi.service.impl
  * @Description 
  * @author xxw
  * @time 2012-10-17 下午04:32:30  
  * @version 1.0
 */
public class FileNetCEProessServiceImpl implements CMProessService {
	private static final Logger log = Logger.getLogger(FileNetCEProessServiceImpl.class);
	private CeProcessor processor = new CeProcessor();
	private BatchXmlProcess batchXmlProcess = new BatchXmlProcess();
	/**
	 * 
	 *@param batchBean
	 *@return 成功：0；其他：失败
	 *@Description
	 *上传或更新批次接口
	 */
	public String saveOrUpdateBatch(BatchBean batchBean) {
		log.debug("开始处理上传批次:" + batchBean.getBatch_id());
		String flag = "-70";
		TimeCounter tc = TimeCounter.record();
		String batchId = batchBean.getBatch_id();
		String appCode = batchBean.getAppCode();
		String busi_date = batchBean.getBusi_date();
		int interVer = Integer.parseInt(batchBean.getInter_ver());
		
		try {
			long startTime = System.currentTimeMillis();
			int innerVersion =new CeProcessor().getInnerVersion(batchId, appCode, busi_date);
			log.info("获取批次["+batchId+"]在CE中的版本号耗时："+(System.currentTimeMillis()-startTime));
			
			List<BatchFileBean>  batchFilelist = batchBean.getBatchFileList();
			String batchPath="";
			for(BatchFileBean bean : batchFilelist){
				if(bean.getFileFullPath().endsWith(".syd")){
					batchPath=bean.getFileFullPath();
					break;
				}
			}
			startTime = System.currentTimeMillis();
			DocBean docBean = ClassUtil.getSydFileReaderClass().readDocBean(new File(batchPath));
			log.info("上传批次["+batchId+"]反射类耗时："+(System.currentTimeMillis()-startTime));
			startTime = System.currentTimeMillis();
			
			if (innerVersion < 0) {// 如果批次不存在则返回-1，存在则返回批次当前的内部版本号
				if (interVer == 1) {
					flag = processor.createBatch(docBean, batchId, new File(batchPath).getParent());
					if (!flag.equals("0")) {
						log.error("批次["+batchId+"]版本["+interVer+"]存储CE失败!");
					}
				} else {
					log.error("批次["+batchId+"]第一次上传，内部版本号不为1！");
				}
			} else {
				//SRC表不存在的情况下，补充SRC记录
//				PerSrcTable.perSrcInfo(batchId, innerVersion, batchBean.getAppCode());
				if (interVer <= innerVersion) {
					// 如果索引中内部版本号小于CE中内部版本号，则跳过
					log.error("批次["+batchId+"]索引中版本号["+interVer+"]小于CE中内部版本号["+innerVersion+"]不进行上传!");
					return "-1";
				} else {
					if (interVer == (innerVersion + 1)) {
						flag = processor.updateBatch(docBean, batchId, new File(batchPath).getParent());
						if (!flag.equals("0")) {
							log.error("批次["+batchId+"]版本["+interVer+"]存储CE失败!");
						}
					} else {
						log.error("批次["+batchId+"]索引中版本号["+interVer+"]CE中内部版本号["+innerVersion+"]版本不连续,不进行上传!");
					}
				}
			}
			log.info("批次["+batchId+"]版本号["+interVer+"]调用CE接口上传耗时："+(System.currentTimeMillis()-startTime));
		} catch (Exception e) {
			log.error("CE上传过程异常批次["+batchId+"]版本["+interVer+"]!", e);
		} finally {
			log.info("处理上传批次["+batchId+"]完毕，耗时" + tc.exhaust() + "ms");
		}
		return flag;
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
		log.info("FileNetCEProessServiceImpl queryBatchForTRM():"+queryStr);
		queryCachePath = this.queryBatchForECM(queryStr,"",queryFlag,0);
		if("<results><resCode>-1</resCode></results>".equals(queryCachePath)){
			return "";
		}else{
			return queryCachePath;
		}
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
	public String queryBatchForECM(String queryStr, String basePath, String queryFlag,int queryType) {
		log.info("开始查询FileNet CE影像批次...");
		// 默认返回XML串
		String resultStr = "<results><resCode>-1</resCode></results>";
		// 查询返回对象
		Results results = new Results();
		try {
			long startTime = System.currentTimeMillis();
			String busiDate = "";
			if(GlobalVar.isDynamicObjectStoreOpen == 1) {
				SAXReader saxReader = new SAXReader();
				Document doc = saxReader.read(new ByteArrayInputStream(new String(queryStr.getBytes("utf-8"), "utf-8").getBytes("utf-8")));
				Element attrs = (Element)doc.selectSingleNode("root/attrs");
				List<Element> child = attrs.elements();
			    for (int i = 0; i < child.size(); i++) {
					Element temp = child.get(i);
					String attrCode = temp.attributeValue("attrCode");
					String attrValue = temp.attributeValue("attrValue");
					if("BUSI_DATE".equals(attrCode)) {
						busiDate = attrValue;
						attrs.remove(temp);
						break;
					}
				}
			    queryStr = doc.asXML();
			}
			RootInfo rootInfo = batchXmlProcess.readCMQueryXml(queryStr);// 请求数据对象
			String appCode = rootInfo.getAppCode();// 业务类型
			List<AttrInfo> attrsList = rootInfo.getAttrs();// 扩展属性结合
			results = processor.queryBatchs(basePath, attrsList, appCode, busiDate,queryType);
			String bCachePath="";//批次缓存路径
			if (results.getResCode().equals("1")) {
				log.debug("查询成功，queryStr:" + queryStr + "，用时：" + (System.currentTimeMillis() - startTime));
				//TRM内部查询 而且缓存中没有
				String batch_id = results.getResultList().get(0).getBatchID();
				bCachePath = StringUtil.getQueryCacheFolder(batch_id);//查询缓存路径  
				//bCachePath = "C:/TRM" + bCachePath;//TODO
			} else {
				// 查询CM失败
				results.setResultList(null);
			}
			//---------------------------------------------
			if("TRM".equals(queryFlag)){//TRM内部查询
				resultStr = bCachePath; 
			}else if("ECM".equals(queryFlag)){//ECM的查询
				resultStr = batchXmlProcess.resultsToXml(results);//返回报文
			}
		} catch (Exception e) {
			log.error("CM查询失败，queryStr:" + queryStr+ "，error：", e);
		}
			return resultStr;
	}
	
	/**
	 *@param appCode 业务类型
	 *@param batchId 批次号
	 *@returnc 成功：true;失败：false
	 *@Description
	 *在CE中删除批次
	 */
	public boolean deleteBatch(String appCode, String batchId) {
		log.info("开始从FileNet CE删除影像批次，批次号："+batchId);
		try {
			CeProxy proxy = CeProxy.authIn();
			String sql = CeMsg.getMessage(CeSql.QUERY_BATCH_FOLDER, batchId);
			List<Folder> fldList = proxy.searchFolders(sql);
			if(fldList.isEmpty() || null==fldList){
				log.info("FileNet CE中影像批次batchId="+batchId+" 不存在！");
				return true;
			}else{
				Folder folder = fldList.get(0);
				folder = proxy.fetchFolder(folder.get_Id().toString());
				proxy.deleteFolder(folder.get_Id().toString());
				log.info("从FileNet CE中删除影像批次成功，批次号："+batchId);
				return true;
			}
		} catch (Exception e) {
			log.error("从FileNet CE中删除影像批次失败，批次号：", e);
			return false;
		} finally {
			CeProxy.authOut();
		}
	}

}
