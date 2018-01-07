package com.sunyard.insurance.scheduler.job;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.cmapi.service.CMProessService;
import com.sunyard.insurance.cmapi.service.impl.CMProessServiceImpl;
import com.sunyard.insurance.cmapi.service.impl.FileNetCEProessServiceImpl;
import com.sunyard.insurance.cmapi.service.impl.FileNetForFAFProcessServiceImpl;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.entity.B_BATCHS_SRC;
import com.sunyard.insurance.entity.TRM_TASK_PLOT;
import com.sunyard.insurance.util.ClassUtil;
import com.sunyard.insurance.util.DateUtil;
import com.sunyard.insurance.webService.DBBusiService;
import com.sunyard.insurance.webServiceImpl.DBBusiServiceImpl;

/**
 * 
 * @Title ImageCleanJob.java
 * @Package com.sunyard.insurance.scheduler.job
 * @Description 清理排程服务类
 * @author xxw
 * @time 2012-9-19 下午04:04:16
 * @version 1.0
 */
public class ImageCleanJob implements Job {

	private static final Logger log = Logger.getLogger(ImageCleanJob.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		String end_time = (String) context.getMergedJobDataMap().get("END_TIME");
		String taskId = (String) context.getMergedJobDataMap().get("TASK_ID");
		boolean isEndTime = false;// 排程是否到时间了
		
		if ("null".equals(taskId) || "".equals(taskId)) {
			log.error("没有获取到清理排程TASK_ID值!");
			return;
		}
		if ("null".equals(end_time) || "".equals(end_time)) {
			log.error("没有获取到清理排程[TAKS_ID=" + taskId + "]的结束时间!");
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
			log.info("清理排程[TAKS_ID=" + taskId + "]的结束时间确定为:"+end_time);
		}
		
		DBBusiService dbService = new DBBusiServiceImpl();
		
		// 获取排程所有策略，根据优先级排序
		List<TRM_TASK_PLOT> taskPlots = dbService.findAllPlotByTaskId(Integer.parseInt(taskId));
		if (null == taskPlots) {
			log.error("没有获取到清理排程[TAKS_ID=" + taskId + "]的策略配置信息!");
			return;
		}
		
		// 循环所有的策略配置
		for (int i = 0; i < taskPlots.size(); i++) {
			TRM_TASK_PLOT plotConfig = taskPlots.get(i);
			String appCode = plotConfig.getId().getApp_code();
			Integer cacheDay = plotConfig.getCache_days();
			String selfOrg = GlobalVar.svrInfoBean.getOrgCode();
			log.info("清理业务类型:[" + appCode + "] 缓存天数:[" + cacheDay + "]天");
			boolean appBoolean = true;
			
			while (appBoolean) {
				String nowTime = DateUtil.formatDate(new Date(), "yyyyMMddHHmm");
				if (nowTime.compareTo(end_time) > 0) {
					isEndTime = true;
					appBoolean = false;
					continue;
				}
				List<String> batchList = dbService.findBatchsForClean(appCode,
						GlobalVar.svrInfoBean.getOrgCode(), 50, cacheDay);
				
				if (null == batchList || batchList.size() == 0) {
					log.info("没有获取到业务类型[" + appCode + "]的待清理批次!");
					appBoolean = false;
					continue;
				}
				
				log.info("向USM发出查询业务类型[" + appCode + "]获取待清理批次数目["+ batchList.size() + "]条");

				for (int k = 0; k < batchList.size(); k++) {
					String batchId = batchList.get(k);
					List<B_BATCHS_SRC> srcList = dbService.findBatchSrcS(batchId, selfOrg);
					
					// 最大版本是否迁移成功3:成功
					String isMigrate = srcList.get(srcList.size()-1).getIsMigrate();
					
					if ("3".equals(isMigrate)) {// 批次所有版本已全部迁移
						// 开始循环删除所有版本
						for (int s = srcList.size() - 1; s >= 0; s--) {
							B_BATCHS_SRC bSrc = srcList.get(s);
							//判断版本是存储在内容管理还是本地
							String batchUrl = bSrc.getSrcUrl();
							boolean isDelBatchVer = false;
							
							if(null==batchUrl || "".equals(batchUrl) || "null".equals(batchUrl)) {
								//存储在CM
								isDelBatchVer = this.delFormCM(bSrc,dbService);
							} else {
								isDelBatchVer = this.delFormLocal(bSrc,dbService);
							}
							
							//清理成功/失败处理
							if(isDelBatchVer) {
								//成功
								log.info("清理排程删除批次["+bSrc.getId().getBatchId()+"]版本["+bSrc.getId().getBatchVer()+"]成功!");
							} else {
								//失败
								log.info("清理排程删除批次["+bSrc.getId().getBatchId()+"]版本["+bSrc.getId().getBatchVer()+"]失败!");
								break;
							}
							
						}//循环某个批次所有版本 END
					}
				}

			}// while END
			
			
			if (isEndTime) {// 已经到时间了,不再执行下面的策略
				log.info("清理排程ID["+taskId+"]结束时间已经到了,退出当前排程！");
				break;
			}

		}
		
		log.info("清理排程ID[" + taskId + "]所有策略执行完成,排程执行结束,结束时间："+ DateUtil.getDateTimeStr());

	}
	
