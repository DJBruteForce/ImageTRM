package com.sunyard.insurance.util;


import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.json.JSONObject;
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
import com.sunyard.insurance.entity.TRM_TASK_PLOT;
import com.sunyard.insurance.webService.DBBusiService;
import com.sunyard.insurance.webServiceImpl.DBBusiServiceImpl;

public class BtachSyncThread implements Runnable {
	
	private String appCode;
	private String batchId;
	private static final Logger loger = Logger.getLogger(BtachSyncThread.class);
	private DBBusiService dbService = new DBBusiServiceImpl();
	private BatchManager batchManager = new BatchManager();
	
	public BtachSyncThread(String appCode,String batchId) {
		this.appCode = appCode;
		this.batchId = batchId;
	}
	
	public void run() {	
		/* 太平个性化，对于外网，所有推送，迁移排程均交给内网TRM做，外网一律跳过 */
		if(GlobalVar.isFTPOpen == 1) {// 如果FTP功能开启，则表示为外网，TRM跳过实施推送
			loger.info("当前所处网络为外网，开启了FTP功能，跳过推送。");
			return;
		}
		//是否有排程配置
		if(null==GlobalVar.taskList || GlobalVar.taskList.size()==0) {
			loger.debug("当前缓存节点没有配置任何排程信息，无法进行批次实时推送");
			return;
		}
		String pushTaskId = "";
		//是否有推送排程
		for (int i = 0; i < GlobalVar.taskList.size(); i++) {
			if(GlobalVar.taskList.get(i).getTask_type().equals("2")) {
				pushTaskId = ""+GlobalVar.taskList.get(i).getTask_id();
				break;
			}
		}
		//获取推送排程策略
		List<TRM_TASK_PLOT> taskPlots = null;
		if(!"".equals(pushTaskId)) {
			taskPlots = dbService.findAllPlotByTaskId(Integer.parseInt(pushTaskId));
		}
		if(null==taskPlots) {
			loger.error("没有获取到推送排程[TAKS_ID="+pushTaskId+"]的策略配置信息!");
			return;
		}
		TRM_TASK_PLOT taskPlot = null;
		//获取是否存在当前业务类型的策略
		for (int i = 0; i < taskPlots.size(); i++) {
			if(taskPlots.get(i).getId().getApp_code().equals(appCode)) {
				taskPlot = taskPlots.get(i);
				break;
			}
		}
		//当前业务类型是否有配置
		if(taskPlot==null) {
			loger.error("没有获取到推送排程[TAKS_ID="+pushTaskId+"]中含有业务类型["+appCode+"]策略信息!");	
			return;
		}
		
		String selfOrgCode = GlobalVar.svrInfoBean.getOrgCode();//自己所在机构号
		Set<String> orgCodeSet = new HashSet<String>();//用来存放所有的推送机构
		
		List<B_BATCHS_SRC> batchSrcList = dbService.findBatchSrcS(batchId, selfOrgCode);//根据本机构号查询批次所有版本
		if(null==batchSrcList || batchSrcList.size()==0) {
			loger.info("实时推送查询到业务类型["+appCode+"],批次号["+batchId+"]没有获取到SRC表记录!");
			return;
		}
		
		loger.info("实时推送查询到业务类型["+appCode+"],批次号["+batchId+"]待推送的版本数目:["+batchSrcList.size()+"]个");
		
		if("1".equals(taskPlot.getPlot_type())) {
			orgCodeSet.add(taskPlot.getTarget_org());
		} else if("2".equals(taskPlot.getPlot_type())) {
			//表达式生效,根据BIZ_ORG与表达式得到推送机构列表
			B_BATCHS_INFO_ID id = new B_BATCHS_INFO_ID(batchId,1);
			B_BATCHS_INFO binfo = dbService.findByBatchInfo(id);
			orgCodeSet = this.getTargetOrgs(batchId,binfo.getBizOrg(),taskPlot.getTask_plot());
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
				loger.info("批次["+batchId+"]推送到机构["+targetorgCode+"]所有版本全部成功!");
			} else {
				loger.info("批次["+batchId+"]推送到机构["+targetorgCode+"]所有版本失败(部分版本推送失败/全部版本推送失败)!");
			}
			
		}
		
