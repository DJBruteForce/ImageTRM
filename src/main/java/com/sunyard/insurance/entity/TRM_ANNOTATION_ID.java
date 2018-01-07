package com.sunyard.insurance.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class TRM_ANNOTATION_ID implements Serializable {

	private static final long serialVersionUID = -2665156852703324205L;

	private String batchId;
	private String pageId;
	
	public TRM_ANNOTATION_ID() {
		super();
	}
	
	public TRM_ANNOTATION_ID(String batchId,String pageId) {
		this.batchId = batchId;
		this.pageId = pageId;
	}
	
	
	@Column(name = "BATCH_ID", nullable = false, length = 32)
	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	
	@Column(name = "PAGE_ID", nullable = false, length = 36)
	public String getPageId() {
		return pageId;
	}

	public void setPageId(String pageId) {
		this.pageId = pageId;
	}

}
