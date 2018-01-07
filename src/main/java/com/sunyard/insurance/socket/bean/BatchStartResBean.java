package com.sunyard.insurance.socket.bean;

import java.io.Serializable;

public class BatchStartResBean implements Serializable {

	private static final long serialVersionUID = -7781372982238917437L;
	private String SERVICECODE;
	private String RESOBJ;
	private String RESCODE;
	private String RESMSG;
	private String BATCHID;
	private String BATCHVER;
	
	public BatchStartResBean(String SERVICECODE,String RESOBJ,String RESCODE,String RESMSG,String BATCHID,String BATCHVER) {
		this.SERVICECODE = SERVICECODE;
		this.RESOBJ = RESOBJ;
		this.RESCODE = RESCODE;
		this.RESMSG = RESMSG;
		this.BATCHID = BATCHID;
		this.BATCHVER = BATCHVER;
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

}
