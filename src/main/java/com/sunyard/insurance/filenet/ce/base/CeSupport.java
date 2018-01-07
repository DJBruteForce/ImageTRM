package com.sunyard.insurance.filenet.ce.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;

import com.filenet.api.admin.ClassDefinition;
import com.filenet.api.admin.LocalizedString;
import com.filenet.api.admin.PropertyDefinition;
import com.filenet.api.admin.PropertyDefinitionString;
import com.filenet.api.admin.PropertyTemplateString;
import com.filenet.api.collection.PropertyDefinitionList;
import com.filenet.api.constants.Cardinality;
import com.filenet.api.core.Connection;
import com.filenet.api.core.ContentTransfer;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.IndependentObject;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.Properties;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;
import com.filenet.api.util.UserContext;

public class CeSupport {
	
	public static final String DOCUMENT_TITLE = new String("DocumentTitle");
	
	protected ObjectStore[] objectStores = null;
	protected ObjectStore os = null;
	
	protected CeSupport(String uri, String username, String password, String objectStore, String historyObjectStore) {
		super();
		Connection conn = getConnection(uri);
		pushSubject(conn, username, password, null);
		initObjectStores(conn, objectStore, historyObjectStore);
	}
	
	protected void initObjectStores(Connection conn, String objectStore, String historyObjectStore) {
		List<ObjectStore> osList = new ArrayList<ObjectStore>();
		os = fetchObjectStore(conn, objectStore);
		osList.add(0, os);
		
		if (historyObjectStore != null && !"".equals(historyObjectStore)) {
			String[] osArr = historyObjectStore.split(",");
			for (int i = 0; i < osArr.length; i++) {
				String osName = osArr[i];
				ObjectStore store = fetchObjectStore(conn, osName);
				if (store != null) {
					osList.add(store);
				}
			}
		}
		objectStores = new ObjectStore[osList.size()];
		objectStores = osList.toArray(objectStores);
	}
	
	protected Connection getConnection(String uri) {
		return Factory.Connection.getConnection(uri);
	}
	
	protected ObjectStore getObjectStore(Connection conn, String osName) {
		Domain domain = Factory.Domain.getInstance(conn, null);
		return Factory.ObjectStore.getInstance(domain, osName);
	}
	
	protected ObjectStore fetchObjectStore(Connection conn, String osName) {
		Domain domain = Factory.Domain.getInstance(conn, null);
		return Factory.ObjectStore.fetchInstance(domain, osName, null);
	}
	
	protected Subject pushSubject(Connection conn, String username, String password, String jaas) {
		Subject subject = UserContext.createSubject(conn, username, password, jaas);
		UserContext uc = UserContext.get();
		uc.pushSubject(subject);
		return subject;
	}
	
	protected Subject popSubject() {
		UserContext uc = UserContext.get();
		return uc.popSubject();
	}
	
