package com.sunyard.insurance.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "TRM_INFORM_RECORD")
// 影像到达通知表
public class TRM_INFORM_RECORD implements Serializable {

	private static final long serialVersionUID = -4649578065074407552L;

	private Integer inform_id;

	private String batch_id;
	private Integer batch_ver;
	private Integer inter_ver;
	private String busi_no;
	private String app_code;
	private String svr_id;
	private String org_code;
	private Date inform_time;
	private String status;
	private String err_msg;
	/** 大地支持TRM_INFORM_RECORD表中增加BUSI_DATE字段，表示批次年份 */
	private String busi_date;

	@Id
	@GeneratedValue
	public Integer getInform_id() {
		return inform_id;
	}

	public void setInform_id(Integer informId) {
		inform_id = informId;
	}

	@Column(name = "BATCH_ID", length = 32, nullable = false)
	public String getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(String batchId) {
		batch_id = batchId;
	}

	@Column(name = "BATCH_VER", length = 4, nullable = false)
	public Integer getBatch_ver() {
		return batch_ver;
	}

	public void setBatch_ver(Integer batchVer) {
		batch_ver = batchVer;
	}

	@Column(name = "INTER_VER", length = 4, nullable = false)
	public Integer getInter_ver() {
		return inter_ver;
	}

	public void setInter_ver(Integer interVer) {
		inter_ver = interVer;
	}

	@Column(name = "BUSI_NO", length = 32, nullable = false)
	public String getBusi_no() {
		return busi_no;
	}

	public void setBusi_no(String busiNo) {
		busi_no = busiNo;
	}

	@Column(name = "APP_CODE", length = 16, nullable = false)
	public String getApp_code() {
		return app_code;
	}

	public void setApp_code(String appCode) {
		app_code = appCode;
	}

	@Column(name = "SVR_ID", length = 16, nullable = false)
	public String getSvr_id() {
		return svr_id;
	}

	public void setSvr_id(String svrId) {
		svr_id = svrId;
	}

	@Column(name = "ORG_CODE", length = 8, nullable = false)
	public String getOrg_code() {
		return org_code;
	}

	public void setOrg_code(String orgCode) {
		org_code = orgCode;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "INFORM_TIME", length = 20)
	public Date getInform_time() {
		return inform_time;
	}

	public void setInform_time(Date informTime) {
		inform_time = informTime;
	}

	@Column(name = "STATUS", length = 2, nullable = false)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Column(name = "ERR_MSG", length = 254)
	public String getErr_msg() {
		return err_msg;
	}

	public void setErr_msg(String errMsg) {
		err_msg = errMsg;
	}
	@Column(name = "BUSI_DATE", length = 4)
	public String getBusi_date() {
		return busi_date;
	}

	public void setBusi_date(String busi_date) {
		this.busi_date = busi_date;
	}

}
