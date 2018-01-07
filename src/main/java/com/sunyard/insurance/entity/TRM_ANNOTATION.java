package com.sunyard.insurance.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "TRM_ANNOTATION")
public class TRM_ANNOTATION implements Serializable {

	private static final long serialVersionUID = 111435524795535666L;

	private TRM_ANNOTATION_ID id;
	private String content_str;
	private Date createDate;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "BATCH_ID", column = @Column(name = "BATCH_ID", nullable = false, length = 32)),
			@AttributeOverride(name = "PAGE_ID", column = @Column(name = "PAGE_ID", nullable = false, length = 36)) })
	public TRM_ANNOTATION_ID getId() {
		return id;
	}

	public void setId(TRM_ANNOTATION_ID id) {
		this.id = id;
	}

	@Column(name = "CONTENT_STR", length = 300)
	public String getContent_str() {
		return content_str;
	}

	public void setContent_str(String contentStr) {
		content_str = contentStr;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", length = 10)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}
