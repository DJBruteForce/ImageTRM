package com.sunyard.insurance.webServiceImpl;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.jws.WebService;
import org.apache.log4j.Logger;
import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.batch.busi.BatchManager;
import com.sunyard.insurance.cmapi.service.CMProessService;
import com.sunyard.insurance.cmapi.service.impl.CMProessServiceImpl;
import com.sunyard.insurance.cmapi.service.impl.FileNetCEProessServiceImpl;
import com.sunyard.insurance.cmapi.service.impl.FileNetForFAFProcessServiceImpl;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.entity.B_BATCHS_INFO;
import com.sunyard.insurance.entity.B_BATCHS_INFO_ID;
import com.sunyard.insurance.entity.B_BATCHS_METADATA;
import com.sunyard.insurance.entity.B_BATCHS_SRC;
import com.sunyard.insurance.entity.ServerInfoBean;
import com.sunyard.insurance.entity.TRM_TASK_RECORD;
import com.sunyard.insurance.util.ClassUtil;
import com.sunyard.insurance.util.DateUtil;
import com.sunyard.insurance.webService.DBBusiService;
import com.sunyard.insurance.webService.ImageBusiService;

@WebService(endpointInterface = "com.sunyard.insurance.webService.ImageBusiService")
public class ImageBusiServiceImpl implements ImageBusiService {

	private static final Logger log = Logger.getLogger(ImageBusiServiceImpl.class);

	private BatchManager batchManager = new BatchManager();

