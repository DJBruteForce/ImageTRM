package com.sunyard.insurance.cmapi.action;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

import com.ibm.mm.sdk.common.DKException;
import com.sunyard.insurance.cmapi.service.ItemTypeProcessService;
import com.sunyard.insurance.common.CMConstant;

/**
 * 
  * @Title ItemTypeProcessAction.java
  * @Package com.sunyard.insurance.cmapi.action
  * @Description 
  * CM建模action
  * @time 2012-8-16 下午03:16:15  @author xxw
  * @version 1.0
  *-------------------------------------------------------
 */
public class ItemTypeProcessAction extends BaseAction {
	/**
	  * 
	  */
	private static final long serialVersionUID = 471064571831861294L;
	private static final Logger log = Logger.getLogger(ItemTypeProcessAction.class);
	
	private  ItemTypeProcessService itemTypeProcessService;

	
	 public ItemTypeProcessService getItemTypeProcessService() {
		return itemTypeProcessService;
	}


	public void setItemTypeProcessService(
			ItemTypeProcessService itemTypeProcessService) {
		this.itemTypeProcessService = itemTypeProcessService;
	}

	//-----------------------------------------------------------------------------
	
	/**
	 * 
	 *@Description 
	 *创建项类型
	 *@return
	 */
	public void createItem(){
		String appCode = super.getRequest().getParameter("appCode");
		// 项类型名
		String itemTypeName = CMConstant.docName+appCode;
		try {
			itemTypeProcessService.createItemType(itemTypeName);
			super.getResponse().setStatus(HttpStatus.SC_OK);
		} catch (DKException e) {
			log.error("创建项类型："+itemTypeName+"，失败！Error：",e);
			super.getResponse().setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		} catch (Exception e) {
			log.error("创建项类型："+itemTypeName+"，失败！Error：",e);
			super.getResponse().setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		}
		
	}
	
	/**
	 * 
	 *@Description 
	 *为业务类型添加扩展属性的同时,为属于该业务类型的险种大类也添加该扩展属性
	 *@return
	 */
	public void createAppAtrr(){
		String appCode = super.getRequest().getParameter("appCode");//业务类型代码
		String insTypes = super.getRequest().getParameter("insTypes");//险种
		String attrCode = super.getRequest().getParameter("attrCode");//属性代码
		try {
			itemTypeProcessService.appAttrRelation(appCode, insTypes, attrCode);
			super.getResponse().setStatus(HttpStatus.SC_OK);
		} catch (DKException e) {
			log.error("为项类型："+appCode+"，创建文档项类型属性："+attrCode+"失败！Error：",e);
			super.getResponse().setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		} catch (Exception e) {
			log.error("为项类型："+appCode+"，创建文档项类型属性："+attrCode+"失败！Error：",e);
			super.getResponse().setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		}
	}
	/**
	 * 
	 *@Description 
	 *CM创建文档项类型属性服务
	 *@return
	 */
	public void createAtrr(){
		String appCode = super.getRequest().getParameter("appCode");//业务类型代码
		String attrCode = super.getRequest().getParameter("attrCode");//属性代码
		// 项类型名
		String itemTypeName = CMConstant.docName+appCode;
		try {
			itemTypeProcessService.attributeRelation(itemTypeName, attrCode);
			super.getResponse().setStatus(HttpStatus.SC_OK);
		} catch (DKException e) {
			log.error("为项类型："+itemTypeName+"，创建文档项类型属性："+attrCode+"失败！Error：",e);
			super.getResponse().setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		} catch (Exception e) {
			log.error("为项类型："+itemTypeName+"，创建文档项类型属性："+attrCode+"失败！Error：",e);
			super.getResponse().setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		}
	}
	/**
	 * 
	 *@Description 
	 *为业务类型创建CM属性的同时,为属于该业务类型的险种创建扩展属性
	 *@return
	 */
	public void createInsAtrr(){
		String appCode = super.getRequest().getParameter("appCode");
		String attrCodes = super.getRequest().getParameter("attrCodes");
		// 项类型名
		String itemTypeName = CMConstant.docName+appCode;
		try {
			itemTypeProcessService.insAttrRelation(itemTypeName, attrCodes);
			super.getResponse().setStatus(HttpStatus.SC_OK);
		} catch (DKException e) {
			log.error("为项类型："+itemTypeName+"，创建文档项类型属性："+attrCodes+"失败！Error：",e);
			super.getResponse().setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		} catch (Exception e) {
			log.error("为项类型："+itemTypeName+"，创建文档项类型属性："+attrCodes+"失败！Error：",e);
			super.getResponse().setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		}
	}
	
	/**
	 * 
	 *@Description 
	 *删除项类型服务
	 *@return
	 */
	public void delItemType(){
		String appCode = super.getRequest().getParameter("appCode");
		// 项类型名
		String itemTypeName = CMConstant.docName+appCode;
		try {
			itemTypeProcessService.deleteItemType(itemTypeName);
			super.getResponse().setStatus(HttpStatus.SC_OK);
		} catch (DKException e) {
			log.error("删除项类型："+itemTypeName+"，失败！Error：",e);
			super.getResponse().setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		} catch (Exception e) {
			log.error("删除项类型："+itemTypeName+"，失败！Error：",e);
			super.getResponse().setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		}
		
	}
	
	/**
	 * 
	 *@Description 
	 *删除项类型关联属性服务
	 *@return
	 */
	public void removeAttr(){
		String appCode = super.getRequest().getParameter("appCode");
		String attrCode = super.getRequest().getParameter("attrCode");
		// 项类型名
		String itemTypeName = CMConstant.docName+appCode;
		try {
			itemTypeProcessService.delRelationAttr(itemTypeName, attrCode);
			super.getResponse().setStatus(HttpStatus.SC_OK);
		} catch (DKException e) {
			log.error("为项类型："+itemTypeName+"，移除文档项类型属性："+attrCode+"失败！Error：",e);
			super.getResponse().setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		} catch (Exception e) {
			log.error("为项类型："+itemTypeName+"，移除文档项类型属性："+attrCode+"失败！Error：",e);
			super.getResponse().setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		}
	}
}
