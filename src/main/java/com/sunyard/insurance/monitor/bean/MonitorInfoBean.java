package com.sunyard.insurance.monitor.bean;

import java.io.Serializable;

public class MonitorInfoBean implements Serializable {

	private static final long serialVersionUID = 8208002463798802419L;
	private String svrId;
	private String svrName;
	private String osName;
	private String cpuPerc;
	private String ramPerc;
	private String discPerc;
	private String motTime;

	public String getSvrId() {
		return svrId;
	}

	public void setSvrId(String svrId) {
		this.svrId = svrId;
	}

	public String getSvrName() {
		return svrName;
	}

	public void setSvrName(String svrName) {
		this.svrName = svrName;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getCpuPerc() {
		return cpuPerc;
	}

	public void setCpuPerc(String cpuPerc) {
		this.cpuPerc = cpuPerc;
	}

	public String getRamPerc() {
		return ramPerc;
	}

	public void setRamPerc(String ramPerc) {
		this.ramPerc = ramPerc;
	}

	public String getDiscPerc() {
		return discPerc;
	}

	public void setDiscPerc(String discPerc) {
		this.discPerc = discPerc;
	}

	public String getMotTime() {
		return motTime;
	}

	public void setMotTime(String motTime) {
		this.motTime = motTime;
	}

}