		//这里判断是否全部推送成功,并且记录数据库状态
		if((isSuccessOrg==orgCodeSet.size()) && (orgCodeSet.size()>0)) {
			loger.info("批次["+batchId+"]推送到所有机构全部成功!");
			//3-全部成功
			dbService.UpdateBatchSRCPushStatus(batchId, selfOrgCode, batchSrcList.size(), 3);
		} else if(isSuccessOrg==0) {
			loger.info("批次["+batchId+"]推送到所有机构失败(全部机构推送失败)!");
			//1-全部机构推送失败
			dbService.UpdateBatchSRCPushStatus(batchId, selfOrgCode, batchSrcList.size(), 1);
		} else {
			loger.info("批次["+batchId+"]推送到所有机构部分推送失败(部分机构推送失败)!");
			//2-部分机构推送成功
			dbService.UpdateBatchSRCPushStatus(batchId, selfOrgCode, batchSrcList.size(), 2);
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
							loger.error("批次["+batchId+"]BIZ_ORG字段为空,无法做推送表达式$SELF解析!");
						} else {
							orgCodeSet.add(bizOrg);
						}
					} else {
						orgCodeSet.add(tOrgStr);
					}
					
				}
			}
		} catch (Exception e) {
			loger.error("解析自定义目标机构号规则出错 ",e);
		}
		
		return orgCodeSet;
	}
	
	//推送一个批次所有版本到目标机构
	public boolean submitBatchToOrg(String targetorgCode,String batchId,List<B_BATCHS_SRC> batchSrcList) {
		
		int isSuccess = 0;//成功推送版本数量
		
		//根据目标机构号获取ServerInfoBean
		ServerInfoBean serverInfo = dbService.getSvrInfoByOrgCode(targetorgCode);
		if(null==serverInfo){
			loger.error("没有目标机构[" + targetorgCode + "]的配置信息!");
			return false;
		}else if (serverInfo.getOrgCode().equals(GlobalVar.svrInfoBean.getOrgCode())) {
			loger.error("推送目标机构[" + targetorgCode+ "]非法，向自己推送，默认返回成功!");
			return true;
		}
		
		loger.info("向目标机构[" + targetorgCode+ "]推送批次["+batchId+"]所有版本影像文件!");
		
		
		//循环版本，遍历每个批次的所有版本，按版本从小到大依次推送
		for(int m=0; m<batchSrcList.size(); m++) {
			B_BATCHS_SRC srcBean = batchSrcList.get(m);
			//判断目标机构是否存在该批次版本
			B_BATCHS_SRC tarSrc = dbService.findBatchSrc(batchId, targetorgCode, srcBean.getId().getBatchVer());
			if(null != tarSrc) {
				isSuccess++;
			} else {
				loger.info("开始推送批次["+batchId+"]版本号["+srcBean.getId().getBatchVer()+"]影像资料!");
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
						loger.info("批次推送[" + batchId + "]没有BATCHS_INFO信息!");
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
					loger.info("批次[" + batchId + "]请求CM查询返回结果:" + batchDir);
					sydPath = batchDir + File.separator + batchId + "_" + srcBean.getId().getBatchVer()+ ".syd";
				}
				//========================================================================================
				try {
					batchBean = ClassUtil.getSydUtilClass().getBatchBean(sydPath);
				} catch (Exception e) {
					loger.error("解析批次["+batchId+"]版本["+srcBean.getId().getBatchVer()+"]SYD文件["+sydPath+"]失败!",e);
					break;
				}
				
				boolean isSubmit = batchManager.imageMigrateByVer(serverInfo.getOrgVip(),serverInfo.getSocket_port(), batchBean);
				if(isSubmit) {
					loger.info("批次["+batchId+"]版本["+batchBean.getBatch_ver()+"]推送到机构["+targetorgCode+"]成功!");
					//推送成功记录数量+1
					isSuccess++;
				} else {
					loger.error("批次["+batchId+"]版本["+batchBean.getBatch_ver()+"]推送到机构["+targetorgCode+"]失败!");
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
