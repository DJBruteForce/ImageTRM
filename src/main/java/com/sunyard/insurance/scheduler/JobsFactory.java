package com.sunyard.insurance.scheduler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.entity.TRM_TASK_CONFIG;
import com.sunyard.insurance.scheduler.job.AutoPushJob;
import com.sunyard.insurance.scheduler.job.ImageCleanJob;
import com.sunyard.insurance.scheduler.job.ImageMigrateJob;
import com.sunyard.insurance.scheduler.job.ImagePushJob;
import com.sunyard.insurance.webServiceImpl.DBBusiServiceImpl;

public class JobsFactory {

	private static final Logger log = Logger.getLogger(JobsFactory.class);
	private static Scheduler scheduler = null;
	public static ExecutorService autoPushThreadPool = Executors.newSingleThreadExecutor();

	public static boolean initScheduler() {

		List<TRM_TASK_CONFIG> taskList = new DBBusiServiceImpl()
				.findAllTaskConfByOrgCode(GlobalVar.svrInfoBean.getOrgCode());
		
		if(null==taskList) {
			return false;
		}
		
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();

			for (int i = 0; i < taskList.size(); i++) {
				TRM_TASK_CONFIG taskConfig = taskList.get(i);
				String taskId = "" + taskConfig.getTask_id();
				String taskType = taskConfig.getTask_type();
				String startTime = taskConfig.getStart_time();
				String endTime = taskConfig.getEnd_time();
				String svr_id = taskConfig.getSvr_id();
				//排除不是自己的排程
				if(!svr_id.equals(GlobalVar.serverId)) {
					continue;
				}
				
				//保存排程到全局变量中
				GlobalVar.taskList.add(taskConfig);
				
				JobDetail jobDetail = null;
				if ("1".equals(taskType)) {
					// 迁移排程
					jobDetail = new JobDetail(taskId, Scheduler.DEFAULT_GROUP,ImageMigrateJob.class);
					log.info("初始化[迁移]排程，TASK_ID为[" + taskId + "]");
					CronTrigger jobTrigger = new CronTrigger(taskId,Scheduler.DEFAULT_GROUP);
					String[] tStr = startTime.split(":");
					//每天的11点整执行
					// "0 00 11 ? * *";
					String cep = "0 " + tStr[1] + " " + tStr[0] + " ? * *";
					jobTrigger.setCronExpression(cep);
					jobDetail.getJobDataMap().put("TASK_ID", taskId);// 传递任务ID
					jobDetail.getJobDataMap().put("END_TIME", endTime);// 传递任务结束时间
					scheduler.scheduleJob(jobDetail, jobTrigger);
				} else if ("2".equals(taskType) && "2".equals(GlobalVar.pushModel)) {
					// 推送排程
					jobDetail = new JobDetail(taskId, Scheduler.DEFAULT_GROUP,ImagePushJob.class);
					log.info("初始化[推送]排程，TASK_ID为[" + taskId + "]");
					CronTrigger jobTrigger = new CronTrigger(taskId,Scheduler.DEFAULT_GROUP);
					String[] tStr = startTime.split(":");
					//每天的11点整执行
					// "0 00 11 ? * *";
					String cep = "0 " + tStr[1] + " " + tStr[0] + " ? * *";
					jobTrigger.setCronExpression(cep);
					jobDetail.getJobDataMap().put("TASK_ID", taskId);// 传递任务ID
					jobDetail.getJobDataMap().put("END_TIME", endTime);// 传递任务结束时间
					scheduler.scheduleJob(jobDetail, jobTrigger);
				} else if ("3".equals(taskType)) {
					// 清理排程
					jobDetail = new JobDetail(taskId, Scheduler.DEFAULT_GROUP,ImageCleanJob.class);
					log.info("初始化[清理]排程，TASK_ID为[" + taskId + "]");
					CronTrigger jobTrigger = new CronTrigger(taskId,Scheduler.DEFAULT_GROUP);
					String[] tStr = startTime.split(":");
					//每天的11点整执行
					// "0 00 11 ? * *";
					String cep = "0 " + tStr[1] + " " + tStr[0] + " ? * *";
					jobTrigger.setCronExpression(cep);
					jobDetail.getJobDataMap().put("TASK_ID", taskId);// 传递任务ID
					jobDetail.getJobDataMap().put("END_TIME", endTime);// 传递任务结束时间
					scheduler.scheduleJob(jobDetail, jobTrigger);
				}
				
			}

			scheduler.start();
		} catch (Exception e) {
			log.info("缓存节点初始化排程失败!",e);
			log.error("缓存节点初始化排程失败!",e);
			return false;
		}
		return true;
	}
	
	public static void shutdownScheduler() {
		//销毁排程
		if(null != scheduler) {
			try {
				log.info("销毁缓存节点排程!");
				scheduler.shutdown(true);
			} catch (SchedulerException e) {
				
				log.error("销毁缓存节点排程异常!",e);
			}
		}
	}

	/**
	 *@Description
	 * 
	 *@param args
	 */
	public static void main(String[] args) {
		JobsFactory.initScheduler();
	}
}
