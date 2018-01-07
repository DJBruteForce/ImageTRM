package com.sunyard.insurance.filenet.faf.cesupport;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.collection.FolderSet;
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
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.core.ReferentialContainmentRelationship;
import com.filenet.api.core.VersionSeries;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.query.RepositoryRow;
import com.filenet.api.query.SearchSQL;
import com.filenet.api.query.SearchScope;
import com.filenet.api.util.UserContext;
import com.sunyard.insurance.filenet.faf.cesupport.entity.CEConnBean;
import com.sunyard.insurance.filenet.faf.cesupport.entity.CEContent;
import com.sunyard.insurance.filenet.faf.cesupport.entity.CEContentSrcType;
import com.sunyard.insurance.filenet.faf.cesupport.entity.CEDocument;
import com.sunyard.insurance.filenet.faf.cesupport.util.XMLUtil;

public class CEManager {
	
	private static final Logger loger = Logger.getLogger(CEService.class);
	private static CEManager manager = null;
	private static CEConnBean ceConn = null;
	private CEManager() {
		super();
	}
	
	static {
		init();
	}
	public static CEManager getInstance() throws Exception {
		if (manager == null) {
			manager = new CEManager();
		}
		return manager;
	}
	public static CEConnBean getConnBean() {
		if (ceConn == null) {
			ceConn = new CEConnBean(CEConfig.URL, CEConfig.USERNAME,CEConfig.PASSWORD, "FileNetP8WSI", CEConfig.DOMAIN, CEConfig.OBJECT_STORE);
//			ceConn = new CEConnBean(CEConfig.URL, CEConfig.USERNAME,CEConfig.PASSWORD, null, CEConfig.DOMAIN, CEConfig.OBJECT_STORE);
		}
		return ceConn;
	}
	/**
	 * Sets JVM parameters for login.
	 */
	private static void init() {
		org.dom4j.Document document = null;
		java.io.InputStream in = null;
		try {
			in = CEManager.class.getClassLoader().getResourceAsStream("/com/sunyard/insurance/filenet/faf/config/ceConfig.xml");
			document = new org.dom4j.io.SAXReader().read(in);
		} catch (Exception e) {
			loger.error("读取/com/sunyard/insurance/filenet/faf/config/ceConfig.xml异常!",e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					loger.error("读取/com/sunyard/insurance/filenet/faf/config/ceConfig.xml关闭流异常!",e);
				}
				in = null;
			}
		}
		if (document != null) {
			org.dom4j.Element root = document.getRootElement();
			CEConfig.URL = XMLUtil.getText(root.element("url"));
			CEConfig.DOMAIN = XMLUtil.getText(root.element("domain"));
			CEConfig.USERNAME = XMLUtil.getText(root.element("username"));
			CEConfig.PASSWORD = XMLUtil.getText(root.element("password"));
			CEConfig.OBJECT_STORE = XMLUtil.getText(root.element("object-store"));
			CEConfig.MAIN_FOLDER = XMLUtil.getText(root.element("main-folder"));
			CEConfig.DOCUMENT_CLASS = XMLUtil.getText(root.element("document-class"));
		}
		try {
			// Set the "wasp.location" parameter.
			// Only web services transport need to set this.
			String waspLocation = "/com/sunyard/insurance/filenet/faf/config/p8config";
			java.net.URL waspLocationURL = CEManager.class.getResource(waspLocation);
			String waspLocationPath = toFilePath(waspLocationURL);
			System.setProperty("wasp.location", waspLocationPath);
			// Setup jaas configuration parameter.
			String jaasConfigFile = null;
			if (CEConfig.URL.toLowerCase().startsWith("http")) {
				jaasConfigFile = "/com/sunyard/insurance/filenet/faf/config/p8config/jaas.conf.WSI";
			} else if (CEConfig.URL.toLowerCase().startsWith("t3")) {
				jaasConfigFile = "/com/sunyard/insurance/filenet/faf/config/p8config/jaas.conf.WebLogic";
			} else if (CEConfig.URL.toLowerCase().startsWith("iiop")) {
				jaasConfigFile = "/com/sunyard/insurance/filenet/faf/config/p8config/jaas.conf.WebSphere";
			} else {
				throw new IllegalArgumentException("The ceurl is illegally.");
			}
			java.net.URL jaasConfigURL = CEManager.class.getResource(jaasConfigFile);
            String jaasConfigFilePath = jaasConfigURL.getPath();
			System.setProperty("java.security.auth.login.config", jaasConfigFilePath);
			// Set the CE_URL for authentication.
			System.setProperty("filenet.pe.bootstrap.ceuri", CEConfig.URL);

			System.out.println("[CEManager]wasp.location = " + System.getProperty("wasp.location"));
			System.out.println("[CEManager]java.security.auth.login.config = "	+ System.getProperty("java.security.auth.login.config"));
			System.out.println("[CEManager]filenet.pe.bootstrap.ceuri = " + System.getProperty("filenet.pe.bootstrap.ceuri"));
		} catch (Throwable t) {
			loger.error("CE设置系统变量异常System.setProperty!",t);
		}
	}
	/**
	 * 
	 *@Description 
	 *
	 *@param url
	 *@return
	 */
	private static String toFilePath(java.net.URL url) {
		if (url == null || !url.getProtocol().equals("file")) {
			return null;
		} else {
			String filename = url.getFile().replace('/', java.io.File.separatorChar);
			int position = 0;
			while ((position = filename.indexOf('%', position)) >= 0) {
				if (position + 2 < filename.length()) {
					String hexStr = filename.substring(position + 1, position + 3);
					char ch = (char) Integer.parseInt(hexStr, 16);
					filename = filename.substring(0, position) + ch + filename.substring(position + 3);
				}
			}
			return new java.io.File(filename).getPath();
		}
	}
	/**
	 * 
	 *@Description 
	 *获取CEConnection 对象
	 *@param uri
	 *@return
	 */
	Connection getCEConnection(String uri) {
		Connection conn = Factory.Connection.getConnection(uri);
		return conn;
	}
	/**
	 * 
	 *@Description 
	 *获取一个Domain对象
	 *@param conn
	 *@param domainName
	 *@param filter
	 *@return
	 */
	Domain getCEDomain(Connection conn, String domainName, PropertyFilter filter) {
		Domain domain = Factory.Domain.fetchInstance(conn, domainName, filter);
		return domain;
	}
	/**
	 * 
	 *@Description 
	 *获取一个ObjectStore对象
	 *@param domain
	 *@param osName
	 *@param filter
	 *@return
	 */
	ObjectStore getObjStore(Domain domain, String osName, PropertyFilter filter) {
		ObjectStore objStore = Factory.ObjectStore.fetchInstance(domain,osName, filter);
		return objStore;
	}
	/**
	 * 
	 *@Description 
	 *创建一个Folder对象
	 *@param objectStore
	 *@param folderId
	 *@return
	 */
	Folder createCEFolder(ObjectStore objectStore, String folderId) {
		Folder folder = Factory.Folder.createInstance(objectStore, folderId);
		return folder;
	}
	/**
	 * 
	 *@Description 
	 *获取一个Folder对象
	 *@param objectStore
	 *@param path
	 *@param filter
	 *@return
	 */
	Folder getCEFolder(ObjectStore objectStore, String path, PropertyFilter filter) {
		Folder folder = Factory.Folder.fetchInstance(objectStore, path, filter);
		return folder;
	}
	/**
	 * 登入
	 *@Description 
	 *创建用户上下文
	 *@param conn
	 *@param username
	 *@param password
	 *@param jaas
	 */
	void authInCEngine(Connection conn, String username, String password, String jaas) {
		System.out.println("[CEManager] conn:"+conn.toString()+" username:"+username+" password:"+password+" jaas:"+jaas);
		javax.security.auth.Subject subject = UserContext.createSubject(conn,username, password, jaas);
		UserContext uc = UserContext.get();
		uc.pushSubject(subject);
	}

	/**
	 * 登出
	 *@Description 
	 *
	 */
	void authOutCEngine() {
		UserContext uc = UserContext.get();
		uc.popSubject();
	}
	
	/**
	 * 创建CEDocment
	 * @param ceConn
	 * @param ceDoc
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void createCEDocument(CEConnBean ceConn, CEDocument ceDoc) throws Exception {
		Connection conn = null;
		Document document = null;
		Folder subFolder = null;
		ReferentialContainmentRelationship relationship = null;
		try {
			conn = getCEConnection(ceConn.getUri());
			if (conn != null) {
				authInCEngine(conn, ceConn.getUsername(), ceConn.getPassword(),	ceConn.getJaas());
			}
			Domain domain = getCEDomain(conn, ceConn.getDomainName(), null);
			ObjectStore objStore = getObjStore(domain, ceConn.getObjectStoreName(), null);

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
	 * 修改CEDocment
	 * @param ceConn
	 * @param ceDoc
	 * @param sql
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void modifyCEDocument(CEConnBean ceConn, CEDocument ceDoc, String sql) throws Exception {
		Connection conn = null;
		Document document = null;
		Document reservation = null;
		try {
			conn = getCEConnection(ceConn.getUri());
			if (conn != null) {
				authInCEngine(conn, ceConn.getUsername(), ceConn.getPassword(), ceConn.getJaas());
			}
			Domain domain = getCEDomain(conn, ceConn.getDomainName(), null);
			ObjectStore objStore = getObjStore(domain, ceConn.getObjectStoreName(), null);
			
			SearchScope sScope = new SearchScope(objStore);
			SearchSQL sSQL = new SearchSQL(sql);
			DocumentSet documents = (DocumentSet) sScope.fetchObjects(sSQL, 50, null, Boolean.valueOf(true));
			for (java.util.Iterator<?> it = documents.iterator(); it.hasNext();) {
				document = (Document) it.next();
				if(!document.get_IsReserved()) {
					document.checkout(ReservationType.EXCLUSIVE, null, null, null);
//					document.save(RefreshMode.REFRESH);
					document.save(RefreshMode.NO_REFRESH);
				}
				//-----------------------------------------------------------------------
				java.util.Map<String, Object> props = ceDoc.getProperties();// 属性
				reservation = (Document) document.get_Reservation();//获取检出的document
				Properties properties = reservation.getProperties();//值
				for (String key : props.keySet()) {
					Object value = props.get(key);
					properties.putObjectValue(key, value);
					System.out.println("KEY:"+key+"==VALUE:"+value);
				}
				
				//------为创建的 document 对象增加相关的外部文件，使用ContentElementList存储文件流对象-------
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
//				reservation.save(RefreshMode.REFRESH);//保存
			}
		} catch (Exception e) {
			try {
				if (document != null) {
					Document cancel = (Document) document.cancelCheckout();//取消检出
					cancel.save(RefreshMode.REFRESH);
				}
			} catch (Exception ex) {
				loger.error("CE document.cancelCheckout() 异常!",ex);
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
	 * 
	 *@Description 
	 *
	 *@param ceConn
	 *@return
	 */
	public ObjectStore getObjectStore(CEConnBean ceConn) {
		Connection conn = getCEConnection(ceConn.getUri());
		if (conn != null) {
			authInCEngine(conn, ceConn.getUsername(), ceConn.getPassword(), ceConn.getJaas());
		}
		Domain domain = getCEDomain(conn, ceConn.getDomainName(), null);
		ObjectStore objStore = getObjStore(domain, ceConn.getObjectStoreName(), null);
		return objStore;
	}
	/**
	 * 
	 *@Description 
	 *
	 *@param objStore
	 *@param sql
	 *@return
	 */
	public java.util.List<CEDocument> searchCEDocuments(ObjectStore objStore, String sql) {
		java.util.List<CEDocument> ceDocList = new java.util.ArrayList<CEDocument>();
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
				ContentTransfer cTransfer = (ContentTransfer) content;
				String contentName = cTransfer.get_RetrievalName();
				String contentType = cTransfer.get_ContentType();
				java.io.InputStream contentInput = cTransfer.accessContentStream();
				ceContent = new CEContent(CEContentSrcType.TRANSFER, contentName, contentType, contentInput);
				ceDoc.addContent(ceContent);
			}
			Properties properties = document.getProperties();
			for (java.util.Iterator<?> it = properties.iterator(); it.hasNext();) {
				Property property = (Property) it.next();
				String key = property.getPropertyName();
				Object value = property.getObjectValue();
				ceDoc.putProperty(key, value);
			}
			ceDocList.add(ceDoc);
		}
		return ceDocList;
	}
	
	/**
	 * 查询CEDocment
	 * @param ceConn
	 * @param sql
	 * @return
	 */
	public java.util.List<CEDocument> searchCEDocuments(CEConnBean ceConn, String sql) {
		java.util.List<CEDocument> ceDocList = new java.util.ArrayList<CEDocument>();
		Connection conn = null;
		try {
			conn = getCEConnection(ceConn.getUri());
			if (conn != null) {
				authInCEngine(conn, ceConn.getUsername(), ceConn.getPassword(), ceConn.getJaas());
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
//					ContentTransfer cTransfer = (ContentTransfer) content;
//					String contentName = cTransfer.get_RetrievalName();
//					String contentType = cTransfer.get_ContentType();
//					System.out.println("ContentConn=====" + cTransfer.getConnection());
//					java.io.InputStream contentInput = cTransfer.accessContentStream();
//					ceContent = new CEContent(CEContentSrcType.TRANSFER,
//							contentName, contentType, contentInput);

					ceDoc.addContent(ceContent);
				}
				Properties properties = document.getProperties();
				for (java.util.Iterator<?> it = properties.iterator(); it.hasNext();) {
					Property property = (Property) it.next();
					String key = property.getPropertyName();
					Object value = property.getObjectValue();
					ceDoc.putProperty(key, value);
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
	 * 查询CE的属性值
	 * @param ceConn
	 * @param sql
	 * @return
	 */
	public java.util.List<java.util.Map<String, Object>> searchCEProperties(CEConnBean ceConn, String sql) {
		java.util.List<java.util.Map<String, Object>> propsList = new java.util.ArrayList<java.util.Map<String, Object>>();
		Connection conn = null;
		try {
			conn = getCEConnection(ceConn.getUri());
			if (conn != null) {
				authInCEngine(conn, ceConn.getUsername(), ceConn.getPassword(), ceConn.getJaas());
			}
			Domain domain = getCEDomain(conn, ceConn.getDomainName(), null);
			ObjectStore objStore = getObjStore(domain, ceConn.getObjectStoreName(), null);
			SearchScope sScope = new SearchScope(objStore);
			SearchSQL sSQL = new SearchSQL(sql);
			RepositoryRowSet rowSet = sScope.fetchRows(sSQL, Integer.getInteger("50"), null, Boolean.valueOf(true));
			for (java.util.Iterator<?> iter = rowSet.iterator(); iter.hasNext();) {
				RepositoryRow row = (RepositoryRow) iter.next();
				Properties properties = row.getProperties();
				java.util.Map<String, Object> props = new java.util.HashMap<String, Object>();
				for (java.util.Iterator<?> it = properties.iterator(); it.hasNext();) {
					Property property = (Property) it.next();
					String key = property.getPropertyName();
					Object value = property.getObjectValue();
					props.put(key, value);
				}
				propsList.add(props);
			}
		} finally {
			if (conn != null) {
				authOutCEngine();
			}
		}
		return propsList;
	}
	
	/**
	 * 
	 * @param ceConn
	 * @param path
	 */
	public void deleteFolderByPath(CEConnBean ceConn, String path) {
		Connection conn = null;
		try {
			conn = getCEConnection(ceConn.getUri());
			if (conn != null) {
				authInCEngine(conn, ceConn.getUsername(), ceConn.getPassword(), ceConn.getJaas());
			}
			Domain domain = getCEDomain(conn, ceConn.getDomainName(), null);
			ObjectStore objStore = getObjStore(domain, ceConn.getObjectStoreName(), null);
			Folder folder = Factory.Folder.fetchInstance(objStore, path, null);
			deleteFolder(folder);
		} finally {
			if (conn != null) {
				authOutCEngine();
			}
		}
	}
	
	/**
	 * 
	 * @param folder
	 */
	private void deleteFolder(Folder folder) {
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
				VersionSeries versionSeries = document.get_VersionSeries();
				versionSeries.delete();
				versionSeries.save(RefreshMode.NO_REFRESH);
			}
		}
		folder.delete();
		folder.save(RefreshMode.NO_REFRESH);
	}
	
}
