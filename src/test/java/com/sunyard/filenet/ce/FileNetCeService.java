package com.sunyard.filenet.ce;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import org.perf4j.StopWatch;
import org.perf4j.log4j.Log4JStopWatch;
import com.filenet.api.property.Properties;
import com.sunyard.filenet.ce.bean.CEContent;
import com.sunyard.filenet.ce.bean.CEContentSrcType;
import com.sunyard.filenet.ce.bean.CEDocument;
import com.sunyard.filenet.ce.bean.CeConnectBean;
import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.batch.bean.BatchFileBean;
import com.sunyard.insurance.common.CMConstant;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.filenet.faf.cesupport.CEConfig;
import com.sunyard.insurance.filenet.faf.cesupport.CEConstant;
import com.sunyard.insurance.filenet.faf.cesupport.util.FileUtil;

public class FileNetCeService {
	
	private static final Logger log = Logger.getLogger(FileNetCeService.class);
	
	public CeConnectBean getCeConnectBean() {
		String uri = "http://192.168.5.130:9080/wsi/FNCEWS40MTOM/";
		String username = "administrator";
		String password = "filenet";
		String jaas = null;
		String domainName = "p8domain";
		String objectStoreName = "EVTFS";
		CeConnectBean bean = new CeConnectBean(uri,username,password,jaas,domainName,objectStoreName);
		return bean;
	}
	
	//获取批次的内部版本号值
	public int queryBatchVersion(String batchId) {
		int innerVer;
		//丰田和其他CE这里组织的SQL语句不同
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT d.INNER_VER FROM Document d");
		sql.append(" WHERE d.IsCurrentVersion=TRUE AND d.BATCH_ID='").append(batchId).append("'");
		
		FileNetCeUtils fileNetUtil = new FileNetCeUtils();
		List<Properties> propsList = fileNetUtil.searchCEProperties(this.getCeConnectBean(), sql.toString());
		
		if (propsList == null || propsList.isEmpty()) {
			innerVer = -1;
		} else {
			//这里只获取第一条记录的内部版本号的值
			Properties currentProps = propsList.get(0);
			String innerVerStr = String.valueOf(currentProps.getStringValue("INNER_VER"));
			innerVer = Integer.parseInt(innerVerStr);
		}
		
		return innerVer;
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
	public String createBatchItem(Map<String, String> attrMap,BatchBean batchBean, List<BatchFileBean>  batchFilelist, String SRC_NAME) throws Exception {
		StringBuilder docTitle = new StringBuilder();
		docTitle.append(batchBean.getBusi_no()).append("_").append(batchBean.getAppCode()).toString();
		String GD_FLAG = new String("0");
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
				properties.put(mapKey, attrMap.get(mapKey));
			}
		}
		
		//添加文件
		List<CEContent> contentList = new ArrayList<CEContent>();
		for (int i = 0; i < batchFilelist.size(); i++) {
			BatchFileBean imgFile = batchFilelist.get(i);
			// 取得文件名
			String fileName = imgFile.getFileName();
			// 取得文件的后缀名
			String suffixName = FileUtil.getFileSuffix(imgFile.getFileName());
			// 取得文件的MIME TYPE
			String mimeType = GlobalVar.mimeHasMap.get(suffixName);
			if (mimeType == null || "".equals(mimeType)) {
				mimeType = CEConstant.DEFAULT_DOCTYPE;
			}
			CEContent ceContent = new CEContent(CEContentSrcType.TRANSFER,fileName, mimeType, new FileInputStream(imgFile.getFileFullPath()));
			contentList.add(ceContent);
		}
		try {
			CEDocument ceDoc = new CEDocument(CEConfig.DOCUMENT_CLASS,CEConstant.DEFAULT_DOCTYPE, CEConfig.MAIN_FOLDER, batchBean.getBatch_id(), properties, contentList);
			new FileNetCeUtils().createCEDocument(this.getCeConnectBean(), ceDoc);//创建CEDocment
		} catch (Exception e) {
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
	public String updateBatchItem(Map<String, String> attrMap,BatchBean batchBean, List<BatchFileBean>  batchFilelist, String SRC_NAME) throws Exception {
		StringBuilder DOC_TITLE = new StringBuilder();
		DOC_TITLE.append(batchBean.getBusi_no()).append("_").append(batchBean.getAppCode()).toString();//DocumentTitle+BUSI_NO+APP_CODE
		String GD_FLAG = new String("0");
		
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
				properties.put(mapKey, attrMap.get(mapKey));
			}
		}
		
