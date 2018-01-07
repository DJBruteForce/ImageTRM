package com.sunyard.insurance.entity;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "TRM_TASK_PLOT")
public class TRM_TASK_PLOT implements Serializable {

	private static final long serialVersionUID = -692180583354607873L;

	private TRM_TASK_PLOT_ID id;
	private Integer app_priority;//业务优先级，越小越高
	private Integer cache_days;//缓存天数
	private String target_org;//目标机构号
	private String task_plot;//自定义规则【推送规则】
	private String plot_type;//规则类型；1-target_org生效；2-task_plot生效
	private String status;

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "task_id", column = @Column(name = "TASK_ID", nullable = false)),
			@AttributeOverride(name = "app_code", column = @Column(name = "APP_CODE", nullable = false, length = 16)) })
	public TRM_TASK_PLOT_ID getId() {
		return id;
	}

	public void setId(TRM_TASK_PLOT_ID id) {
		this.id = id;
	}

	@Column(name = "APP_PRIORITY", nullable = false)
	public Integer getApp_priority() {
		return app_priority;
	}

	public void setApp_priority(Integer appPriority) {
		app_priority = appPriority;
	}

	@Column(name = "CACHE_DAYS")
	public Integer getCache_days() {
		return cache_days;
	}

	public void setCache_days(Integer cacheDays) {
		cache_days = cacheDays;
	}

	@Column(name = "TARGET_ORG", length = 8)
	public String getTarget_org() {
		return target_org;
	}

	public void setTarget_org(String targetOrg) {
		target_org = targetOrg;
	}

	@Column(name = "TASK_PLOT", length = 100)
	public String getTask_plot() {
		return task_plot;
	}

	public void setTask_plot(String taskPlot) {
		task_plot = taskPlot;
	}

	@Column(name = "PLOT_TYPE", length = 4)
	public String getPlot_type() {
		return plot_type;
	}

	public void setPlot_type(String plotType) {
		plot_type = plotType;
	}

	@Column(name = "STATUS", length = 4, nullable = false)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
