package com.sunyard.insurance.socket.bean;

import java.io.Serializable;

public class FileBean implements Serializable {

	private static final long serialVersionUID = -3261666558099472312L;
	private String FILENAME;
	private String CHECKFLAG;

	public FileBean(String fILENAME, String cHECKFLAG) {
		super();
		FILENAME = fILENAME;
		CHECKFLAG = cHECKFLAG;
	}
	
	public String getFILENAME() {
		return FILENAME;
	}

	public void setFILENAME(String fILENAME) {
		FILENAME = fILENAME;
	}

	public String getCHECKFLAG() {
		return CHECKFLAG;
	}

	public void setCHECKFLAG(String cHECKFLAG) {
		CHECKFLAG = cHECKFLAG;
	}

}
