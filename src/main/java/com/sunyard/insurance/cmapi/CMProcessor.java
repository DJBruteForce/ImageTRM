package com.sunyard.insurance.cmapi;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import com.ibm.mm.sdk.common.DKAttrDefICM;
import com.ibm.mm.sdk.common.DKConstant;
import com.ibm.mm.sdk.common.DKConstantICM;
import com.ibm.mm.sdk.common.DKDDO;
import com.ibm.mm.sdk.common.DKDatastoreDefICM;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.common.DKImageICM;
import com.ibm.mm.sdk.common.DKItemTypeDefICM;
import com.ibm.mm.sdk.common.DKNVPair;
import com.ibm.mm.sdk.common.DKParts;
import com.ibm.mm.sdk.common.DKResults;
import com.ibm.mm.sdk.common.DKSequentialCollection;
import com.ibm.mm.sdk.common.dkIterator;
import com.ibm.mm.sdk.server.DKDatastoreExtICM;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.cmapi.model.AttrInfo;
import com.sunyard.insurance.cmapi.model.cmquery.BatchVer;
import com.sunyard.insurance.cmapi.model.cmquery.InnerVer;
import com.sunyard.insurance.cmapi.model.cmquery.Result;
import com.sunyard.insurance.cmapi.model.cmquery.Results;
import com.sunyard.insurance.cmapi.util.CMConnectionPool;
import com.sunyard.insurance.common.CMConstant;
import com.sunyard.insurance.common.FileExtensionFileFilter;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.util.DateUtil;
/**
 * 
  * @Title CMProcessor.java
  * @Package com.sunyard.insurance.cmapi
  * @Description CM相关操作类
  * @author xxw
  * @time 2012-10-12 下午02:29:08  
  * @version 1.0
 */
public class CMProcessor {
	private static final Logger log = Logger.getLogger(CMProcessor.class);
	
	/**
	 * 
	 *@Description 
	 *提交批次
	 *@param attrMap
	 *@param batchBean
	 *@param batchFilelist
	 *@param isExist
	 *@param dsICM
	 *@return
	 */
	public static String submitBatch(BatchBean batchBean,boolean isExist,DKDatastoreICM dsICM) {
		//批次提交是否成功标志，0成功，否则失败
		String flag = "0";
		try {
			try {
				//加入事务机制
				dsICM.startTransaction();
				if(isExist){
					//修改批次
					log.debug("开始更新批次，批次号："+batchBean.getBatch_id()+"，业务代码："+batchBean.getAppCode());
					StopWatch watch = new Log4JStopWatch();
					flag = updateBatchItem(batchBean, dsICM);
					log.debug("更新批次结束，批次号："+batchBean.getBatch_id()+"，业务代码："+batchBean.getAppCode()+"，返回码："+flag);
					watch.stop("update DDO");
				}else{
					//新增批次
					log.debug("开始增加批次，批次号："+batchBean.getBatch_id()+"，业务代码："+batchBean.getAppCode());
					StopWatch watch = new Log4JStopWatch();
					flag = createBatchItem(batchBean, dsICM);
					log.debug("增加批次结束，批次号："+batchBean.getBatch_id()+"，业务代码："+batchBean.getAppCode()+"，返回码："+flag);
					watch.stop("create DDO");
				}
				dsICM.commit();
			} catch (DKException e) {
				dsICM.rollback();
				log.error("批次提交失败！批次："+batchBean.getBatch_id()+"，Error：", e);
				return CMConstant.CM_IMG_CMPROCESS_CM_OPERATION_ERROR;
			} catch (Exception e) {
				dsICM.rollback();
				log.error("批次提交失败！批次："+batchBean.getBatch_id()+"，Error：", e);
				return CMConstant.CM_IMG_CMPROCESS_CM_OPERATION_ERROR;
			}

		} catch (Exception ex) {
			log.error("批次提交回滚事务失败！批次："+batchBean.getBatch_id()+"，Error：", ex);
			return CMConstant.CM_IMG_CMPROCESS_CM_OPERATION_ERROR;
		} 
		
		return flag;
	}

