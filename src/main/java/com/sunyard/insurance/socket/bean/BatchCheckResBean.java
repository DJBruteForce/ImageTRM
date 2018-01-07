package com.sunyard.insurance.socket.bean;

import java.io.Serializable;

public class BatchCheckResBean implements Serializable {

	private static final long serialVersionUID = -8260306672011531939L;
	private String SERVICECODE;
	private String RESOBJ;
	private String RESCODE;
	private String RESMSG;

	// private List<FileBean> FILES = new ArrayList<FileBean>();

	public BatchCheckResBean(String sERVICECODE, String rESOBJ, String rESCODE,
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
