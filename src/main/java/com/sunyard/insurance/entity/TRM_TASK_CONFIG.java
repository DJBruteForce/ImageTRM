package com.sunyard.insurance.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TRM_TASK_CONFIG")
public class TRM_TASK_CONFIG implements Serializable {

	private static final long serialVersionUID = -4351483758471856644L;

	private Integer task_id;
	private String org_code;
	private String task_type;
	private String svr_id;
	private String start_time;
	private String end_time;
	private String status;

	@Id
	@GeneratedValue
	public Integer getTask_id() {
		return task_id;
	}

	public void setTask_id(Integer taskId) {
		task_id = taskId;
	}

	@Column(name = "ORG_CODE", length = 16, nullable = false)
	public String getOrg_code() {
		return org_code;
	}

	public void setOrg_code(String orgCode) {
		org_code = orgCode;
	}

	@Column(name = "TASK_TYPE", length = 4, nullable = false)
	public String getTask_type() {
		return task_type;
	}

	public void setTask_type(String taskType) {
		task_type = taskType;
	}

	@Column(name = "SVR_ID", length = 16, nullable = false)
	public String getSvr_id() {
		return svr_id;
	}

	public void setSvr_id(String svrId) {
		svr_id = svrId;
	}

	@Column(name = "START_TIME", length = 20, nullable = false)
	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String startTime) {
		start_time = startTime;
	}

	@Column(name = "END_TIME", length = 20, nullable = false)
	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String endTime) {
		end_time = endTime;
	}

	@Column(name = "STATUS", length = 4, nullable = false)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	};

}
