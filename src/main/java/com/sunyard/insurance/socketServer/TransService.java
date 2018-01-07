package com.sunyard.insurance.socketServer;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.batch.busi.BatchManager;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.entity.TRM_INFORM_RECORD;
import com.sunyard.insurance.util.BtachSyncThread;
import com.sunyard.insurance.util.ClassUtil;
import com.sunyard.insurance.util.CommonFun;
import com.sunyard.insurance.util.ZipUtil;

public class TransService {

	private static final Logger loger = Logger.getLogger(TransService.class);
	private static BatchManager batchManager = new BatchManager();

	/**
	 * socket传输校验用户
	 * 
	 * @return
	 */
	public static String checkUser(String uname, String password) {
		loger.info("[客户端]传输用户校验[用户名]" + uname);
		return "0";// 校验失败就返回-1
	}

	/**
	 * SunTXM001批次开始提交
	 * 
	 * @return
	 */
	public static String startBatchFile(String batchID, String batchTemPath) {
		loger.info("[客户端]批次batchID:" + batchID + "开始传输，指定临时存储目录为"
				+ batchTemPath);
		File batchFile = new File(batchTemPath);
		boolean b = true;
		if (!batchFile.exists()) {
			b = batchFile.mkdirs();
		}

		if (b) {
			return "0003";
		} else {
			return "-1";
		}
	}

	/**
	 * SunTXM002文件校验MD5成功2失败-10
	 * 
	 * @return
	 */
	public static String checkBatchFile(String str, String tempDir) {

		String[] arr = str.split(",");
		String batchID = arr[0];
		String fileName = arr[1];
		String MD5Code = arr[2];

		File uploadFile = new File(tempDir + File.separator + fileName);

		if (!uploadFile.exists()) {
			loger.info("批次batchID:" + batchID + "文件MD5校验，文件名[" + fileName
					+ "]存储路径" + tempDir + "，文件不存在，警告！");
			return "-10";
		}
		long startTime = System.currentTimeMillis();
		String md5Code = CommonFun.getFileMD5(uploadFile);
		if (md5Code.equalsIgnoreCase(MD5Code)) {
			loger.info("批次batchID:" + batchID + "文件MD5校验，文件名[" + fileName
					+ "]存储路径" + tempDir + "，MD5码[" + MD5Code + "]校验成功！耗时:"+(System.currentTimeMillis()-startTime));
			return "2";
		} else {
			loger.info("批次batchID:" + batchID + "文件MD5校验，文件名[" + fileName
					+ "]存储路径" + tempDir + "，MD5码[" + MD5Code + "]校验不符合，警告！耗时:"+(System.currentTimeMillis()-startTime));
			return "-10";
		}
	}