		Set<String> imgSet = new HashSet<String>();
		List<CEContent> contentList = new ArrayList<CEContent>();
		for (int i = 0; i < batchFilelist.size(); i++) {
			BatchFileBean imgFile = batchFilelist.get(i);
			// 取得文件名
			String fileName = imgFile.getFileName();
			// 取得文件的后缀名
			String suffixName = FileUtil.getFileSuffix(imgFile.getFileName());
			// 取得文件的MIME TYPE
			String mimeType = GlobalVar.mimeHasMap.get(suffixName);
			if (mimeType == null || "".equals(mimeType)) {
				mimeType = CEConstant.DEFAULT_DOCTYPE;
			}
			CEContent ceContent = new CEContent(CEContentSrcType.TRANSFER,fileName, mimeType, new FileInputStream(imgFile.getFileFullPath()));
			contentList.add(ceContent);
			imgSet.add(fileName);
		}

		StringBuilder searchSql = new StringBuilder();
		searchSql.append("SELECT d.BATCH_ID, d.ContentElements FROM Document d");
		searchSql.append(" WHERE d.IsCurrentVersion=TRUE AND d.BATCH_ID='").append(batchBean.getBatch_id()).append("'");
		
		StringBuilder modifySql = new StringBuilder();
		modifySql.append("SELECT d.Reservation FROM Document d");
		modifySql.append(" WHERE d.IsCurrentVersion=TRUE AND d.BATCH_ID='").append(batchBean.getBatch_id()).append("'");
		
		try {
			List<CEDocument> ceDocList = new FileNetCeUtils().searchCEDocuments(this.getCeConnectBean(), searchSql.toString());
			CEDocument currentDoc = ceDocList.get(0);
			List<CEContent> curContentList = currentDoc.getContentList();
			for(int i = curContentList.size() - 1; i >= 0; i--) {
				CEContent cec = curContentList.get(i);
				String contentName = cec.getContentName();
				if(imgSet.contains(contentName)) {
					curContentList.remove(i);
				}
			}
			contentList.addAll(curContentList);
			CEDocument ceDoc = new CEDocument(CEConfig.DOCUMENT_CLASS,CEConstant.DEFAULT_DOCTYPE, CEConfig.MAIN_FOLDER,batchBean.getBatch_id(), properties, contentList);
			new FileNetCeUtils().modifyCEDocument(this.getCeConnectBean(), ceDoc, modifySql.toString());//修改CEDocment
		} catch (Exception e) {
			throw e;
		}
		return "0";
	}
	
	/**
	 * 提交批次
	 * @param attrList
	 * @param BATCH_ID
	 * @param APP_CODE
	 * @param BUSI_NO
	 * @param SRC_NAME 路径
	 * @param INNER_VER
	 * @param isExist 是否是新增  true:是  false:否
	 * @return
	 */
	public String submitBatch(Map<String, String> attrMap,BatchBean batchBean, boolean isExist) {
		// 批次提交是否成功标志，0成功，否则失败
		String flag = "0";
		List<BatchFileBean>  batchFilelist = batchBean.getBatchFileList();
		String batchPath="";
		for(BatchFileBean bean : batchFilelist){
			if(bean.getFileFullPath().endsWith(".syd")){
				batchPath=new File(bean.getFileFullPath()).getParent();
				break;
			}
		}
		
		try {
			if (isExist) {
				// 新增批次
				StopWatch watch = new Log4JStopWatch();
				flag = createBatchItem(attrMap, batchBean,batchFilelist,batchPath);
				watch.stop("create CEDocument");
			} else {
				// 修改批次
				StopWatch watch = new Log4JStopWatch();
				flag = updateBatchItem(attrMap, batchBean,batchFilelist,batchPath);
				watch.stop("update CEDocument");
			}
		} catch (Exception e) {
			log.error("提交修改CE批次异常!",e);
			return CMConstant.CM_IMG_CMPROCESS_CM_OPERATION_ERROR;
		}
		return flag;
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
