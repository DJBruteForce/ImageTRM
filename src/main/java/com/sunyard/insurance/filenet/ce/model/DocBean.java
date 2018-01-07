package com.sunyard.insurance.filenet.ce.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocBean {
	
	private String sydFileName = null;

	private BatchBean batchBean = null;

	private List<PageBean> pageList = new ArrayList<PageBean>();

	private List<PropBean> propList = new ArrayList<PropBean>();

	private List<NodeBean> nodeList = new ArrayList<NodeBean>();

	private Map<String, String> nodeMap = new HashMap<String, String>();
	
	public DocBean() {
		super();
	}

	public DocBean(String sydFileName, BatchBean batchBean,
			List<PageBean> pageList, List<PropBean> propList,
			List<NodeBean> nodeList, Map<String, String> nodeMap) {
		super();
		this.sydFileName = sydFileName;
		this.batchBean = batchBean;
		if (pageList != null) {
			this.pageList.addAll(pageList);
		}
		if (propList != null) {
			this.propList.addAll(propList);
		}
		if (nodeList != null) {
			this.nodeList.addAll(nodeList);
		}
		if (nodeMap != null) {
			this.nodeMap.putAll(nodeMap);
		}
	}

	public String getSydFileName() {
		return sydFileName;
	}

	public void setSydFileName(String sydFileName) {
		this.sydFileName = sydFileName;
	}

	public BatchBean getBatchBean() {
		return batchBean;
	}

	public void setBatchBean(BatchBean batchBean) {
		this.batchBean = batchBean;
	}

	public List<PageBean> getPageList() {
		return pageList;
	}

	@SuppressWarnings("unused")
	private void setPageList(List<PageBean> pageList) {
		this.pageList = pageList;
	}

	public List<PropBean> getPropList() {
		return propList;
	}

	@SuppressWarnings("unused")
	private void setPropList(List<PropBean> propList) {
		this.propList = propList;
	}

	public List<NodeBean> getNodeList() {
		return nodeList;
	}

	@SuppressWarnings("unused")
	private void setNodeList(List<NodeBean> nodeList) {
		this.nodeList = nodeList;
	}

	public Map<String, String> getNodeMap() {
		return nodeMap;
	}

	@SuppressWarnings("unused")
	private void setNodeMap(Map<String, String> nodeMap) {
		this.nodeMap = nodeMap;
	}

	public void addPage(PageBean pageBean) {
		pageList.add(pageBean);
	}

	public void addProp(PropBean propBean) {
		propList.add(propBean);
	}

}
