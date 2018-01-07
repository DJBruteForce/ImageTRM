package com.sunyard.insurance.cmapi.service.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import com.ibm.mm.sdk.common.DKConstant;
import com.ibm.mm.sdk.common.DKDDO;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.common.DKNVPair;
import com.ibm.mm.sdk.common.DKParts;
import com.ibm.mm.sdk.common.DKResults;
import com.ibm.mm.sdk.common.dkIterator;
import com.ibm.mm.sdk.server.DKDatastoreExtICM;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.batch.bean.BatchFileBean;
import com.sunyard.insurance.cmapi.CMProcessor;
import com.sunyard.insurance.cmapi.model.AttrInfo;
import com.sunyard.insurance.cmapi.model.cmquery.Results;
import com.sunyard.insurance.cmapi.model.cmquery.RootInfo;
import com.sunyard.insurance.cmapi.service.CMProessService;
import com.sunyard.insurance.cmapi.util.BatchXmlProcess;
import com.sunyard.insurance.cmapi.util.CMConnectionPool;
import com.sunyard.insurance.common.CMConstant;

/**
 * 
  * @Title CMProessServiceImpl.java
  * @Package com.sunyard.insurance.cmapi.impl
  * @Description 
  * CM 提交 ，更新 ,查询实现类
  * @time 2012-8-8 上午11:27:08  @author xxw
  * @version 1.0
  *-------------------------------------------------------
 */
public class CMProessServiceImpl implements CMProessService {
	private static final Logger log = Logger.getLogger(CMProessServiceImpl.class);
	
	private BatchXmlProcess batchXmlProcess = new BatchXmlProcess();
	

