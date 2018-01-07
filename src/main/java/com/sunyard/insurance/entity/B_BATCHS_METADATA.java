package com.sunyard.insurance.entity;

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
@Table(name = "B_BATCHS_METADATA")
public class B_BATCHS_METADATA implements java.io.Serializable {

	private static final long serialVersionUID = 474019834864947676L;
	private B_BATCHS_METADATA_ID id;
	private String appCode;
	private String propValue;
	private Date createTime;//产生时间，用于分区表 

	// Constructors

	/** default constructor */
	public B_BATCHS_METADATA() {
	}

	/** full constructor */
	public B_BATCHS_METADATA(B_BATCHS_METADATA_ID id, String appCode,
			String propValue) {
		this.id = id;
		this.appCode = appCode;
		this.propValue = propValue;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "batchId", column = @Column(name = "BATCH_ID", nullable = false, length = 32)),
			@AttributeOverride(name = "propCode", column = @Column(name = "PROP_CODE", nullable = false, length = 32)) })
	public B_BATCHS_METADATA_ID getId() {
		return this.id;
	}

	public void setId(B_BATCHS_METADATA_ID id) {
		this.id = id;
	}

	@Column(name = "APP_CODE", nullable = false, length = 16)
	public String getAppCode() {
		return this.appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	@Column(name = "PROP_VALUE", nullable = false, length = 2048)
	public String getPropValue() {
		return this.propValue;
	}

	public void setPropValue(String propValue) {
		this.propValue = propValue;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_TIME", length = 20)
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}