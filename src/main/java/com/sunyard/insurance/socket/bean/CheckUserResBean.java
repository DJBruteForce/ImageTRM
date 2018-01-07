package com.sunyard.insurance.socket.bean;

import java.io.Serializable;

public class CheckUserResBean implements Serializable {

	private static final long serialVersionUID = -4494240164415490517L;
	
	private String SERVICECODE;
	private String RESOBJ;
	private String RESCODE;
	private String RESMSG;
	
	public CheckUserResBean(String SERVICECODE,String RESOBJ,String RESCODE,String RESMSG) {
		this.SERVICECODE = SERVICECODE;
		this.RESOBJ = RESOBJ;
		this.RESCODE = RESCODE;
		this.RESMSG = RESMSG;
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

}