	/**
	 * 提交或更新批次
	 *@param batchId
	 *@param tempDir
	 *@return 批次提交是否成功标志，"0"-成功，否则失败
	 *@Description
	 *
	 */
	public String saveOrUpdateBatch(BatchBean batchBean) {
		//业务类型
		String appCode = "";
		//syd索引中的内部版本号
		int batchVer = 0;
		//syd索引中的内部版本号
		int innerVer = 0;
		//CM连接对象
		DKDatastoreICM dsICM = null;
		//提交CM是否成功标志,0成功，否则失败
		String flag = "-1";//原来默认值为0，会导致抛异常时也返回上传成功,xxw
		try{
			log.debug("开始处理上传批次:"+batchBean.getBatch_id());
			long startTime = System.currentTimeMillis();
			//业务类型
			appCode = batchBean.getAppCode();
			//索引中的内部版本号
			batchVer = Integer.valueOf(batchBean.getBatch_ver());
			//索引中的内部版本号
			innerVer = Integer.valueOf(batchBean.getInter_ver());
			//批次文件全路径和文件名
			List<BatchFileBean>  batchFilelist = batchBean.getBatchFileList();
			//批次扩展属性,判断是否有险种
			Map<String, String> attrMap = batchBean.getProMap();
			if(null !=attrMap.get("INS_TYPE")){
				appCode = attrMap.get("INS_TYPE");
			}
			
			//创建CM连接
			long cmConTime = System.currentTimeMillis();
			if(CMConstant.isCMPool.equals("0")){//是否使用CM连接池 0：不使用 ;1：使用
				dsICM =  new DKDatastoreICM();//创建新连接
				dsICM.connect(CMConstant.RMN,CMConstant.CMUser,CMConstant.CMPwd,"");
			}else{//使用连接池
				dsICM = CMConnectionPool.getConnection(CMConstant.CMUser, CMConstant.CMPwd);
			}
			log.info("获取CM连接sessionId:["+dsICM.getSessionId()+"]用时："+(System.currentTimeMillis()-cmConTime));
			StopWatch getInnerVerWatch = new Log4JStopWatch();
			//获取CM该批次的内部版本号,xxw
			int cmInnerVer = CMProcessor.getInnerVer(batchBean.getBatch_id(),appCode,dsICM);
			log.info("获取批次["+batchBean.getBatch_id()+"]的CM内部版本号:"+cmInnerVer+" 当前待上传版本号:"+innerVer);
			getInnerVerWatch.stop("获取批次的版本");
			if(cmInnerVer<0){//如果批次在CM中不存在,则返回-1，存在则返回批次当前的内部版本号
				if(innerVer==1){//新增批次
					flag = CMProcessor.submitBatch(batchBean,false,dsICM);
					if("0".equals(flag)){//flag=0,上传成功
						log.info("上传批次:"+batchBean.getBatch_id()+"到IBM CM成功，用时："+(System.currentTimeMillis()-startTime));
					}
				}else{
					log.info("批次第一次上传，内部版本号不为1！当前待上传版本号:"+innerVer);
					return "-1";
				}
			}else{//批次在CM中存在
				//完善SRC表记录，个性化新增(完善不存在SRC表记录的情况)
				//PerSrcTable.perSrcByCM(batchBean.getBatch_id(), cmInnerVer);
				if(innerVer<=cmInnerVer){
					//如果索引中内部版本号小于或等于CM中内部版本号，则跳过
					log.error("索引中内部版本号小于等于CM中内部版本号，不处理该批次："+batchBean.getBatch_id()+"，当前待上传版本号："+innerVer+"，CM内部版本号:"+cmInnerVer);
					String batchPath = batchFilelist.size()>0?batchFilelist.get(0).getFileFullPath():"";
					if(null!= batchPath && !"".equals(batchPath)) {
						FileUtils.deleteDirectory(new File(batchPath).getParentFile());
					}
					return "-1";
				}else{
					if((cmInnerVer+1)==innerVer){//如果内部版本号连续，则更新该批次
						flag = CMProcessor.submitBatch(batchBean,true,dsICM);
					}else{
						log.error("内部版本号不连续！当前待上传版本号："+innerVer+"，CM内部版本号:"+cmInnerVer);
						return "-1";
					}
				}
			}
		}catch(Exception ex){
			log.error("上传批次存储CM失败！返回结果，batchId:"+batchBean.getBatch_id()+"，error：",ex);
		}finally{
			try {
				// 如果有到CM的连接对象，要返回该连接对象到连接池
				if (dsICM != null) {
					log.warn("返回/销毁CM连接sessionID:["+dsICM.getSessionId()+"]");
					if(CMConstant.isCMPool.equals("0")) {
						dsICM.disconnect();
						dsICM.destroy();
						dsICM = null;
					}else{
						CMConnectionPool.returnConnection(dsICM);
						dsICM = null;
					}
				}
			} catch (Exception e2) {
				// 返回连接到连接池失败错误
				log.error("销毁/返回CM连接到连接池异常!", e2);
			}
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
	public String queryBatchForTRM(Map<String,String> map, String queryFlag){
		String queryCachePath="";
		//查询xml字符串
		String queryStr = batchXmlProcess.plamoCMXML(map);
		queryCachePath = this.queryBatchForECM(queryStr,"",queryFlag,0);
		if("<results><resCode>-1</resCode></results>".equals(queryCachePath)){
			return "";
		}else{
			return queryCachePath;
		}
	}
	/**
	 * 查询批次
	 *@param queryStr,如下
	 *<?xml version='1.0' encoding='UTF-8' ?>
	 *<root><appCode>CL</appCode>
	 *<attrs>
	 *<attr attrCode="INS_TYPE" attrValue="CL_D"/>
	 *<attr attrCode="BUSI_NUM" attrValue="CLD81409354414000171202"/>
	 *</attrs>
	 *</root>
	 *@param basePath，如下
	 *String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ request.getContextPath() + "/";
	 *@return
	 *@Description
	 *
	 */
	public String  queryBatchForECM(String queryStr,String basePath,String queryFlag,int queryType) {
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
						break;
					}
				}
			}
			//查询CM
			StopWatch queryWatch = new Log4JStopWatch();
			long queryTime = System.currentTimeMillis();
			//去CM查询批次
			results = CMProcessor.queryBatch(basePath,attrsList,appCode);
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
			log.error("CM查询失败，queryStr:"+queryStr+"，error：",ex);
		}
		
		return resultStr;
	}
	
	/**
	 * 
	 *@Description 
	 *
	 *@param appCode 业务编号
	 *@param batchId 批次号
	 *@return true :成功；false: 失败
	 */
	public boolean deleteBatch(String appCode,String batchId){
		DKDatastoreICM dsICM = null;
		String itemTypeName = "";// 项类型名称
		try{
			long cmConTime = System.currentTimeMillis();
			if(CMConstant.isCMPool.equals("0")){
				dsICM =  new DKDatastoreICM();//创建新连接
				dsICM.connect(CMConstant.RMN,CMConstant.CMUser,CMConstant.CMPwd,"");
			}else{
				dsICM = CMConnectionPool.getConnection(CMConstant.CMUser, CMConstant.CMPwd);
			}
			log.info("获取CM连接sessionId:["+dsICM.getSessionId()+"]用时："+(System.currentTimeMillis()-cmConTime));
			if(appCode==null || appCode.equals("")) {
				itemTypeName = "*";
			}else{
				itemTypeName = CMConstant.docName+appCode;
			}
			// 创建查询字符串
			String queryStr = "/" + itemTypeName + "[@"+CMConstant.docBatchId+"=\"" + batchId + "\"]";
			DKDDO ddoDocument = null;
			//如果queryStr直接为"/*"，CM查询会直接报错
			if(queryStr.toString().equals("/*")){
				log.info("查询返回的结果集数量太大，queryStr:"+queryStr);
				return false;//
			}
			DKNVPair options[] = new DKNVPair[3];
			options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "1");
			options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer(DKConstant.DK_CM_CONTENT_YES));
			options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);
			DKResults dkResults=null;
			//查询字符串
			long queryTime = System.currentTimeMillis();
			// 查询对应与批次号的批次，返回的集合中只有一个批次
			dkResults = (DKResults)dsICM.evaluate(queryStr,DKConstant.DK_CM_XQPE_QL_TYPE, options);
			log.info("查询文档"+queryStr+"，用时："+(System.currentTimeMillis()-queryTime));
			if (dkResults.cardinality() == 0){
				// 集合为空，CM中无指定批次或批次状态为忙，返回错误
				log.info("批次查询，查询条件："+queryStr+"，集合为空，CM中无指定批次, 批次查询为空");
				return true;
			}else if(dkResults.cardinality()>CMConstant.maxResultNum){//CM查询最大条数
				//查询返回的结果集数量太大，精确查询条件
				log.error("批次查询，查询条件："+queryStr+"，返回的结果集数量太大,超过了10条");
				return false;
			}else if(dkResults.cardinality() != 0){
				dkIterator iter = dkResults.createIterator();//创建迭代器
				while (iter.more()) {
					ddoDocument = (DKDDO) iter.next();
				}
				//CM版本号
				String innerVer = (String)(ddoDocument.getData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docInnerVer)));
				if(Integer.parseInt(innerVer.trim())<0){
					log.info("在CM中不存在批次:"+batchId);
					return true;
				}else{
					//加入事务机制
					dsICM.startTransaction();
					//是否检出
					DKDatastoreExtICM dsExtICM = new DKDatastoreExtICM(dsICM);
					if (dsExtICM.isCheckedOut(ddoDocument)) { // 指定批次被检出
						// 批次被检出，返回错误
						log.error("删除批次项，批次检出失败，批次["+batchId+"]项类型["+itemTypeName+"]Error：批次状态不允许本操作,批次被检出！");
						return false;
					}
					// 把指定批次检出
					dsICM.checkOut(ddoDocument);
					short dataid = ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS);
					DKParts dkParts = (DKParts) ddoDocument.getData(dataid);
					dkParts.removeAllElements();
					ddoDocument.del();
					dsICM.commit();
					log.info("在IBMCM中删除批次项成功！批次号:"+batchId);
				}
				//释放对象
				ddoDocument=null;
				iter = null;
			}
		}catch(Exception ex){
			log.error("删除批次失败！批次号："+batchId+"，项类型："+itemTypeName+"，回滚! Error：", ex);
			try {
				dsICM.rollback();//回滚
			} catch (DKException e) {
				log.error("删除批次回滚异常!",e);
			} catch (Exception e) {
				log.error("删除批次回滚异常!",e);
			}
			return false;
		}finally{
			try{
				if (dsICM != null) {
					log.warn("返回/销毁CM连接sessionID:["+dsICM.getSessionId()+"]");
					if(CMConstant.isCMPool.equals("0")){
						dsICM.disconnect();
						dsICM.destroy();
						dsICM = null;
					}else{
						CMConnectionPool.returnConnection(dsICM);
					}
				}
			}catch(Exception e){
				log.error("销毁/返回CM连接到连接池异常!", e);
			}
		}
		
	 	return true;
	}
	
	
	
}