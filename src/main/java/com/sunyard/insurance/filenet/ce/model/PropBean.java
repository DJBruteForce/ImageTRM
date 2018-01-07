package com.sunyard.insurance.filenet.ce.model;

public class PropBean {

	private String code = "";
	
	private String value = "";

	public PropBean() {
		super();
	}
	
	public PropBean(String code, String value) {
		super();
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
