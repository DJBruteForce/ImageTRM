package com.sunyard.insurance.scheduler.job;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONObject;
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
  * @Title ImagePushJob.java
  * @Package com.sunyard.insurance.scheduler.job
  * @Description 推送排程服务类
  * @author xxw
  * @time 2012-9-19 下午04:17:42  
  * @version 1.0
 */
public class ImagePushJob implements Job {
	
	private static final Logger log = Logger.getLogger(ImagePushJob.class);
	
	DBBusiService dbService = new DBBusiServiceImpl();
	BatchManager batchManager = new BatchManager();
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		/* 太平个性化，对于外网，所有推送，迁移排程均交给内网TRM做，外网一律跳过 */
		if(GlobalVar.isFTPOpen == 1) {// 如果FTP功能开启，则表示为外网，TRM跳过实施推送
			log.info("当前所处网络为外网，开启了FTP功能，跳过推送排程!");
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
			log.error("没有获取到推送排程[TAKS_ID="+taskId+"]的结束时间!");
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
			log.info("推送排程[TAKS_ID=" + taskId + "]的结束时间确定为:"+end_time);
		}
		
		//获取排程所有策略，根据优先级排序
		List<TRM_TASK_PLOT> taskPlots = dbService.findAllPlotByTaskId(Integer.parseInt(taskId));
		if(null==taskPlots) {
			log.error("没有获取到推送排程[TAKS_ID="+taskId+"]的策略配置信息!");
			return;
		}
		
		
		// 循环所有的策略配置
	   for (int i = 0; i < taskPlots.size();i++ ) {
			TRM_TASK_PLOT plotConfig = taskPlots.get(i);
			String appCode = plotConfig.getId().getApp_code();
			String selfOrgCode = GlobalVar.svrInfoBean.getOrgCode();//自己所在机构号
			log.info("推送业务类型[" + appCode+ "]优先级[" + plotConfig.getApp_priority() + "]");
			Set<String> orgCodeSet = new HashSet<String>();//用来存放所有的推送机构
			String plotType = plotConfig.getPlot_type();//规则类型；1-target_org生效；2-task_plot生效
			if("1".equals(plotType)) {//目标机构生效
				orgCodeSet.add(plotConfig.getTarget_org());
			}
			
			boolean appBoolean = true;
			while(appBoolean) {
				String nowTime = DateUtil.formatDate(new Date(), "yyyyMMddHHmm");
				if(nowTime.compareTo(end_time)>0) {//每处理完50笔判断下是否到达结束时间
					log.info("推送排程ID["+taskId+"]结束时间到了!");
					isEndTime = true;
					appBoolean = false;
					continue;
				}
				//获取50笔
				List<String> batchList = dbService.findBatchsForPush(appCode,selfOrgCode, 50);//查询本机构业务类型50条待推送
				if(null==batchList || batchList.size()==0) {
					log.info("推送排程：业务类型["+appCode+"]没有获取到待推送的批次,继续执行下一个排程策略");//该业务类型迁移完毕了
					appBoolean = false;
					continue;
				}
				log.info("推送排程查询到业务类型["+appCode+"],待推送的批次数目["+batchList.size()+"]条");
				//开始推送批次，遍历所有需要推送的批次
				for(int k=0; k<batchList.size(); k++){
					String batchId = batchList.get(k);
					List<B_BATCHS_SRC> batchSrcList = dbService.findBatchSrcS(batchId, selfOrgCode);//根据本机构号查询批次所有版本
					if(null==batchSrcList || batchSrcList.size()==0) {
						log.info("推送排程查询到业务类型["+appCode+"],批次号["+batchId+"]没有获取到SRC表记录!");
						continue;
					}
					log.info("推送排程查询到业务类型["+appCode+"],批次号["+batchId+"]待推送的版本数目:["+batchSrcList.size()+"]个");
					if("2".equals(plotType)) {
						//表达式生效,根据BIZ_ORG与表达式得到推送机构列表
						B_BATCHS_INFO_ID id = new B_BATCHS_INFO_ID(batchId,1);
						B_BATCHS_INFO binfo = dbService.findByBatchInfo(id);
						orgCodeSet = this.getTargetOrgs(batchId,binfo.getBizOrg(),plotConfig.getTask_plot());
					}
					
					//批次成功机构数量
					int isSuccessOrg = 0;
					
					//循环推送机构orgCodeSet,遍历所有机构推送,依次推送
					for(Iterator<String> itor = orgCodeSet.iterator();itor.hasNext();) {
						String targetorgCode = itor.next();
						//推送批次所有版本到目标机构
						boolean b = this.submitBatchToOrg(targetorgCode, batchId, batchSrcList);
						if(b) {
							//批次所有版本全部成功
							isSuccessOrg++;
							log.info("批次["+batchId+"]推送到机构["+targetorgCode+"]所有版本全部成功!");
						} else {
							log.info("批次["+batchId+"]推送到机构["+targetorgCode+"]所有版本失败(部分版本推送失败/全部版本推送失败)!");
						}
						
					}
					
					//这里判断是否全部推送成功,并且记录数据库状态
					if((isSuccessOrg==orgCodeSet.size()) && (orgCodeSet.size()>0)) {
						log.info("批次["+batchId+"]推送到所有机构全部成功!");
						//3-全部成功
						dbService.UpdateBatchSRCPushStatus(batchId, selfOrgCode,batchSrcList.size(), 3);
					} else if(isSuccessOrg==0) {
						log.info("批次["+batchId+"]推送到所有机构失败(全部机构推送失败)!");
						//1-全部机构推送失败
						dbService.UpdateBatchSRCPushStatus(batchId, selfOrgCode,batchSrcList.size(), 1);
					} else {
						log.info("批次["+batchId+"]推送到所有机构部分推送失败(部分机构推送失败)!");
						//2-部分机构推送成功
						dbService.UpdateBatchSRCPushStatus(batchId, selfOrgCode,batchSrcList.size(), 2);
					}
				  
				}//循环50笔 要推送的批次
			}//while END
			
			if(isEndTime) {
				log.info("推送排程ID["+taskId+"]结束时间到了,不再执行下面的策略!");
				continue;
			}
			
			/**
			//判断当前循环是否最后一个策略
			if(i == (taskPlots.size()-1)) {
				//还没到结束时间
				if(!isEndTime) {
					//根据配置，判断是否重新循环
					if("1".equals(GlobalVar.pushJobIsWait)){//等待
						i = -1;
						try {
							log.info("推送排程ID["+taskId+"]循环缓存策略完毕,休眠"+GlobalVar.pushJobSleepSecs+" 秒");
							Thread.sleep(GlobalVar.pushJobSleepSecs*1000);
						} catch (InterruptedException e) {
							log.info("推送排程ID["+taskId+"]休眠等待异常!",e);
						}
					}
				}
			}
			**/
			
	   	}//for END 
	   	
