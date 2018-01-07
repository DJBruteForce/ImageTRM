package com.sunyard.filenet.ce.bean;

import java.io.InputStream;

public class CEContent {

	private CEContentSrcType sourceType = CEContentSrcType.TRANSFER;// 默认文档部件源类型
	private String contentName = "";// 资源名称
	private String contentType = "";// 资源MIME
	private InputStream contentInput = null;// 资源流
	private String contentLocation = "";// 资源引用

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

	public CEContentSrcType getSourceType() {
		return sourceType;
	}

	public void setSourceType(CEContentSrcType sourceType) {
		this.sourceType = sourceType;
	}

	public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public InputStream getContentInput() {
		return contentInput;
	}

	public void setContentInput(InputStream contentInput) {
		this.contentInput = contentInput;
	}

	public String getContentLocation() {
		return contentLocation;
	}

	public void setContentLocation(String contentLocation) {
		this.contentLocation = contentLocation;
	}

}
