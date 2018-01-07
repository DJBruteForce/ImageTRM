package com.sunyard.filenet.ce;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;
import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.RepositoryRowSet;
import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.constants.ReservationType;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentElement;
import com.filenet.api.core.ContentReference;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.UserContext;
import com.sunyard.filenet.ce.bean.CEContent;
import com.sunyard.filenet.ce.bean.CEContentSrcType;
import com.sunyard.filenet.ce.bean.CEDocument;
import com.sunyard.filenet.ce.bean.CeConnectBean;

public class FileNetCeUtils {
	
	//创建CE用户上下文
	public void createUserContext(Connection conn, String username, String password, String jaas) {
		Subject subject = UserContext.createSubject(conn,username, password, jaas);
		UserContext uc = UserContext.get();
		uc.pushSubject(subject);
	}
	
	//将当前登录用户的主题对象从用户上下文中清除掉
	public void authOutCEngine() {
		UserContext.get().popSubject();
	}
	
	//获取domain对象
	public Domain getCEDomain(Connection conn, String domainName, PropertyFilter filter) {
		Domain domain = Factory.Domain.fetchInstance(conn, domainName, filter);
		return domain;
	}
	
	//获取ObjStore对象
	public ObjectStore getObjStore(Domain domain, String osName, PropertyFilter filter) {
		ObjectStore objStore = Factory.ObjectStore.fetchInstance(domain,osName, filter);
		return objStore;
	}
	
	//获取Folder对象
	public Folder getCEFolder(ObjectStore objectStore, String path, PropertyFilter filter) {
		Folder folder = Factory.Folder.fetchInstance(objectStore, path, filter);
		return folder;
	}
	
	//创建一个Folder对象
	public Folder createCEFolder(ObjectStore objectStore, String folderId) {
		Folder folder = Factory.Folder.createInstance(objectStore, folderId);
		return folder;
	}
	
