package com.sunyard.insurance.filenet.ce.model;

import java.util.ArrayList;
import java.util.List;

public class NodeBean {

	private String id = "";
	
	private String name = "";
	
	private List<NodeBean> nodeList = new ArrayList<NodeBean>();

	private List<String> leafList = new ArrayList<String>();

	public NodeBean() {
		super();
	}

	public NodeBean(String id, String name, List<NodeBean> nodeList,
			List<String> leafList) {
		super();
		this.id = id;
		this.name = name;
		if (nodeList != null) {
			this.nodeList.addAll(nodeList);
		}
		if (leafList != null) {
			this.leafList.addAll(leafList);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NodeBean> getNodeList() {
		return nodeList;
	}

	@SuppressWarnings("unused")
	private void setNodeList(List<NodeBean> nodeList) {
		this.nodeList = nodeList;
	}

	public List<String> getLeafList() {
		return leafList;
	}

	@SuppressWarnings("unused")
	private void setLeafList(List<String> leafList) {
		this.leafList = leafList;
	}

}
