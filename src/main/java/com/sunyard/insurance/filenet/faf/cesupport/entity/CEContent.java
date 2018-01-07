package com.sunyard.insurance.filenet.faf.cesupport.entity;

import java.io.InputStream;


public class CEContent extends CEObject {

	protected CEContentSrcType sourceType = CEContentSrcType.TRANSFER;//文档部件源类型
	
	protected String contentName = "";

	protected String contentType = "";
	
	protected InputStream contentInput = null;
	
	protected String contentLocation = "";

	public CEContent(CEContentSrcType sourceType, String contentName,
			String contentType, InputStream contentInput) {
		this.sourceType = sourceType;
		this.contentName = contentName;
		this.contentType = contentType;
		this.contentInput = contentInput;
	}
	
	public CEContent(CEContentSrcType sourceType, String contentType,
			String contentLocation) {
		this.sourceType = sourceType;
		this.contentType = contentType;
		this.contentLocation = contentLocation;
	}
	
	/**
	 * @return the sourceType
	 */
	public CEContentSrcType getSourceType() {
		return sourceType;
	}

	/**
	 * @param sourceType the sourceType to set
	 */
	public void setSourceType(CEContentSrcType sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * @return the contentName
	 */
	public String getContentName() {
		return contentName;
	}

	/**
	 * @param contentPath the contentName to set
	 */
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the contentInput
	 */
	public InputStream getContentInput() {
		return contentInput;
	}

	/**
	 * @param contentInput the contentInput to set
	 */
	public void setContentInput(InputStream contentInput) {
		this.contentInput = contentInput;
	}

	/**
	 * @return the contentLocation
	 */
	public String getContentLocation() {
		return contentLocation;
	}

	/**
	 * @param contentLocation the contentLocation to set
	 */
	public void setContentLocation(String contentLocation) {
		this.contentLocation = contentLocation;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contentName == null) ? 0 : contentName.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CEContent other = (CEContent) obj;
		if (contentName == null) {
			if (other.contentName != null)
				return false;
		} else if (!contentName.equals(other.contentName))
			return false;
		return true;
	}

	protected void validate(Object... args) {
		
	}
	
}
