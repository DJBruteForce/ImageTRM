package com.sunyard.insurance.filenet.ce.proxy;

import java.util.Iterator;
import java.util.Map;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.FolderSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.Document;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.core.UpdatingBatch;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;

public class CeBatchProxy extends CeProxy {

	private static ThreadLocal<CeBatchProxy> authorizations = new ThreadLocal<CeBatchProxy>();
	
	private UpdatingBatch batch = null;
	
	protected CeBatchProxy() {
		super();
		batch = createUpdatingBatch();
	}
	
	protected CeBatchProxy(String objectStoreName) {
		super(objectStoreName);
		batch = createUpdatingBatch();
	}
	public static CeBatchProxy authIn() {
		CeBatchProxy proxy = authorizations.get();
		if (proxy == null) {
			proxy = new CeBatchProxy();
			authorizations.set(proxy);
		}
		return proxy;
	}
	
	public static CeBatchProxy authIn(String objectStoreName) {
		CeBatchProxy proxy = authorizations.get();
		if (proxy == null) {
			proxy = new CeBatchProxy(objectStoreName);
			authorizations.set(proxy);
		}
		return proxy;
	}
	
	public static void authOut() {
		CeBatchProxy proxy = authorizations.get();
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
			document.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY,CheckinType.MAJOR_VERSION);//检入

			Folder parentFolder = fetchFolder(os, parentFolderId, null);
			rcr = parentFolder.file(document, AutoUniqueName.AUTO_UNIQUE, docTitle, DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
		} catch (Exception e) {
			try {
				if (rcr != null) {
					rcr.delete();
				}
			} catch (Exception ex) {
			}
			try {
				if (document != null) {
					document.delete();
				}
			} catch (Exception ex) {
			}
			if (e instanceof EngineRuntimeException) {
				throw (EngineRuntimeException) e;
			} else {
				throw new EngineRuntimeException(e, ExceptionCode.E_UNEXPECTED,	null);
			}
		} finally {
			if (document != null) {
				batch.add(document, null);
			}
			if (rcr != null) {
				batch.add(rcr, null);
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
	public Folder createFolder(String folderName, String parentFolderId,
			String folderClass, Map<String, Object> fldProps) {
		Folder folder = null;
		try {
			Folder parentFolder = fetchFolder(os, parentFolderId, null);
			folder = createFolder(os, folderName, parentFolder,
					folderClass);
			if (fldProps != null && !fldProps.isEmpty()) {
				updateProperties(folder, fldProps);
			}
		} catch (Exception e) {
			try {
				if (folder != null) {
					folder.delete();
				}
			} catch (Exception ex) {
			}
			if (e instanceof EngineRuntimeException) {
				throw (EngineRuntimeException) e;
			} else {
				throw new EngineRuntimeException(e, ExceptionCode.E_UNEXPECTED,	null);
			}
		} finally {
			if (folder != null) {
				batch.add(folder, null);
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
	public void updateDocument(String documentId,
			ContentElementList contentList, Map<String, Object> docProps) {
		Document document = null;
		Document reservation = null;
		try {
			document = fetchDocument(os, documentId, null);
			reservation = getReservation(document);
			if (contentList != null && !contentList.isEmpty()) {
				if (reservation != null) {
					reservation.set_ContentElements(contentList);
				}
			}
			document = (reservation != null) ? reservation : document;
			if (docProps != null && !docProps.isEmpty()) {
				updateProperties(document, docProps);
			}
			if (reservation != null) {
				reservation.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);//检入
			}
		} catch (Exception e) {
			if (e instanceof EngineRuntimeException) {
				throw (EngineRuntimeException) e;
			} else {
				throw new EngineRuntimeException(e, ExceptionCode.E_UNEXPECTED, null);
			}
		} finally {
			if (reservation != null) {
				batch.add(reservation, null);
			} else if (document != null) {
				batch.add(document, null);
			}
		}
	}
	/**
	 * 
	 *@param folderId
	 *@param fldProps
	 *@Description
	 *
	 */
	public void updateFolder(String folderId, Map<String, Object> fldProps) {
		if (fldProps != null && !fldProps.isEmpty()) {
			Folder folder = fetchFolder(os, folderId, null);
			updateProperties(folder, fldProps);
			batch.add(folder, null);
		}
	}
	/**
	 * 
	 *@param documentId
	 *@Description
	 *
	 */
	public void deleteDocument(String documentId) {
		Document document = fetchDocument(os, documentId, null);
		if (document != null) {
			deleteDocument(document);
		}
	}
	/**
	 * 
	 *@param folderId
	 *@Description
	 *
	 */
	public void deleteFolder(String folderId) {
		Folder folder = fetchFolder(os, folderId, null);
		if (folder != null) {
			deleteFolder(folder);
		}
	}
	/**
	 * 
	 *@Description 
	 *
	 *@param documentId
	 */
	public void checkOut(String documentId) {
		Document document = fetchDocument(os, documentId, null);
		Boolean isCheckOut = isCheckOut(document);
		if (isCheckOut != null && !isCheckOut) {
			document.checkout(ReservationType.EXCLUSIVE, null, null, null);
			document.save(RefreshMode.NO_REFRESH);
		}
	}
	/**
	 * 
	 *@Description 
	 *取消检出
	 *@param documentId
	 */
	public void cancelCheckOut(String documentId) {
		Document document = fetchDocument(os, documentId, null);
		Document reservation = getReservation(document);
		if (reservation != null) {
			reservation.delete();
			reservation.save(RefreshMode.NO_REFRESH);
		}
	}

	public void updateBatch() {
		batch.updateBatch();
	}
	/**
	 * 
	 *@Description 
	 *
	 *@return
	 */
	protected UpdatingBatch createUpdatingBatch() {
		return UpdatingBatch.createUpdatingBatchInstance(os.get_Domain(), RefreshMode.REFRESH);
	}
	/**
	 * 
	 *@param document
	 *@Description
	 *
	 */
	protected void deleteDocument(Document document) {
		VersionSeries versionSeries = document.get_VersionSeries();
		versionSeries.delete();
		batch.add(versionSeries, null);
	}
	
	/**
	 * 
	 *@param folder
	 *@Description
	 *
	 */
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
		batch.add(folder, null);
	}
	
}
