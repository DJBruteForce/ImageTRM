package com.sunyard.insurance.cmapi.service;

import com.ibm.mm.sdk.common.DKAttrDefICM;
import com.ibm.mm.sdk.common.DKDatastoreDefICM;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.server.DKDatastoreICM;
/**
 * 
  * @Title ItemTypeProcessService.java
  * @Package com.sunyard.insurance.cmapi.service
  * @Description 
  * CM建模接口
  * @time 2012-8-16 下午02:59:57  @author xxw
  * @version 1.0
  *-------------------------------------------------------
 */
public interface ItemTypeProcessService {
	/**
	 * 创建项类型
	 * @param itemTypeName
	 * @return
	 * @throws DKException
	 * @throws Exception
	 */
	public  void createItemType(String itemTypeName) throws DKException,Exception;
	/**
	 * 删除项类型
	 * @param itemTypeName
	 * @return
	 * @throws DKException
	 * @throws Exception
	 */
	public  void deleteItemType(String itemTypeName) throws DKException,Exception;
	/**
	 * 创建文档项类型
	 * @param itemTypeName
	 * @return
	 * @throws DKException
	 * @throws Exception
	 */
	public  void createDoc(DKDatastoreICM dsICM, DKDatastoreDefICM dsDefICM, String itemTypeName)throws DKException, Exception;
	/**
	 * 创建文档部件项类型
	 * @param dsICM
	 * @param itemTypeName
	 * @return
	 * @throws DKException
	 * @throws Exception
	 */
	public  void createDocPart(DKDatastoreICM dsICM, DKDatastoreDefICM dsDefICM, String itemTypeName)throws DKException, Exception;
	/**
	 * 关联文档部件
	 * @param dsICM
	 * @param sourceItemType
	 * @param targetItemType
	 * @throws DKException
	 * @throws Exception
	 */
	public  void createItemTypeRelation(DKDatastoreICM dsICM, DKDatastoreDefICM dsDefICM, String sourceItemType, String targetItemType)throws DKException, Exception;
	/**
     * 判断是否已有文档部件
     * @param dsICM
     * @param dsDefICM
     * @param itemTypeName
     * @return
     * @throws DKException
     * @throws Exception
     */
	public  boolean doesRelationExist(DKDatastoreICM dsICM, DKDatastoreDefICM dsDefICM, String itemTypeName)throws DKException, Exception;
	 /**
     * 关联属性
     * @param itemTypeName
     * @param attrName
     */
	public  void attributeRelation(String itemTypeName,String attrName)throws Exception;
	 /**
     * 为险种关联属性
     * @param itemTypeName
     * @param attrName
     */
	public  void insAttrRelation(String itemTypeName,String attrNames)throws Exception;
	/**
     * 为业务类型关联属性的同时为属于该业务类型的险种大类关联扩展属性
     * @param appCode
     * @param insTypes
     * @param attrName
     * @throws Exception
     */
	public  void appAttrRelation(String appCode,String insTypes,String attrName)throws Exception;
	
	/**
     * 移除关联属性（该接口无效）
     * @param itemTypeName
     * @param attrName
     */
	public  void delRelationAttr(String itemTypeName,String attrName) throws Exception;
	
	/**
	 * 创建属性
	 * @param dsICM
	 * @param attrName
	 * @return
	 * @throws DKException
	 * @throws Exception
	 */
    public  DKAttrDefICM defineAttribute(DKDatastoreICM dsICM, String attrName,int len) throws DKException,Exception;
	
	
}
