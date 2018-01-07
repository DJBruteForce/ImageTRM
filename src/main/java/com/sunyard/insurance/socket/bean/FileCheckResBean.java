package com.sunyard.insurance.socket.bean;

import java.io.Serializable;

public class FileCheckResBean implements Serializable {

	private static final long serialVersionUID = 4980512301482061204L;
	private String SERVICECODE;
	private String RESOBJ;
	private String RESCODE;
	private String RESMSG;

	public FileCheckResBean(String sERVICECODE, String rESOBJ, String rESCODE,
			String rESMSG) {
		super();
		SERVICECODE = sERVICECODE;
		RESOBJ = rESOBJ;
		RESCODE = rESCODE;
		RESMSG = rESMSG;
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
