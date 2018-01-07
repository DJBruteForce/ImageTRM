package com.sunyard.insurance.batch.bean;

import java.io.Serializable;

public class BatchFileBean implements Serializable {

	private static final long serialVersionUID = 4239399038600283219L;
	private String fileName;
	private String fileFullPath;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileFullPath() {
		return fileFullPath;
	}

	public void setFileFullPath(String fileFullPath) {
		this.fileFullPath = fileFullPath;
	}

}
