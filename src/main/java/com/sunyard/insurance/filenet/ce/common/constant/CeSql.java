package com.sunyard.insurance.filenet.ce.common.constant;

public class CeSql {

	public static final String QUERY_INNER_VER = "SELECT TOP 1 f.[INNER_VER] FROM Folder f WITH INCLUDESUBCLASSES WHERE f.[BATCH_ID]='$xxx'";
	
	public static final String QUERY_BATCH_FOLDER = "SELECT TOP 1 f.[This], f.[Id] FROM Folder f WITH INCLUDESUBCLASSES WHERE f.[BATCH_ID]='$xxx'";
	
	public static final String QUERY_DOCUMENT = "SELECT TOP 1 d.[This], d.[Id] FROM Document d WITH INCLUDESUBCLASSES WHERE d.[IsCurrentVersion]=TRUE AND d.[DocumentTitle]='$xxx'";
	
	public static final String QUERY_RELATIONSHIP = "SELECT TOP 1 r.[Id] FROM ReferentialContainmentRelationship r WITH INCLUDESUBCLASSES WHERE (r.[Head] = Object($xxx)) AND (r.[Tail] = Object($xxx))";
	
	public static final String QUERY_BATCHS = "SELECT TOP 100 f.[This], f.[Id], f.[BATCH_ID], f.[INNER_VER], f.[ContainedDocuments] FROM Folder f WITH INCLUDESUBCLASSES";
	
	public static final String QUERY_SOURCE_IMGS = "SELECT d.[This], d.[Id], d.[Name], d.[DocumentTitle], d.[ContentElements] FROM Document d WITH INCLUDESUBCLASSES INNER JOIN Folder f WITH INCLUDESUBCLASSES ON d.[BUSI_NUM]=f.[BUSI_NUM] WHERE f.[APP_CODE]='$xxx' AND f.[BUSI_NUM]='$xxx' AND d.[IMG_TYPE]='$xxx'";
	
	public static final String QUERY_BATCH_ID = "SELECT TOP 1 f.[BATCH_ID] FROM Folder f WITH INCLUDESUBCLASSES WHERE f.[APP_CODE]='$xxx' AND f.[BUSI_NUM]='$xxx'";
	
	public static final String QUERY_TARGET_BATCH = "SELECT TOP 1 f.[This], f.[Id], f.[BATCH_ID] FROM Folder f WITH INCLUDESUBCLASSES WHERE f.[APP_CODE]='$xxx' AND f.[BUSI_NUM]='$xxx'";
	
	public static final String QUERY_NEWEST_SYD = "SELECT TOP 1 d.[This], d.[Id], d.[Name], d.[DocumentTitle], d.[ContentElements] FROM Document d WITH INCLUDESUBCLASSES WHERE d.[IsCurrentVersion]=TRUE AND d.[DocumentTitle] LIKE '$xxx_%.syd' ORDER BY d.[DocumentTitle] DESC";
	
}
