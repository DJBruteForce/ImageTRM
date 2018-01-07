package com.sunyard.insurance.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * BBatchsMetadataId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class B_BATCHS_METADATA_ID implements java.io.Serializable {

	private static final long serialVersionUID = -847310333656814655L;
	private String batchId;
	private String propCode;

	// Constructors

	/** default constructor */
	public B_BATCHS_METADATA_ID() {
	}

	/** full constructor */
	public B_BATCHS_METADATA_ID(String batchId, String propCode) {
		this.batchId = batchId;
		this.propCode = propCode;
	}

	// Property accessors

	@Column(name = "BATCH_ID", nullable = false, length = 32)
	public String getBatchId() {
		return this.batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	@Column(name = "PROP_CODE", nullable = false, length = 32)
	public String getPropCode() {
		return this.propCode;
	}

	public void setPropCode(String propCode) {
		this.propCode = propCode;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof B_BATCHS_METADATA_ID))
			return false;
		B_BATCHS_METADATA_ID castOther = (B_BATCHS_METADATA_ID) other;

		return ((this.getBatchId() == castOther.getBatchId()) || (this
				.getBatchId() != null
				&& castOther.getBatchId() != null && this.getBatchId().equals(
				castOther.getBatchId())))
				&& ((this.getPropCode() == castOther.getPropCode()) || (this
						.getPropCode() != null
						&& castOther.getPropCode() != null && this
						.getPropCode().equals(castOther.getPropCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getBatchId() == null ? 0 : this.getBatchId().hashCode());
		result = 37 * result
				+ (getPropCode() == null ? 0 : this.getPropCode().hashCode());
		return result;
	}

}