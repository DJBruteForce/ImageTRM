package com.sunyard.insurance.batch.busi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.batch.bean.BatchFileBean;
import com.sunyard.insurance.cmapi.service.CMProessService;
import com.sunyard.insurance.cmapi.service.impl.CMProessServiceImpl;
import com.sunyard.insurance.cmapi.service.impl.FileNetCEProessServiceImpl;
import com.sunyard.insurance.cmapi.service.impl.FileNetForFAFProcessServiceImpl;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.entity.B_BATCHS_SRC;
import com.sunyard.insurance.entity.B_BATCHS_SRC_ID;
import com.sunyard.insurance.entity.TRM_INFORM_RECORD;
import com.sunyard.insurance.socketTransClient.SocketClientApi;
import com.sunyard.insurance.socketTransClient.bean.ClientBatchBean;
import com.sunyard.insurance.socketTransClient.bean.ClientBatchFileBean;
import com.sunyard.insurance.util.CommonFun;
import com.sunyard.insurance.util.DateUtil;
import com.sunyard.insurance.util.NumberUtil;
import com.sunyard.insurance.webService.DBBusiService;
import com.sunyard.insurance.webServiceImpl.DBBusiServiceImpl;
import com.sunyard.insurance.webServiceImpl.ImageInformServiceImpl;

/**
 * 
 * 项目名称:SunTRM 类名称:BatchManager 类描述:批次管理类，提供本地缓存提交CM、缓存批次删除等方法 创建人:wuzelin
 * 创建时间:2012-7-30下午03:39:50 修改人:wuzelin 修改时间:2012-7-30下午03:39:50 修改备注:
 * 
 * @version V1.0
 */
public class CopyOfBatchManager15 {
	
	private static final Logger loger = Logger.getLogger(CopyOfBatchManager15.class);
	private DBBusiService dbService = new DBBusiServiceImpl();
	private ImageInformServiceImpl imageBusiService = new ImageInformServiceImpl();

