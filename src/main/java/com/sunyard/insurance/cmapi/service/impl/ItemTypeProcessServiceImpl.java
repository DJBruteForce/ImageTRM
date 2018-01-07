package com.sunyard.insurance.cmapi.service.impl;

import org.apache.log4j.Logger;

import com.ibm.mm.sdk.common.DKAttrDefICM;
import com.ibm.mm.sdk.common.DKConstant;
import com.ibm.mm.sdk.common.DKConstantICM;
import com.ibm.mm.sdk.common.DKDatastoreDefICM;
import com.ibm.mm.sdk.common.DKException;
import com.ibm.mm.sdk.common.DKItemTypeDefICM;
import com.ibm.mm.sdk.common.DKItemTypeRelationDefICM;
import com.ibm.mm.sdk.common.dkAttrDef;
import com.ibm.mm.sdk.common.dkCollection;
import com.ibm.mm.sdk.common.dkIterator;
import com.ibm.mm.sdk.server.DKDatastoreICM;
import com.sunyard.insurance.cmapi.service.ItemTypeProcessService;
import com.sunyard.insurance.cmapi.util.CMConnectionPool;
import com.sunyard.insurance.common.CMConstant;

/**
 * 
 * @Title ItemTypeProcessServiceImpl.java
 * @Package com.sunyard.insurance.cmapi.service.impl
 * @Description 
 * CM建模接口实现类
 * @time 2012-8-17 下午03:35:13  @author xxw   @version 1.0
 *-------------------------------------------------------
 */
