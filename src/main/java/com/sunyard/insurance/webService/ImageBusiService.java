package com.sunyard.insurance.webService;

import javax.jws.WebService;

@WebService
public interface ImageBusiService {

	/**
	 * 
	 *@Description 批次同步
	 *@param batchId批次号
	 *@param toOrgCode目标机构号
	 *@return
	 */
	public String batchSynchro(String batchId, String toOrgCode);
	
	
	/**
	 * 
	 *@Description 批次同步
	 *@param batchId批次号
	 *@param toOrgCode目标机构号
	 *@return
	 */
	public String batchVerSynchro(String batchId, Integer batchVer, String ipStr,Integer port);
	
	/**
	 * 
	 *@Description 
	 *对外提供从CM中删除批次
	 *@param appCode
	 *@param busiNo
	 *@param batchId
	 *@return
	 */
	public boolean DeleteBatchFromCM(String appCode, String busiNo,String batchId);

}