	/**
	 *@Description 批次本地缓存
	 *@param batchBean
	 *@return boolean
	 */
	public boolean batchSaveToLocal(BatchBean batchBean) {
		String batchId = batchBean.getBatch_id();
		String batchVer = batchBean.getBatch_ver();
		String interVer = batchBean.getInter_ver();
		
		if(Integer.parseInt(batchVer)!=1) {
			//判断是否版本不连续上传
			B_BATCHS_SRC tempSrc_1 = dbService.findBatchSrc(batchId, GlobalVar.svrInfoBean.getOrgCode(), (Integer.parseInt(batchVer)-1));
			if(null == tempSrc_1) {
				loger.info("批次号["+batchId+"]版本号["+(Integer.parseInt(batchVer)-1)+"]版本不存在，忽略本次上传!");
				return false;
			}
		}
		
		B_BATCHS_SRC tempSrc = dbService.findBatchSrc(batchId, GlobalVar.svrInfoBean.getOrgCode(), Integer.parseInt(batchVer));
		if(null != tempSrc) {
			loger.info("批次号["+batchId+"]版本号["+batchVer+"]的影像已存在，忽略本次上传!");
			return false;
		}
		
		// 批次最终存储地址
		String batchSavePath = "";
		String saveType = GlobalVar.svrInfoBean.getSaveType();
		String DateType = GlobalVar.svrInfoBean.getDateType();
		
		if ("2".equals(saveType) || "1".equals(batchVer)) {
			// 不同版本不同的存储目录
			StringBuffer savePath = new StringBuffer();
			savePath.append(GlobalVar.svrInfoBean.getRootPath()+ File.separator);
			//加上APP_CODE
			savePath.append(batchBean.getAppCode() + File.separator);
			//文件夹样式
			if ("1".equals(DateType)) {
				savePath.append(DateUtil.getYearStr() + File.separator);
				savePath.append(DateUtil.getMonthStr() + File.separator);
				savePath.append(DateUtil.getDayStr() + File.separator);
			} else {
				savePath.append(DateUtil.getYearStr() + File.separator);
				savePath.append(DateUtil.getMonthStr() + DateUtil.getDayStr()
						+ File.separator);
			}
			for (int i = 0; i < GlobalVar.svrInfoBean.getHashRand(); i++) {
				savePath.append(NumberUtil.getRandomNum(99) + File.separator);
			}
			savePath.append(batchId + "_" + batchVer);
			batchSavePath = savePath.toString();
		} else {
			// 多个版本同一个存储目录,获取原有存储目录
			Integer lastVer = Integer.parseInt(batchVer) - 1;// 上一版本
			B_BATCHS_SRC batchSrc = dbService.findBatchSrc(batchId,
					GlobalVar.svrInfoBean.getOrgCode(), lastVer);
			if (null == batchSrc) {
				loger.info("没有找到批次[" + batchId + "]机构号["
						+ GlobalVar.svrInfoBean.getOrgCode() + "]的历史存储地址!");
				return false;
			}
			batchSavePath = batchSrc.getSrcUrl();
			if ("".equals(batchSavePath)) {
				loger.info("找到批次[" + batchId + "]机构号["
						+ GlobalVar.svrInfoBean.getOrgCode() + "]的历史存储地址为空!");
				return false;
			}
		}
		loger.info("批次[" + batchId + "]版本[" + batchVer + "]设定存储目录为[" + batchSavePath + "]");
		
		//判断存储目录是否存在同样版本的SYD文件，存在则删除	
		File sydF = new File(batchSavePath+File.separator+batchId+"_"+batchVer+".syd");
		if(sydF.exists()) {
			loger.info("批次[" + batchId + "]目的地存储地址存在版本["+batchVer+"]syd文件,执行删除!");
			try {
				FileUtils.forceDelete(sydF);
			} catch (IOException e) {
				loger.error("批次[" + batchId + "]目的地存储地址存在版本["+batchVer+"]syd文件,删除失败!",e);
			}
		}
		
		// 拷贝文件到存储目录
		List<BatchFileBean> batchFiles = batchBean.getBatchFileList();
		for (int i = 0; i < batchFiles.size(); i++) {
			File srcFile = new File(batchFiles.get(i).getFileFullPath());
			File destFile = new File(batchSavePath+File.separator+srcFile.getName());
			try {
				//存储目标地址不存在该文件
				if(!destFile.exists()) {
					FileUtils.copyFile(srcFile, destFile);
				}
			} catch (IOException e) {
				loger.info("批次[" + batchId + "]版本[" + batchVer + "]复制文件异常["
						+ batchFiles.get(i).getFileFullPath() + "]",e);
				return false;
			}
		}

		loger.info("批次[" + batchId + "]版本[" + batchVer + "]从临时目录拷贝所有文件成功!");

		// 批次记录到数据库
		B_BATCHS_SRC bSrc = new B_BATCHS_SRC();
		B_BATCHS_SRC_ID bSrcId = new B_BATCHS_SRC_ID();
		bSrcId.setBatchId(batchId);
		bSrcId.setBatchVer(Integer.parseInt(batchVer));
		bSrcId.setOrgCode(GlobalVar.svrInfoBean.getOrgCode());
		bSrc.setId(bSrcId);
		bSrc.setInterVer(Integer.parseInt(interVer));
		bSrc.setSrcUrl(batchSavePath);
		bSrc.setAppCode(batchBean.getAppCode());
		bSrc.setIsMigrate("0");
		bSrc.setIsPush("0");
		bSrc.setStatus(1);
		bSrc.setResourceType(1);
		bSrc.setUpdateDate(new Date());
		bSrc.setUpdateDateStr(DateUtil.getDateTimeStr());
		
		loger.info("批次[" + batchId + "]版本[" + batchVer + "]请求记录数据库!");
		boolean isSaveToDB = dbService.saveOrUpdateBatchs(batchBean, bSrc);
		
		if (!isSaveToDB) {
			loger.info("批次[" + batchId + "]版本[" + batchVer + "]记录数据库失败了!");
			return false;
		} else {
			loger.info("批次[" + batchId + "]版本[" + batchVer + "]记录数据库成功!");
			return true;
		}
	}
	