public class ItemTypeProcessServiceImpl implements ItemTypeProcessService{
	private static final Logger log = Logger.getLogger(ItemTypeProcessServiceImpl.class);
	static int defualLen = 128;
	static int relationLen = 2048;
	/**
	 * 创建项类型
	 * @param itemTypeName
	 * @return
	 * @throws DKException
	 * @throws Exception
	 */
	public  void createItemType(String itemTypeName) throws DKException,Exception{
		//获取CM连接
		DKDatastoreICM dsICM = null;
		try{
			if(CMConstant.isCMPool.equals("0")){
				dsICM =  new DKDatastoreICM();//创建新连接
				dsICM.connect(CMConstant.RMN,CMConstant.CMUser,CMConstant.CMPwd,"");
			}else{
				dsICM = CMConnectionPool.getConnection(CMConstant.CMUser, CMConstant.CMPwd);
			}
			DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();
	    	// 判断项类型文档是否存在
			DKItemTypeDefICM docItemType = (DKItemTypeDefICM) dsDefICM.retrieveEntity(itemTypeName); 
	    	if(docItemType==null){//没有就创建
	    		createDoc(dsICM,dsDefICM,itemTypeName);
	    	}
	    	// 判断文档部件项类型名是否存在
        	DKItemTypeDefICM partItemType = (DKItemTypeDefICM) dsDefICM.retrieveEntity(CMConstant.docPartName); 
        	if(partItemType==null){//没有就创建
        		createDocPart(dsICM, dsDefICM, CMConstant.docPartName);
        	}
	    	//判断是否已有文档部件
	    	if(!doesRelationExist(dsICM,dsDefICM,itemTypeName)){
			    createItemTypeRelation(dsICM,dsDefICM,itemTypeName,CMConstant.docPartName);
	    	}
		    log.info("创建项类型："+itemTypeName+"成功！");
		}catch(DKException e){
			throw e;
		}catch(Exception e){
			throw e;
		}finally {
			try {
				// 如果有到CM的连接对象，要返回该连接对象到连接池
				if (dsICM != null) {
					log.warn("返回/销毁CM连接sessionID:["+dsICM.getSessionId()+"]");
					if(CMConstant.isCMPool.equals("0")){
						dsICM.disconnect();
						dsICM.destroy();
						dsICM = null;
					}else{
						CMConnectionPool.returnConnection(dsICM);
					}
				}
			} catch (Exception ex) {
				log.error("销毁/返回CM连接到连接池异常!", ex);
			}
		}
	}
	/**
	 * 删除项类型
	 * @param itemTypeName
	 * @return
	 * @throws DKException
	 * @throws Exception
	 */
	public  void deleteItemType(String itemTypeName) throws DKException,Exception {
		//获取CM连接
		DKDatastoreICM dsICM = null;
		try{
			if(CMConstant.isCMPool.equals("0")){
				dsICM =  new DKDatastoreICM();//创建新连接
				dsICM.connect(CMConstant.RMN,CMConstant.CMUser,CMConstant.CMPwd,"");
			}else{
				dsICM = CMConnectionPool.getConnection(CMConstant.CMUser, CMConstant.CMPwd);
			}
			DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();
	    	// 判断项类型是否存在
			DKItemTypeDefICM itemType = (DKItemTypeDefICM) dsDefICM.retrieveEntity(itemTypeName); 
			if(itemType!=null){
				itemType.del();
			}
		   log.info("删除项类型："+itemTypeName+"成功！");
		}catch(DKException e){
			throw e;
		}catch(Exception e){
			throw e;
		}finally {
			try {
				// 如果有到CM的连接对象，要返回该连接对象到连接池
				if (dsICM != null) {
					log.warn("返回/销毁CM连接sessionID:["+dsICM.getSessionId()+"]");
					if(CMConstant.isCMPool.equals("0")){
						dsICM.disconnect();
						dsICM.destroy();
						dsICM = null;
					}else{
						CMConnectionPool.returnConnection(dsICM);
					}
				}
			} catch (Exception ex) {
				log.error("销毁/返回CM连接到连接池异常!", ex);
			}
		}
	}
	/**
	 * 创建文档项类型
	 * @param itemTypeName
	 * @return
	 * @throws DKException
	 * @throws Exception
	 */
	public  void createDoc(DKDatastoreICM dsICM, DKDatastoreDefICM dsDefICM, String itemTypeName) throws DKException,Exception{
		try{
			DKItemTypeDefICM itemType = new DKItemTypeDefICM(dsICM);
		    // 项类型名,必须少于15个字节
		    itemType.setName(itemTypeName);
		    // 项类型描述
		    itemType.setDescription(itemTypeName);
		    // 项类型分类
		    itemType.setClassification(DKConstantICM.DK_ICM_ITEMTYPE_CLASS_DOC_MODEL);    
		    // 资源管理器
		    //itemType.setDefaultRMCode((short)1);
		    // 集合
		    //itemType.setDefaultCollCode((short)1);
		    // 关联默认属性
		    //----------------------------------
		    //批次号
		    DKAttrDefICM attr = (DKAttrDefICM) dsDefICM.retrieveAttr(CMConstant.docBatchId); 
			if(attr==null){
				attr = defineAttribute(dsICM,CMConstant.docBatchId,defualLen);
			}
			//是否唯一
		    attr.setUnique(true);
		    //是否可以为空
		    attr.setNullable(false);
		    itemType.addAttr(attr);
		    //----------------------------------
		    //外部版本号
		    attr = (DKAttrDefICM) dsDefICM.retrieveAttr(CMConstant.docInnerVer); 
			if(attr==null){
				attr = defineAttribute(dsICM,CMConstant.docInnerVer,defualLen);
			}
			//是否唯一
		    attr.setUnique(false);
		    //是否可以为空
		    attr.setNullable(false);
		    itemType.addAttr(attr);  
		    
			//是否唯一
		    attr.setUnique(false);
		    //是否可以为空
		    attr.setNullable(false);
		    itemType.addAttr(attr);
		    //----------------------------------
		    //业务编号
		    attr = (DKAttrDefICM) dsDefICM.retrieveAttr(CMConstant.docBusiNo); 
			if(attr==null){
				attr = defineAttribute(dsICM,CMConstant.docBusiNo,defualLen);
			}
			//是否唯一
		    attr.setUnique(false);
		    //是否可以为空
		    attr.setNullable(true);
		    itemType.addAttr(attr);
		   
			//是否唯一
		    attr.setUnique(false);
		    //是否可以为空
		    attr.setNullable(true);
		    itemType.addAttr(attr);
		    itemType.add();
		    log.info("创建文档项类型："+itemTypeName+"成功！");
		}catch(DKException e){
    		log.error("创建文档项类型："+itemTypeName+"失败！Error：",e);
    		throw new DKException("创建文档项类型失败");
    	}catch(Exception e){
    		log.error("创建文档项类型："+itemTypeName+"失败！Error：",e);
    		throw new DKException("创建文档项类型失败");
    	}
	}
	/**
	 * 创建文档部件项类型
	 * @param dsICM
	 * @param itemTypeName
	 * @return
	 * @throws DKException
	 * @throws Exception
	 */
	public  void createDocPart(DKDatastoreICM dsICM, DKDatastoreDefICM dsDefICM, String itemTypeName) throws DKException,Exception{
		try{
			DKItemTypeDefICM itemType = new DKItemTypeDefICM(dsICM);
		    // 项类型名,必须少于15个字节
		    itemType.setName(itemTypeName);
		    // 项类型描述
		    itemType.setDescription(itemTypeName);
		    // 项类型分类
		    itemType.setClassification(DKConstantICM.DK_ICM_ITEMTYPE_CLASS_DOC_PART);  
		    // 媒体对象类
		    itemType.setXDOClassName(DKConstantICM.DK_ICM_XDO_IMAGE_CLASS_NAME);
	        itemType.setXDOClassID(DKConstantICM.DK_ICM_XDO_IMAGE_CLASS_ID);
		    // 资源管理器
		    //itemType.setDefaultRMCode((short)1);
		    // 集合
		    //itemType.setDefaultCollCode((short)1);
		    // 关联默认属性，文件名
		    DKAttrDefICM attr = (DKAttrDefICM) dsDefICM.retrieveAttr(CMConstant.docPartSourceID); 
			if(attr==null){//没有就创建
				attr = defineAttribute(dsICM,CMConstant.docPartSourceID,defualLen);
			}
			//是否唯一
//		    attr.setUnique(true);
		    //是否可以为空
		    attr.setNullable(false);
		    itemType.addAttr(attr);  
		    
		    itemType.add();
		    log.info("创建文档部件项类型："+itemTypeName+"成功！");
		}catch(DKException e){
			log.error("创建文档部件项类型："+itemTypeName+"失败！Error：",e);
    		throw new DKException("创建文档部件项类型失败");
    	}catch(Exception e){
    		log.error("创建项类型："+itemTypeName+"失败！Error：",e);
    		throw new DKException("创建文档部件项类型失败");
    	}
	}
	