	public String batchSynchro(String batchId, String toOrgCode) {
		DBBusiService dbService = new DBBusiServiceImpl();
		ServerInfoBean svrInfoBean = dbService.getSvrInfoByOrgCode(toOrgCode);
		if (null == svrInfoBean) {
			log.info("批次同步[" + batchId + "]没有目标机构[" + toOrgCode + "]的配置信息!");
			return "0";
		}

		if (svrInfoBean.getOrgCode().equals(GlobalVar.svrInfoBean.getOrgCode())) {
			log.info("批次同步[" + batchId + "]同步目标机构[" + toOrgCode
					+ "]非法，不能向自己同步!");
			return "0";
		}

		// 获取本节点批次各版本影像记录
		List<B_BATCHS_SRC> srcList = dbService.findBatchSrcS(batchId,
				GlobalVar.svrInfoBean.getOrgCode());
		if (null == srcList) {
			log.info("批次同步[" + batchId + "]同步查询SRC表记录集失败!");
			return "0";
		}
		if (srcList.size() == 0) {
			log.info("批次同步[" + batchId + "]同步查询SRC表记录为0,没有SRC表记录!");
			return "0";
		}

		String resultStr = "1";
		log.info("批次同步[" + batchId + "]同步查询SRC表查询结果记录数为:" + srcList.size());
		// 循环上传版本区分CM以及本地缓存
		for (int i = 0; i < srcList.size(); i++) {
			B_BATCHS_SRC srcBean = srcList.get(i);
			Integer batch_ver = srcBean.getId().getBatchVer();
			BatchBean batchBean = null;

			B_BATCHS_SRC temBean = dbService.findBatchSrc(batchId, toOrgCode,
					batch_ver);
			if (null != temBean) {
				// 说明对方有该版本，跳过
				log.info("批次同步[" + batchId + "]目的地存在版本[" + batch_ver
						+ "]的影像,跳过该版本!");
				continue;
			}
			String sydPath = "";
			if (null != srcBean.getSrcUrl() && !"".equals(srcBean.getSrcUrl())
					&& !"null".equals(srcBean.getSrcUrl())) {
				log.info("批次同步[" + batchId + "]版本[" + batch_ver
						+ "]影像资料存储于本地缓存!");
				sydPath = srcBean.getSrcUrl() + File.separator + batchId + "_"
						+ batch_ver + ".syd";
			} else {
				log.info("批次同步[" + batchId + "]版本[" + batch_ver
						+ "]影像资料存储于CM内容管理!");
				Map<String, String> pros = new HashMap<String, String>();
				// 获取业务类型
				B_BATCHS_INFO_ID infoId = new B_BATCHS_INFO_ID();
				infoId.setBatchId(batchId);
				infoId.setBatchVer(batch_ver);
				B_BATCHS_INFO batchInfo = dbService.findByBatchInfo(infoId);
				if (null == batchInfo) {
					log.info("批次同步[" + batchId + "]没有BATCHS_INFO信息!");
					resultStr = "0";
					break;
				} else {
					pros.put("APP_CODE", batchInfo.getAppCode());
					if("FileNetCE".equals(GlobalVar.CMType)){
						pros.put("BATCH_ID", batchInfo.getId().getBatchId());
					}else if("IBMCM".equals(GlobalVar.CMType)){
						pros.put("BatchID", batchInfo.getId().getBatchId());
					}
					pros.put("BUSI_NUM", batchInfo.getBusiNo());
				}
				// 获取批次扩展属性
				List<B_BATCHS_METADATA> metadatas = dbService
						.findByBatchMetadata(batchId);
				if (null != metadatas) {
					for (int j = 0; j < metadatas.size(); j++) {
						String proCode = metadatas.get(j).getId().getPropCode();
						String proValue = metadatas.get(j).getPropValue();
						pros.put(proCode,proValue);
					}
				}
				// 请求CM查询批次
				CMProessService proService = null;
				if("FileNetCE".equals(GlobalVar.CMType)){
					 proService = new FileNetCEProessServiceImpl();
				}else if("IBMCM".equals(GlobalVar.CMType)){
					 proService = new CMProessServiceImpl();
				}else if("FileNetForFAF".equals(GlobalVar.CMType)){
					 proService = new FileNetForFAFProcessServiceImpl();
				}
				String batchDir = proService.queryBatchForTRM(pros, "TRM");
				log.info("批次同步[" + batchId + "]请求CM查询返回结果:" + batchDir);
				sydPath = batchDir + File.separator + batchId + "_" + batch_ver
						+ ".syd";
			}

			log.info("批次同步[" + batchId + "]版本[" + batch_ver + "]影像SYD文件完整路径为:"
					+ sydPath);

			try {
				batchBean = ClassUtil.getSydUtilClass().getBatchBean(sydPath);
			} catch (Exception e) {
				batchBean = null;
				log.info("批次同步[" + batchId + "]解析SYD文件[" + sydPath+ "]异常!",e);
			}

			if (null == batchBean) {
				log.info("批次同步[" + batchId + "]转换SYD文件[" + sydPath + "]为对象失败!");
				resultStr = "0";
				break;
			}
			// 批次操作记录
			TRM_TASK_RECORD taskRecord = new TRM_TASK_RECORD();
			taskRecord.setApp_code(batchBean.getAppCode());
			taskRecord.setBatch_id(batchBean.getBatch_id());
			taskRecord.setBatch_ver(Integer.parseInt(batchBean.getBatch_ver()));
			taskRecord.setBusi_no(batchBean.getBusi_no());
			taskRecord.setInter_ver(Integer.parseInt(batchBean.getInter_ver()));
			taskRecord.setSvr_id(GlobalVar.serverId);
			taskRecord.setTask_time(new Date());
			taskRecord.setTask_type("4");
			taskRecord.setTask_time_str(DateUtil.getDateTimeStr());
			
			boolean b = batchManager.imageMigrateByVer(svrInfoBean.getOrgVip(),
					svrInfoBean.getSocket_port(), batchBean);
			if (!b) {
				//失败
				resultStr = "0";
				taskRecord.setResult_code("0");
				taskRecord.setError_msg("传输失败");
				dbService.saveTask_Record(taskRecord);
				break;
			} else {
				//成功
				taskRecord.setResult_code("1");
				dbService.saveTask_Record(taskRecord);
			}
		}
		
		return resultStr;
	}
	