	/**
	 * 查询CE的属性
	 * @param ceConn CE连接信息
	 * @param sql 组织好的SQL语句
	 * @return
	 */
	public ArrayList<Properties> searchCEProperties(CeConnectBean ceConn, String sql) {
		ArrayList<Properties> PropertyList = new ArrayList<Properties>();
		Connection conn = null;
		try {
			conn = Factory.Connection.getConnection(ceConn.getUri());
			if (conn != null) {
				createUserContext(conn, ceConn.getUsername(), ceConn.getPassword(), ceConn.getJaas());
			}
			Domain domain = getCEDomain(conn, ceConn.getDomainName(), null);
			ObjectStore objStore = getObjStore(domain, ceConn.getObjectStoreName(), null);
			SearchScope sScope = new SearchScope(objStore);
			SearchSQL sSQL = new SearchSQL(sql);
			RepositoryRowSet rowSet = sScope.fetchRows(sSQL, 50, null, Boolean.valueOf(true));
			for (java.util.Iterator<?> iter = rowSet.iterator(); iter.hasNext();) {
				RepositoryRow row = (RepositoryRow) iter.next();
				Properties prop = row.getProperties();
				//将查询到的prop放入list
				PropertyList.add(prop);
			}
		} finally {
			if (conn != null) {
				authOutCEngine();
			}
		}
		return PropertyList;
	}
	
	
	/**
	 * 创建CEDocment
	 * @param ceConn
	 * @param ceDoc
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void createCEDocument(CeConnectBean ceConn, CEDocument ceDoc) throws Exception {
		Connection conn = null;
		Document document = null;
		Folder subFolder = null;
		ReferentialContainmentRelationship relationship = null;
		try {
			conn = Factory.Connection.getConnection(ceConn.getUri());
			if (conn != null) {
				createUserContext(conn, ceConn.getUsername(), ceConn.getPassword(), ceConn.getJaas());
			}
			Domain domain = getCEDomain(conn, ceConn.getDomainName(), null);
			ObjectStore objStore = getObjStore(domain, ceConn.getObjectStoreName(), null);
			
			ContentElementList contentList = Factory.ContentElement.createList();
			List<CEContent> list = ceDoc.getContentList();
			for (int i = 0; i < list.size(); i++) {
				CEContent ceContent = list.get(i);
				if (CEContentSrcType.TRANSFER.equals(ceContent.getSourceType())) {//转换
					ContentTransfer cTransfer = Factory.ContentTransfer.createInstance();
					cTransfer.setCaptureSource(ceContent.getContentInput());
					cTransfer.set_ContentType(ceContent.getContentType());
					cTransfer.set_RetrievalName(ceContent.getContentName());
					contentList.add(cTransfer);
				} else if (CEContentSrcType.REFERENCE.equals(ceContent.getSourceType())) {//参考
					ContentReference cReference = Factory.ContentReference.createInstance();
					cReference.set_ContentLocation(ceContent.getContentLocation());
					cReference.set_ContentType(ceContent.getContentType());
					contentList.add(cReference);
				}
			}

			document = Factory.Document.createInstance(objStore, ceDoc.getDocClass());
			document.set_ContentElements(contentList);
			document.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY,	CheckinType.MAJOR_VERSION);//检入

			Properties properties = document.getProperties();//获取属性
			java.util.Map<String, Object> props = ceDoc.getProperties();
			for (String key : props.keySet()) {
				Object value = props.get(key);
				properties.putObjectValue(key, value);
			}
			document.set_MimeType(ceDoc.getMimeType());
			document.save(RefreshMode.REFRESH);

			Folder folder = getCEFolder(objStore, ceDoc.getMainFolder(), null);
			subFolder = folder.createSubFolder(ceDoc.getSubFolder());
			subFolder.save(RefreshMode.REFRESH);

			relationship = subFolder.file(document, AutoUniqueName.AUTO_UNIQUE,
//					String.valueOf(ceDoc.getProperties().get(CEConstant.DOC_TITLE)),
					ceDoc.getSubFolder(),
					DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
			relationship.save(RefreshMode.REFRESH);
		} catch (Exception e) {
			if (relationship != null) {
				relationship.delete();
				relationship = null;
			}
			if (subFolder != null) {
				subFolder.delete();
				subFolder = null;
			}
			if (document != null) {
				document.delete();
				document = null;
			}
			throw e;
		} finally {
			if (conn != null) {
				authOutCEngine();
			}
		}
	}
	
	/**
	 * 查询CEDocment
	 * @param ceConn
	 * @param sql
	 * @return
	 */
	public java.util.List<CEDocument> searchCEDocuments(CeConnectBean ceConn, String sql) {
		java.util.List<CEDocument> ceDocList = new java.util.ArrayList<CEDocument>();
		Connection conn = null;
		try {
			conn = Factory.Connection.getConnection(ceConn.getUri());
			if (conn != null) {
				createUserContext(conn, ceConn.getUsername(), ceConn.getPassword(), ceConn.getJaas());
			}
			Domain domain = getCEDomain(conn, ceConn.getDomainName(), null);
			ObjectStore objStore = getObjStore(domain, ceConn.getObjectStoreName(), null);
			SearchScope sScope = new SearchScope(objStore);
			SearchSQL sSQL = new SearchSQL(sql);
			DocumentSet documents = (DocumentSet) sScope.fetchObjects(sSQL, Integer.getInteger("50"), null, Boolean.valueOf(true));
			for (java.util.Iterator<?> iter = documents.iterator(); iter.hasNext();) {
				Document document = (Document) iter.next();
				CEDocument ceDoc = new CEDocument();
				ContentElementList contentList = document.get_ContentElements();
				for (int i = 0; i < contentList.size(); i++) {
					ContentElement content = (ContentElement) contentList.get(i);
					CEContent ceContent = null;
					if (content != null && content instanceof ContentTransfer) {
						ContentTransfer cTransfer = (ContentTransfer) content;
						String contentName = cTransfer.get_RetrievalName();
						String contentType = cTransfer.get_ContentType();
						java.io.InputStream contentInput = cTransfer.accessContentStream();
//						java.io.InputStream contentInput = document.accessContentStream(i);
						ceContent = new CEContent(CEContentSrcType.TRANSFER, contentName, contentType, contentInput);
					} else if (content != null && content instanceof ContentReference) {
						ContentReference cReference = (ContentReference) content;
						String contentType = cReference.get_ContentType();
						String contentLocation = cReference.get_ContentLocation();
						ceContent = new CEContent(CEContentSrcType.REFERENCE, contentType, contentLocation);
					}
					ceDoc.getContentList().add(ceContent);
				}
				Properties properties = document.getProperties();
				for (java.util.Iterator<?> it = properties.iterator(); it.hasNext();) {
					Property property = (Property) it.next();
					String key = property.getPropertyName();
					Object value = property.getObjectValue();
					ceDoc.getProperties().put(key, value);
				}
				ceDocList.add(ceDoc);
			}
		} finally {
			if (conn != null) {
				authOutCEngine();
			}
		}
		return ceDocList;
	}
	