	public boolean batchSaveToExtraCache (BatchBean batchBean) {
		String batchId = batchBean.getBatch_id();
		String batchVer = batchBean.getBatch_ver();
		String interVer = batchBean.getInter_ver();
		
		if(Integer.parseInt(batchVer)!=1) {
			//判断是否版本不连续上传
			B_BATCHS_SRC tempSrc_1 = dbService.findBatchSrc(batchId, GlobalVar.svrInfoBean.getOrgCode(), (Integer.parseInt(batchVer)-1));
			if(null == tempSrc_1) {
				loger.info("批次号["+batchId+"]版本号["+(Integer.parseInt(batchVer)-1)+"]版本不存在，忽略本次上传!");
				return false;
			}
		}
		
		B_BATCHS_SRC tempSrc = dbService.findBatchSrc(batchId, GlobalVar.svrInfoBean.getOrgCode(), Integer.parseInt(batchVer));
		if(null != tempSrc) {
			loger.info("批次号["+batchId+"]版本号["+batchVer+"]的影像已存在，忽略本次上传!");
			return false;
		}
		// 批次最终存储地址
		String batchSavePath = "";
		String saveType = GlobalVar.svrInfoBean.getSaveType();
		String DateType = GlobalVar.svrInfoBean.getDateType();
		
		if ("2".equals(saveType) || "1".equals(batchVer)) {
			// 不同版本不同的存储目录
			StringBuffer savePath = new StringBuffer();
			savePath.append(GlobalVar.svrInfoBean.getRootPath() + File.separator);
			//加上APP_CODE
			savePath.append(batchBean.getAppCode() + File.separator);
			//文件夹样式
			if ("1".equals(DateType)) {
				savePath.append(DateUtil.getYearStr() + File.separator);
				savePath.append(DateUtil.getMonthStr() + File.separator);
				savePath.append(DateUtil.getDayStr() + File.separator);
			} else {
				savePath.append(DateUtil.getYearStr() + File.separator);
				savePath.append(DateUtil.getMonthStr() + DateUtil.getDayStr()
						+ File.separator);
			}
			for (int i = 0; i < GlobalVar.svrInfoBean.getHashRand(); i++) {
				savePath.append(NumberUtil.getRandomNum(99) + File.separator);
			}
			savePath.append(batchId + "_" + batchVer);
			batchSavePath = savePath.toString();
		} else {
			// 多个版本同一个存储目录,获取原有存储目录
			Integer lastVer = Integer.parseInt(batchVer) - 1;// 上一版本
			B_BATCHS_SRC batchSrc = dbService.findBatchSrc(batchId,
					GlobalVar.svrInfoBean.getOrgCode(), lastVer);
			if (null == batchSrc) {
				loger.info("没有找到批次[" + batchId + "]机构号["
						+ GlobalVar.svrInfoBean.getOrgCode() + "]的历史存储地址!");
				return false;
			}
			batchSavePath = batchSrc.getSrcUrl();
			if ("".equals(batchSavePath)) {
				loger.info("找到批次[" + batchId + "]机构号["
						+ GlobalVar.svrInfoBean.getOrgCode() + "]的历史存储地址为空!");
				return false;
			}
		}
		loger.info("批次[" + batchId + "]版本[" + batchVer + "]设定存储目录为[" + batchSavePath + "]");
		loger.info("批次[" + batchId + "]版本[" + batchVer + "]记录在数据库中的存储目录为[" + batchSavePath + "]");
		
		//判断存储目录是否存在同样版本的SYD文件，存在则删除
		File sydF = new File(batchSavePath+File.separator+batchId+"_"+batchVer+".syd");
		if(sydF.exists()) {
			loger.info("批次[" + batchId + "]目的地存储地址存在当前版本影像syd文件，判断为垃圾文件执行删除!");
			try {
				FileUtils.forceDelete(sydF);
			} catch (IOException e) {
				loger.info("批次[" + batchId + "]目的地存储地址文件执行删除异常!",e);
			}
		}
		
		// 拷贝文件到存储目录
		List<BatchFileBean> batchFiles = batchBean.getBatchFileList();
		for (int i = 0; i < batchFiles.size(); i++) {
			File srcFile = new File(batchFiles.get(i).getFileFullPath());
			File destFile = new File(batchSavePath+File.separator+srcFile.getName());
			try {
				//存储目标地址不存在该文件
				if(!destFile.exists()) {
					FileUtils.copyFile(srcFile, destFile);
				}
			} catch (IOException e) {
				loger.info("批次[" + batchId + "]版本[" + batchVer + "]复制文件异常["
						+ batchFiles.get(i).getFileFullPath() + "]",e);
				return false;
			}
		}

		loger.info("批次[" + batchId + "]版本[" + batchVer + "]从临时目录拷贝所有文件成功!");
		

		// 批次记录到数据库
		B_BATCHS_SRC bSrc = new B_BATCHS_SRC();
		B_BATCHS_SRC_ID bSrcId = new B_BATCHS_SRC_ID();
		bSrcId.setBatchId(batchId);
		bSrcId.setBatchVer(Integer.parseInt(batchVer));
		bSrcId.setOrgCode(GlobalVar.svrInfoBean.getOrgCode());
		bSrc.setId(bSrcId);
		bSrc.setInterVer(Integer.parseInt(interVer));
		bSrc.setSrcUrl(batchSavePath);// 数据库中记录的批次缓存路径为USM界面中配置的根路径加上拼装的路径，待查询时路径再进行切换。
		bSrc.setAppCode(batchBean.getAppCode());
		bSrc.setIsMigrate("0");
		bSrc.setIsPush("0");
		bSrc.setStatus(1);
		bSrc.setResourceType(1);
		bSrc.setUpdateDate(new Date());
		bSrc.setUpdateDateStr(DateUtil.getDateTimeStr());
		
		loger.info("批次[" + batchId + "]版本[" + batchVer + "]请求记录数据库!");
		boolean isSaveToDB = dbService.saveOrUpdateBatchs(batchBean, bSrc);
		
		if (!isSaveToDB) {
			loger.info("批次[" + batchId + "]版本[" + batchVer + "]记录数据库失败了!");
			return false;
		} else {
			loger.info("批次[" + batchId + "]版本[" + batchVer + "]记录数据库成功!");
			return true;
		}
	}