	   log.info("推送排程ID["+taskId+"]执行结束!");
	}
	
	
	/**
	 * 
	 *@Description 
	 *操作记录到操作记录表
	 *@param batchBean
	 *@param isSubmit 推送是否成功
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
		taskRecord.setTask_type("2");//2-推送批次操作
		taskRecord.setTask_time_str(DateUtil.getDateTimeStr());
		if(isSubmit){//成功
			taskRecord.setResult_code("1");//1-成功代码
			dbService.saveTask_Record(taskRecord);
		}else{//失败
			taskRecord.setResult_code("0");
			taskRecord.setError_msg("推送批次失败");
			dbService.saveTask_Record(taskRecord);
		}
	}
	
	/**
	 *@Description 
	 *获取推送的目标机构号 
	 *@param syd
	 *@param taskPlot
	 *@return
	 */
	private Set<String> getTargetOrgs(String batchId,String bizOrg, String taskPlot){
		Set<String> orgCodeSet = new HashSet<String>();
		try {
			String[] plots = taskPlot.split("@");
			for(int i=0; i<plots.length; i++){
				Object[] str =  plots[i].split("=");
				JSONObject jsonOb = JSONObject.fromObject(str[0]);
				String plot_biz_org = (String)jsonOb.get("BIZ_ORG");
				if("*".equals(plot_biz_org) || bizOrg.equals(plot_biz_org)) {
					//获取满足该表达式的推送机构
					String tOrgStr = ""+str[1];
					if("$SELF".equalsIgnoreCase(tOrgStr)) {
						if(null == bizOrg || "".equals(bizOrg)) {
							log.error("批次["+batchId+"]BIZ_ORG字段为空,无法做推送表达式$SELF解析!");
						} else {
							orgCodeSet.add(bizOrg);
						}
					} else {
						orgCodeSet.add(tOrgStr);
					}
					
				}
			}
		} catch (Exception e) {
			log.error("解析自定义目标机构号规则出错 ",e);
		}
		
		return orgCodeSet;
	}
	
	//推送一个批次所有版本到目标机构
	public boolean submitBatchToOrg(String targetorgCode,String batchId,List<B_BATCHS_SRC> batchSrcList) {
		
		int isSuccess = 0;//成功推送版本数量
		
		//根据目标机构号获取ServerInfoBean
		ServerInfoBean serverInfo = dbService.getSvrInfoByOrgCode(targetorgCode);
		if(null==serverInfo){
			log.error("[推送排程]没有目标机构[" + targetorgCode + "]的配置信息!");
			return false;
		}else if (serverInfo.getOrgCode().equals(GlobalVar.svrInfoBean.getOrgCode())) {
			log.info("[推送排程]目标机构[" + targetorgCode+ "]非法，向自己推送，默认返回成功!");
			return true;
		}
		
		log.info("向目标机构[" + targetorgCode+ "]推送批次["+batchId+"]所有版本影像文件!");
		
		
		//循环版本，遍历每个批次的所有版本，按版本从小到大依次推送
		for(int m=0; m<batchSrcList.size(); m++) {
			B_BATCHS_SRC srcBean = batchSrcList.get(m);
			//判断目标机构是否存在该批次版本
			B_BATCHS_SRC tarSrc = dbService.findBatchSrc(batchId, targetorgCode, srcBean.getId().getBatchVer());
			if(null != tarSrc) {
				isSuccess++;
			} else {
				log.info("开始推送批次["+batchId+"]版本号["+srcBean.getId().getBatchVer()+"]影像资料!");
				String batchUrl = srcBean.getSrcUrl();
				BatchBean batchBean = null;
				String sydPath = "";
				if(null!=batchUrl && !"".equals(batchUrl) && !"null".equals(batchUrl)) {
					//批次存储在本地，从本地获取，然后执行推送
					sydPath = batchUrl+File.separator+batchId+"_"+srcBean.getId().getBatchVer()+".syd";
				} else {
					//批次存储在CM,请求CM查询批次到本地临时缓存后，然后执行推送
					Map<String, String> prosMap = new HashMap<String, String>();
					B_BATCHS_INFO_ID infoId = new B_BATCHS_INFO_ID();
					infoId.setBatchId(batchId);
					infoId.setBatchVer(srcBean.getId().getBatchVer());
					B_BATCHS_INFO batchInfo = dbService.findByBatchInfo(infoId);
					if (null == batchInfo) {
						log.info("批次推送[" + batchId + "]没有BATCHS_INFO信息!");
						break;
					} else {
						prosMap.put("APP_CODE", batchInfo.getAppCode());
						if("FileNetCE".equals(GlobalVar.CMType)){
							prosMap.put("BATCH_ID", batchInfo.getId().getBatchId());
						}else if("IBMCM".equals(GlobalVar.CMType)){
							prosMap.put("BatchID", batchInfo.getId().getBatchId());
						}
						prosMap.put("BUSI_NUM", batchInfo.getBusiNo());
					}
					// 获取批次扩展属性
					List<B_BATCHS_METADATA> metadataList = dbService.findByBatchMetadata(batchId);
					if (null != metadataList) {
						for (int j = 0; j < metadataList.size(); j++) {
							prosMap.put(metadataList.get(j).getId().getPropCode(),metadataList.get(j).getPropValue());
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
					String batchDir = proService.queryBatchForTRM(prosMap, "TRM");
					log.info("批次[" + batchId + "]请求CM查询返回结果:" + batchDir);
					sydPath = batchDir + File.separator + batchId + "_" + srcBean.getId().getBatchVer()+ ".syd";
				}
				//========================================================================================
				try {
					batchBean = ClassUtil.getSydUtilClass().getBatchBean(sydPath);
				} catch (Exception e) {
					log.error("解析批次["+batchId+"]版本["+srcBean.getId().getBatchVer()+"]SYD文件["+sydPath+"]失败!",e);
					break;
				}
				
				boolean isSubmit = batchManager.imageMigrateByVer(serverInfo.getOrgVip(),serverInfo.getSocket_port(), batchBean);
				if(isSubmit) {
					log.info("批次["+batchId+"]版本["+batchBean.getBatch_ver()+"]推送到机构["+targetorgCode+"]成功!");
					//推送成功记录数量+1
					isSuccess++;
					//保存推送操作记录
				    this.saveTaskRecord(batchBean, isSubmit, dbService);
				} else {
					log.error("批次["+batchId+"]版本["+batchBean.getBatch_ver()+"]推送到机构["+targetorgCode+"]失败!");
					//保存推送操作记录
				    this.saveTaskRecord(batchBean, isSubmit, dbService);
					break;
				}
			}
		}//for END 批次所有版本
		
		
		if(isSuccess==batchSrcList.size()) {
			//表示版本全部推送到目标机构成功
			return true;
		} else {
			//部分成功或者全部失败
			return false;
		}
		
	}

}
