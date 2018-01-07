package com.sunyard.insurance.filenet.ce.model;

import java.util.ArrayList;
import java.util.List;

public class PageBean {

	private String pageId = "";
	
	private String createUser = "";

	private String createTime = "";

	private String modifyUser = "";

	private String modifyTime = "";

	private String pageUrl = "";

	private String thumUrl = "";

	private String isLocal = "";

	private String pageVer = "";

	private String pageDesc = "";

	private String uploadOrg = "";

	private String pageCrc = "";

	private String pageFormat = "";

	private String pageEncrypt = "";

	private String orginalName = "";

	private List<PageExtBean> extList = new ArrayList<PageExtBean>();

	public PageBean() {
		super();
	}

	public PageBean(String pageId, String createUser, String createTime,
			String modifyUser, String modifyTime, String pageUrl,
			String thumUrl, String isLocal, String pageVer, String pageDesc,
			String uploadOrg, String pageCrc, String pageFormat,
			String pageEncrypt, String orginalName, List<PageExtBean> extList) {
		super();
		this.pageId = pageId;
		this.createUser = createUser;
		this.createTime = createTime;
		this.modifyUser = modifyUser;
		this.modifyTime = modifyTime;
		this.pageUrl = pageUrl;
		this.thumUrl = thumUrl;
		this.isLocal = isLocal;
		this.pageVer = pageVer;
		this.pageDesc = pageDesc;
		this.uploadOrg = uploadOrg;
		this.pageCrc = pageCrc;
		this.pageFormat = pageFormat;
		this.pageEncrypt = pageEncrypt;
		this.orginalName = orginalName;
		if (extList != null) {
			this.extList.addAll(extList);
		}
	}

	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getThumUrl() {
		return thumUrl;
	}

	public void setThumUrl(String thumUrl) {
		this.thumUrl = thumUrl;
	}

	public String getIsLocal() {
		return isLocal;
	}

	public void setIsLocal(String isLocal) {
		this.isLocal = isLocal;
	}

	public String getPageVer() {
		return pageVer;
	}

	public void setPageVer(String pageVer) {
		this.pageVer = pageVer;
	}

	public String getPageDesc() {
		return pageDesc;
	}

	public void setPageDesc(String pageDesc) {
		this.pageDesc = pageDesc;
	}

	public String getUploadOrg() {
		return uploadOrg;
	}

	public void setUploadOrg(String uploadOrg) {
		this.uploadOrg = uploadOrg;
	}

	public String getPageCrc() {
		return pageCrc;
	}

	public void setPageCrc(String pageCrc) {
		this.pageCrc = pageCrc;
	}

	public String getPageFormat() {
		return pageFormat;
	}

	public void setPageFormat(String pageFormat) {
		this.pageFormat = pageFormat;
	}

	public String getPageEncrypt() {
		return pageEncrypt;
	}

	public void setPageEncrypt(String pageEncrypt) {
		this.pageEncrypt = pageEncrypt;
	}

	public String getOrginalName() {
		return orginalName;
	}

	public void setOrginalName(String orginalName) {
		this.orginalName = orginalName;
	}

	public List<PageExtBean> getExtList() {
		return extList;
	}

	@SuppressWarnings("unused")
	private void setExtList(List<PageExtBean> extList) {
		this.extList = extList;
	}
	
	public void addExt(PageExtBean extBean) {
		extList.add(extBean);
	}

}