	/**
	 *@Description 批次上传修改到CM等内容管理产品
	 *@param batchBean
	 *@return boolean
	 */
	public boolean batchSaveToCM(BatchBean batchBean) {
		boolean isSaveCM = false;
		
		// 提交成功返回"0"
		long saveStartTime = System.currentTimeMillis();
		CMProessService proService = null;
		if("FileNetCE".equals(GlobalVar.CMType)){
			 proService = new FileNetCEProessServiceImpl();
		}else if("IBMCM".equals(GlobalVar.CMType)){
			 proService = new CMProessServiceImpl();
		}else if("FileNetForFAF".equals(GlobalVar.CMType)){
			 proService = new FileNetForFAFProcessServiceImpl();
		}
		String isSubmit =  proService.saveOrUpdateBatch(batchBean);
		if ("0".equals(isSubmit)) {// 存储CM成功
			loger.info("批次["+batchBean.getBatch_id()+"]版本["+batchBean.getBatch_ver()+"]提交到内容管理产品CM|CE成功!耗时:"+(System.currentTimeMillis()-saveStartTime));
			B_BATCHS_SRC batchSrc = new B_BATCHS_SRC();
			B_BATCHS_SRC_ID batchSrcId = new B_BATCHS_SRC_ID();
			batchSrcId.setBatchId(batchBean.getBatch_id());
			batchSrcId.setBatchVer(Integer.valueOf(batchBean.getBatch_ver()));
			batchSrcId.setOrgCode(GlobalVar.svrInfoBean.getOrgCode());
			batchSrc.setId(batchSrcId);	
			batchSrc.setInterVer(Integer.valueOf(batchBean.getInter_ver()));
			batchSrc.setAppCode(batchBean.getAppCode());
			batchSrc.setIsMigrate("0");// 0-未迁移
			batchSrc.setIsPush("0");// 0-未推送
			batchSrc.setSrcUrl("");// 存入CM时，srcurl为空
			batchSrc.setStatus(1);// 1-默认批次有效
			batchSrc.setResourceType(0);// 存储模式存储在CM
			batchSrc.setUpdateDate(new Date());
			batchSrc.setUpdateDateStr(DateUtil.getDateTimeStr());
			
			loger.info("批次[" + batchBean.getBatch_id() + "]版本[" + batchBean.getBatch_ver() + "]请求记录数据库!");
			//写INFO、METADATA、SRC表
			boolean isSaveDB = dbService.saveOrUpdateBatchs(batchBean, batchSrc);
			
			if (isSaveDB) {
				loger.info("批次["+batchBean.getBatch_id()+"]版本["+batchBean.getBatch_ver()+"]信息记录到表成功!总耗时:"+(System.currentTimeMillis()-saveStartTime));
				isSaveCM = true;
			} else {
				loger.info("批次["+batchBean.getBatch_id()+"]版本["+batchBean.getBatch_ver()+"]信息记录到表失败了!总耗时:"+(System.currentTimeMillis()-saveStartTime));
				loger.error("批次["+batchBean.getBatch_id()+"]版本["+batchBean.getBatch_ver()+"]信息记录到表失败了!总耗时:"+(System.currentTimeMillis()-saveStartTime));
				isSaveCM = false;
			}
		} else  if ("-1".equals(isSubmit)) {
			//为上汽做个性化，如果重复上传默认成功
			isSaveCM = true;
		} else {
			loger.info("批次["+batchBean.getBatch_id()+"]版本["+batchBean.getBatch_ver()+"]提交到内容管理产品IBMCM失败了!耗时:"+(System.currentTimeMillis()-saveStartTime));
			loger.error("批次["+batchBean.getBatch_id()+"]版本["+batchBean.getBatch_ver()+"]提交到内容管理产品IBMCM失败了!耗时:"+(System.currentTimeMillis()-saveStartTime));
		}
		
		return isSaveCM;

	}

	
	/**
	 * 
	 *@Description 单批次单版本影像传输
	 *@param ip
	 *@param port
	 *@param batchBean
	 *@return
	 */
	public boolean imageMigrateByVer(String ip, int port, BatchBean batchBean) {
		SocketClientApi api = new SocketClientApi(ip, port, "SunTRM003");
		ClientBatchBean clientBatchBean = new ClientBatchBean();
		clientBatchBean.setBatchId(batchBean.getBatch_id());
		clientBatchBean.setBatch_ver(batchBean.getBatch_ver());
		List<BatchFileBean> fileList = batchBean.getBatchFileList();
		// 文件LIST
		List<ClientBatchFileBean> batchFileList = new ArrayList<ClientBatchFileBean>();
		for (int i = 0; i < fileList.size(); i++) {
			ClientBatchFileBean fileBean = new ClientBatchFileBean();
			fileBean.setFileName(fileList.get(i).getFileName());
			fileBean.setFilePath(fileList.get(i).getFileFullPath());
			File f = new File(fileList.get(i).getFileFullPath());
			fileBean.setFileSize(f.length());
			fileBean.setMd5Code(CommonFun.getFileMD5(f));
			batchFileList.add(fileBean);
		}
		clientBatchBean.setBatchFileList(batchFileList);

		try {
			clientBatchBean = api.submitBatch(clientBatchBean);
			if ("1".equals(clientBatchBean.getReCode())) {
				loger.info("批次[" + clientBatchBean.getBatchId() + "]版本["
						+ clientBatchBean.getBatch_ver() + "]传输成功!");
				return true;
			} else {
				loger.info("批次[" + clientBatchBean.getBatchId() + "]版本["
						+ clientBatchBean.getBatch_ver() + "]传输失败，错误信息!"
						+ clientBatchBean.getReCode()
						+ clientBatchBean.getReMsg());
				return false;
			}
		} catch (Exception e) {
			loger.error("批次[" + clientBatchBean.getBatchId() + "]版本["
					+ clientBatchBean.getBatch_ver() + "]传输异常!",e);
			return false;
		}

	}
	
