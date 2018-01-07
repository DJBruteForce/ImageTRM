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
@Table(name = "TRM_TASK_RECORD")
public class TRM_TASK_RECORD implements Serializable {

	private static final long serialVersionUID = -5496127861587310896L;

	private Integer record_id;
	private String svr_id;
	private String batch_id;
	private Integer batch_ver;
	private Integer inter_ver;
	private String busi_no;
	private String app_code;
	private String task_type;
	private Date task_time;
	private String result_code;
	private String error_msg;
	private String task_time_str;//记录时间，格式YYYY-mm-dd hh:mm:ss

	@Id
	@GeneratedValue
	public Integer getRecord_id() {
		return record_id;
	}

	public void setRecord_id(Integer recordId) {
		record_id = recordId;
	}

	@Column(name = "SVR_ID", length = 16, nullable = false)
	public String getSvr_id() {
		return svr_id;
	}

	public void setSvr_id(String svrId) {
		svr_id = svrId;
	}

	@Column(name = "BATCH_ID", length = 32, nullable = false)
	public String getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(String batchId) {
		batch_id = batchId;
	}

	@Column(name = "BATCH_VER", nullable = false)
	public Integer getBatch_ver() {
		return batch_ver;
	}

	public void setBatch_ver(Integer batchVer) {
		batch_ver = batchVer;
	}

	@Column(name = "INTER_VER", nullable = false)
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

	@Column(name = "TASK_TYPE", length = 4, nullable = false)
	public String getTask_type() {
		return task_type;
	}

	public void setTask_type(String taskType) {
		task_type = taskType;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TASK_TIME", length = 20)
	public Date getTask_time() {
		return task_time;
	}

	public void setTask_time(Date taskTime) {
		task_time = taskTime;
	}
	
	@Column(name = "RESULT_CODE", length = 10, nullable = false)
	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String resultCode) {
		result_code = resultCode;
	}

	@Column(name = "ERROR_MSG", length = 254)
	public String getError_msg() {
		return error_msg;
	}

	public void setError_msg(String errorMsg) {
		error_msg = errorMsg;
	}
	@Column(name = "TASK_TIME_STR", length = 20)
	public String getTask_time_str() {
		return task_time_str;
	}

	public void setTask_time_str(String taskTimeStr) {
		task_time_str = taskTimeStr;
	}
}
