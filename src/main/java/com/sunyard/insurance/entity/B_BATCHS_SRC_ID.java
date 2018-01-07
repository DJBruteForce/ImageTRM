package com.sunyard.insurance.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class B_BATCHS_SRC_ID implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2393518197710500730L;
	// Fields
	private String batchId;
	private String orgCode;
	private Integer batchVer;

	// Constructors

	/** default constructor */
	public B_BATCHS_SRC_ID() {
	}

	/** full constructor */
	public B_BATCHS_SRC_ID(String batchId, String orgCode, Integer batchVer) {
		this.batchId = batchId;
		this.orgCode = orgCode;
		this.batchVer = batchVer;
	}

	// Property accessors

	@Column(name = "BATCH_ID", nullable = false, length = 32)
	public String getBatchId() {
		return this.batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	@Column(name = "ORG_CODE", nullable = false, length = 8)
	public String getOrgCode() {
		return this.orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	@Column(name = "BATCH_VER", nullable = false, precision = 22, scale = 0)
	public Integer getBatchVer() {
		return this.batchVer;
	}

	public void setBatchVer(Integer batchVer) {
		this.batchVer = batchVer;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof B_BATCHS_SRC_ID))
			return false;
		B_BATCHS_SRC_ID castOther = (B_BATCHS_SRC_ID) other;

		return ((this.getBatchId() == castOther.getBatchId()) || (this
				.getBatchId() != null
				&& castOther.getBatchId() != null && this.getBatchId().equals(
				castOther.getBatchId())))
				&& ((this.getOrgCode() == castOther.getOrgCode()) || (this
						.getOrgCode() != null
						&& castOther.getOrgCode() != null && this.getOrgCode()
						.equals(castOther.getOrgCode())))
				&& ((this.getBatchVer() == castOther.getBatchVer()) || (this
						.getBatchVer() != null
						&& castOther.getBatchVer() != null && this
						.getBatchVer().equals(castOther.getBatchVer())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getBatchId() == null ? 0 : this.getBatchId().hashCode());
		result = 37 * result
				+ (getOrgCode() == null ? 0 : this.getOrgCode().hashCode());
		result = 37 * result
				+ (getBatchVer() == null ? 0 : this.getBatchVer().hashCode());
		return result;
	}

}