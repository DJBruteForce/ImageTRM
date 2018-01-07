package com.sunyard.insurance.socket.bean;

import java.io.Serializable;

public class MetadataBean implements Serializable {

	private static final long serialVersionUID = 8928911725561237220L;
	private String CODE;
	private String VALUE;
	
	public String getCODE() {
		return CODE;
	}

	public void setCODE(String cODE) {
		CODE = cODE;
	}

	public String getVALUE() {
		return VALUE;
	}

	public void setVALUE(String vALUE) {
		VALUE = vALUE;
	}

}
