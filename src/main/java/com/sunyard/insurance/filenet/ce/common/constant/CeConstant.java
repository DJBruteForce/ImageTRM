package com.sunyard.insurance.filenet.ce.common.constant;


public class CeConstant {
	
	public static final String MARK = "$xxx";

	public static final String NOT_ARCHIVE = "0";

	public static final String ARCHIVED = "1";

	public static final String ROOT_FOLDER = "/IMG_FLD";

	public static final String DEFAULT_DOCTYPE = "text/plain";
	// ---------------------------------------------------------------------------------
	public static int maxResultNum = 10;// CM查询最大条数
	// -------CM返回异常信息-----------------------------------------------------------------
	// 输入参数格式错误
	public final static String CM_IMG_CMPROCESS_PARAMETER_FORMAT_ERROR = "-10";
	// 取得到服务器的连接失败
	public final static String CM_IMG_CMPROCESS_CONNECT_TO_SERVER_ERROR = "-20";
	// 返回连接到连接池失败
	public final static String CM_IMG_CMPROCESS_RETURN_CONNECTION_ERROR = "-21";
	// 服务器返回超时
	public final static String CM_IMG_CMPROCESS_SERVER_TIMEOUT_ERROR = "-22";
	// 从服务器接收文件或文件夹失败
	public final static String CM_IMG_CMPROCESS_RECEIVE_FILE_OR_FOLDER_ERROR = "-23";
	// 向服务器发送文件或文件夹失败
	public final static String CM_IMG_CMPROCESS_SEND_FILE_OR_FOLDER_ERROR = "-24";
	// 待上传批次不存在
	public final static String CM_IMG_CMPROCESS_BATCH_NOT_EXIST_ERROR = "-30";
	// 待上传索引文件不存在
	public final static String CM_IMG_CMPROCESS_BATCH_IDX_FILE_NOT_EXIST_ERROR = "-31";
	// 指定批次在服务器上已存在
	public final static String CM_IMG_CMPROCESS_BATCH_ALREADY_EXIST_ERROR = "-32";
	// 替换服务器上的批次索引文件失败
	public final static String CM_IMG_CMPROCESS_REPLACE_BATCH_IDX_FILE_ERROR = "-33";
	// 批次索引文件解析错误
	public final static String CM_IMG_CMPROCESS_BATCH_IDX_FILE_RESOLVE_ERROR = "-34";
	// 待上传影像文件不存在错误
	public final static String CM_IMG_CMPROCESS_BATCH_IMG_FILE_NOT_EXIST_ERROR = "-35";
	// 获取批次失败
	public final static String CM_IMG_CMPROCESS_OBTAIN_BATCH_ERROR = "-40";
	// 从CM中检出批次失败
	public final static String CM_IMG_CMPROCESS_CHECKOUT_BATCH_ERROR = "-41";
	// 本级服务器无此批次，存在上级服务器
	public final static String CM_IMG_CMPROCESS_BATCH_NOT_EXIST_HAS_UPSERVER_ERROR = "-41";
	// 向CM检入批次失败
	public final static String CM_IMG_CMPROCESS_CHECKIN_BATCH_ERROR = "-42";
	// 本级服务器无此批次，不存在上级服务器
	public final static String CM_IMG_CMPROCESS_BATCH_NOT_EXIST_NO_UPSERVER_ERROR = "-42";
	// CM服务器无此批次
	public final static String CM_IMG_CMPROCESS_BATCH_NOT_EXIST_ON_CM_ERROR = "-43";
	// 指定级数的服务器无此批次
	public final static String CM_IMG_CMPROCESS_BATCH_NOT_EXIST_ON_SPECIFIED_SERVER_ERROR = "-43";
	// CM中无符合查询条件的批次
	public final static String CM_IMG_CMPROCESS_BATCHES_NOT_EXIST_ON_CM_ERROR = "-44";
	// 指定批次已被处理
	public final static String CM_IMG_CMPROCESS_BATCH_ALREADY_PROCESSED_ERROR = "-45";
	// 用于查询的属性名在被查询的项类型中不存在
	public final static String CM_IMG_CMPROCESS_ATTRIBUTE_NOT_EXIST_ERROR = "-46";
	// 指定批次不存在文档部件属性
	public final static String CM_IMG_CMPROCESS_DKPART_ATTR_NOT_EXIST_ERROR = "-47";
	// 批次无指定页码号
	public final static String CM_IMG_CMPROCESS_PAGE_NOT_EXIST_ERROR = "-48";
	// 向CM查询批次时出错
	public final static String CM_IMG_CMPROCESS_QUERY_CM_ERROR = "-49";
	// 批次状态不允许本操作
	public final static String CM_IMG_CMPROCESS_BATCH_STATUS_FORBID_CURRENT_OPERATION_ERROR = "-50";
	// 批次状态错误
	public final static String CM_IMG_CMPROCESS_BATCH_STATUS_ERROR = "-51";
	// 业务信息版本已同步
	public final static String CM_IMG_CMPROCESS_BUSINESS_INFO_VER_ALREADY_SYNC_ERROR = "-60";
	// 业务信息文件不存在
	public final static String CM_IMG_CMPROCESS_BUSINESS_INFO_FILE_NOT_EXIST_ERROR = "-61";
	// 解析业务信息文件错误
	public final static String CM_IMG_CMPROCESS_BUSINESS_INFO_FILE_RESOLVE_ERROR = "-62";
	// 业务信息版本同步失败
	public final static String CM_IMG_CMPROCESS_BUSINESS_INFO_SYNC_FAIL_ERROR = "-63";
	// 生成批次索引文件失败
	public final static String CM_IMG_CMPROCESS_BUSINESS_INFO_CREAT_FAIL_ERROR = "-64";
	// 结卷/离线操作失败
	public final static String CM_IMG_CMPROCESS_CLOSEFILE_OR_OFFLINE_FAIL_ERROR = "-65";
	// 抠小图失败
	public final static String CM_IMG_CMPROCESS_CROP_TINY_IMAGE_ERROR = "-66";
	// 记录CM服务器日志到数据库失败
	public final static String CM_IMG_CMPROCESS_INSERT_CMLOG_ERROR = "-67";
	// CM操作错误
	public final static String CM_IMG_CMPROCESS_CM_OPERATION_ERROR = "-70";
	// CM不支持此交易
	public final static String CM_IMG_CMPROCESS_TRANSACTION_NOT_SUPPORTED_ERROR = "-71";
	// 此交易对于指定应用未开启
	public final static String CM_IMG_CMPROCESS_TRANSACTION_DISABLED_FOR_APP = "-72";
	// 查询返回的结果集数量太大
	public final static String CM_IMG_CMPROCESS_QUERY_RESULT_SET_TOO_LARGE = "-73";

