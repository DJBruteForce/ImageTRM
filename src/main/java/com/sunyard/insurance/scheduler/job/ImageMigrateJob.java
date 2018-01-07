package com.sunyard.insurance.scheduler.job;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
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
import com.sunyard.insurance.entity.TRM_TASK_PLOT;
import com.sunyard.insurance.entity.TRM_TASK_RECORD;
import com.sunyard.insurance.util.ClassUtil;
import com.sunyard.insurance.util.DateUtil;
import com.sunyard.insurance.webService.DBBusiService;
import com.sunyard.insurance.webServiceImpl.DBBusiServiceImpl;

/**
 * 
 * @Title ImageMigrateJob.java
 * @Package com.sunyard.insurance.scheduler.job
 * @Description 迁移排程工作类
 * @author wuzelin
 * @time 2012-8-7 上午10:53:49
 * @version 1.0
 */
public class ImageMigrateJob implements Job {
	
	private static final Logger log = Logger.getLogger(ImageMigrateJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		/* 太平个性化，对于外网，所有推送，迁移排程均交给内网TRM做，外网一律跳过 */
		if(GlobalVar.isFTPOpen == 1) {// 如果FTP功能开启，则表示为外网，TRM跳过实施推送
			log.info("当前所处网络为外网，开启了FTP功能，跳过迁移排程!");
			return;
		}
		
		String end_time = (String) context.getMergedJobDataMap().get("END_TIME");
		String taskId = (String) context.getMergedJobDataMap().get("TASK_ID");
		boolean isEndTime = false;//排程是否到时间了
		if ("null".equals(taskId) || "".equals(taskId)) {
			log.error("没有获取到推送排程TASK_ID值!");
			return;
		}
		if ("null".equals(end_time) || "".equals(end_time)) {
			log.error("没有获取到迁移排程[TAKS_ID=" + taskId + "]的结束时间!");
			return;
		} else {
			//获取准确的停止时间
			int nowTime = Integer.parseInt(DateUtil.formatDate(new Date(), "HHmm"));
			int endTime = Integer.parseInt(end_time.replace(":", ""));
			if(endTime>=nowTime) {
				end_time = DateUtil.formatDate(new Date(), "yyyyMMdd")+end_time.replace(":", "");
			} else {
				end_time = DateUtil.getDateBefAft(-1, "yyyyMMdd")+end_time.replace(":", "");
			}
			log.info("迁移排程[TAKS_ID=" + taskId + "]的结束时间确定为:"+end_time);
		}

		DBBusiService dbService = new DBBusiServiceImpl();
		BatchManager batchManager = new BatchManager();
		
		// 获取排程所有策略，根据优先级排序
		List<TRM_TASK_PLOT> taskPlots = dbService.findAllPlotByTaskId(Integer.parseInt(taskId));
		if (null == taskPlots) {
			log.error("没有获取到迁移排程[TAKS_ID=" + taskId + "]的策略配置信息!");
			return;
		}

		// 循环所有的策略配置
		for (int i = 0; i < taskPlots.size(); i++) {
			TRM_TASK_PLOT plotConfig = taskPlots.get(i);
			String appCode = plotConfig.getId().getApp_code();
			String targetOrg = plotConfig.getTarget_org();
			String selfOrg = GlobalVar.svrInfoBean.getOrgCode();
			
			log.info("迁移业务类型[" + appCode+ "]优先级[" + plotConfig.getApp_priority() + "]目标机构号["+ targetOrg + "]");
			
			//目标机构校验
			ServerInfoBean svrInfoBean = dbService.getSvrInfoByOrgCode(targetOrg);
			if (null == svrInfoBean) {
				log.info("没有目标机构[" + targetOrg + "]的配置信息!");
				continue;
			}
			
			if (svrInfoBean.getOrgCode().equals(GlobalVar.svrInfoBean.getOrgCode())) {
				log.info("迁移目标机构[" + targetOrg+ "]非法，不能向自己迁移!");
				continue;
			}
			
			boolean appBoolean = true;
			//向USM发出业务类型待迁移批次查询
			while(appBoolean) {
				String nowTime = DateUtil.formatDate(new Date(), "yyyyMMddHHmm");
				if(nowTime.compareTo(end_time)>0) {
					isEndTime = true;
					appBoolean = false;
					continue;
				}
				List<String> btachList = dbService.findBatchsForMigrate(appCode,selfOrg, 50);
				if(null==btachList || btachList.size()==0) {
					//该业务类型迁移完毕了
					log.info("迁移排程：业务类型["+appCode+"]没有获取到待迁移批次,退出该策略!");
					appBoolean = false;
					continue;
				}
				
				log.info("迁移排程查询到业务类型["+appCode+"]待迁移批次数目:"+btachList.size());
				
				for(int b=0;b<btachList.size();b++) {
					String batchId = btachList.get(b);
					List<B_BATCHS_SRC> batchSrcList = dbService.findBatchSrcS(batchId, selfOrg);
					
					for(int k=0;k<batchSrcList.size();k++) {
						B_BATCHS_SRC bSrc = batchSrcList.get(k);
						log.info("开始向目标机构["+targetOrg+"]迁移批次["+batchId+"]版本号["+bSrc.getId().getBatchVer()+"]!");
						//判断目标机构是否存在该批次版本,如果存在直接变更已迁移状态
						B_BATCHS_SRC tarSrc = dbService.findBatchSrc(batchId, targetOrg, bSrc.getId().getBatchVer());
						if(null!=tarSrc) {
							//更新状态，记录批次操作记录
							bSrc.setIsMigrate("3");
							boolean isUpdate = dbService.updateBatchSrc(bSrc);
							if(isUpdate) {
								log.info("迁移批次["+batchId+"]版本号["+bSrc.getId().getBatchVer()+"机构号["+selfOrg+"]更新SRC表记录成功!");
								continue;
							} else {
								log.info("迁移批次["+batchId+"]版本号["+bSrc.getId().getBatchVer()+"机构号["+selfOrg+"]更新SRC表记录失败!");
								break;//这里有必要break吗？
							}
						}
						
						//获取批次文件进行传输
						String batchUrl = bSrc.getSrcUrl();
						BatchBean batchBean = null;
						String sydPath = "";
						if(null!=batchUrl && !"".equals(batchUrl) && !"null".equals(batchUrl)) {
							//存储在本地
							sydPath = batchUrl+File.separator+batchId+"_"+bSrc.getId().getBatchVer()+".syd";
						} else {
							//存储在CM,请求CM查询批次
							Map<String, String> pros = new HashMap<String, String>();
							B_BATCHS_INFO_ID infoId = new B_BATCHS_INFO_ID();
							infoId.setBatchId(batchId);
							infoId.setBatchVer(bSrc.getId().getBatchVer());
							B_BATCHS_INFO batchInfo = dbService.findByBatchInfo(infoId);
							if (null == batchInfo) {
								log.info("批次迁移[" + batchId + "]没有BATCHS_INFO信息!");
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
							List<B_BATCHS_METADATA> metadatas = dbService.findByBatchMetadata(batchId);
							if (null != metadatas) {
								for (int j = 0; j < metadatas.size(); j++) {
									pros.put(metadatas.get(j).getId().getPropCode(),
											metadatas.get(j).getPropValue());
								}
							}
							CMProessService proService = null;
							if("FileNetCE".equals(GlobalVar.CMType)){
								 proService = new FileNetCEProessServiceImpl();
							}else if("IBMCM".equals(GlobalVar.CMType)){
								 proService = new CMProessServiceImpl();
							}else if("FileNetForFAF".equals(GlobalVar.CMType)){
								 proService = new FileNetForFAFProcessServiceImpl();
							}
							String batchDir = proService.queryBatchForTRM(pros, "TRM");
							log.info("批次迁移[" + batchId + "]请求CM查询返回结果:" + batchDir);
							sydPath = batchDir + File.separator + batchId + "_" + bSrc.getId().getBatchVer()+ ".syd";
						}
						
						try {
							batchBean = ClassUtil.getSydUtilClass().getBatchBean(sydPath);
						} catch (Exception e) {
							log.info("解析批次["+batchId+"]版本["+bSrc.getId().getBatchVer()+"]SYD文件["+sydPath+"]失败!",e);
							break;
						}
						
						boolean isSubmit = batchManager.imageMigrateByVer(svrInfoBean.getOrgVip(),
								svrInfoBean.getSocket_port(), batchBean);
						
						if(isSubmit) {
							//更新迁移成功状态，记录批次操作记录
							log.info("批次["+batchId+"]版本["+bSrc.getId().getBatchVer()+"]迁移成功!");
							bSrc.setIsMigrate("3");
							dbService.updateBatchSrc(bSrc);
							this.saveTaskRecord(batchBean, isSubmit, dbService);
						} else {
							//记录批次操作记录
							log.info("批次["+batchId+"]版本["+bSrc.getId().getBatchVer()+"]迁移失败!");
//							bSrc.setIsMigrate("2");
//							dbService.updateBatchSrc(bSrc);
							dbService.UpdateBatchSRCMigrateStatus(batchId, selfOrg, bSrc.getId().getBatchVer(), 2);
							this.saveTaskRecord(batchBean, isSubmit, dbService);
							//break;
						}
						
					}
					
				}
				
			} //while END
			
			if(isEndTime) {
				//已经到时间了,不再执行下面的策略	
				log.info("迁移排程ID["+taskId+"]时间到了,退出当前排程!"+DateUtil.getDateTimeStr());
				break;
			}
			
		}//循环所有策略结束
		
		log.info("迁移排程ID["+taskId+"]所有策略执行完成,排程执行结束!"+DateUtil.getDateTimeStr());
		
	}
	
	
	/**
	 * 
	 *@Description 
	 *操作记录到操作记录表
	 *@param batchBean
	 *@param isSubmit 迁移是否成功
	 *@param dbService
	 */
	private void saveTaskRecord(BatchBean batchBean, boolean isSubmit, DBBusiService dbService){
		TRM_TASK_RECORD taskRecord = new TRM_TASK_RECORD();
		taskRecord.setApp_code(batchBean.getAppCode());
		taskRecord.setBatch_id(batchBean.getBatch_id());
		taskRecord.setBatch_ver(Integer.parseInt(batchBean.getBatch_ver()));
		taskRecord.setBusi_no(batchBean.getBusi_no());
		taskRecord.setInter_ver(Integer.parseInt(batchBean.getInter_ver()));
		taskRecord.setSvr_id(GlobalVar.serverId);
		taskRecord.setTask_time(new Date());
		taskRecord.setTask_type("1");//1-迁移操作
		taskRecord.setTask_time_str(DateUtil.getDateTimeStr());
		if(isSubmit){//成功
			taskRecord.setResult_code("1");//1-成功代码
			dbService.saveTask_Record(taskRecord);
		}else{//失败
			taskRecord.setResult_code("0");
			taskRecord.setError_msg("迁移批次失败");
			dbService.saveTask_Record(taskRecord);
		}
	}
}
