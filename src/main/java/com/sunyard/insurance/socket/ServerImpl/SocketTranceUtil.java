package com.sunyard.insurance.socket.ServerImpl;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.batch.busi.BatchManager;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.entity.B_BATCHS_SRC;
import com.sunyard.insurance.entity.TRM_INFORM_RECORD;
import com.sunyard.insurance.socket.bean.BatchCheckBean;
import com.sunyard.insurance.socket.bean.BatchCheckResBean;
import com.sunyard.insurance.socket.bean.BatchStartBean;
import com.sunyard.insurance.socket.bean.BatchStartResBean;
import com.sunyard.insurance.socket.bean.CheckUserBean;
import com.sunyard.insurance.socket.bean.CheckUserResBean;
import com.sunyard.insurance.socket.bean.FileCheckBean;
import com.sunyard.insurance.socket.bean.FileCheckResBean;
import com.sunyard.insurance.socket.bean.TranceFileBean;
import com.sunyard.insurance.socket.bean.TranceFileResBean;
import com.sunyard.insurance.socket.bean.TranceReqObj;
import com.sunyard.insurance.socket.bean.TranceResObj;
import com.sunyard.insurance.socket.bean.TranceServiceCode;
import com.sunyard.insurance.util.BtachSyncThread;
import com.sunyard.insurance.util.ClassUtil;
import com.sunyard.insurance.util.CommonFun;
import com.sunyard.insurance.util.ZipUtil;
import com.sunyard.insurance.webService.DBBusiService;
import com.sunyard.insurance.webServiceImpl.DBBusiServiceImpl;

public class SocketTranceUtil {
	
	private static final Logger loger = Logger.getLogger(SocketTranceUtil.class);
	private static BatchManager batchManager = new BatchManager();
	private static DBBusiService dbService = new DBBusiServiceImpl();
	private static final String SUCCESS = "SUCCESS";
	
	public static CheckUserResBean checkUser(CheckUserBean checkUserBean) {
		if("admin".equals(checkUserBean.getUSERCODE()) &&
				"admin".equals(checkUserBean.getPASSWORD())) {
			CheckUserResBean checkUserResBean = new CheckUserResBean(TranceServiceCode.CHECK_USER.toString(),TranceResObj.SunTRM.toString(),SUCCESS,"用户校验成功!");
			return checkUserResBean;
		} else {
			CheckUserResBean checkUserResBean = new CheckUserResBean(TranceServiceCode.CHECK_USER.toString(),TranceResObj.SunTRM.toString(),"ERROR1001","批次上传用户登录校验失败!");
			return checkUserResBean;
		}
	}
	
	public static BatchStartResBean batchStart(BatchStartBean batchStartBean) {
		//先要判断传递的批次号版本号是否存在
		B_BATCHS_SRC tempSrc = dbService.findBatchSrc(batchStartBean.getBATCHID(), GlobalVar.svrInfoBean.getOrgCode(), Integer.parseInt(batchStartBean.getBATCHVER()));
		if(null != tempSrc) {
			loger.info("批次号["+batchStartBean.getBATCHID()+"]版本号["+batchStartBean.getBATCHVER()+"]的影像已存在，忽略本次上传!");
			BatchStartResBean batchStartResBean = new BatchStartResBean(TranceServiceCode.BATCH_START.toString(),TranceResObj.SunTRM.toString(),"ERROR2001","该批次版本已经存在,请重新扫描上传!",batchStartBean.getBATCHID(),batchStartBean.getBATCHVER());
			return batchStartResBean;
		}
		//这里根据传递批次的信息进行僬侥
		BatchStartResBean batchStartResBean = new BatchStartResBean(TranceServiceCode.BATCH_START.toString(),TranceResObj.SunTRM.toString(),SUCCESS,"服务端已经准备好接受批次文件!",batchStartBean.getBATCHID(),batchStartBean.getBATCHVER());
		return batchStartResBean;
	}
	
