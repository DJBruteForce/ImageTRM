package com.sunyard.insurance.cmapi.service;

import java.util.Map;

import com.sunyard.insurance.batch.bean.BatchBean;

/**
 * 
  * @Title CMProessService.java
  * @Package com.sunyard.insurance.cmapi
  * @Description 
  * CM 提交批次 ，更新批次
  * @time 2012-8-8 上午11:22:53  @author xxw
  * @version 1.0
  *-------------------------------------------------------
 */
public interface CMProessService {
	/**
	 * 提交或更新批次
	 *@param batchId
	 *@param tempDir
	 *@return 批次提交是否成功标志，"0"-成功，否则失败
	 *@Description
	 *
	 */
	public String saveOrUpdateBatch(BatchBean batchBean);
	
	/**
	 * 
	 *@Description 
	 *对外查询接口
	 *@param queryStr
	 *@param basePath
	 *@param queryFlag 查询标识，queryFlag="ECM",来自ECM的查询
	 *@return
	 */
	public String  queryBatchForECM(String  queryStr, String basePath,String queryFlag,int queryType);
	
	/**
	 * 
	 *@Description 
	 *TRM内部查询批次
	 *@param map 查询参数
	 *@param queryFlag,查询标识"TRM"
	 *@return
	 */
	public String queryBatchForTRM(Map<String, String> map, String queryFlag);
	
	/**
	 *@param appCode 业务类型
	 *@param batchId 批次号
	 *@returnc 成功：true;失败：false
	 *@Description
	 *在CE中删除批次
	 */
	public boolean deleteBatch(String appCode,String batchId);

	
}