	/**
	 * 
	 *@Description 
	 *创建批次项
	 *@param attrMap
	 *@param batchBean
	 *@param batchFiles
	 *@param dsICM
	 *@return
	 *@throws Exception
	 */
	private static String createBatchItem(BatchBean batchBean, DKDatastoreICM dsICM)
			throws Exception {
		// 项类型名称,文档项类型名
		String itemTypeName = CMConstant.docName+batchBean.getAppCode();
		//判断是否存在险种
		if(null != batchBean.getProMap().get("INS_TYPE") && !"".equals(batchBean.getProMap().get("INS_TYPE"))) {
			itemTypeName = CMConstant.docName+batchBean.getProMap().get("INS_TYPE"); 
		}
		
		DKDDO documentDDO = null;
		try {
			// 创建一个文档项，对应应用的一个批次
			documentDDO = dsICM.createDDO(itemTypeName,DKConstant.DK_CM_DOCUMENT);
		} catch (Exception e) {
			log.error("创建批次项，创建文档项失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：", e);
			return CMConstant.CM_IMG_CMPROCESS_CREATEDDO_FAIL_ERRORE;
		}
		try {
			//获取项类型包含的所有属性
			List<String> allAttrs = getBatchAttrs(dsICM,itemTypeName);
			// 给批次属性赋值
			documentDDO.setData(documentDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docBatchId),batchBean.getBatch_id());
			documentDDO.setData(documentDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docInnerVer),batchBean.getInter_ver());
			documentDDO.setData(documentDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docGdFlag),"0");
			documentDDO.setData(documentDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docBusiNo),batchBean.getBusi_no());
			if(null!=batchBean.getProMap() && batchBean.getProMap().size()>0){
				Set<String> key = batchBean.getProMap().keySet();
				for(Iterator<String> it = key.iterator(); it.hasNext();){
					String mapKey = (String) it.next();
					//判断项类型是否包含该扩展属性
					if(allAttrs.contains(mapKey)){
						String attrValue = batchBean.getProMap().get(mapKey);
						if(attrValue.length()>CMConstant.attrSubLen){
							attrValue = attrValue.substring(0, CMConstant.attrSubLen);//截取扩展属性字段,xxw
						}
						documentDDO.setData(documentDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, mapKey),attrValue);
					}
				}
			}
			//释放对象
			allAttrs = null;
		} catch (Exception e) {
			log.error("创建批次项，给批次属性赋值失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：", e);
			return CMConstant.CM_IMG_CMPROCESS_SETDDO_ATTRIBUTE_FAIL_ERRORE;
		}
		// 获取文档项的文档部件属性dkParts
		short dataid = documentDDO.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS);
		if (dataid == 0) {
			log.error("创建批次项，获取文档部件失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：批次DDO对象无文档部件属性！批次DDO对象可能不属于一个Document类型的项类型，或者批次DDO对象的属性未被提取！");
			return CMConstant.CM_IMG_CMPROCESS_DDO_NO_DOCUMENTPART_ERRORE;
		}
		DKParts dkParts = (DKParts)documentDDO.getData(dataid);//获取文档项的文档部件属性dkParts,xxw
		StopWatch imgsWatch = new StopWatch();
		long imgsTime = System.currentTimeMillis();
		DKImageICM image = null;
		for(int j=0; j<batchBean.getBatchFileList().size(); j++){
			StopWatch imgWatch = new Log4JStopWatch();
			long imgTime = System.currentTimeMillis();
			// 取得文件路径,批次文件全路径
			String imgPath = batchBean.getBatchFileList().get(j).getFileFullPath();
			// 取得文件名
			String imgFileName = batchBean.getBatchFileList().get(j).getFileName();
			// 取得文件的后缀名
			String suffixName = imgFileName.substring(imgFileName.lastIndexOf('.') + 1);
			// 取得文件的MIMI TYPE
			String mimeType = GlobalVar.mimeHasMap.get(suffixName);
			try {
				// 创建一个文档部件
				image = (DKImageICM) dsICM.createDDO(CMConstant.docPartName,DKConstantICM.DK_ICM_SEMANTIC_TYPE_BASE);//文档部件项类型名
			} catch (Exception e) {
				log.error("创建批次项，创建批次文档部件对象失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：", e);
				return CMConstant.CM_IMG_CMPROCESS_CREATEPART_FAIL_ERRORE;
			}
			try{
				// 设置文档部件的MIME type
				if (mimeType == null) {
					image.setMimeType("text/plain");
				} else {
					image.setMimeType(mimeType);
				}
				image.setData(image.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docPartSourceID), imgFileName);//文档部件项类型默认属性，文件名
				image.setData(image.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docInnerVer), batchBean.getInter_ver());//文档项类型默认属性，内部版本号
			} catch (Exception e) {
				log.error("创建批次项，给文档部件属性赋值失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：", e);
				return CMConstant.CM_IMG_CMPROCESS_SETPART_ATTRIBUTE_FAIL_ERRORE;
			}
			try {
				// 加载对应的影像文件到文档部件中
				image.setContentFromClientFile(imgPath);
				// 添加影像到文档部件集合中
				dkParts.addElement(image);
				//释放对象
				image = null;
			} catch (Exception e) {
				log.error("创建批次项，加载对应的文件到文档部件中失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：", e);
				return CMConstant.CM_IMG_CMPROCESS_ADD_CONTENT_FAIL_ERRORE;
			}
			log.info("create docPart,批次:"+batchBean.getBatch_id()+"，用时："+(System.currentTimeMillis()-imgTime));
			imgWatch.stop("create docPart");
		}//遍历batchFiles结束
		log.info("create docParts,批次:"+batchBean.getBatch_id()+"，用时："+(System.currentTimeMillis()-imgsTime));
		imgsWatch.stop("create docParts");
		try{
			StopWatch watch = new Log4JStopWatch();
			long addTime = System.currentTimeMillis();
			documentDDO.add();
			//释放对象
			documentDDO = null;
			dkParts = null;
			log.info("add DDO,批次:"+batchBean.getBatch_id()+"，用时："+(System.currentTimeMillis()-addTime));
			watch.stop("add DDO");
		}catch(Exception e){
			log.error("创建批次项，DDO对象执行ADD方法时失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：", e);
			return CMConstant.CM_IMG_CMPROCESS_CREATEDDO_ADD_FAIL_ERRORE;
		}
		return "0";
	}
	
	/**
	 * 
	 *@Description 
	 *更新批次项
	 *@param attrMap
	 *@param batchBean
	 *@param batchFilelist
	 *@param dsICM
	 *@return
	 *@throws Exception
	 */
	@SuppressWarnings("deprecation")
	private static String updateBatchItem(BatchBean batchBean,DKDatastoreICM dsICM)
			throws Exception {
		// 项类型名称
		String itemTypeName = CMConstant.docName+batchBean.getAppCode();
		//判断是否存在险种
		if(null != batchBean.getProMap().get("INS_TYPE") && !"".equals(batchBean.getProMap().get("INS_TYPE"))) {
			itemTypeName = CMConstant.docName+batchBean.getProMap().get("INS_TYPE");
		}
		
		// 查询语句
		String queryStr="/"+itemTypeName+"[@"+CMConstant.docBatchId+"=\""+batchBean.getBatch_id()+"\"]";
		DKDDO ddoDocument = null;
		try{
			DKNVPair options[] = new DKNVPair[3];
			options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "1");
			options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer(DKConstant.DK_CM_CONTENT_YES));
			options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);
			//查询CM,xxw
			DKResults results = (DKResults) dsICM.evaluate(queryStr,DKConstant.DK_CM_XQPE_QL_TYPE, options);
			if (results.cardinality() == 0) {
				// 集合为空，CM中无指定批次或批次状态为忙，返回错误
				log.error("更新批次项，批次获取失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：集合为空，CM中无指定批次或批次状态为忙");
				return CMConstant.CM_IMG_CMPROCESS_BATCH_NOT_EXIST_ON_CM_ERROR;
			}
			dkIterator iter = results.createIterator();
			while (iter.more()) {
				ddoDocument = (DKDDO) iter.next();
			}
			DKDatastoreExtICM dsExtICM = new DKDatastoreExtICM(dsICM);
			if (dsExtICM.isCheckedOut(ddoDocument)) { // 指定批次被检出
				// 批次被检出，返回错误
				log.error("更新批次项，批次检出失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：批次状态不允许本操作,批次被检出！");
				return CMConstant.CM_IMG_CMPROCESS_CHECKOUT_BATCH_ERROR;
			}
			// 把指定批次检出
			dsICM.checkOut(ddoDocument);
			try {
				//获项类型包含的所有属性
				List<String> allAttrs = getBatchAttrs(dsICM,itemTypeName);
				// 更新批次属性
				ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docInnerVer),batchBean.getInter_ver());
				ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docBusiNo),batchBean.getBusi_no());
				if(null!=batchBean.getProMap() && batchBean.getProMap().size()>0){
					Set<String> key = batchBean.getProMap().keySet();
					for(Iterator<String> it = key.iterator();it.hasNext();){
						 String mapKey = (String) it.next();
						//判断项类型是否包含该扩展属性
						if(allAttrs.contains(mapKey)){
							String attrValue = batchBean.getProMap().get(mapKey);
							if(attrValue.length()>CMConstant.attrSubLen){
								attrValue = attrValue.substring(0, CMConstant.attrSubLen);//截取扩展属性字段
							}
							ddoDocument.setData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, mapKey),attrValue);
						}
					}
				}
				//释放对象
				allAttrs = null;
			} catch (Exception e) {
				log.error("更新批次项，给批次项属性赋值失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：", e);
				return CMConstant.CM_IMG_CMPROCESS_SETDDO_ATTRIBUTE_FAIL_ERRORE;
			}
			// 获取文档项的文档部件属性dkParts
			short dataid = ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS);
			if (dataid == 0) {
				log.error("更新批次项，获取文档部件失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：批次DDO对象无文档部件属性！批次DDO对象可能不属于一个Document类型的项类型，或者批次DDO对象的属性未被提取！");
				return CMConstant.CM_IMG_CMPROCESS_DDO_NO_DOCUMENTPART_ERRORE;
			}
			DKParts dkParts = (DKParts)ddoDocument.getData(dataid);
			iter = dkParts.createIterator();
			DKImageICM image = null;
			HashMap<String,DKImageICM> imgMap = new HashMap<String,DKImageICM>();
			while (iter.more()) {
				image = (DKImageICM) iter.next();
				image.retrieve(DKConstant.DK_CM_CONTENT_ATTRONLY);
				//文档部件项类型默认属性，文件名
				String sourceID = (String)image.getData(image.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docPartSourceID));
				imgMap.put(sourceID, image);
				//释放对象
				image = null;
			}
			StopWatch imgsWatch = new StopWatch();
			long imgsTime = System.currentTimeMillis();
			for(int i=0;i<batchBean.getBatchFileList().size();i++) {//遍历batchFilelist开始
				StopWatch imgWatch = new Log4JStopWatch();
				long imgTime = System.currentTimeMillis();
				// 取得文件路径,批次文件夹全路径
				String imgPath = batchBean.getBatchFileList().get(i).getFileFullPath();
				// 取得文件名
				String imgFileName = batchBean.getBatchFileList().get(i).getFileName();
				// 取得文件的后缀名
				String suffixName = imgFileName.substring(imgFileName.lastIndexOf('.') + 1);
				// 取得文件的MIMI TYPE
				String mimeType = GlobalVar.mimeHasMap.get(suffixName);
				// 根据文件名判断是否文件并且已存在，如果存在则替换文件内容
				if(imgMap.get(imgFileName)!=null){
					image = imgMap.get(imgFileName);
					try{//加载对应的影像文件到文档部件中
						image.setContentFromClientFile(imgPath);
						image.setData(image.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docInnerVer), batchBean.getInter_ver());
					}catch(Exception e){
						log.error("更新批次项，更新对应的文件到文档部件中失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：", e);
						return CMConstant.CM_IMG_CMPROCESS_ADD_CONTENT_FAIL_ERRORE;
					}
				}else{
					try {
						// 创建一个文档部件
						image = (DKImageICM) dsICM.createDDO(CMConstant.docPartName,DKConstantICM.DK_ICM_SEMANTIC_TYPE_BASE);
					} catch (Exception e) {
						log.error("更新批次项，创建文档部件对象时失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：", e);
						return CMConstant.CM_IMG_CMPROCESS_CREATEPART_FAIL_ERRORE;
					}
					try{
						// 设置文档部件的MIME type
						if (mimeType == null) {
							image.setMimeType("text/plain");
						} else {
							image.setMimeType(mimeType);
						}
						image.setData(image.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docPartSourceID), imgFileName);
						image.setData(image.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docInnerVer), batchBean.getInter_ver());
					} catch (Exception e) {
						log.error("更新批次项，给文档部件属性赋值时失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：", e);
						return CMConstant.CM_IMG_CMPROCESS_SETPART_ATTRIBUTE_FAIL_ERRORE;
					}
					try {
						// 加载对应的影像文件到文档部件中
						image.setContentFromClientFile(imgPath);
						// 添加影像到文档部件集合中
						dkParts.addElement(image);
						//释放对象
						image = null;
					} catch (Exception e) {
						log.error("更新批次项，加载对应的文件到文档部件中失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：", e);
						return CMConstant.CM_IMG_CMPROCESS_ADD_CONTENT_FAIL_ERRORE;
					}
				}
				log.info("update docPart,批次:"+batchBean.getBatch_id()+"，文件["+imgPath+"]用时："+(System.currentTimeMillis()-imgTime));
				imgWatch.stop("update docPart");
			}//遍历batchFilelist结束
			log.info("update docParts,批次:"+batchBean.getBatch_id()+"，用时："+(System.currentTimeMillis()-imgsTime));
			imgsWatch.stop("update docParts");
			try{
				StopWatch watch = new Log4JStopWatch();
				long updateTime = System.currentTimeMillis();
				ddoDocument.update();
				log.info("update DDO,批次:"+batchBean.getBatch_id()+"，用时："+(System.currentTimeMillis()-updateTime));
				watch.stop("update DDO");
				//释放对象
				results = null;
				imgMap = null;
				dkParts = null;
				iter = null;
			}catch(Exception e){
				log.error("更新批次项，DDO对象执行update方法时失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：", e);
				return CMConstant.CM_IMG_CMPROCESS_CREATEDDO_UPDATE_FAIL_ERRORE;
			}
		}catch(Exception ex){
			log.error("更新批次项失败，批次："+batchBean.getBatch_id()+"，项类型："+itemTypeName+"，Error：",ex);
			return "-1";
		}finally{
			DKDatastoreExtICM dsExtICM = new DKDatastoreExtICM(dsICM);
			if (dsExtICM.isCheckedOut(ddoDocument)) { // 指定批次被检出
				//检入批次
				dsICM.checkIn(ddoDocument);
			}
			//释放对象
			ddoDocument = null;
		}
		return "0";
}

	/**
	 * xxw
	 * CM查询批次
	 * @param basePath 如：http://127.0.0.1:8090/SunICM/
	 * @param attrList 
	 * @param appCode 业务类型或险种大类 ，如：YSF,CL_D
	 * @param tempDir 本地临时存放目录 ,如：C:/scic/scanFolder/CM/query/zip\120718143909093002
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Results queryBatch(String basePath,List<AttrInfo> attrList, String appCode) {
		Results results = new Results();
		List<Result> resultList = new ArrayList<Result>();
		int num = 0;
		DKDatastoreICM dsICM = null;
		// 查询语句
		StringBuilder queryStr = new StringBuilder();
		try {
			long cmConTime = System.currentTimeMillis();
			if(CMConstant.isCMPool.equals("0")){
				dsICM =  new DKDatastoreICM();//创建新连接
				dsICM.connect(CMConstant.RMN,CMConstant.CMUser,CMConstant.CMPwd,"");
			}else{
				dsICM = CMConnectionPool.getConnection(CMConstant.CMUser, CMConstant.CMPwd);
			}
			log.info("获取CM连接,sessionId:["+dsICM.getSessionId()+"]用时："+(System.currentTimeMillis()-cmConTime));
			// 项类型名称
			String itemTypeName = "";
			if(appCode==null || appCode.equals("")){
				itemTypeName = "*";
			}else{
				itemTypeName = CMConstant.docName+appCode;
			}
			if(attrList!=null && attrList.size()>0){//扩展属性
				queryStr.append("[");
				AttrInfo attrInfo = null;
				for(int i=0;i<attrList.size();i++){
					attrInfo = attrList.get(i);
					if(!attrInfo.getAttrValue().equals("")) {
	//					if(attrInfo.getAttrCode().equals("BATCH_ID")) {
	//						queryStr.append("@"+attrInfo.getAttrCode()+" = \""+attrInfo.getAttrValue()+"\" and ");
	//					} else if(attrInfo.getAttrCode().equals("BUSI_NUM")) {
	//						queryStr.append("@"+attrInfo.getAttrCode()+" = \""+attrInfo.getAttrValue()+"\" and ");
	//					} else if(!attrInfo.getAttrCode().equals("INS_TYPE")) {
	//						//queryStr.append("@"+attrInfo.getAttrCode()+" like \"%"+attrInfo.getAttrValue()+"%\" and ");
	//						queryStr.append("@"+attrInfo.getAttrCode()+" = \""+attrInfo.getAttrValue()+"\" and ");
	//					}
						
						//只按BATCH_ID查询
						if(attrInfo.getAttrCode().equals("BatchID")) {
							queryStr.append("@"+attrInfo.getAttrCode()+" = \""+attrInfo.getAttrValue()+"\" and ");
							break;
						} else if(attrInfo.getAttrCode().equals("BATCH_ID")) {
							queryStr.append("@"+attrInfo.getAttrCode()+" = \""+attrInfo.getAttrValue()+"\" and ");
							break;
						} else if(!attrInfo.getAttrCode().equals("INS_TYPE")) {
							//其他属性拼接查询
							queryStr.append("@"+attrInfo.getAttrCode()+" = \""+attrInfo.getAttrValue()+"\" and ");
						}
						
					}
					
				}
				
				if(queryStr.toString().endsWith("[")){
					queryStr.delete(queryStr.length()-1, queryStr.length());
				}else{
					queryStr.delete(queryStr.length()-5, queryStr.length());
					queryStr.append("]");
				}
			}
			//如果queryStr直接为"/*"，CM查询会直接报错
			if(queryStr.toString().equals("/*")){
				results.setResCode(CMConstant.CM_IMG_CMPROCESS_QUERY_RESULT_SET_TOO_LARGE);
				return results;
			}
			DKNVPair options[] = new DKNVPair[3];
			options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, Integer.toString(CMConstant.maxResultNum+1));
			options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer(DKConstant.DK_CM_CONTENT_YES));
			options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);
			DKResults dkResults = null;
		
			//查询字符串
			queryStr = queryStr.insert(0,"/"+itemTypeName);
			long queryTime = System.currentTimeMillis();
			dkResults = (DKResults) dsICM.evaluate(queryStr.toString(),DKConstant.DK_CM_XQPE_QL_TYPE, options);
			log.info("查询批次"+queryStr+"，用时："+(System.currentTimeMillis()-queryTime));
			if (dkResults.cardinality() == 0) {
				// 集合为空，CM中无指定批次或批次状态为忙，返回错误
				log.info("批次查询，查询条件："+queryStr+"，集合为空，CM中无指定批次");
				results.setResCode(CMConstant.CM_IMG_CMPORCESS_QUERY_RESULT_ISNULL);
				return results;
			}else if(dkResults.cardinality()>CMConstant.maxResultNum){//CM查询最大条数
				//查询返回的结果集数量太大，精确查询条件
				log.error("批次查询，查询条件："+queryStr+"，返回的结果集数量太大,超过了10条");
				results.setResCode(CMConstant.CM_IMG_CMPROCESS_QUERY_RESULT_SET_TOO_LARGE);
				return results;
			}
			dkIterator iter = dkResults.createIterator();//对查询结果集创建迭代器
			DKDDO ddoDocument = null;
			Outsourcer outSourcer = null;
			while (iter.more()) {
				outSourcer = new Outsourcer();
				StopWatch Watch = new Log4JStopWatch();
				long watchTime = System.currentTimeMillis();
				ddoDocument = (DKDDO) iter.next();
				//批次基本属性
				String batchID = ((String)ddoDocument.getData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docBatchId))).trim();
				String busiNo = ((String)ddoDocument.getData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docBusiNo))).trim();
				String innerVer = ((String)ddoDocument.getData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docInnerVer))).trim();
				
				
				//批次在各缓存目录下最高版本的存储情况
				Map<Integer,String> batchVerPath = new HashMap<Integer,String>();
				for(int i=0;i<GlobalVar.CMCatchDay;i++) {
					StringBuilder catchDirStr = new StringBuilder();
					catchDirStr.append(CMConstant.queryCacheFolder+File.separator);
					catchDirStr.append(DateUtil.getDateBefAft(i, "yyyyMMdd")+File.separator);
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
						    	int batchVer = Integer.parseInt(fileName.substring(start+1,end));
						    	if(batchVer>maxVerNum) {
						    		maxVerNum = batchVer;
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
				} else {
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
						//表示缓存中没有任何版本的缓存文件，需要向CM重新下载
						StringBuilder strNew = new StringBuilder();
						strNew.append(CMConstant.queryCacheFolder+File.separator);
						strNew.append(DateUtil.getDateStrCompact()+File.separator);
						strNew.append(batchID.substring(0,1)+File.separator+batchID.substring(1,2)+File.separator);
						strNew.append(batchID);
						batchCatchDir = strNew.toString();
						
					} else {
						//表示缓存中有部分版本的文件，只需增量下载文件
						batchCatchDir = batchVerPath.get(catchMaxVer);
					}
					
					//下载文件
					File batchCatchFile = new File(batchCatchDir);
					if(!batchCatchFile.exists()) {
						batchCatchFile.mkdirs();
					}
					
					short dataid = ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR,DKConstant.DK_CM_DKPARTS);
					if (dataid == 0) {
						log.error("批次查询失败！查询条件："+queryStr+"，Error：指定批次不存在文档部件PART属性");
						results.setResCode(CMConstant.CM_IMG_CMPROCESS_ATTRIBUTE_NOT_EXIST_ERROR);
						return results;
					}
					DKParts dkParts = (DKParts) ddoDocument.getData(dataid);
					dkIterator iter2 = dkParts.createIterator();
					List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
					while (iter2.more()) {
						DKImageICM image = (DKImageICM) iter2.next();
						StopWatch setQueryWatch = new Log4JStopWatch();
						/**
						if(catchMaxVer!=0){
							image.retrieve(DKConstant.DK_CM_CONTENT_ATTRONLY);
						}else{
							image.retrieve(DKConstant.DK_CM_CONTENT_YES);
						}
						**/
						image.retrieve(DKConstant.DK_CM_CONTENT_YES);
						setQueryWatch.stop("设置查询");
						ReadImgCall call = new ReadImgCall(image,queryStr.toString(),batchCatchDir,catchMaxVer);
						tasks.add(call);
					}
					StopWatch getIMGWatch = new Log4JStopWatch();
					long getIMGTime = System.currentTimeMillis();
					outSourcer.execute(tasks);//多线程从CM中下载文件，xxw
					log.info("获取文件"+queryStr+"，用时："+(System.currentTimeMillis()-getIMGTime));
					getIMGWatch.stop("获取文件");
					log.info("获取文档"+queryStr+"，用时:"+(System.currentTimeMillis()-watchTime));
					Watch.stop("获取文档*");
					
					//组装数据
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
					
				}
			}
			
			//释放对象
			iter = null;
			dkResults = null;
			
			results.setResCode("1");//成功结果
			results.setResultList(resultList);
			return results;
		} catch (Exception e) {
			log.error("批次查询失败！查询条件："+queryStr+"，Error：", e);
			results.setResCode(CMConstant.CM_IMG_CMPORCESS_QUERY_FAIL_ERROR);
			return results;
		} finally {
			try {
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
			} catch (Exception e2) {
				log.error("销毁/返回CM连接到连接池异常!", e2);
			}
		}
	}
	
	/**
	 * 获取批次在CM的版本号，xxw
	 * @param batchID
	 * @param appCode
	 * @return
	 */
	public static int getInnerVer(String batchID, String appCode,DKDatastoreICM dsICM) throws Exception{
		// 项类型的名称
		String itemTypeName = CMConstant.docName+appCode;
		// 创建查询字符串
		String queryStr = "/" + itemTypeName + "[@"+CMConstant.docBatchId+"=\"" + batchID + "\"]";
		DKResults results = null;
		String innerVer = "-1";
		try {
			DKNVPair options[] = new DKNVPair[3];
			options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "1");
			options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer(DKConstant.DK_CM_CONTENT_ATTRONLY));
			options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);
			// 查询对应与批次号的批次，返回的集合中只有一个批次
			results = (DKResults) dsICM.evaluate(queryStr,DKConstant.DK_CM_XQPE_QL_TYPE, options);
			
			dkIterator iter = results.createIterator();//创建迭代器，xxw
			DKDDO ddoDocument = null;
			while (iter.more()) {
				ddoDocument = (DKDDO) iter.next();
				//CM版本号
				innerVer = (String)(ddoDocument.getData(ddoDocument.dataId(DKConstant.DK_CM_NAMESPACE_ATTR, CMConstant.docInnerVer)));
			}
			//释放对象
			ddoDocument = null;
			iter = null;
			return Integer.parseInt(innerVer.trim());
		}catch (Exception e) {
			log.error("上传批次存储CM，获取批次内部版本号失败！批次号："+batchID+"，项类型："+itemTypeName+"，Error：", e);
			throw new Exception("上传批次存储CM，获取批次内部版本号失败！");
		}finally {
			results = null;
		}
	}
	
	/**
	 * 获取项类型所有属性
	 * @param dsICM
	 * @param itemTypeName
	 * @return
	 */
	public static List<String> getBatchAttrs(DKDatastoreICM dsICM, String itemTypeName)throws DKException,Exception{
		List<String> attrList = new ArrayList<String>();
		try{
			DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();
			DKItemTypeDefICM itemType = (DKItemTypeDefICM) dsDefICM.retrieveEntity(itemTypeName); 
			DKSequentialCollection attrColl = (DKSequentialCollection)itemType.listAllAttributes();
			dkIterator iter = attrColl.createIterator();
			DKAttrDefICM attr = null;
	        while(iter.more()){
	        	attr = (DKAttrDefICM) iter.next();
	        	attrList.add(attr.getName());
	        }
		}catch(DKException e){
			log.error("获取项类型："+itemTypeName+"所有属性失败！Error：",e);
			throw new DKException("获取项类型："+itemTypeName+"所有属性失败");
		}catch(Exception e){
			log.error("获取项类型："+itemTypeName+"所有属性失败！Error：",e);
			throw new Exception("获取项类型："+itemTypeName+"所有属性失败");
		}
		return attrList;
	}
	
	/**
	 * 判断批次是否存在
	 * @param batchID
	 * @param appCode
	 * @return
	 * @throws Exception
	 */
	public static int doesBatchExist(List<AttrInfo> attrList, String appCode) {
		DKDatastoreICM dsICM = null;
		DKResults results = null;
		// 查询语句
		StringBuilder queryStr = new StringBuilder();
		String sessionID = "";
		try {
			long cmConTime = System.currentTimeMillis();
			if(CMConstant.isCMPool.equals("0")){
				dsICM =  new DKDatastoreICM();//创建新连接
				dsICM.connect(CMConstant.RMN,CMConstant.CMUser,CMConstant.CMPwd,"");
			}else{
				dsICM = CMConnectionPool.getConnection(CMConstant.CMUser, CMConstant.CMPwd);
			}
			log.info("获取CM连接,sessionId:["+dsICM.getSessionId()+"]用时："+(System.currentTimeMillis()-cmConTime));
			sessionID = dsICM.getSessionId();
			cmConTime = System.currentTimeMillis();
			// 项类型名称
			String itemTypeName = "";
			if(appCode==null || appCode.equals("")){
				itemTypeName = "*";
			}else{
				itemTypeName = CMConstant.docName+appCode;
			}
			if(attrList!=null && attrList.size()>0){//扩展属性
				queryStr.append("[");
				AttrInfo attrInfo = null;
				for(int i=0;i<attrList.size();i++){
					attrInfo = attrList.get(i);
					if(!attrInfo.getAttrValue().equals("") && !attrInfo.getAttrCode().equals("INS_TYPE")){
						//由于性能问题，此处不使用like进行查询
						//queryStr.append("@"+attrInfo.getAttrCode()+" like \"%"+attrInfo.getAttrValue()+"%\" and ");
						queryStr.append("@"+attrInfo.getAttrCode()+" = \""+attrInfo.getAttrValue()+"\" and ");
					}
				}
				if(queryStr.toString().endsWith("[")){
					queryStr.delete(queryStr.length()-1, queryStr.length());
				}else{
					queryStr.delete(queryStr.length()-5, queryStr.length());
					queryStr.append("]");
				}
			}
			log.info("判断批次是否存在！判断条件："+queryStr);
			DKNVPair options[] = new DKNVPair[3];
			options[0] = new DKNVPair(DKConstant.DK_CM_PARM_MAX_RESULTS, "1");
			options[1] = new DKNVPair(DKConstant.DK_CM_PARM_RETRIEVE, new Integer(DKConstant.DK_CM_CONTENT_ATTRONLY));
			options[2] = new DKNVPair(DKConstant.DK_CM_PARM_END, null);
			log.info("CM连接,sessionId:["+sessionID+"]拼接查询条件["+queryStr+"]用时："+(System.currentTimeMillis()-cmConTime));
			cmConTime = System.currentTimeMillis();
			//查询字符串
			queryStr = queryStr.insert(0,"/"+itemTypeName);
			// 查询对应与批次号的批次，返回的集合中只有一个批次
			results = (DKResults) dsICM.evaluate(queryStr.toString(),DKConstant.DK_CM_XQPE_QL_TYPE, options);
			if (results.cardinality() == 0){
				log.info("CM连接,sessionId:["+sessionID+"]查询返回0用时："+(System.currentTimeMillis()-cmConTime));
				return 0;
			}else{
				log.info("CM连接,sessionId:["+sessionID+"]查询返回1用时："+(System.currentTimeMillis()-cmConTime));
				return 1;
			}
		}catch (Exception e) {
			log.error("判断批次是否存在失败！判断条件："+queryStr+"，Error：", e);
			return -1;
		}finally {
			try {
				log.info("CM连接,sessionId:["+sessionID+"]准备返回连接池："+(dsICM != null)+"=="+(CMConstant.isCMPool.equals("0")));
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
				results = null;
			} catch (Exception e2) {
				log.error("销毁/返回CM连接到连接池异常!", e2);
			}
		}
	}
}