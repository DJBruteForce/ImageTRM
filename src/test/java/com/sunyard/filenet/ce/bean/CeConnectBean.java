package com.sunyard.filenet.ce.bean;

public class CeConnectBean {

	private String uri = "";
	private String username = "";
	private String password = "";
	private String jaas = null;
	private String domainName = "";
	private String objectStoreName = "";
	
	// 构造方法
	public CeConnectBean(String uri, String username, String password,
			String jaas, String domainName, String objectStoreName) {
		this.uri = uri;
		this.username = username;
		this.password = password;
		this.jaas = jaas;
		this.domainName = domainName;
		this.objectStoreName = objectStoreName;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJaas() {
		return jaas;
	}

	public void setJaas(String jaas) {
		this.jaas = jaas;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getObjectStoreName() {
		return objectStoreName;
	}

	public void setObjectStoreName(String objectStoreName) {
		this.objectStoreName = objectStoreName;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