	/**
	 * 关联文档部件
	 * @param dsICM
	 * @param sourceItemType
	 * @param targetItemType
	 * @throws DKException
	 * @throws Exception
	 */
    public  void createItemTypeRelation(DKDatastoreICM dsICM, DKDatastoreDefICM dsDefICM, String sourceItemType, String targetItemType) throws DKException, Exception
    {
    	try{
        	DKItemTypeRelationDefICM itemTypeRel = new DKItemTypeRelationDefICM(dsICM);
        	//文档
        	itemTypeRel.setSourceItemTypeID(dsDefICM.getEntityIdByName(sourceItemType));
        	//文档部件
        	itemTypeRel.setTargetItemTypeID(dsDefICM.getEntityIdByName(targetItemType));
                
        	itemTypeRel.setVersionControl((short)DKConstantICM.DK_ICM_VERSION_CONTROL_NEVER);
                
        	itemTypeRel.setDefaultACLCode(DKConstantICM.DK_ICM_PUBLIC_READ_ACL); 
    	        
        	itemTypeRel.setDefaultRMCode((short)1);
        	itemTypeRel.setDefaultCollCode((short)1);
                
        	dsDefICM.add(itemTypeRel);
        	log.info("为项类型："+sourceItemType+"，设置文档部件："+targetItemType+" 成功！");
    	}catch(DKException e){
    		log.error("为项类型："+sourceItemType+"，设置文档部件："+targetItemType+"失败！Error：",e);
    		throw new DKException("为项类型设置文档部件失败");
    	}catch(Exception e){
    		log.error("为项类型："+sourceItemType+"，设置文档部件："+targetItemType+"失败！Error：",e);
    		throw new Exception("为项类型设置文档部件失败");
    	}
    }
    /**
     * 判断是否已有文档部件
     * @param dsICM
     * @param dsDefICM
     * @param itemTypeName
     * @return
     * @throws DKException
     * @throws Exception
     */
    public  boolean doesRelationExist(DKDatastoreICM dsICM, DKDatastoreDefICM dsDefICM, String itemTypeName) throws DKException, Exception{
    	dsDefICM.clearCache();
        DKItemTypeDefICM itemTypeDef = (DKItemTypeDefICM) dsDefICM.retrieveEntity(itemTypeName);
        dkCollection coll = dsDefICM.retrieveItemTypeRelations(itemTypeDef.getItemTypeId());
        dkIterator iter = coll.createIterator();
        while(iter.more()){
        	return true;
        }
        return false;
    }
    /**
     * 关联属性
     * @param itemTypeName
     * @param attrName
     */
	public  void attributeRelation(String itemTypeName,String attrName) throws Exception{
		DKDatastoreICM dsICM = null;
		try{
			if(CMConstant.isCMPool.equals("0")){
				dsICM =  new DKDatastoreICM();//创建新连接
				dsICM.connect(CMConstant.RMN,CMConstant.CMUser,CMConstant.CMPwd,"");
			}else{
				dsICM = CMConnectionPool.getConnection(CMConstant.CMUser, CMConstant.CMPwd);
			}
			
			DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef(); 
			DKItemTypeDefICM itemType = (DKItemTypeDefICM) dsDefICM.retrieveEntity(itemTypeName); 
			dkAttrDef attrDef = itemType.retrieveAttr(attrName);
			//判断该项类型是否已有该属性
			if(attrDef==null){
				DKAttrDefICM attrDefICM = (DKAttrDefICM) dsDefICM.retrieveAttr(attrName); 
				if(attrDefICM==null){
					attrDefICM = defineAttribute(dsICM,attrName,relationLen);
				}
				//是否唯一
				attrDefICM.setUnique(false);
				//是否可以为空
				attrDefICM.setNullable(true);
				itemType.addAttr(attrDefICM);   
				itemType.update();	
			}
			log.info("为项类型："+itemTypeName+"，设置文档项类型属性："+attrName+"，成功！");
		}catch(Exception e){
			throw e;
		}finally {
			try {
				// 如果有到CM的连接对象，要返回该连接对象到连接池
				if (dsICM != null) {
					log.warn("返回/销毁CM连接sessionID:["+dsICM.getSessionId()+"]");
					if(CMConstant.isCMPool.equals("0")){
						dsICM.disconnect();
						dsICM.destroy();
						dsICM = null;
					}else{
						CMConnectionPool.returnConnection(dsICM);
					}
				}
			} catch (Exception ex) {
				log.error("销毁/返回CM连接到连接池异常!", ex);
			}
		}
	}
	