	/**
	 * 修改CEDocment
	 * @param ceConn
	 * @param ceDoc
	 * @param sql
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void modifyCEDocument(CeConnectBean ceConn, CEDocument ceDoc, String sql) throws Exception {
		Connection conn = null;
		Document document = null;
		Document reservation = null;
		try {
			conn = Factory.Connection.getConnection(ceConn.getUri());
			if (conn != null) {
				createUserContext(conn, ceConn.getUsername(), ceConn.getPassword(), ceConn.getJaas());
			}
			Domain domain = getCEDomain(conn, ceConn.getDomainName(), null);
			ObjectStore objStore = getObjStore(domain, ceConn.getObjectStoreName(), null);
			
			SearchScope sScope = new SearchScope(objStore);
			SearchSQL sSQL = new SearchSQL(sql);
			DocumentSet documents = (DocumentSet) sScope.fetchObjects(sSQL, Integer.getInteger("50"), null, Boolean.valueOf(true));
			for (java.util.Iterator<?> it = documents.iterator(); it.hasNext();) {
				document = (Document) it.next();
				//-----------------------------------
				Boolean isCheckOut = document.get_IsReserved();
				if (isCheckOut != null && !isCheckOut) {
					document.checkout(ReservationType.EXCLUSIVE, null, null, null);
					document.save(RefreshMode.NO_REFRESH);
				}
				//-----------------------------------
				java.util.Map<String, Object> props = ceDoc.getProperties();// 属性
				reservation = (Document) document.get_Reservation();//获取检出的document
				Properties properties = document.getProperties();//值
					for (String key : props.keySet()) {
						Object value = props.get(key);
						properties.putObjectValue(key, value);
					}
				ContentElementList contentList = Factory.ContentElement.createList();
				java.util.List<CEContent> list = ceDoc.getContentList();
				for (int i = 0; i < list.size(); i++) {
					CEContent ceContent = list.get(i);
					if (CEContentSrcType.TRANSFER.equals(ceContent.getSourceType())) {//转换
						ContentTransfer cTransfer = Factory.ContentTransfer.createInstance();
						cTransfer.setCaptureSource(ceContent.getContentInput());
						cTransfer.set_ContentType(ceContent.getContentType());
						cTransfer.set_RetrievalName(ceContent.getContentName());
						contentList.add(cTransfer);
					} else if (CEContentSrcType.REFERENCE.equals(ceContent.getSourceType())) {//参考
						ContentReference cReference = Factory.ContentReference.createInstance();
						cReference.set_ContentLocation(ceContent.getContentLocation());
						cReference.set_ContentType(ceContent.getContentType());
						contentList.add(cReference);
					}
				}
				reservation.set_ContentElements(contentList);
				reservation.set_MimeType(ceDoc.getMimeType());//设置document的类型
				reservation.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);//检入
				reservation.save(RefreshMode.REFRESH);//保存
			}
		} catch (Exception e) {
			try {
				if (document != null) {
					Document cancel = (Document) document.cancelCheckout();//取消检出
					cancel.save(RefreshMode.REFRESH);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			if (reservation != null) {
				reservation.delete();
				reservation = null;
			}
			if (document != null) {
				document.delete();
				document = null;
			}
			throw e;
		} finally {
			if (conn != null) {
				authOutCEngine();
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
