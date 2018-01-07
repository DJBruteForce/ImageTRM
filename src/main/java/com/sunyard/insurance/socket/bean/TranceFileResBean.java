package com.sunyard.insurance.socket.bean;

import java.io.Serializable;

public class TranceFileResBean implements Serializable {

	private static final long serialVersionUID = 4044659894383229276L;
	private String SERVICECODE;
	private String RESOBJ;
	private String RESCODE;
	private String RESMSG;
	private String FILENAME;
	private String FILESIZENOW;
	private String MD5STRNOW;
	private String CATCHSIZE;

	public TranceFileResBean(String sERVICECODE, String rESOBJ, String rESCODE,
			String rESMSG, String fILENAME, String fILESIZENOW,
			String mD5STRNOW, String cATCHSIZE) {
		super();
		SERVICECODE = sERVICECODE;
		RESOBJ = rESOBJ;
		RESCODE = rESCODE;
		RESMSG = rESMSG;
		FILENAME = fILENAME;
		FILESIZENOW = fILESIZENOW;
		MD5STRNOW = mD5STRNOW;
		CATCHSIZE = cATCHSIZE;
	}

	public String getSERVICECODE() {
		return SERVICECODE;
	}

	public void setSERVICECODE(String sERVICECODE) {
		SERVICECODE = sERVICECODE;
	}

	public String getRESOBJ() {
		return RESOBJ;
	}

	public void setRESOBJ(String rESOBJ) {
		RESOBJ = rESOBJ;
	}

	public String getRESCODE() {
		return RESCODE;
	}

	public void setRESCODE(String rESCODE) {
		RESCODE = rESCODE;
	}

	public String getRESMSG() {
		return RESMSG;
	}

	public void setRESMSG(String rESMSG) {
		RESMSG = rESMSG;
	}

	public String getFILENAME() {
		return FILENAME;
	}

	public void setFILENAME(String fILENAME) {
		FILENAME = fILENAME;
	}

	public String getFILESIZENOW() {
		return FILESIZENOW;
	}

	public void setFILESIZENOW(String fILESIZENOW) {
		FILESIZENOW = fILESIZENOW;
	}

	public String getCATCHSIZE() {
		return CATCHSIZE;
	}

	public void setCATCHSIZE(String cATCHSIZE) {
		CATCHSIZE = cATCHSIZE;
	}

	public String getMD5STRNOW() {
		return MD5STRNOW;
	}

	public void setMD5STRNOW(String mD5STRNOW) {
		MD5STRNOW = mD5STRNOW;
	}

}
