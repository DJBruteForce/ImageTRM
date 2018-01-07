package com.sunyard.insurance.socket.bean;

import java.io.Serializable;

public class FileCheckBean implements Serializable {

	private static final long serialVersionUID = -5080407288520444319L;
	private String SERVICECODE;
	private String REQOBJ;
	private String BATCHID;
	private String BATCHVER;
	private String FILENAME;
	private String MD5STR;

	public FileCheckBean(String sERVICECODE, String rEQOBJ, String bATCHID,
			String bATCHVER, String fILENAME, String mD5STR) {
		super();
		SERVICECODE = sERVICECODE;
		REQOBJ = rEQOBJ;
		BATCHID = bATCHID;
		BATCHVER = bATCHVER;
		FILENAME = fILENAME;
		MD5STR = mD5STR;
	}

	public String getSERVICECODE() {
		return SERVICECODE;
	}

	public void setSERVICECODE(String sERVICECODE) {
		SERVICECODE = sERVICECODE;
	}

	public String getREQOBJ() {
		return REQOBJ;
	}

	public void setREQOBJ(String rEQOBJ) {
		REQOBJ = rEQOBJ;
	}

	public String getBATCHID() {
		return BATCHID;
	}

	public void setBATCHID(String bATCHID) {
		BATCHID = bATCHID;
	}

	public String getBATCHVER() {
		return BATCHVER;
	}

	public void setBATCHVER(String bATCHVER) {
		BATCHVER = bATCHVER;
	}

	public String getFILENAME() {
		return FILENAME;
	}

	public void setFILENAME(String fILENAME) {
		FILENAME = fILENAME;
	}

	public String getMD5STR() {
		return MD5STR;
	}

	public void setMD5STR(String mD5STR) {
		MD5STR = mD5STR;
	}

}
