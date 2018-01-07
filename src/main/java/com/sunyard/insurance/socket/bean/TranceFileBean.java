package com.sunyard.insurance.socket.bean;

import java.io.Serializable;

public class TranceFileBean implements Serializable {

	private static final long serialVersionUID = -3218439574460375234L;

	private String SERVICECODE;
	private String REQOBJ;
	private String BATCHID;
	private String BATCHVER;
	private String FILENAME;
	private Long FILESIZE;
	private String MD5STR;
	
	public TranceFileBean(String sERVICECODE, String rEQOBJ, String bATCHID,
			String bATCHVER, String fILENAME, Long fILESIZE, String mD5STR) {
		super();
		SERVICECODE = sERVICECODE;
		REQOBJ = rEQOBJ;
		BATCHID = bATCHID;
		BATCHVER = bATCHVER;
		FILENAME = fILENAME;
		FILESIZE = fILESIZE;
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

	public Long getFILESIZE() {
		return FILESIZE;
	}

	public void setFILESIZE(Long fILESIZE) {
		FILESIZE = fILESIZE;
	}

	public String getMD5STR() {
		return MD5STR;
	}

	public void setMD5STR(String mD5STR) {
		MD5STR = mD5STR;
	}
	
}
