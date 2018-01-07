package com.sunyard.filenet.ce.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CEDocument {

	private String docClass = "";
	private String mimeType = "";// 文件类型MIME
	private String mainFolder = "";// 根文件名夹名
	private String subFolder = "";
	private Map<String, Object> properties = new HashMap<String, Object>();
	private List<CEContent> contentList = new ArrayList<CEContent>();

	public CEDocument() {
		
	}
	
	public CEDocument(String docClass, String mimeType, String mainFolder,
			String subFolder, Map<String, Object> properties,
			List<CEContent> contentList) {
		this.docClass = docClass;
		this.mimeType = mimeType;
		this.mainFolder = mainFolder;
		this.subFolder = subFolder;
		this.properties = properties;
		this.contentList = contentList;

	}
	
	public String getDocClass() {
		return docClass;
	}

	public void setDocClass(String docClass) {
		this.docClass = docClass;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMainFolder() {
		return mainFolder;
	}

	public void setMainFolder(String mainFolder) {
		this.mainFolder = mainFolder;
	}

	public String getSubFolder() {
		return subFolder;
	}

	public void setSubFolder(String subFolder) {
		this.subFolder = subFolder;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public List<CEContent> getContentList() {
		return contentList;
	}

	public void setContentList(List<CEContent> contentList) {
		this.contentList = contentList;
	}

}