	protected boolean existsProperty(ObjectStore os, String className, String propName) {
		ClassDefinition classDef = Factory.ClassDefinition.fetchInstance(os, className, null);
		PropertyDefinitionList propDefList = classDef.get_PropertyDefinitions();
		for (int i = 0; i < propDefList.size(); i++) {
			PropertyDefinition propDef = (PropertyDefinition) propDefList.get(i);
			String symbolicName = propDef.get_SymbolicName();
			if (symbolicName.equals(propName)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected ClassDefinition createClassDefinition(ObjectStore os, String parentClassName, String subClassName) {
		ClassDefinition classDef = Factory.ClassDefinition.fetchInstance(os, parentClassName, null);
		ClassDefinition subClassDef = classDef.createSubclass();

		LocalizedString displayNameStr = getLocalizedString(subClassName, os.get_LocaleName());
		subClassDef.set_DisplayNames(Factory.LocalizedString.createList());
		subClassDef.get_DisplayNames().add(displayNameStr);
		subClassDef.set_DescriptiveTexts(Factory.LocalizedString.createList());
		subClassDef.get_DescriptiveTexts().add(displayNameStr);

		return subClassDef;
	}

	@SuppressWarnings("unchecked")
	protected PropertyTemplateString createPropertyTemplateString(ObjectStore os, String propName) {
		PropertyTemplateString template = Factory.PropertyTemplateString.createInstance(os);
		template.set_Cardinality(Cardinality.SINGLE);

		LocalizedString displayNameStr = getLocalizedString(propName, os.get_LocaleName());
		template.set_DisplayNames(Factory.LocalizedString.createList());
		template.get_DisplayNames().add(displayNameStr);
		template.set_DescriptiveTexts(Factory.LocalizedString.createList());
		template.get_DescriptiveTexts().add(displayNameStr);

		return template;
	}

	@SuppressWarnings("unchecked")
	protected ClassDefinition relatePropertyString(ObjectStore os, PropertyTemplateString template, String className) {
		ClassDefinition classDef = Factory.ClassDefinition.fetchInstance(os,className, null);
		PropertyDefinitionString propDefStr = (PropertyDefinitionString) template.createClassProperty();
		PropertyDefinitionList propDefList = classDef.get_PropertyDefinitions();
		propDefList.add(propDefStr);

		return classDef;
	}

	protected LocalizedString getLocalizedString(String text, String locale) {
		LocalizedString localStr = Factory.LocalizedString.createInstance();
		localStr.set_LocalizedText(text);
		localStr.set_LocaleName(locale);
		return localStr;
	}
	
	protected Document createDocument(ObjectStore os, String docClass,
			String docTitle) {
		Document document = Factory.Document.createInstance(os, docClass);
		Properties properties = document.getProperties();
		properties.putValue(DOCUMENT_TITLE, docTitle);
		return document;
	}
	
	protected Folder createFolder(ObjectStore os, String folderName,
			Folder parentFolder, String folderClass) {
		Folder folder = Factory.Folder.createInstance(os, folderClass);
		folder.set_Parent(parentFolder);
		folder.set_FolderName(folderName);
		return folder;
	}
	
	protected void updateProperties(IndependentObject io,
			Map<String, Object> propMap) {
		Properties properties = io.getProperties();
		for (String key : propMap.keySet()) {
			Object value = propMap.get(key);
			properties.putObjectValue(key, value);
		}
	}
	
	protected Document getReservation(Document document) {
		return (Document) ((document != null) ? document.get_Reservation() : null);
	}
	
	protected Boolean isCheckOut(Document document) {
		return (Boolean) ((document != null) ? document.get_IsReserved() : null);
	}
	
	protected Document fetchDocument(ObjectStore os, String id,
			PropertyFilter filter) {
		Document document = Factory.Document.fetchInstance(os, new Id(id), filter);
		return document;
	}
	
	protected Folder fetchFolder(ObjectStore os, String id, PropertyFilter filter) {
		Folder folder = Factory.Folder.fetchInstance(os, new Id(id), filter);
		return folder;
	}
	
	protected Folder fetchFolderByPath(ObjectStore os, String path, PropertyFilter filter) {
		Folder folder = Factory.Folder.fetchInstance(os, path, filter);
		return folder;
	}
	/**
	 * 
	 *@Description 
	 *添加影像文件到CE
	 *@param filePath
	 *@param fileName
	 *@param mimeType
	 *@return
	 *@throws FileNotFoundException
	 */
	public static ContentTransfer createContentTransfer(String filePath, String fileName, String mimeType) throws FileNotFoundException {
		InputStream is = new FileInputStream(filePath);
		ContentTransfer contentTransfer = Factory.ContentTransfer.createInstance();
		contentTransfer.setCaptureSource(is);
		contentTransfer.set_RetrievalName(fileName);
		contentTransfer.set_ContentType(mimeType);
		return contentTransfer;
	}
	/**
	 * 
	 *@Description 
	 *
	 *@param os
	 *@param className
	 *@return
	 */
	protected ClassDefinition fetchClassDefinition(ObjectStore os,
			String className) {
		ClassDefinition classDef = Factory.ClassDefinition.fetchInstance(os, className, null);
		return classDef;
	}
	
}