	// *******************************************************************************************
	// 创建批次DDO对象时失败
	public final static String CM_IMG_CMPROCESS_CREATEDDO_FAIL_ERRORE = "-100";
	// 给批次属性赋值时失败
	public final static String CM_IMG_CMPROCESS_SETDDO_ATTRIBUTE_FAIL_ERRORE = "-101";
	// 批次DDO对象无文档部件属性！批次DDO对象可能不属于一个Document类型的项类型，或者批次DDO对象的属性未被提取
	public final static String CM_IMG_CMPROCESS_DDO_NO_DOCUMENTPART_ERRORE = "-102";
	// 创建批次文档部件对象时失败
	public final static String CM_IMG_CMPROCESS_CREATEPART_FAIL_ERRORE = "-103";
	// 给影像属性赋值时失败
	public final static String CM_IMG_CMPROCESS_SETPART_ATTRIBUTE_FAIL_ERRORE = "-104";
	// 加载对应的影像文件到文档部件中失败
	public final static String CM_IMG_CMPROCESS_ADD_CONTENT_FAIL_ERRORE = "-105";
	// 创建批次DDO对象时，执行ADD方法失败
	public final static String CM_IMG_CMPROCESS_CREATEDDO_ADD_FAIL_ERRORE = "-106";
	// 执行批次DDO对象UPDATE方法失败
	public final static String CM_IMG_CMPROCESS_CREATEDDO_UPDATE_FAIL_ERRORE = "-107";

	// -------------------------------------------------------------------------------
	// 批次查询为空
	public final static String CM_IMG_CMPORCESS_QUERY_RESULT_ISNULL = "0";
	// 批次查询异常错误
	public final static String CM_IMG_CMPORCESS_QUERY_FAIL_ERROR = "-1";

	// -------------------------------------------------------------------------------
}
