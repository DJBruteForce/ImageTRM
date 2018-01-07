package com.sunyard.insurance.cmapi.model.cmquery;

import java.util.List;

/**
 * 
 * @Title Result.java
 * @Package com.sunyard.insurance.cmapi.model.cmquery
 * @Description
 * 
 * @time 2012-8-21 上午09:47:08 @author xxw @version 1.0
 *       -------------------------------------------------------
 */
public class Result {
	String id = "";
	String batchID = "";
	String appCode = "";
	String busiNo = "";
	String xmlPath = "";
	List<BatchVer> batchVers = null;
	/************** 永成添加（内部版本号） ******************/
	List<InnerVer> innerVers = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBatchID() {
		return batchID;
	}

	public void setBatchID(String batchID) {
		this.batchID = batchID;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getBusiNo() {
		return busiNo;
	}

	public void setBusiNo(String busiNo) {
		this.busiNo = busiNo;
	}

	public String getXmlPath() {
		return xmlPath;
	}

	public void setXmlPath(String xmlPath) {
		this.xmlPath = xmlPath;
	}

	public List<BatchVer> getBatchVers() {
		return batchVers;
	}

	public void setBatchVers(List<BatchVer> batchVers) {
		this.batchVers = batchVers;
	}

	public List<InnerVer> getInnerVers() {
		return innerVers;
	}

	public void setInnerVers(List<InnerVer> innerVers) {
		this.innerVers = innerVers;
	}
}