    /**
     * 为险种关联文档项类型属性
     * @param itemTypeName
     * @param attrName
     */
	public  void insAttrRelation(String itemTypeName,String attrNames) throws Exception{
		DKDatastoreICM dsICM = null;
		try{
			if(CMConstant.isCMPool.equals("0")){
				dsICM =  new DKDatastoreICM();//创建新连接
				dsICM.connect(CMConstant.RMN,CMConstant.CMUser,CMConstant.CMPwd,"");
			}else{
				dsICM = CMConnectionPool.getConnection(CMConstant.CMUser, CMConstant.CMPwd);
			}
			
			DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef(); 
			DKItemTypeDefICM itemType = (DKItemTypeDefICM) dsDefICM.retrieveEntity(itemTypeName); 
			
			String[] attrs = attrNames.split(",");
			for(int i=0;i<attrs.length;i++){
				String attrName = attrs[i];
				dkAttrDef attrDef = itemType.retrieveAttr(attrName);
				//判断该项类型是否已有该属性
				if(attrDef==null){
					DKAttrDefICM attrDefICM = (DKAttrDefICM) dsDefICM.retrieveAttr(attrName); 
					if(attrDefICM==null){
						attrDefICM = defineAttribute(dsICM,attrName,relationLen);
					}
					//是否唯一
					attrDefICM.setUnique(false);
					//是否可以为空
					attrDefICM.setNullable(true);
					itemType.addAttr(attrDefICM);   
				}
			}
			itemType.update();
			log.info("为项类型："+itemTypeName+"，设置文档项类型属性："+attrNames+"，成功！");
		}catch(Exception e){
			throw e;
		}finally {
			try {
				// 如果有到CM的连接对象，要返回该连接对象到连接池
				if (dsICM != null) {
					log.warn("返回/销毁CM连接sessionID:["+dsICM.getSessionId()+"]");
					if(CMConstant.isCMPool.equals("0")){
						dsICM.disconnect();
						dsICM.destroy();
						dsICM = null;
					}else{
						CMConnectionPool.returnConnection(dsICM);
					}
				}
			} catch (Exception ex) {
				log.error("销毁/返回CM连接到连接池异常!", ex);
			}
		}
	}
	
