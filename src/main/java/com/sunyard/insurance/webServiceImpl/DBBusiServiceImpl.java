package com.sunyard.insurance.webServiceImpl;

import java.util.List;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.log4j.Logger;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.entity.B_BATCHS_INFO;
import com.sunyard.insurance.entity.B_BATCHS_INFO_ID;
import com.sunyard.insurance.entity.B_BATCHS_METADATA;
import com.sunyard.insurance.entity.B_BATCHS_SRC;
import com.sunyard.insurance.entity.B_OBJECT_STORE;
import com.sunyard.insurance.entity.ServerInfoBean;
import com.sunyard.insurance.entity.TRM_ANNOTATION;
import com.sunyard.insurance.entity.TRM_TASK_CONFIG;
import com.sunyard.insurance.entity.TRM_TASK_PLOT;
import com.sunyard.insurance.entity.TRM_TASK_RECORD;
import com.sunyard.insurance.webService.DBBusiService;

/**
 * 
 * @Title DBBusiServiceImpl.java
 * @Package com.sunyard.insurance.webServiceImpl
 * @Description webservice 客户端操作数据库实现类
 * @time 2012-8-2 下午03:34:24 @author xxw
 * @version 1.0 
 */
public class DBBusiServiceImpl implements DBBusiService {	

	private static final Logger log = Logger.getLogger(DBBusiServiceImpl.class);
	private static JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
	private static DBBusiService service = null;
	
	/**
	 * 
	 *@Description 获取webservice客户端，这里为了考虑性能使用静态变量
	 *@return
	 */
	public DBBusiService getDBBusiService() throws Exception {
		if(null==service) {
			String serviceURL = GlobalVar.USMURL + "/webServices/DBBusiService";
			log.info("创建webService[DBBusiService]客户端:"+serviceURL);
			factory.setServiceClass(DBBusiService.class);
			factory.setAddress(serviceURL);
			service = (DBBusiService) factory.create();
			
			//设置超时时间
			Client proxy = ClientProxy.getClient(service); 
			HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
	        HTTPClientPolicy policy = new HTTPClientPolicy();
	        policy.setConnectionTimeout(180*1000);//连接超时时间
	        policy.setReceiveTimeout(180*1000);//影响超时时间
	        conduit.setClient(policy);
	        //设置超时时间
		}
		return service;
	}

