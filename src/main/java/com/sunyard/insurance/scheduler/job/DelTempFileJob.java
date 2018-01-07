package com.sunyard.insurance.scheduler.job;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.sunyard.insurance.common.CMConstant;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.util.DateUtil;

public class DelTempFileJob implements Job {

	private static final Logger log = Logger.getLogger(DelTempFileJob.class);
	
	public void delScanTemFile() {
		log.info("=========删除上传临时文件夹========");
		String scanTemp = GlobalVar.tempDir + File.separator + "scan";
		File f = new File(scanTemp);

		if (f.isDirectory()) {
			File[] files = f.listFiles();
			log.info("临时文件夹总个数为:"+files.length);
			for (int i = 0; i < files.length; i++) {
				File tem = files[i];
				if (!DateUtil.getDateStrCompact().equals(tem.getName())) {
					// 不是当天的文件
					log.info("删除上传临时文件夹：" + tem.getAbsolutePath());
					try {
						FileUtils.forceDelete(tem);
					} catch (IOException e) {
						log.error("删除上传临时文件夹失败!：" + tem.getAbsolutePath(),e);
					}
				}
			}
		}

		/* 太平FTP功能外网TRM缓存目录过期批次清理个性化 */
		// 删除太平外网TRM缓存目录内的缓存文件
		// 1.首先判断FTP开关是否开启
		// 2.开启则寻找过期的天数目录列表，予以删除
		delExtraCacheLifeCircleEndFolder();
	}
	
	/**
	 * 
	 *@Description 
	 *删除CMCE查询缓存在本地的文件
	 *缓存在本地n天后被删除
	 */
	public void delCMQueryCacheFile(){
		log.info("===清理CM|CE查询缓存在本地的影像文件===");
		String tempQueryCache = "";
		if("FileNetCE".equals(GlobalVar.CMType)){
			tempQueryCache = GlobalVar.tempDir+File.separator+"CM_CACHE";
		}else if("IBMCM".equals(GlobalVar.CMType)){
			tempQueryCache = CMConstant.queryCacheFolder;
		}else if("FileNetForFAF".equals(GlobalVar.CMType)){
			tempQueryCache = GlobalVar.tempDir+File.separator+"CM_CACHE";
		}
		log.info("CM|CE查询缓存路径为："+tempQueryCache);
		File file = new File(tempQueryCache);
		if(file.isDirectory()){
			File[] files = file.listFiles();
			log.info("CM|CE查询缓存在本地的文件夹总个数为:"+files.length);
			//SAVE_CM_QUERY_CACHE_DAYS天之前时间
			Integer tempDate =Integer.parseInt(DateUtil.getDateBefAft(GlobalVar.CMCatchDay, "yyyyMMdd"));
			
			for(int i = 0; i < files.length; i++) {
				File temp = files[i];
				if(Integer.parseInt(temp.getName())<=tempDate) {
					log.info("删除CM|CE查询缓存在本地的文件夹：" + temp.getAbsolutePath());
					try {
						FileUtils.forceDelete(temp);
					} catch (IOException e) {
						log.error("删除CM|CE查询缓存在本地的文件夹["+temp.getAbsolutePath()+"]失败!",e);
					}
				}
			}
		}
	}
	
	public void delExtraCacheLifeCircleEndFolder() {
		try {
			if(GlobalVar.isFTPOpen == 1) {
				File cacheFolder = new File(GlobalVar.svrInfoBean.getRootPath());// 增设的缓存根目录
				List<String> list = new ArrayList<String>();
				if(cacheFolder.exists()) {
					String[] busiTypeFolders = cacheFolder.list();// 所有业务类型目录
					for (int i = 0; i < busiTypeFolders.length; i++) {
						File busiTypeFolder = new File(cacheFolder.getAbsolutePath() + File.separator + busiTypeFolders[i]);
						String busiTypeFolderPath = busiTypeFolder.getAbsolutePath();
						if(busiTypeFolder.isDirectory()) {
							String[] yearFolders = busiTypeFolder.list();// 获取业务类型下的所有年份目录
							if(yearFolders.length != 0) {
								for (int j = 0; j < yearFolders.length; j++) {
									File yearFolder = new File(busiTypeFolderPath + File.separator + yearFolders[j]);
									String yearName = yearFolder.getName();
									if(yearFolder.isDirectory()) {
										String[] monthFolders = yearFolder.list();// 获取年份下的所有月份目录
										if(monthFolders.length != 0) {
											for (int k = 0; k < monthFolders.length; k++) {
												File monthFolder = new File(yearFolder + File.separator + monthFolders[k]);
												String monthName = monthFolder.getName();
												String[] dayFolders = monthFolder.list();
												if(dayFolders.length != 0) {
													for (int l = 0; l < dayFolders.length; l++) {
														String dayName = dayFolders[l];
														String consistDate = yearName + "/" + monthName + "/" + dayName;
														Date date = new Date();//获取当前日期
														SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
														Date folerDate = formatter.parse(consistDate);
														int days = (int) Math.abs((date.getTime() - folerDate.getTime())  / (24 * 60 * 60 * 1000));// 计算文件夹的日期距离当前日期的天数
														if(days > GlobalVar.extraCacheDays) {
															list.add(cacheFolder.getAbsolutePath() + File.separator + busiTypeFolder.getName() + File.separator + consistDate);
														}
														
													}
												} else {// 天目录为空目录，加入要删除目录的列表中
													String consistPath = yearName + "/" + monthName;
													list.add(cacheFolder.getAbsolutePath() + File.separator + busiTypeFolder.getName() + File.separator + consistPath);
												}
											}
										} else {// 月份目录为空目录，加入要删除目录的列表中
											String consistPath = yearName;
											list.add(cacheFolder.getAbsolutePath() + File.separator + busiTypeFolder.getName() + File.separator + consistPath);
										}
									}
								}
							} else {// 年份目录为空目录，加入要删除目录的列表中
								list.add(cacheFolder.getAbsolutePath() + File.separator + busiTypeFolder.getName());
							}
						}
					}
				}
				/* 遍历待删除目录列表，予以删除 */
				for (int i = 0; i < list.size(); i++) {
					File file = new File(list.get(i));
					log.info("目录[" + list.get(i) + "]缓存期到，删除");
					FileUtils.forceDelete(file);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void execute(JobExecutionContext cont) throws JobExecutionException {
		this.delScanTemFile();
		this.delCMQueryCacheFile();
	}

	public static void main(String[] args) {
		System.out.println(DateUtil.getDateBefAft(2, "yyyyMMdd"));
		//CMConstant.queryCacheFolder = "C:/TRM/cmCatch/cacheFile";
		//new DelTempFileJob().delCMQueryCacheFile();
	}

}
