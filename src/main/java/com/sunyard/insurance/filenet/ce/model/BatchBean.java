package com.sunyard.insurance.filenet.ce.model;

public class BatchBean {

	private String batchId = "";

	private String batchVer = "";

	private String interVer = "";

	private String busiNo = "";

	private String busiName = "";

	private String appCode = "";

	private String status = "";

	private String createUser = "";

	private String createDate = "";

	private String modUser = "";

	private String modDate = "";

	/** 大地多Object Store新增字段 */
	private String busi_date = "";
	public BatchBean() {
		super();
	}

	public BatchBean(String batchId, String batchVer, String interVer,
			String busiNo, String busiName, String appCode, String status,
			String createUser, String createDate, String modUser, String modDate) {
		super();
		this.batchId = batchId;
		this.batchVer = batchVer;
		this.interVer = interVer;
		this.busiNo = busiNo;
		this.busiName = busiName;
		this.appCode = appCode;
		this.status = status;
		this.createUser = createUser;
		this.createDate = createDate;
		this.modUser = modUser;
		this.modDate = modDate;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getBatchVer() {
		return batchVer;
	}

	public void setBatchVer(String batchVer) {
		this.batchVer = batchVer;
	}

	public String getInterVer() {
		return interVer;
	}

	public void setInterVer(String interVer) {
		this.interVer = interVer;
	}

	public String getBusiNo() {
		return busiNo;
	}

	public void setBusiNo(String busiNo) {
		this.busiNo = busiNo;
	}

	public String getBusiName() {
		return busiName;
	}

	public void setBusiName(String busiName) {
		this.busiName = busiName;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getModUser() {
		return modUser;
	}

	public void setModUser(String modUser) {
		this.modUser = modUser;
	}

	public String getModDate() {
		return modDate;
	}

	public void setModDate(String modDate) {
		this.modDate = modDate;
	}
	public String getBusi_date() {
		return busi_date;
	}

	public void setBusi_date(String busi_date) {
		this.busi_date = busi_date;
	}

}