	public static TranceFileResBean tranceFile(String batchTemPath, TranceFileBean tranceFileBean) {
		File f = new File(batchTemPath+File.separator+tranceFileBean.getFILENAME());
		//这里判断下是不是syd文件，如果是的话要先进行删除，以保证此前别人上传的syd不影响当前的人上传
		if(tranceFileBean.getFILENAME().endsWith(".syd")) {
			f.delete();
		}
		
		if(f.exists()) {
			TranceFileResBean tranceFileResBean = new TranceFileResBean(TranceServiceCode.TRANCE_FILE.toString(),TranceResObj.SunTRM.toString(),
					SUCCESS,"文件存在!",tranceFileBean.getFILENAME(),""+f.length(),CommonFun.getFileMD5(f),""+GlobalVar.transBuffersize);
			return tranceFileResBean;
		} else {
			TranceFileResBean tranceFileResBean = new TranceFileResBean(TranceServiceCode.TRANCE_FILE.toString(),TranceResObj.SunTRM.toString(),
					SUCCESS,"文件不存在!",tranceFileBean.getFILENAME(),"0","",""+GlobalVar.transBuffersize);
			return tranceFileResBean;
		}
	}
	
	public static FileCheckResBean fileCheck(String batchTemPath,FileCheckBean fileCheckBean) {
		File f = new File(batchTemPath+File.separator+fileCheckBean.getFILENAME());
		if(f.exists()) {
			if((fileCheckBean.getMD5STR().toUpperCase()).equals(CommonFun.getFileMD5(f).toUpperCase())) {
				FileCheckResBean tranceFileResBean = new FileCheckResBean(TranceServiceCode.FILE_CHECK.toString(),TranceResObj.SunTRM.toString(),SUCCESS,"文件MD5校验成功!");
				return tranceFileResBean;
			} else {
				FileCheckResBean tranceFileResBean = new FileCheckResBean(TranceServiceCode.FILE_CHECK.toString(),TranceResObj.SunTRM.toString(),"ERROR4001","服务端已接收的文件和客户端上传的文件MD5不一致!");
				return tranceFileResBean;
			}
		} else {
			FileCheckResBean tranceFileResBean = new FileCheckResBean(TranceServiceCode.FILE_CHECK.toString(),TranceResObj.SunTRM.toString(),"ERROR4002","文件没有接受到该文件!");
			return tranceFileResBean;
		}
	}
	
	
	public static BatchCheckResBean BatchCheck(String batchTemPath,BatchCheckBean batchCheckBean) {
		//先判断是否存在文件不存在
		boolean flag = true;
		File f = new File(batchTemPath);
		if(f.listFiles().length<Integer.parseInt(batchCheckBean.getFILENUM())) {
			flag = false;
			loger.error("传输平台|Socket|批次["+batchCheckBean.getBATCHID()+"]文件["+batchTemPath+"]接受数量校验不一致!");
		}
		
		if(!flag) {
			BatchCheckResBean batchCheckResBean = new BatchCheckResBean(TranceServiceCode.BATCH_CHECK.toString(),TranceResObj.SunTRM.toString(),
					"ERROR5002","服务器批次文件接受不完整!");
			return batchCheckResBean;
		}
		
		//先判断下上传上来的是不是ZIP文件，如果是先进行解压缩
		if((f.listFiles().length==1) && (f.listFiles()[0].getName().toLowerCase().endsWith(".zip"))) {
			try {
				ZipUtil.unZip(f.listFiles()[0].getAbsolutePath(), batchTemPath);
				loger.info("传输平台|Socket|文件上传["+f.listFiles()[0].getAbsolutePath()+"]为ZIP类型文件,解压完成!");
			} catch (IOException e) {
				loger.error("传输平台|Socket|文件上传["+f.listFiles()[0].getAbsolutePath()+"]为ZIP类型文件,解压异常!",e);
			}
		}
		
		BatchBean batchBean = null;
		BatchCheckResBean batchCheckResBean = null;
		//解析SYD文件
		
		String sydFilePath = batchTemPath+File.separator+batchCheckBean.getBATCHID()+"_"+batchCheckBean.getBATCHVER()+".syd";
		File sydFile = new File(sydFilePath);
		if(!sydFile.exists()) {
			loger.error("传输平台|Socket|绝对路径["+batchTemPath+"]SYD文件不存在!");
			batchCheckResBean = new BatchCheckResBean(TranceServiceCode.BATCH_CHECK.toString(),TranceResObj.SunTRM.toString(),
					"ERROR5003","syd文件不存在!");
			return batchCheckResBean;
		}
		try {
			batchBean = ClassUtil.getSydUtilClass().getBatchBean(sydFilePath);
		} catch (Exception e) {
			loger.error("传输平台|Socket|解析批次SYD文件[" + sydFilePath + "]异常!",e);
			batchCheckResBean = new BatchCheckResBean(TranceServiceCode.BATCH_CHECK.toString(),TranceResObj.SunTRM.toString(),
					"ERROR5004","解析syd文件过程异常,请检查上传的syd文件是否正确!");
			return batchCheckResBean;
		}
		
		// 存储批次到内容管理平台
		if (GlobalVar.svrInfoBean.getSaveInCM().equals("1")) {
			loger.info("传输平台|Socket|批次[" + batchTemPath + "]开始提交到内容管理产品....");
			boolean isSubmit = batchManager.batchSaveToCM(batchBean);
			if (isSubmit) {// 提交成功返回值为true
				batchCheckResBean = new BatchCheckResBean(TranceServiceCode.BATCH_CHECK.toString(),TranceResObj.SunTRM.toString(),
						SUCCESS,"批次上传存储成功!");
				return batchCheckResBean;
			} else {
				batchCheckResBean = new BatchCheckResBean(TranceServiceCode.BATCH_CHECK.toString(),TranceResObj.SunTRM.toString(),
						"ERROR5005","批次上传存储失败,具体原因为存储内容管理产品失败!");
				return batchCheckResBean;
			}
		} else {
			/* 太平个性化，增设额外自定义的缓存目录 */
//					File extraCacheFolder = new File(GlobalVar.extraCacheFolder);
			if(GlobalVar.isFTPOpen == 1) {// FTP开关开启，表示为外网TRM
				// 1.进行FTP上传
				// 2.上传FTP成功后，上传到本地增设的缓存目录
				loger.info("传输平台|Socket|批次[" + batchTemPath + "]开始进行本地缓存....");
				boolean isSubmit = batchManager.batchSaveToExtraCache(batchBean);
				if (isSubmit) {
					batchCheckResBean = new BatchCheckResBean(TranceServiceCode.BATCH_CHECK.toString(),TranceResObj.SunTRM.toString(),
							SUCCESS,"批次上传存储成功!");
				} else {
					batchCheckResBean = new BatchCheckResBean(TranceServiceCode.BATCH_CHECK.toString(),TranceResObj.SunTRM.toString(),
							"ERROR5006","批次上传存储失败,具体原因为ftp存储失败!");
				}
			} else {
				// FTP开关关闭，表示为内网TRM
				// 1.直接将批次上传到SunUSM配置的缓存目录中存储
				// 调用本地缓存批次功能
				loger.info("传输平台|Socket|批次[" + batchTemPath + "]开始进行本地缓存....");
				boolean isSubmit = batchManager.batchSaveToLocal(batchBean);
				if (isSubmit) {
					batchCheckResBean = new BatchCheckResBean(TranceServiceCode.BATCH_CHECK.toString(),TranceResObj.SunTRM.toString(),
							SUCCESS,"批次上传存储成功!");
				} else {
					batchCheckResBean = new BatchCheckResBean(TranceServiceCode.BATCH_CHECK.toString(),TranceResObj.SunTRM.toString(),
							"ERROR5007","批次上传存储失败,具体原因为本地存储失败!");
				}
			}
		}
		
		// 上传成功就删除临时文件夹
		if (batchCheckResBean.getRESCODE().equals(SUCCESS)) {
			loger.info("传输平台|Socket|批次[" + batchCheckBean.getBATCHID() + "]存储成功！");
			try {
				FileUtils.deleteDirectory(new File(batchTemPath));
				loger.info("传输平台|Socket|批次[" + batchCheckBean.getBATCHID() + "]删除批次临时存储目录["+ batchTemPath + "]成功!");
			} catch (IOException e) {
				loger.error("传输平台|Socket|批次[" + batchCheckBean.getBATCHID() + "]删除批次临时存储目录["+ batchTemPath + "]失败了!");
			}

			//非SunTRM上传影像,切入影像到达通知事物
			if (!TranceReqObj.SunTRM.equals(batchCheckBean.getREQOBJ())) {
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
						loger.info("传输平台|Socket|批次[" + batchCheckBean.getBATCHID() + "]版本["+ batchBean.getBatch_ver() + "]到达通知成功！");
					} else {
						loger.info("传输平台|Socket|批次[" + batchCheckBean.getBATCHID() + "]版本["+ batchBean.getBatch_ver() + "]到达通知失败！");
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
			loger.info("批次batchID'[" + batchCheckBean.getBATCHID() + "]存储失败！");
		}
		
		
		return batchCheckResBean;
		
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