	/**
	 * 
	 *@Description
	 * 
	 *@param str
	 *            socket传递的字符串
	 *@param batchTemPath
	 *            批次存储临时文件夹
	 *@param formType
	 *            上传来源是客户端还是SunTRM
	 *@return
	 */
	public static String endBatchFile(String str, String batchTemPath,
			String fromType) {

		String[] arr = str.split(",");
		String batchId = arr[0];
		String iCount = arr[1];
		String resultStr = null;
		
		String sydFilePath = "";
		BatchBean batchBean = null;
		
		//先判断下上传上来的是不是ZIP文件，如果是先进行解压缩
		File f = new File(batchTemPath);
		if((f.listFiles().length==1) && (f.listFiles()[0].getName().toLowerCase().endsWith(".zip"))) {
			try {
				ZipUtil.unZip(f.listFiles()[0].getAbsolutePath(), batchTemPath);
			} catch (IOException e) {
				loger.error("SOCKET文件上传["+f.listFiles()[0].getAbsolutePath()+"]为ZIP类型文件,解压异常!",e);
			}
			loger.info("SOCKET文件上传["+f.listFiles()[0].getAbsolutePath()+"]为ZIP类型文件,解压完成!");
		}
		
		try {
			String sydFileName = ClassUtil.getSydUtilClass().getSydFileFullPath(batchTemPath);
			if("".equals(sydFileName)) {
				loger.error("批次临时存储目录["+batchTemPath+"]中未找到SYD文件!");
				return "-7";
			}
			sydFilePath = batchTemPath + File.separator + sydFileName;
			batchBean = ClassUtil.getSydUtilClass().getBatchBean(sydFilePath);
		} catch (Exception e) {
			loger.error("解析批次SYD文件[" + sydFilePath + "]异常!",e);
			return "-7";
		}
		
		// 这里加上一个批次文件数量校验
		if (batchBean.getBatchFileList().size() < Integer.parseInt(iCount)) {
			loger.info("批次[" + batchTemPath + "]批次数量校验不一致！服务端接收数量:"+batchBean.getBatchFileList().size()+" Client上传数量:" + iCount);
			return "-1";
		} else {
			loger.info("批次[" + batchTemPath + "]批次数量校验成功！Client上传数量:" + iCount);
		}

		// 存储批次到内容管理平台
		if (GlobalVar.svrInfoBean.getSaveInCM().equals("1")) {
			// 调用内容管理上传修改功能
			loger.info("批次[" + batchTemPath + "]开始提交到内容管理产品....");
			boolean isSubmit = batchManager.batchSaveToCM(batchBean);
			if (isSubmit) {// 提交成功返回值为true
				resultStr = "2";
			} else {
				resultStr = "-1";
			}
		} else {
			/* 太平个性化，增设额外自定义的缓存目录 */
//			File extraCacheFolder = new File(GlobalVar.extraCacheFolder);
			if(GlobalVar.isFTPOpen == 1) {// FTP开关开启，表示为外网TRM
				// 1.进行FTP上传
				// 2.上传FTP成功后，上传到本地增设的缓存目录
				loger.info("批次[" + batchTemPath + "]开始进行本地缓存....");
				boolean isSubmit = batchManager.batchSaveToExtraCache(batchBean);
				if (isSubmit) {
					resultStr = "2";
				} else {
					resultStr = "-1";
				}
			} else {// FTP开关关闭，表示为内网TRM
				// 1.直接将批次上传到SunUSM配置的缓存目录中存储
				// 调用本地缓存批次功能
				loger.info("批次[" + batchTemPath + "]开始进行本地缓存....");
				boolean isSubmit = batchManager.batchSaveToLocal(batchBean);
				if (isSubmit) {
					resultStr = "2";
				} else {
					resultStr = "-1";
				}
			}
		}

		// 上传成功就删除临时文件夹
		if (resultStr.equals("2")) {
			loger.info("批次batchID[" + batchId + "]存储成功！");
			try {
				FileUtils.deleteDirectory(new File(batchTemPath));
				loger.info("批次BATCH_ID[" + batchId + "]删除批次临时存储目录["+ batchTemPath + "]成功!");
			} catch (IOException e) {
				loger.error("批次BATCH_ID[" + batchId + "]删除批次临时存储目录["+ batchTemPath + "]失败了!");
			}

			// 非SunTRM上传影像,切入影像到达通知事物
			if (!"SunTRM003".equals(fromType)) {
				if (null != GlobalVar.informAppMap.get(batchBean.getAppCode())) {
					loger.debug("发起影像到达通知BATCH_ID:"+batchBean.getBatch_id()+"版本:"+batchBean.getBatch_ver());
					TRM_INFORM_RECORD record = new TRM_INFORM_RECORD();
					record.setBatch_id(batchBean.getBatch_id());
					record.setBusi_no(batchBean.getBusi_no());
					record.setBatch_ver(Integer.parseInt(batchBean.getBatch_ver()));
					record.setInter_ver(Integer.parseInt(batchBean.getInter_ver()));
					record.setOrg_code(GlobalVar.svrInfoBean.getOrgCode());
					record.setApp_code(batchBean.getAppCode());
					record.setInform_time(new Date());
					record.setSvr_id(GlobalVar.serverId);
					record.setStatus("0");
					record.setBusi_date(batchBean.getBusi_date());
					boolean isInform = batchManager.addInformRecord(record);
					if (isInform) {
						loger.info("批次BATCH_ID[" + batchId + "]版本["+ batchBean.getBatch_ver() + "]到达通知成功！");
					} else {
						loger.info("批次BATCH_ID[" + batchId + "]版本["+ batchBean.getBatch_ver() + "]到达通知失败！");
						loger.error("批次BATCH_ID[" + batchId + "]版本["+ batchBean.getBatch_ver() + "]到达通知失败！");
					}
				}
			}
			
			//开启实时推送活动
			if("1".equals(GlobalVar.pushModel)) {
				if(GlobalVar.isFTPOpen != 1) {
					new Thread(new BtachSyncThread(batchBean.getAppCode(),batchBean.getBatch_id())).start();
				} else {
					loger.info("当前所处环境为外网，跳过实时推送。");
				}
			}
			
		} else {
			loger.info("批次batchID'[" + batchId + "]存储失败！");
		}
		return resultStr;
	}
	
	
	/**
	 * 文件数量校验
	 * 
	 * @param batchId
	 * @param iCount
	 * @param batchTemPath
	 * @return
	 */
	public static boolean batchCheckNum(String batchId, String iCount,
			String batchTemPath) {
		File batchDir = new File(batchTemPath);
		String[] fileList = batchDir.list();
		loger.info("文件夹["+batchTemPath+"]拥有文件个数:"+fileList.length);
		// 文件数量小于校验数量
		if (fileList.length < Integer.parseInt(iCount)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
