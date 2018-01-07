package com.sunyard.insurance.cmapi.model.cmquery;

import java.util.ArrayList;
import java.util.List;

import com.sunyard.insurance.cmapi.model.AttrInfo;

/**
 * 
 * @Title RootInfo.java
 * @Package com.sunyard.insurance.cmapi.model.cmquery
 * @Description
 * 
 * @time 2012-8-21 上午09:47:22 @author xxw @version 1.0
 *       -------------------------------------------------------
 */
public class RootInfo {
	private String appCode = "";
	private String folderName = "";
	private List<AttrInfo> attrs = new ArrayList<AttrInfo>();

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public List<AttrInfo> getAttrs() {
		return attrs;
	}

	public void setAttrs(List<AttrInfo> attrs) {
		this.attrs = attrs;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	
}
