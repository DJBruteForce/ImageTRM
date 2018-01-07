package com.sunyard.insurance.filenet.faf.cesupport.entity;

public class CEConnBean extends CEObject {

	protected String uri = "";
	
	protected String username = "";
	
	protected String password = "";
	
	protected String jaas = null;
	
	protected String domainName = "";
	
	protected String objectStoreName = "";
	
	public CEConnBean(String uri, String domainName, String objectStoreName) {
		this.uri = uri;
		this.domainName = domainName;
		this.objectStoreName = objectStoreName;
	}

	public CEConnBean(String uri, String username,
			String password, String jaas, String domainName,
			String objectStoreName) {
		this.uri = uri;
		this.username = username;
		this.password = password;
		this.jaas = jaas;
		this.domainName = domainName;
		this.objectStoreName = objectStoreName;
	}

	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the jaas
	 */
	public String getJaas() {
		return jaas;
	}

	/**
	 * @param jaas the jaas to set
	 */
	public void setJaas(String jaas) {
		this.jaas = jaas;
	}

	/**
	 * @return the domainName
	 */
	public String getDomainName() {
		return domainName;
	}

	/**
	 * @param domainName the domainName to set
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	/**
	 * @return the objectStoreName
	 */
	public String getObjectStoreName() {
		return objectStoreName;
	}

	/**
	 * @param objectStoreName the objectStoreName to set
	 */
	public void setObjectStoreName(String objectStoreName) {
		this.objectStoreName = objectStoreName;
	}

	protected void validate(Object... args) {
		
	}
	
}