	//从内容管理中删除
	public boolean delFormCM(B_BATCHS_SRC bSrc,DBBusiService dbService) {
		CMProessService proService = null;
		if("FileNetCE".equals(GlobalVar.CMType)){
			 proService = new FileNetCEProessServiceImpl();
		}else if("IBMCM".equals(GlobalVar.CMType)){
			 proService = new CMProessServiceImpl();
		}else if("FileNetForFAF".equals(GlobalVar.CMType)){
			 proService = new FileNetForFAFProcessServiceImpl();
		}
		boolean isDeleteInCM = proService.deleteBatch(bSrc.getAppCode(), bSrc.getId().getBatchId());
		if(isDeleteInCM) {
			log.info("清理排程从内容管理中删除批次["+bSrc.getId().getBatchId()+"]版本["+bSrc.getId().getBatchVer()+"]成功!");
			//再删除数据库记录
			boolean isDelRecord = dbService.deleteBatchSRC(bSrc.getId().getBatchId(), GlobalVar.svrInfoBean.getOrgCode(), bSrc.getId().getBatchVer());
			if(isDelRecord) {
				log.info("清理排程删除批次["+bSrc.getId().getBatchId()+"]版本["+bSrc.getId().getBatchVer()+"]数据库记录成功!");
				return true;
			} else {
				log.info("清理排程删除批次["+bSrc.getId().getBatchId()+"]版本["+bSrc.getId().getBatchVer()+"]数据库失败!");
				return false;
			}
		} else {
			log.info("清理排程从内容管理中删除批次["+bSrc.getId().getBatchId()+"]版本["+bSrc.getId().getBatchVer()+"]失败!");
			return false;
		}
	}
	
	
	
	//从本地缓存中删除
	public boolean delFormLocal(B_BATCHS_SRC bSrc,DBBusiService dbService) {
		BatchBean batchBean = null;
		String sydPath = bSrc.getSrcUrl()+File.separator+bSrc.getId().getBatchId()+"_"+bSrc.getId().getBatchVer()+".syd";
		
		try {
			if(1==bSrc.getId().getBatchVer()) {
			//是版本一的情况下直接删除整个目录
			File bf = new File(bSrc.getSrcUrl());
			
				FileUtils.deleteDirectory(bf);
			
			}
		} catch (IOException e) {
			log.error("清理排程批次["+bSrc.getId().getBatchId()+"]版本["+bSrc.getId().getBatchVer()+"]存储目录异常!");
			return true;
		}
		
		try {
			File sydFile = new File(sydPath);
			if(sydFile.exists()){
				batchBean = ClassUtil.getSydUtilClass().getBatchBean(sydPath);
			}
		} catch (Exception e) {
			log.error("清理排程获取批次["+bSrc.getId().getBatchId()+"]版本["+bSrc.getId().getBatchVer()+"]SYD对象异常!");
			return true;
		}
		
		//先删除数据库记录,后删除文件
		boolean isDelRecord = dbService.deleteBatchSRC(bSrc.getId().getBatchId(), GlobalVar.svrInfoBean.getOrgCode(), bSrc.getId().getBatchVer());
		if(!isDelRecord) {
			log.error("清理排程删除批次["+bSrc.getId().getBatchId()+"]版本["+bSrc.getId().getBatchVer()+"]数据库记录失败!");
			return false;
		}
		
		if((null != batchBean) && (null != batchBean.getBatchFileList())) {
			//删除所有文件
			for(int i=0;i<batchBean.getBatchFileList().size();i++) {
				File f = new File(batchBean.getBatchFileList().get(i).getFileFullPath());
				try {
					FileUtils.forceDelete(f);
					//递归删除空的父目录
					String filePath = f.getParent();
					while(true) {
						File tPath = new File(filePath);
						if(tPath.listFiles().length==0) {
							FileUtils.forceDelete(tPath);
							filePath = tPath.getParent();
						} else {
							break;
						}
					}
				} catch (IOException e) {
					log.error("清理排程删除批次文件失败!",e);	
				}
			}
			return true;
		} else {
			log.error("清理排程删除批次["+bSrc.getId().getBatchId()+"]版本["+bSrc.getId().getBatchVer()+"]SYD文件丢失!");
			return false;
		}
	}
	
}
