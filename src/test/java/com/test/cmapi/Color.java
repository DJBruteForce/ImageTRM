package com.test.cmapi;

public enum Color {

	CANCEL("1001", "已取消"), GREEN("1002", "已受理"), BLANK("1003", "核保中"), YELLO(
			"1004", "核保通过");

	private String code;
	private String name;

	private Color(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
