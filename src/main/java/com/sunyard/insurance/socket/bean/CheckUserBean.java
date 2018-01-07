package com.sunyard.insurance.socket.bean;

import java.io.Serializable;

public class CheckUserBean implements Serializable {

	private static final long serialVersionUID = -9037546530456025963L;
	private String SERVICECODE;
	private String REQOBJ;
	private String USERCODE;
	private String PASSWORD;
	
	
	public CheckUserBean(String SERVICECODE,String REQOBJ,String USERCODE,String PASSWORD) {
		this.SERVICECODE = SERVICECODE;
		this.REQOBJ = REQOBJ;
		this.USERCODE = USERCODE;
		this.PASSWORD = PASSWORD;
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

	public String getUSERCODE() {
		return USERCODE;
	}

	public void setUSERCODE(String uSERCODE) {
		USERCODE = uSERCODE;
	}

	public String getPASSWORD() {
		return PASSWORD;
	}

	public void setPASSWORD(String pASSWORD) {
		PASSWORD = pASSWORD;
	}

}
