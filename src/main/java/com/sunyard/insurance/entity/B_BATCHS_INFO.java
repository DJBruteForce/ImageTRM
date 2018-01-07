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
@Table(name = "B_BATCHS_INFO")
public class B_BATCHS_INFO implements java.io.Serializable {

	
	private static final long serialVersionUID = 8247992184975681418L;
	
	private B_BATCHS_INFO_ID id;
	private Integer interVer;
	private String busiNo;
	private String appCode;
	private Integer status;
	private String createUser;
	private Date createDate;
	private String modUser;
	private String bizOrg;
	private Date modDate;

	// Constructors

	/** default constructor */
	public B_BATCHS_INFO() {
	}

	/** minimal constructor */
	public B_BATCHS_INFO(B_BATCHS_INFO_ID id) {
		this.id = id;
	}

	/** full constructor */
	public B_BATCHS_INFO(B_BATCHS_INFO_ID id, Integer interVer,
			String busiNo, String appCode, Integer status,
			String createUser, Date createDate, String modUser, Date modDate,
			String orgCode, Integer cpsFlag, String disStatus,String bizOrg) {
		this.id = id;
		this.interVer = interVer;
		this.busiNo = busiNo;
		this.appCode = appCode;
		this.status = status;
		this.createUser = createUser;
		this.createDate = createDate;
		this.modUser = modUser;
		this.modDate = modDate;
		this.bizOrg = bizOrg;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "batchId", column = @Column(name = "BATCH_ID", nullable = false, length = 32)),
			@AttributeOverride(name = "batchVer", column = @Column(name = "BATCH_VER", nullable = false, precision = 22, scale = 0)) })
	public B_BATCHS_INFO_ID getId() {
		return this.id;
	}

	public void setId(B_BATCHS_INFO_ID id) {
		this.id = id;
	}

	@Column(name = "INTER_VER", precision = 22, scale = 0)
	public Integer getInterVer() {
		return this.interVer;
	}

	public void setInterVer(Integer interVer) {
		this.interVer = interVer;
	}

	@Column(name = "BUSI_NO", length = 32)
	public String getBusiNo() {
		return this.busiNo;
	}

	public void setBusiNo(String busiNo) {
		this.busiNo = busiNo;
	}

	@Column(name = "APP_CODE", length = 16)
	public String getAppCode() {
		return this.appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	@Column(name = "STATUS", precision = 22, scale = 0)
	public Integer getStatus() {
		return this.status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Column(name = "CREATE_USER", length = 16)
	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATE_DATE", length = 7)
	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Column(name = "MOD_USER", length = 16)
	public String getModUser() {
		return this.modUser;
	}

	public void setModUser(String modUser) {
		this.modUser = modUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MOD_DATE", length = 7)
	public Date getModDate() {
		return this.modDate;
	}

	public void setModDate(Date modDate) {
		this.modDate = modDate;
	}
	
	@Column(name = "BIZ_ORG", length = 10)
	public String getBizOrg() {
		return bizOrg;
	}

	public void setBizOrg(String bizOrg) {
		this.bizOrg = bizOrg;
	}
	
}