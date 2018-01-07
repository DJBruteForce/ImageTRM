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

/**
 * BBatchsSrc entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "B_BATCHS_SRC")
public class B_BATCHS_SRC implements java.io.Serializable {

	private static final long serialVersionUID = -1541429044641852429L;
	// Fields
	private B_BATCHS_SRC_ID id;
	private Integer interVer;
	private Integer resourceType;
	private Integer status;
	private String srcUrl;
	private Date updateDate;
	private String appCode;
	private String isMigrate;
	private String isPush;
	private String updateDateStr;//到达时间，用于清理排程

	// Constructors

	/** default constructor */
	public B_BATCHS_SRC() {
	}

	/** minimal constructor */
	public B_BATCHS_SRC(B_BATCHS_SRC_ID id, Integer interVer) {
		this.id = id;
		this.interVer = interVer;
	}

	/** full constructor */
	public B_BATCHS_SRC(B_BATCHS_SRC_ID id, Integer interVer,
			Integer resourceType, Integer status, String srcUrl,
			Date updateDate,String appCode, String isMigrate, String isPush, String updateDateStr) {
		this.id = id;
		this.interVer = interVer;
		this.resourceType = resourceType;
		this.status = status;
		this.srcUrl = srcUrl;
		this.updateDate = updateDate;
		this.appCode = appCode;
		this.isMigrate = isMigrate;
		this.isPush = isPush;
		this.updateDateStr = updateDateStr;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "batchId", column = @Column(name = "BATCH_ID", nullable = false, length = 32)),
			@AttributeOverride(name = "orgCode", column = @Column(name = "ORG_CODE", nullable = false, length = 8)),
			@AttributeOverride(name = "batchVer", column = @Column(name = "BATCH_VER", nullable = false, precision = 22, scale = 0)) })
	public B_BATCHS_SRC_ID getId() {
		return this.id;
	}

	public void setId(B_BATCHS_SRC_ID id) {
		this.id = id;
	}

	@Column(name = "INTER_VER", nullable = false, precision = 22, scale = 0)
	public Integer getInterVer() {
		return this.interVer;
	}

	public void setInterVer(Integer interVer) {
		this.interVer = interVer;
	}

	@Column(name = "RESOURCE_TYPE", precision = 22, scale = 0)
	public Integer getResourceType() {
		return this.resourceType;
	}

	public void setResourceType(Integer resourceType) {
		this.resourceType = resourceType;
	}

	@Column(name = "STATUS", precision = 22, scale = 0)
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "SRC_URL", length = 254)
	public String getSrcUrl() {
		return this.srcUrl;
	}

	public void setSrcUrl(String srcUrl) {
		this.srcUrl = srcUrl;
	}

	// @Temporal(TemporalType.TIMESTAMP)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATE_DATE", length = 7)
	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@Column(name = "IS_MIGRATE", length = 1)
	public String getIsMigrate() {
		return this.isMigrate;
	}

	public void setIsMigrate(String isMigrate) {
		this.isMigrate = isMigrate;
	}

	@Column(name = "IS_PUSH", length = 1)
	public String getIsPush() {
		return this.isPush;
	}

	public void setIsPush(String isPush) {
		this.isPush = isPush;
	}

	@Column(name = "APP_CODE", length = 16)
	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	@Column(name = "UPDATE_DATE_STR", length = 20)
	public String getUpdateDateStr() {
		return updateDateStr;
	}

	public void setUpdateDateStr(String updateDateStr) {
		this.updateDateStr = updateDateStr;
	}
}
