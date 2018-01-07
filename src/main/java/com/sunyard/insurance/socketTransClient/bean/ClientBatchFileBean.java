package com.sunyard.insurance.socketTransClient.bean;

public class ClientBatchFileBean {
	private String fileName;
	private String filePath;
	private long fileSize;
	private String md5Code;
	private boolean isSubmitSuccess = false;

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getMd5Code() {
		return this.md5Code;
	}

	public void setMd5Code(String md5Code) {
		this.md5Code = md5Code;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isSubmitSuccess() {
		return this.isSubmitSuccess;
	}

	public void setSubmitSuccess(boolean isSubmitSuccess) {
		this.isSubmitSuccess = isSubmitSuccess;
	}
}
