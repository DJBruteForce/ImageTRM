package com.sunyard.insurance.filenet.faf.cesupport.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
  * @Title CEDocument.java
  * @Package com.sunyard.insurance.filenet.faf.cesupport.entity
  * @Description 
  * @author Administrator
  * @time 2012-12-4 上午10:36:15  
  * @version 1.0
 */
public class CEDocument extends CEObject {

	protected String docClass = "";
	
	protected String mimeType = "";//文件类型
	
	protected String mainFolder = "";//根文件名夹名
	
	protected String subFolder = "";

	protected Map<String, Object> properties = null;

	protected List<CEContent> contentList = null;

	public CEDocument() {
		setProperties(new HashMap<String, Object>());
		setContentList(new ArrayList<CEContent>());
	}
	
	public CEDocument(String docClass, String mimeType, String mainFolder,
			String subFolder, Map<String, Object> properties, List<CEContent> contentList) {
		this.docClass = docClass;
		this.mimeType = mimeType;
		this.mainFolder = mainFolder;
		this.subFolder = subFolder;

		Map<String, Object> props = new HashMap<String, Object>();
		props.putAll(properties);
		setProperties(props);

		List<CEContent> list = new ArrayList<CEContent>();
		list.addAll(contentList);
		setContentList(list);
	}

	/**
	 * @return the docClass
	 */
	public String getDocClass() {
		return docClass;
	}

	/**
	 * @param docClass the docClass to set
	 */
	public void setDocClass(String docClass) {
		this.docClass = docClass;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
	/**
	 * @return the mainFolder
	 */
	public String getMainFolder() {
		return mainFolder;
	}

	/**
	 * @param mainFolder the mainFolder to set
	 */
	public void setMainFolder(String mainFolder) {
		this.mainFolder = mainFolder;
	}

	/**
	 * @return the subFolder
	 */
	public String getSubFolder() {
		return subFolder;
	}

	/**
	 * @param subFolder the subFolder to set
	 */
	public void setSubFolder(String subFolder) {
		this.subFolder = subFolder;
	}

	/**
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/*
	 * @param properties the properties to set
	 */
	protected void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	public Object putProperty(String key, Object value) {
		return this.properties.put(key, value);
	}

	/**
	 * @return the contentList
	 */
	public List<CEContent> getContentList() {
		return contentList;
	}

	/*
	 * @param contentList the contentList to set
	 */
	protected void setContentList(List<CEContent> contentList) {
		this.contentList = contentList;
	}
	
	public boolean addContent(CEContent content) {
		return this.contentList.add(content);
	}
	
	protected void validate(Object... args) {
		
	}

}
