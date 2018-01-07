package com.sunyard.insurance.filenet.ce.common.constant;

import com.sunyard.insurance.filenet.ce.util.StringUtil;

public class CeMsg {

	public static final String GET_INNER_VERSION_ERR = "Get batch's inner version failded, sql=$xxx.";

	public static final String UPDATE_BATCH_ERR = "Update batch failded, batchId=$xxx, batchFolder=$xxx.";
	
	public static final String CREATE_BATCH_ERR = "Create batch failded, batchId=$xxx, batchFolder=$xxx.";
	
	public static final String QUERY_BATCH_NOT_EXISTS_MSG = "Query batch is not exists, tempDir=$xxx, sql=$xxx.";
	
	public static final String QUERY_BATCH_RESULT_OVERFLOW_MSG = "Query batch's result overflow, list's size=$xxx, limit size=$xxx, sql=$xxx.";
	
	public static final String QUERY_BATCH_ERR = "Query batch failded, tempDir=$xxx, sql=$xxx.";
	
	public static final String QUERY_BATCH_WITHOUT_DOC_MSG = "Query batch failded, batch without document, sql=$xxx.";
	
	public static final String EXISTS_BATCH_ERR = "Exists batch failded, sql=$xxx.";
	
	public static final String LINK_BATCH_ERR = "Link batch failded.";
	
	public static final String CREATE_FOLDER_CLASS_MSG = "Create folder class success, parentCls=$xxx, subClass=$xxx.";
	
	public static final String CREATE_FOLDER_PROPERT_MSG = "Create folder's property success, className=$xxx, propName=$xxx.";
	
	public static final String OPERATE_EXHAUST_TIME_MSG = "Sql: $xxx , operation: $xxx, exhaust: $xxx ms.";
	
	public static final String DELETE_FOLDER_CLASS_MSG = "Delete folder class success, className=$xxx.";
	
	public static final String DELETE_FOLDER_PROPERTY_MSG = "Delete folder's property success, className=$xxx, propName=$xxx.";
	
	public static final String getMessage(String message, Object... args) {
		return StringUtil.getMessage(message, CeConstant.MARK, args);
	}
	
}
