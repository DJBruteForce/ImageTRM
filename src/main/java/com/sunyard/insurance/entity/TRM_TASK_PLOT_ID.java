package com.sunyard.insurance.entity;

import javax.persistence.Column;

public class TRM_TASK_PLOT_ID implements java.io.Serializable {

	private static final long serialVersionUID = 4360848049779981521L;

	private Integer task_id;
	private String app_code;

	public TRM_TASK_PLOT_ID() {
	}

	public TRM_TASK_PLOT_ID(Integer task_id, String app_code) {
		this.task_id = task_id;
		this.app_code = app_code;
	}

	@Column(name = "TASK_ID", nullable = false)
	public Integer getTask_id() {
		return task_id;
	}

	public void setTask_id(Integer taskId) {
		task_id = taskId;
	}

	@Column(name = "APP_CODE", nullable = false, length = 16)
	public String getApp_code() {
		return app_code;
	}

	public void setApp_code(String appCode) {
		app_code = appCode;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof TRM_TASK_PLOT_ID))
			return false;
		TRM_TASK_PLOT_ID castOther = (TRM_TASK_PLOT_ID) other;

		return ((this.getTask_id() == castOther.getTask_id()) || (this
				.getTask_id() != null
				&& castOther.getTask_id() != null && this.getTask_id().equals(
				castOther.getTask_id())))
				&& ((this.getApp_code() == castOther.getApp_code()) || (this
						.getApp_code() != null
						&& castOther.getApp_code() != null && this
						.getApp_code().equals(castOther.getApp_code())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getTask_id() == null ? 0 : this.getTask_id().hashCode());
		result = 37 * result
				+ (getApp_code() == null ? 0 : this.getApp_code().hashCode());
		return result;
	}
}
