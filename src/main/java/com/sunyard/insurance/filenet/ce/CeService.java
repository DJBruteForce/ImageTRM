package com.sunyard.insurance.filenet.ce;

import java.util.List;
import java.util.Map;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.filenet.api.property.Properties;

public interface CeService {

	/**
	 * 
	 *@Description 
	 *
	 *@param docClass
	 *@param docTitle
	 *@param contentList
	 *@param docProps
	 *@param parentFolderId
	 *@return
	 */
	Document createDocument(String docClass, String docTitle, ContentElementList contentList, Map<String, Object> docProps, String parentFolderId);

	/**
	 * 
	 *@Description 
	 *
	 *@param folderName
	 *@param parentFolderId
	 *@param folderClass
	 *@param fldProps
	 *@return
	 */
	Folder createFolder(String folderName, String parentFolderId, String folderClass, Map<String, Object> fldProps);
	/**
	 * 
	 *@Description 
	 *
	 *@param documentId
	 *@param contentList
	 *@param docProps
	 */
	void updateDocument(String documentId, ContentElementList contentList, Map<String, Object> docProps);
	/**
	 * 
	 *@Description 
	 *
	 *@param folderId
	 *@param fldProps
	 */
	void updateFolder(String folderId, Map<String, Object> fldProps);
	/**
	 * 
	 *@Description 
	 *
	 *@param documentId
	 */
	void deleteDocument(String documentId);
	/**
	 * 
	 *@Description 
	 *
	 *@param folderId
	 */
	void deleteFolder(String folderId);
	/**
	 * 
	 *@Description 
	 *
	 *@param headId
	 *@param tailId
	 *@return
	 */
	boolean existsRelationship(String headId, String tailId);
	/**
	 * 
	 *@Description 
	 *
	 *@param documentId
	 *@return
	 */
	Document fetchDocument(String documentId);
	/**
	 * 
	 *@Description 
	 *
	 *@param folderId
	 *@return
	 */
	Folder fetchFolder(String folderId);
	/**
	 * 
	 *@Description 
	 *
	 *@param folderPath
	 *@return
	 */
	Folder fetchFolderByPath(String folderPath);
	/**
	 * 
	 *@Description 
	 *
	 *@param sql
	 *@return
	 */
	List<Folder> searchFolders(String sql);
	/**
	 * 
	 *@Description 
	 *
	 *@param sql
	 *@return
	 */
	List<Document> searchDocuments(String sql);
	/**
	 * 
	 *@Description 
	 *
	 *@param sql
	 *@return
	 */
	List<Properties> searchProperties(String sql);
	/**
	 * 
	 *@Description 
	 *
	 *@param parentClassName
	 *@param subClassName
	 */
	void createClass(String parentClassName, String subClassName);
	/**
	 * 
	 *@Description 
	 *
	 *@param className
	 *@param propName
	 */
	void createProperty(String className, String propName);
	
}
