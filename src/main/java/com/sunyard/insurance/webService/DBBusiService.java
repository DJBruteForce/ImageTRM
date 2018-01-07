package com.sunyard.insurance.webService;

import java.util.List;
import javax.jws.WebService;
import com.sunyard.insurance.batch.bean.BatchBean;
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

/**
 * 
 * @Title DBBusiService.java
 * @Package com.sunyard.insurance.webService
 * @Description webservice 服务端操作数据库service接口
 * @time 2012-8-1 下午05:10:28 @author xxw
 * @version 1.0 -------------------------------------------------------
 */

@WebService
public interface DBBusiService {

	/**
	 *@Description 增加SRC表记录
	 *@param batch_src
	 *@return
	 */
	public boolean saveBatchSRC(B_BATCHS_SRC batch_src);

	/**
	 * 
	 *@Description 更新SRC表记录
	 * 
	 *@param batchSrc
	 *@return
	 */
	public boolean updateBatchSrc(B_BATCHS_SRC batchSrc);

	/**
	 *@Description 删除SRC表记录
	 *@param batch_id
	 *@param org_code
	 *@param batch_ver
	 *@return
	 */
	public boolean deleteBatchSRC(String batch_id, String org_code,
			Integer batch_ver);

	/**
	 * 
	 *@Description 删除SRC表记录通过batchId
	 *@param batchId
	 *@param orgCode
	 *@return
	 */
	public boolean deleteBatchSrcById(String batchId, String orgCode);

	/**
	 * 
	 *@Description 查找批次INFO信息
	 *@param infoId
	 *@return
	 */
	public B_BATCHS_INFO findByBatchInfo(B_BATCHS_INFO_ID infoId);

	/**
	 *@Description 查找批次SRC信息
	 *@param orgCode
	 *@return
	 */
	public B_BATCHS_SRC findBatchSrc(String batch_id, String org_code,
			Integer batch_ver);

	/**
	 * 
	 *@Description 查找批次METADATA信息
	 *@param batchId
	 *@return
	 */
	public List<B_BATCHS_METADATA> findByBatchMetadata(String batchId);

	/**
	 * 
	 *@Description 根据机构号、批次号查询记录
	 *@param batch_id
	 *@param org_code
	 *@return list
	 */
	public List<B_BATCHS_SRC> findBatchSrcS(String batch_id, String org_code);

	/**
	 *@Description 获取服务器节点信息
	 *@param svrId
	 *            节点ID 服务启动初始化信息时 返回三个表的字段
	 */
	public ServerInfoBean getSvrInfo(String svrId);

	/**
	 * 
	 *@Description 获取服务器节点信息
	 *@param org_code机构号
	 *@return
	 */
	public ServerInfoBean getSvrInfoByOrgCode(String org_code);

	/**
	 * 根据机构号获取所有影像到达通知业务类型
	 * 
	 * @Description
	 * 
	 *@param orgCode
	 *@return
	 */
	public List<String> findAllInformConfig(String orgCode);

	/**
	 * 根据orgCode获得该机构下所有排程任务
	 * 
	 * @Description
	 * 
	 *@param orgCode
	 *            机构号
	 *@return
	 */
	public List<TRM_TASK_CONFIG> findAllTaskConfByOrgCode(String orgCode);

	/**
	 * 根据排程任务id，得到该排程下所有策略。
	 * 
	 * @Description
	 * 
	 *@param taskId
	 *@return
	 */
	public List<TRM_TASK_PLOT> findAllPlotByTaskId(Integer taskId);

	/**
	 * 
	 *@Description 保存批次操作记录到表
	 * 
	 *@param taskRecord
	 *@return
	 */
	public boolean saveTask_Record(TRM_TASK_RECORD taskRecord);

	/**
	 * 
	 *@Description 查询出num条已迁移的批次(待清理)
	 * 
	 *@param orgCode
	 *@param num
	 *            数量
	 *@return
	 */
	public List<String> findBatchsForClean(String appCode, String orgCode,
			Integer num, Integer cacheDays);

	/**
	 * 
	 *@Description 查询出num条未迁移的批次
	 * 
	 *@param orgCode
	 *@param num
	 *            数量
	 *@return
	 */
	public List<String> findBatchsForMigrate(String appCode, String orgCode,
			Integer num);

	/**
	 * 
	 *@Description 查询出num条未推送的批次
	 * 
	 *@param orgCode
	 *@param num
	 *            数量
	 *@return
	 */
	public List<String> findBatchsForPush(String appCode, String orgCode,
			Integer num);
	
	
	/**
	 * 
	 *@Description 查询出num条推送失败的批次
	 * 
	 *@param orgCode
	 *@param num
	 *            数量
	 *@return
	 */
	public List<String> findBatchsForPush12(String appCode, String orgCode,
			Integer num);
	
	/**
	 * 
	 *@Description 保存或者更新INFO、METADATA插入SRC表方法
	 * 
	 *@param bean
	 *            BatchBean对象
	 *@param batchSrc
	 *            B_BATCHS_SRC表对象
	 *@return
	 */
	public boolean saveOrUpdateBatchs(BatchBean bean, B_BATCHS_SRC batchSrc);
	
	/**
	 * 
	 *@Description 
	 * 更改批次推送状态
	 *@param batchId
	 *@param orgCode
	 *@param Status
	 *@return
	 */
	public boolean UpdateBatchSRCPushStatus(String batchId,String orgCode,int sizeInt,int Status);
	
	
	/**
	 * 
	 *@Description 
	 * 更改批次迁移状态
	 *@param batchId
	 *@param orgCode
	 *@param Status
	 *@return
	 */
	public boolean UpdateBatchSRCMigrateStatus(String batchId,String orgCode,int sizeInt,int Status);
	
	/**
	 * 
	 *@Description 
	 * 查询图片批注
	 *@param pageId
	 *@return
	 */
	public TRM_ANNOTATION queryPicAnnotation(String pageId);
	
	/**
	 * 
	 *@Description 
	 *增加图片批注
	 *@param annotation
	 *@return
	 */
	public boolean savePicAnnotation(TRM_ANNOTATION annotation);
	
	/**
	 * 根据业务类型和busi_date查询B_OBJECT_STORE表记录
	 *@Description 
	 *
	 *@param appCode 业务类型
	 *@param busiDate 业务年份时间
	 *@return 符合条件的B_OBJECT_STORE表对象，未查询到符合条件的记录返回NULL
	 */
	public B_OBJECT_STORE findObjectStoreByAppCodeAndBusiDate(String appCode, String busiDate);
}