	/**
	 * 
	 *@Description 单批次单版本影像传输(模拟控件上传)
	 *@param ip
	 *@param port
	 *@param batchBean
	 *@return
	 */
	public boolean imageMigrateByVer2(String ip, int port, BatchBean batchBean) {
		SocketClientApi api = new SocketClientApi(ip, port,"SunTXM003");
		ClientBatchBean clientBatchBean = new ClientBatchBean();
		clientBatchBean.setBatchId(batchBean.getBatch_id());
		clientBatchBean.setBatch_ver(batchBean.getBatch_ver());
		List<BatchFileBean> fileList = batchBean.getBatchFileList();
		// 文件LIST
		List<ClientBatchFileBean> batchFileList = new ArrayList<ClientBatchFileBean>();
		for (int i = 0; i < fileList.size(); i++) {
			ClientBatchFileBean fileBean = new ClientBatchFileBean();
			fileBean.setFileName(fileList.get(i).getFileName());
			fileBean.setFilePath(fileList.get(i).getFileFullPath());
			File f = new File(fileList.get(i).getFileFullPath());
			fileBean.setFileSize(f.length());
			fileBean.setMd5Code(CommonFun.getFileMD5(f));
			batchFileList.add(fileBean);
		}
		clientBatchBean.setBatchFileList(batchFileList);

		try {
			clientBatchBean = api.submitBatch(clientBatchBean);
			if ("1".equals(clientBatchBean.getReCode())) {
				loger.info("批次[" + clientBatchBean.getBatchId() + "]版本["
						+ clientBatchBean.getBatch_ver() + "]传输成功!");
				return true;
			} else {
				loger.info("批次[" + clientBatchBean.getBatchId() + "]版本["
						+ clientBatchBean.getBatch_ver() + "]传输失败，错误信息!"
						+ clientBatchBean.getReCode()
						+ clientBatchBean.getReMsg());
				return false;
			}
		} catch (Exception e) {
			loger.error("批次[" + clientBatchBean.getBatchId() + "]版本["
					+ clientBatchBean.getBatch_ver() + "]传输异常!",e);
			return false;
		}

	}

	/**
	 *@Description 影像到达通知方法
	 *@param record
	 *@return
	 */
	public boolean addInformRecord(TRM_INFORM_RECORD record) {
		return imageBusiService.imageInformRecord(record);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

}
