package com.sunyard.insurance.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "B_OBJECT_STORE")
public class B_OBJECT_STORE implements Serializable {
	private static final long serialVersionUID = -5490011851674587106L;

	private B_OBJECT_STORE_ID id;
	
	private String objectStoreName;
	
	/** default constructor */
	public B_OBJECT_STORE() {
		
	}

	/** minimal constructor */
	public B_OBJECT_STORE(B_OBJECT_STORE_ID id) {
		this.id = id;
	}

	/** full constructor */
	public B_OBJECT_STORE(B_OBJECT_STORE_ID id, String objectStoreName) {
		this.id = id;
		this.objectStoreName = objectStoreName;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "appCode", column = @Column(name = "APP_CODE", nullable = false, length = 16)),
			@AttributeOverride(name = "busiDate", column = @Column(name = "BUSI_DATE", nullable = false, length = 4)) })
	public B_OBJECT_STORE_ID getId() {
		return id;
	}

	public void setId(B_OBJECT_STORE_ID id) {
		this.id = id;
	}

	@Column(name = "OBJECT_STORE_NAME", length = 16)
	public String getObjectStoreName() {
		return objectStoreName;
	}

	public void setObjectStoreName(String objectStoreName) {
		this.objectStoreName = objectStoreName;
	}
	
}
