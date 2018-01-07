package com.sunyard.insurance.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class B_OBJECT_STORE_ID implements Serializable {
	private static final long serialVersionUID = 7105737625076740288L;
	
	/** 业务类型 */
	private String appCode;
	
	/** 业务年份 */
	private String busiDate;
	
	public B_OBJECT_STORE_ID () {
		
	}

	/** 构造器 */
	public B_OBJECT_STORE_ID(String appCode, String busiDate) {
		super();
		this.appCode = appCode;
		this.busiDate = busiDate;
	}

	@Column(name = "APP_CODE", nullable = false, length = 16)
	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	@Column(name = "BUSI_DATE", nullable = false, length = 4)
	public String getBusiDate() {
		return busiDate;
	}

	public void setBusiDate(String busiDate) {
		this.busiDate = busiDate;
	}
}