	/**
	 * 
	 * @param bBatchSrc
	 * @return
	 * @Description 保存B_BATCHS_SRC对象
	 */
	public boolean saveBatchSRC(B_BATCHS_SRC bBatchSrc) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.saveBatchSRC(bBatchSrc);
		} catch (Exception e) {
			log.error("调用webService操作数据库[增加批次B_BATCHS_SRC]异常BATCT_ID["
					+ bBatchSrc.getId().getBatchId() + "]",e);
			return false;
		}
	}

	/**
	 * 
	 *@param batchSrc
	 *@return
	 *@Description 更新B_BATCHS_SRC对象
	 * 
	 */
	public boolean updateBatchSrc(B_BATCHS_SRC batchSrc) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.updateBatchSrc(batchSrc);
		} catch (Exception e) {
			log.error("调用webService操作数据库[更新批次B_BATCHS_SRC]异常BATCT_ID["
					+ batchSrc.getId().getBatchId() + "]",e);
			return false;
		}
	}

	/**
	 * 删除批次
	 * 
	 * @param batch_id
	 * @param org_code
	 * @param batch_ver
	 * @return
	 * @Description
	 * 
	 */
	public boolean deleteBatchSRC(String batch_id, String org_code,
			Integer batch_ver) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.deleteBatchSRC(batch_id, org_code, batch_ver);
		} catch (Exception e) {
			log.error("调用webService操作数据库[删除批次B_BATCHS_SRC]异常BATCT_ID["
					+ batch_id + "]版本[" + batch_ver + "]",e);
			return false;
		}
	}
	
	public boolean deleteBatchSrcById(String batch_id, String org_code) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.deleteBatchSrcById(batch_id, org_code);
		} catch (Exception e) {
			log.error("调用webService操作数据库[删除批次B_BATCHS_SRC]异常BATCT_ID["+ batch_id + "]",e);
			return false;
		}
	}
	/**
	 * 
	 * @param batchId
	 * @param orgCode
	 * @param batchVer
	 * @return
	 * @Description
	 * 
	 */
	public B_BATCHS_SRC findBatchSrc(String batchId, String orgCode,
			Integer batchVer) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.findBatchSrc(batchId, orgCode, batchVer);
		} catch (Exception e) {
			log.error("调用webService操作数据[查询批次B_BATCHS_SRC]异常BATCH_ID[" + batchId
					+ "]版本[" + batchVer + "]",e);
			return null;
		}
	}

	public List<B_BATCHS_SRC> findBatchSrcS(String batchId, String orgCode) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.findBatchSrcS(batchId, orgCode);
		} catch (Exception e) {
			log.error("调用webService操作数据库查询BATCH_ID["+batchId+"]机构号["+orgCode+"]的所有SRC表记录异常!",e);
			return null;
		}
	}

	/**
	 * 
	 * @param svrId
	 * @return
	 * @Description
	 * 
	 */
	public ServerInfoBean getSvrInfo(String svrId) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.getSvrInfo(svrId);
		} catch (Exception e) {
			log.error("调用webService操作数据库[根据节点ID获取节点信息]异常SVR_ID[" + svrId + "]",e);
			return null;
		}
	}

	/**
	 * 
	 * @param orgCode
	 * @return
	 * @Description
	 * 
	 */
	public List<String> findAllInformConfig(String orgCode) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.findAllInformConfig(orgCode);
		} catch (Exception e) {
			log.error("调用webService操作数据库[获取机构影像到达通知配置]异常ORG_CODE[" + orgCode+ "]",e);
			return null;
		}
	}

	/**
	 * 
	 * @param orgCode
	 * @return
	 * @Description 根据机构号获取ServerInfoBean
	 * 
	 */
	public ServerInfoBean getSvrInfoByOrgCode(String orgCode) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.getSvrInfoByOrgCode(orgCode);
		} catch (Exception e) {
			log.error("调用webService操作数据库[根据机构号获取节点信息]异常ORG_CODE[" + orgCode+ "]",e);
			return null;
		}
	}

	/**
	 * 
	 *@param infoId
	 *@return
	 *@Description 根据主键查询B_BATCHS_INFO
	 * 
	 */
	public B_BATCHS_INFO findByBatchInfo(B_BATCHS_INFO_ID infoId) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.findByBatchInfo(infoId);
		} catch (Exception e) {
			log.error("调用webService操作数据库[根据联合主键查询B_BATCHS_INFO]异常BATCH_ID["
					+ infoId.getBatchId() + "]版本[" + infoId.getBatchVer() + "]",e);
			return null;
		}
	}

	/**
	 * 
	 *@param batchId
	 *@return
	 *@Description 根据BATCH_ID查询批次所有扩展属性
	 * 
	 */
	public List<B_BATCHS_METADATA> findByBatchMetadata(String batchId) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.findByBatchMetadata(batchId);
		} catch (Exception e) {
			log.error("调用webService操作数据库[根据BATCH_ID获取批次扩展属性]异常BATCH_ID["+ batchId + "]",e);
			return null;
		}
	}

	/**
	 * 
	 *@param orgCode
	 *@return
	 *@Description 根据机构获取缓存机构所有排程配置
	 * 
	 */
	public List<TRM_TASK_CONFIG> findAllTaskConfByOrgCode(String orgCode) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.findAllTaskConfByOrgCode(orgCode);
		} catch (Exception e) {
			log.error("调用webService操作数据库[根据orgCode获取该机构下所有排程任务]异常ORG_CODE["+ orgCode + "]",e);
			return null;
		}
	}

	/**
	 * 
	 *@param taskId
	 *@return
	 *@Description 根据排程ID获取排程所有策略
	 * 
	 */
	public List<TRM_TASK_PLOT> findAllPlotByTaskId(Integer taskId) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.findAllPlotByTaskId(taskId);
		} catch (Exception e) {
			log.error("调用webService操作数据库[根据taskId获取该排程下所有策略]异常TASK_ID["+ taskId + "]",e);
			return null;
		}
	}

	/**
	 * 
	 *@param taskRecord
	 *@return
	 *@Description 保存批次操作记录
	 * 
	 */
	public boolean saveTask_Record(TRM_TASK_RECORD taskRecord) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.saveTask_Record(taskRecord);
		} catch (Exception e) {
			log.error("调用webService操作数据库[保存批次操作记录异常BATCH_ID["+ taskRecord.getBatch_id() + "]",e);
			return false;
		}
	}

	/**
	 * 
	 *@param appCode
	 *@param orgCode
	 *@param num
	 *@param cacheDays
	 *@return
	 *@Description 查询待清理批次
	 * 
	 */
	public List<String> findBatchsForClean(String appCode, String orgCode,
			Integer num, Integer cacheDays) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.findBatchsForClean(appCode, orgCode, num, cacheDays);
		} catch (Exception e) {
			log.error("调用webService操作数据库[获取待清理批次]异常APP_CODE[" + appCode + "]",e);
			return null;
		}
	}

	/**
	 * 
	 *@param appCode
	 *@param orgCode
	 *@param num
	 *@return
	 *@Description
	 * 
	 */
	public List<String> findBatchsForMigrate(String appCode, String orgCode,
			Integer num) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.findBatchsForMigrate(appCode, orgCode, num);
		} catch (Exception e) {
			log.error("调用webService操作数据库[获取待迁移批次]异常APP_CODE[" + appCode + "]",e);
			return null;
		}
	}

	/**
	 * 
	 *@param appCode
	 *@param orgCode
	 *@param num
	 *@return
	 *@Description
	 * 
	 */
	public List<String> findBatchsForPush(String appCode, String orgCode,
			Integer num) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.findBatchsForPush(appCode, orgCode, num);
		} catch (Exception e) {
			log.error("调用webService操作数据库[获取待推送批次]异常APP_CODE[" + appCode + "]",e);
			return null;
		}
	}
	
	/**
	 * 
	 *@param appCode
	 *@param orgCode
	 *@param num
	 *@return
	 *@Description
	 * 
	 */
	public List<String> findBatchsForPush12(String appCode, String orgCode,
			Integer num) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.findBatchsForPush12(appCode, orgCode, num);
		} catch (Exception e) {
			log.error("调用webService操作数据库[获取推送失败批次]异常APP_CODE[" + appCode + "]",e);
			return null;
		}
	}
	
	/**
	 * 
	 *@param bean
	 *@param batchSrc
	 *@return
	 *@Description 对INFO、METADATA、SRC表的更新或插入
	 *
	 */
	public boolean saveOrUpdateBatchs(BatchBean bean, B_BATCHS_SRC batchSrc) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.saveOrUpdateBatchs(bean, batchSrc);
		} catch (Exception e) {
			log.error("调用webService操作数据库保存业务信息表异常BATCH_ID["
					+ bean.getBatch_id() + "]版本[" + bean.getBatch_ver() + "]",e);
			return false;
		}
	}

	public boolean UpdateBatchSRCPushStatus(String batchId, String orgCode,int sizeInt,int Status) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.UpdateBatchSRCPushStatus(batchId, orgCode,sizeInt, Status);
		} catch (Exception e) {
			log.error("调用webservice远程操作数据库更新批次["+batchId+"]]机构号["+orgCode+"]推送状态为["+Status+"]异常!",e);
			return false;
		}
	}
	
	public boolean UpdateBatchSRCMigrateStatus(String batchId, String orgCode,
			int sizeInt, int Status) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.UpdateBatchSRCMigrateStatus(batchId, orgCode,sizeInt, Status);
		} catch (Exception e) {
			log.error("调用webservice远程操作数据库更新批次["+batchId+"]]机构号["+orgCode+"]迁移状态为["+Status+"]异常!",e);
			return false;
		}
	}

	public TRM_ANNOTATION queryPicAnnotation(String pageId) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.queryPicAnnotation(pageId);
		} catch (Exception e) {
			log.error("调用webservice远程操作数据库查询图片["+pageId+"]批注异常!",e);
			return null;
		}
	}

	public boolean savePicAnnotation(TRM_ANNOTATION annotation) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.savePicAnnotation(annotation);
		} catch (Exception e) {
			log.error("调用webservice远程操作数据库添加图片批注异常!",e);
			return false;
		}
	}

	public B_OBJECT_STORE findObjectStoreByAppCodeAndBusiDate(String appCode, String busiDate) {
		try {
			DBBusiService service = this.getDBBusiService();
			return service.findObjectStoreByAppCodeAndBusiDate(appCode, busiDate);
		} catch (Exception e) {
			log.error("webservice远程操作数据库查询B_OBJECT_STORE表APP_CODE=[" + appCode + "],BUSI_DATE=[" + busiDate + "]异常!",e);
			return null;
		}
	}

	
}
