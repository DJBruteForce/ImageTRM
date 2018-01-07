package com.sunyard.insurance.cmapi.model;

import java.util.List;
/**
 * 
  * @Title ImgXmlInfo.java
  * @Package com.sunyard.insurance.cmapi.model
  * @Description 
  * 批次XML信息的model对象
  * @time 2012-8-8 下午01:59:43  @author xxw
  * @version 1.0
  *-------------------------------------------------------
 */
public class ImgXmlInfo {
	//XML中的批次号
	private String realBatchID = "";
	//业务代码
	private String appCode = "";
	//业务编号
	private String busiNo = "";
	//批次版本号
	private int batchVer = 0;
	//内部版本号
	private int innerVer = 0;
	//扩展属性集合
	private List<AttrInfo> attrList = null;

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public List<AttrInfo> getAttrList() {
		return attrList;
	}

	public void setAttrList(List<AttrInfo> attrList) {
		this.attrList = attrList;
	}

	public String getRealBatchID() {
		return realBatchID;
	}

	public void setRealBatchID(String realBatchID) {
		this.realBatchID = realBatchID;
	}

	public int getInnerVer() {
		return innerVer;
	}

	public void setInnerVer(int innerVer) {
		this.innerVer = innerVer;
	}

	public String getBusiNo() {
		return busiNo;
	}

	public void setBusiNo(String busiNo) {
		this.busiNo = busiNo;
	}

	public int getBatchVer() {
		return batchVer;
	}

	public void setBatchVer(int batchVer) {
		this.batchVer = batchVer;
	}
}
