package com.sunyard.insurance.filenet.ce.proxy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyTemplate;
import com.filenet.api.admin.PropertyTemplateString;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.FolderSet;
import com.filenet.api.collection.IndependentObjectSet;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;
import com.filenet.api.property.Properties;
import com.filenet.api.query.RepositoryRow;
import com.sunyard.insurance.filenet.ce.CeService;
import com.sunyard.insurance.filenet.ce.FnConfigOptions;
import com.sunyard.insurance.filenet.ce.FnConfiguration;
import com.sunyard.insurance.filenet.ce.base.CeQuery;
import com.sunyard.insurance.filenet.ce.base.CeSupport;
import com.sunyard.insurance.filenet.ce.common.constant.CeMsg;
import com.sunyard.insurance.filenet.ce.common.constant.CeSql;

public class CeProxy extends CeSupport implements CeService {

	private static ThreadLocal<CeProxy> authorizations = new ThreadLocal<CeProxy>();
	protected CeQuery query = null;
	static {
		FnConfiguration.configurate();
	}
	protected CeProxy() {
		super(FnConfigOptions.getContentEngineUrl(), FnConfigOptions.getUsername(), FnConfigOptions.getPassword(), FnConfigOptions.getObjectStoreName(), FnConfigOptions.getHistoryObjectStore());
		query = new CeQuery(1, false);
	}
	/**
	 * 构造方法
	 * @param ObjectStoreName 指定的Object Store Name
	 */
	protected CeProxy(String objectStoreName) {
		super(FnConfigOptions.getContentEngineUrl(), FnConfigOptions.getUsername(), FnConfigOptions.getPassword(), objectStoreName, FnConfigOptions.getHistoryObjectStore());
		query = new CeQuery(1, false);
	}
	/**
	 * 
	 *@Description 
	 *登录
	 *@return
	 */
	public static CeProxy authIn() {
		CeProxy proxy = authorizations.get();
		if (proxy == null) {
			proxy = new CeProxy();
			authorizations.set(proxy);
		}
		return proxy;
	}
	/**
	 * 登录
	 * @param ObjectStoreName 指定要操作的Object Store名
	 * @return 操作指定Object Store名的CE代理对象
	 */
	public static CeProxy authIn(String objectStoreName) {
		CeProxy proxy = authorizations.get();
		if (proxy == null) {
			proxy = new CeProxy(objectStoreName);
			authorizations.set(proxy);
		}
		return proxy;
	}
	/**
	 * 
	 *@Description 
	 *登出
	 */
	public static void authOut() {
		CeProxy proxy = authorizations.get();
		if (proxy != null) {
			proxy.popSubject();
			authorizations.set(null);
		}
		proxy = null;
	}
	/**
	 * 创建document
	 *@param docClass
	 *@param docTitle
	 *@param contentList
	 *@param docProps
	 *@param parentFolderId
	 *@return
	 *@Description
	 *
	 */
	public Document createDocument(String docClass, String docTitle,
			ContentElementList contentList, Map<String, Object> docProps, String parentFolderId) {
		Document document = null;
		ReferentialContainmentRelationship rcr = null;
		try {
			document = createDocument(os, docClass, docTitle);
			if (docProps != null && !docProps.isEmpty()) {
				updateProperties(document, docProps);
			}
			if (contentList != null && !contentList.isEmpty()) {
				document.set_ContentElements(contentList);
			}
			document.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);//检入
			document.save(RefreshMode.REFRESH);
			Folder parentFolder = fetchFolder(os, parentFolderId, null);
			rcr = parentFolder.file(document, AutoUniqueName.AUTO_UNIQUE, document.get_Name(), DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
			rcr.save(RefreshMode.NO_REFRESH);
		} catch (Exception e) {
			try {
				if (rcr != null) {
					rcr.delete();
					rcr.save(RefreshMode.NO_REFRESH);
				}
			} catch (Exception ex) {
			}
			try {
				if (document != null) {
					document.delete();
					document.save(RefreshMode.NO_REFRESH);
				}
			} catch (Exception ex) {
			}
			if (e instanceof EngineRuntimeException) {
				throw (EngineRuntimeException) e;
			} else {
				throw new EngineRuntimeException(e, ExceptionCode.E_UNEXPECTED,	null);
			}
		}
		return document;
	}
	/**
	 * 创建folder
	 *@param folderName
	 *@param parentFolderId
	 *@param folderClass
	 *@param fldProps
	 *@return
	 *@Description
	 *
	 */
	public Folder createFolder(String folderName, String parentFolderId, String folderClass, Map<String, Object> fldProps) {
		Folder folder = null;
		try {
			Folder parentFolder = fetchFolder(os, parentFolderId, null);
			folder = createFolder(os, folderName, parentFolder,	folderClass);
			if (fldProps != null && !fldProps.isEmpty()) {
				updateProperties(folder, fldProps);
			}
			folder.save(RefreshMode.REFRESH);
		} catch (Exception e) {
			try {
				if (folder != null) {
					folder.delete();
					folder.save(RefreshMode.NO_REFRESH);
				}
			} catch (Exception ex) {
			}
			if (e instanceof EngineRuntimeException) {
				throw (EngineRuntimeException) e;
			} else {
				throw new EngineRuntimeException(e, ExceptionCode.E_UNEXPECTED,	null);
			}
		}
			return folder;
	}
	/**
	 * 更新document
	 *@param documentId
	 *@param contentList
	 *@param docProps
	 *@Description
	 *
	 */
	public void updateDocument(String documentId, ContentElementList contentList, Map<String, Object> docProps) {
		Document document = null;
		Document reservation = null;
		try {
			document = fetchDocument(os, documentId, null);
			if (contentList != null && !contentList.isEmpty()) {
				Boolean isCheckOut = isCheckOut(document);
				if (isCheckOut != null && !isCheckOut) {
					document.checkout(ReservationType.EXCLUSIVE, null, null, null);
					document.save(RefreshMode.NO_REFRESH);
				}
				document = fetchDocument(os, documentId, null);
				reservation = getReservation(document);
				if (reservation != null) {
					reservation.set_ContentElements(contentList);
					reservation.save(RefreshMode.REFRESH);
				}
			}
			document = (reservation != null) ? reservation : document;
			if (docProps != null && !docProps.isEmpty()) {
				updateProperties(document, docProps);
				document.save(RefreshMode.REFRESH);
			}
			if (reservation != null) {
				reservation.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY,CheckinType.MAJOR_VERSION);
				reservation.save(RefreshMode.REFRESH);
			}
		} catch (Exception e) {
			try {
				if (reservation != null) {
					reservation.delete();
					reservation.save(RefreshMode.NO_REFRESH);
				}
			} catch (Exception ex) {
			}
			if (e instanceof EngineRuntimeException) {
				throw (EngineRuntimeException) e;
			} else {
				throw new EngineRuntimeException(e, ExceptionCode.E_UNEXPECTED, null);
			}
		}
	}
	
	public void updateFolder(String folderId, Map<String, Object> fldProps) {
		if (fldProps != null && !fldProps.isEmpty()) {
			Folder folder = fetchFolder(os, folderId, null);
			updateProperties(folder, fldProps);
			folder.save(RefreshMode.REFRESH);
		}
	}
	
	public void deleteDocument(String documentId) {
		Document document = fetchDocument(os, documentId, null);
		if (document != null) {
			deleteDocument(document);
		}
	}
	
	public void deleteFolder(String folderId) {
		Folder folder = fetchFolder(os, folderId, null);
		if (folder != null) {
			deleteFolder(folder);
		}
	}
	
	public boolean existsRelationship(String headId, String tailId) {
		String sql = CeMsg.getMessage(CeSql.QUERY_RELATIONSHIP, headId, tailId);
		RepositoryRowSet rowSet = query.searchRows(sql, os);
		if (rowSet != null && !rowSet.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public Document fetchDocument(String documentId) {
		Document document = null;
		try {
			document = fetchDocument(os, documentId, null);
		} catch (EngineRuntimeException e) {
			if (!e.getExceptionCode().equals(ExceptionCode.E_OBJECT_NOT_FOUND))
				throw e;
		}
			return document;
	}
	
	public Folder fetchFolder(String folderId) {
		Folder folder = null;
		try {
			folder = fetchFolder(os, folderId, null);
		} catch (EngineRuntimeException e) {
			if (!e.getExceptionCode().equals(ExceptionCode.E_OBJECT_NOT_FOUND))
				throw e;
		}
		return folder;
	}
	
	public Folder fetchFolderByPath(String folderPath) {
		Folder folder = null;
		try {
			folder = fetchFolderByPath(os, folderPath, null);
		} catch (EngineRuntimeException e) {
			if (!e.getExceptionCode().equals(ExceptionCode.E_OBJECT_NOT_FOUND))
				throw e;
		}
		return folder;
	}
	
	public List<Folder> searchFolders(String sql) {
		List<Folder> list = new ArrayList<Folder>();
		IndependentObjectSet objSet = query.searchObjects(sql, objectStores);
		for (Iterator<?> it = objSet.iterator(); it.hasNext();) {
			Folder fld = (Folder) it.next();
			list.add(fld);
		}
		return list;
	}

	public List<Document> searchDocuments(String sql) {
		List<Document> list = new ArrayList<Document>();
		IndependentObjectSet objSet = query.searchObjects(sql, objectStores);
		for (Iterator<?> it = objSet.iterator(); it.hasNext();) {
			Document doc = (Document) it.next();
			list.add(doc);
		}
		return list;
	}

	public List<Properties> searchProperties(String sql) {
		List<Properties> list = new ArrayList<Properties>();
		RepositoryRowSet rowSet = query.searchRows(sql, objectStores);
		for (Iterator<?> it = rowSet.iterator(); it.hasNext();) {
			RepositoryRow row = (RepositoryRow) it.next();
			Properties properties = row.getProperties();
			list.add(properties);
		}
		return list;
	}
	
	public void linkDocumentToFolder(String documentId, String folderId) {
		ReferentialContainmentRelationship rcr = null;
		try {
			if (existsRelationship(documentId, folderId)) {
				return;
			}
			Folder folder = fetchFolder(os, folderId, null);
			Document document = fetchDocument(os, documentId, null);
			rcr = folder.file(document, AutoUniqueName.AUTO_UNIQUE, document.get_Name(),
					DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
			rcr.save(RefreshMode.NO_REFRESH);
		} catch (Exception e) {
			try {
				if (rcr != null) {
					rcr.delete();
					rcr.save(RefreshMode.NO_REFRESH);
				}
			} catch (Exception ex) {
			}
			if (e instanceof EngineRuntimeException) {
				throw (EngineRuntimeException) e;
			} else {
				throw new EngineRuntimeException(e, ExceptionCode.E_UNEXPECTED,	null);
			}
		}
	}
	
	public void unlinkDocumentFromFolder(String documentId, String folderId) {
		ReferentialContainmentRelationship rcr = null;
		try {
			Folder folder = fetchFolder(os, folderId, null);
			Document document = fetchDocument(os, documentId, null);
			rcr = folder.unfile(document);
			rcr.save(RefreshMode.NO_REFRESH);
		} catch (Exception e) {
			if (e instanceof EngineRuntimeException) {
				EngineRuntimeException ere = (EngineRuntimeException) e;
				if (!ere.getExceptionCode().equals(
						ExceptionCode.API_NOT_FILED_IN_FOLDER)) {
					throw ere;
				}
			} else {
				throw new EngineRuntimeException(e, ExceptionCode.E_UNEXPECTED, null);
			}
		}
	}
	
	public void createClass(String parentClassName, String subClassName) {
		ClassDefinition classDef = null;
		try {
			classDef = createClassDefinition(os, parentClassName, subClassName);
			classDef.save(RefreshMode.NO_REFRESH);
		} catch (Exception e) {
			if (classDef != null) {
				classDef.delete();
				classDef.save(RefreshMode.NO_REFRESH);
			}
			if (e instanceof EngineRuntimeException) {
				throw (EngineRuntimeException) e;
			} else {
				throw new EngineRuntimeException(e, ExceptionCode.E_UNEXPECTED,	null);
			}
		}
	}
	
	public void createProperty(String className, String propName) {
		PropertyTemplateString template = null;
		ClassDefinition classDef = null;
		if (!existsProperty(os, className, propName)) {
			template = createPropertyTemplateString(os, propName);
			template.save(RefreshMode.REFRESH);
			classDef = relatePropertyString(os, template, className);
			classDef.save(RefreshMode.NO_REFRESH);
		}
	}
	
	protected void deleteDocument(Document document) {
		VersionSeries versionSeries = document.get_VersionSeries();
		versionSeries.delete();
		versionSeries.save(RefreshMode.NO_REFRESH);
	}
	
	protected void deleteFolder(Folder folder) {
		FolderSet subFolders = folder.get_SubFolders();
		if (subFolders != null && !subFolders.isEmpty()) {
			for (Iterator<?> it = subFolders.iterator(); it.hasNext();) {
				Folder subFolder = (Folder) it.next();
				deleteFolder(subFolder);
			}
		}
		DocumentSet docSet = folder.get_ContainedDocuments();
		if (docSet != null && !docSet.isEmpty()) {
			for (Iterator<?> it = docSet.iterator(); it.hasNext();) {
				Document document = (Document) it.next();
				deleteDocument(document);
			}
		}
		folder.delete();
		folder.save(RefreshMode.NO_REFRESH);
	}
	/**
	 * 
	 *@Description 
	 *
	 *@param className
	 */
	public void deleteClass(String className) {
		ClassDefinition classDef = fetchClassDefinition(os, className);
		if (classDef != null) {
			classDef.delete();
			classDef.save(RefreshMode.REFRESH);
		}
	}
	
	/**
	 * 
	 *@Description 
	 *
	 *@param className
	 *@param propName
	 */
	public void deleteProperty(String className, String propName) {
		ClassDefinition classDef = fetchClassDefinition(os, className);
		PropertyDefinitionList propDefList = classDef.get_PropertyDefinitions();
		for (int i = 0; i < propDefList.size(); i++) {
			PropertyDefinition propDef = (PropertyDefinition) propDefList.get(i);
			if (propDef.get_DisplayName().equals(propName)) {
				propDefList.remove(propDef);
				classDef.save(RefreshMode.REFRESH);
				PropertyTemplate template = propDef.get_PropertyTemplate();
				template.delete();
				template.save(RefreshMode.REFRESH);
				break;
			}
		}
	}

}