	public String batchVerSynchro(String batchId, Integer batchVer, String ipStr,Integer port) {
		DBBusiService dbService = new DBBusiServiceImpl();
		//从本地获取要同步的批次版本
		B_BATCHS_SRC temBean = dbService.findBatchSrc(batchId, GlobalVar.svrInfoBean.getOrgCode(), batchVer);
		if (null == temBean) {
			log.info("批次[" + batchId + "]版本[" + batchVer+ "]不存在当前批次版本的影像记录!");
			return "0";
		}
		
		String sydPath = "";
		if (null != temBean.getSrcUrl() && !"".equals(temBean.getSrcUrl())&& !"null".equals(temBean.getSrcUrl())) {
			log.info("批次同步[" + batchId + "]版本[" + batchVer+ "]影像资料存储于本地缓存!");
			sydPath = temBean.getSrcUrl() + File.separator + batchId + "_"+ batchVer + ".syd";
		} else {
			log.info("批次同步[" + batchId + "]版本[" + batchVer+ "]影像资料存储于内容管理!");
			Map<String, String> pros = new HashMap<String, String>();
			// 获取业务类型
			B_BATCHS_INFO_ID infoId = new B_BATCHS_INFO_ID();
			infoId.setBatchId(batchId);
			infoId.setBatchVer(batchVer);
			B_BATCHS_INFO batchInfo = dbService.findByBatchInfo(infoId);
			if (null == batchInfo) {
				log.info("批次同步[" + batchId + "]没有BATCHS_INFO信息!");
				return "0";
			} else {
				pros.put("APP_CODE", batchInfo.getAppCode());
				if("FileNetCE".equals(GlobalVar.CMType)){
					pros.put("BATCH_ID", batchInfo.getId().getBatchId());
				}else if("IBMCM".equals(GlobalVar.CMType)){
					pros.put("BatchID", batchInfo.getId().getBatchId());
				}
				pros.put("BUSI_NUM", batchInfo.getBusiNo());
			}
			// 获取批次扩展属性
			List<B_BATCHS_METADATA> metadatas = dbService.findByBatchMetadata(batchId);
			if (null != metadatas) {
				for (int j = 0; j < metadatas.size(); j++) {
					String proCode = metadatas.get(j).getId().getPropCode();
					String proValue = metadatas.get(j).getPropValue();
					pros.put(proCode,proValue);
				}
			}
			// 请求内容管理查询批次
			CMProessService proService = null;
			if("FileNetCE".equals(GlobalVar.CMType)){
				 proService = new FileNetCEProessServiceImpl();
			}else if("IBMCM".equals(GlobalVar.CMType)){
				 proService = new CMProessServiceImpl();
			}else if("FileNetForFAF".equals(GlobalVar.CMType)){
				 proService = new FileNetForFAFProcessServiceImpl();
			}
			String batchDir = proService.queryBatchForTRM(pros, "TRM");
			log.info("批次同步[" + batchId + "]请求CM查询返回结果:" + batchDir);
			sydPath = batchDir + File.separator + batchId + "_" + batchVer+ ".syd";
		}
		
		
		BatchBean batchBean = null;
		log.info("批次同步[" + batchId + "]版本[" + batchVer + "]影像SYD文件完整路径为:"+ sydPath);
		
		try {
			batchBean = ClassUtil.getSydUtilClass().getBatchBean(sydPath);
		} catch (Exception e) {
			batchBean = null;
			log.info("批次同步[" + batchId + "]解析SYD文件[" + sydPath+ "]异常!",e);
		}
		if (null == batchBean) {
			log.info("批次同步[" + batchId + "]转换SYD文件[" + sydPath + "]为对象失败!");
			return "0";	
		}
		
		// 批次操作记录
		TRM_TASK_RECORD taskRecord = new TRM_TASK_RECORD();
		taskRecord.setApp_code(batchBean.getAppCode());
		taskRecord.setBatch_id(batchBean.getBatch_id());
		taskRecord.setBatch_ver(Integer.parseInt(batchBean.getBatch_ver()));
		taskRecord.setBusi_no(batchBean.getBusi_no());
		taskRecord.setInter_ver(Integer.parseInt(batchBean.getInter_ver()));
		taskRecord.setSvr_id(GlobalVar.serverId);
		taskRecord.setTask_time(new Date());
		taskRecord.setTask_type("4");
		taskRecord.setTask_time_str(DateUtil.getDateTimeStr());
		
		//同步批次
		boolean b = batchManager.imageMigrateByVer2(ipStr,port, batchBean);
		if (b) {
			//成功
			taskRecord.setResult_code("1");
			dbService.saveTask_Record(taskRecord);
			return "1";
		} else {
			//失败
			taskRecord.setResult_code("0");
			taskRecord.setError_msg("同步--传输失败");
			dbService.saveTask_Record(taskRecord);
			return "0";
		}
		
	}
	
	/**
	 * 
	 *@param appCode
	 *@param busiNo
	 *@param batchId
	 *@return
	 *@Description
	 *
	 */
	public boolean DeleteBatchFromCM(String appCode, String busiNo, String batchId) {
		CMProessService proService = null;
		if("FileNetCE".equals(GlobalVar.CMType)){
			 proService = new FileNetCEProessServiceImpl();
		}else if("IBMCM".equals(GlobalVar.CMType)){
			 proService = new CMProessServiceImpl();
		}else if("FileNetForFAF".equals(GlobalVar.CMType)){
			 proService = new FileNetForFAFProcessServiceImpl();
		}
		return proService.deleteBatch(appCode, batchId);
	}

	
	
	

}
