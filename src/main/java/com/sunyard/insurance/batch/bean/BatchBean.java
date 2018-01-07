package com.sunyard.insurance.batch.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchBean implements Serializable {

	private static final long serialVersionUID = 8356576560964724898L;
	private String appCode;
	private String batch_id;
	private String batch_ver;
	private String inter_ver;
	private String busi_no;
	private String create_user;
	private String mod_user;
	private String biz_org;
	/** 大地多Object Store新增字段 */
	private String busi_date;
	private Map<String, String> proMap = new HashMap<String, String>();;
	List<BatchFileBean> BatchFileList = new ArrayList<BatchFileBean>();

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(String batchId) {
		batch_id = batchId;
	}

	public String getBatch_ver() {
		return batch_ver;
	}

	public void setBatch_ver(String batchVer) {
		batch_ver = batchVer;
	}

	public String getInter_ver() {
		return inter_ver;
	}

	public void setInter_ver(String interVer) {
		inter_ver = interVer;
	}

	public String getBusi_no() {
		return busi_no;
	}

	public void setBusi_no(String busiNo) {
		busi_no = busiNo;
	}

	public Map<String, String> getProMap() {
		return proMap;
	}

	public void setProMap(Map<String, String> proMap) {
		this.proMap = proMap;
	}

	public List<BatchFileBean> getBatchFileList() {
		return BatchFileList;
	}

	public void setBatchFileList(List<BatchFileBean> batchFileList) {
		BatchFileList = batchFileList;
	}

	public String getCreate_user() {
		return create_user;
	}

	public void setCreate_user(String createUser) {
		create_user = createUser;
	}

	public String getMod_user() {
		return mod_user;
	}

	public void setMod_user(String modUser) {
		mod_user = modUser;
	}
	
	public String getBiz_org() {
		return biz_org;
	}

	public void setBiz_org(String bizOrg) {
		biz_org = bizOrg;
	}
	
	public String getBusi_date() {
		return busi_date;
	}

	public void setBusi_date(String busi_date) {
		this.busi_date = busi_date;
	}
	
}