    /**
     * 为业务类型关联属性的同时为属于该业务类型的险种大类关联扩展属性
     * @param appCode
     * @param insTypes
     * @param attrName
     * @throws Exception
     */
	public  void appAttrRelation(String appCode,String insTypes,String attrName) throws Exception{
		DKDatastoreICM dsICM = null;
		try{
			if(CMConstant.isCMPool.equals("0")){
				dsICM =  new DKDatastoreICM();//创建新连接
				dsICM.connect(CMConstant.RMN,CMConstant.CMUser,CMConstant.CMPwd,"");
			}else{
				dsICM = CMConnectionPool.getConnection(CMConstant.CMUser, CMConstant.CMPwd);
			}
			
			//开始事务
//			dsICM.startTransaction();
			DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef();
			
			String itemTypeNames = "";
			if(insTypes==null || insTypes.equals("")){//没有险种时，把业务类型代码作为项类型
				itemTypeNames = appCode;
			}else{
				itemTypeNames = appCode+","+insTypes;
			}
			String[] itemTypes = itemTypeNames.split(",");
			for(int i =0;i<itemTypes.length;i++){
				String itemTypeName = CMConstant.docName+itemTypes[i];
				DKItemTypeDefICM itemType = (DKItemTypeDefICM) dsDefICM.retrieveEntity(itemTypeName); 
				dkAttrDef attrDef = itemType.retrieveAttr(attrName);
				//判断该项类型是否已有该属性
				if(attrDef==null){
					DKAttrDefICM attrDefICM = (DKAttrDefICM) dsDefICM.retrieveAttr(attrName); 
					if(attrDefICM==null){//没有则添加该项类型属性
						attrDefICM = defineAttribute(dsICM,attrName,relationLen);
					}
					//是否唯一
					attrDefICM.setUnique(false);
					//是否可以为空
					attrDefICM.setNullable(true);
					itemType.addAttr(attrDefICM);
					itemType.update();
				}
				log.info("为项类型："+itemTypeName+"，设置文档项类型属性："+attrName+"，成功！");
			}
			//提交事务
//			dsICM.commit();
		}catch(Exception e){
			//回滚事务
//			dsICM.rollback();
			throw e;
		}finally {
			try {
				// 如果有到CM的连接对象，要返回该连接对象到连接池
				if (dsICM != null) {
					log.warn("返回/销毁CM连接sessionID:["+dsICM.getSessionId()+"]");
					if(CMConstant.isCMPool.equals("0")){
						dsICM.disconnect();
						dsICM.destroy();
						dsICM = null;
					}else{
						CMConnectionPool.returnConnection(dsICM);
					}
				}
			} catch (Exception ex) {
				log.error("销毁/返回CM连接到连接池异常!", ex);
			}
		}
	}
	
    /**
     * 移除关联属性（该接口无效）
     * @param itemTypeName
     * @param attrName
     */
	public  void delRelationAttr(String itemTypeName,String attrName) throws Exception{
		DKDatastoreICM dsICM = null;
		try{
			if(CMConstant.isCMPool.equals("0")){
				dsICM =  new DKDatastoreICM();//创建新连接
				dsICM.connect(CMConstant.RMN,CMConstant.CMUser,CMConstant.CMPwd,"");
			}else{
				dsICM = CMConnectionPool.getConnection(CMConstant.CMUser, CMConstant.CMPwd);
			}
			
			DKDatastoreDefICM dsDefICM = (DKDatastoreDefICM) dsICM.datastoreDef(); 
			DKItemTypeDefICM itemType = (DKItemTypeDefICM) dsDefICM.retrieveEntity(itemTypeName); 
			dkAttrDef attrDef = itemType.retrieveAttr(attrName);
			itemType.removeAttr(attrDef.getName());
			itemType.update();
			log.info("为项类型："+itemTypeName+"，移除文档项类型属性："+attrName+"，成功！");
		}catch(Exception e){
			throw e;
		}finally {
			try {
				// 如果有到CM的连接对象，要返回该连接对象到连接池
				if (dsICM != null) {
					log.warn("返回/销毁CM连接sessionID:["+dsICM.getSessionId()+"]");	
					if(CMConstant.isCMPool.equals("0")){
						dsICM.disconnect();
						dsICM.destroy();
						dsICM = null;
					}else{
						CMConnectionPool.returnConnection(dsICM);
					}
				}
			} catch (Exception ex) {
				log.error("销毁/返回CM连接到连接池异常!", ex);
			}
		}
	}
	
	/**
	 * 创建文档项类型属性
	 * @param dsICM
	 * @param attrName
	 * @return
	 * @throws DKException
	 * @throws Exception
	 */
    public  DKAttrDefICM defineAttribute(DKDatastoreICM dsICM, String attrName,int len) throws DKException,Exception{
    	DKAttrDefICM attr = new DKAttrDefICM(dsICM); 
    	try{
            // maximum 15 characters
            attr.setName(attrName);
            attr.setDescription(attrName);
            attr.setType(DKConstant.DK_CM_VARCHAR);
            attr.setSize(len);
            attr.add();
            log.info("创建文档项类型属性："+attrName+"，成功！");
            return attr;
    	}catch(DKException e){
    		log.error("创建文档项类型属性："+attrName+"，失败！Error：",e);
    		throw new DKException("创建文档项类型属性失败");
    	}catch(Exception e){
    		log.error("创建文档项类型属性："+attrName+"，失败！Error：",e);
    		throw new Exception("创建文档项类型属性失败");
    	}
    }
    
}
