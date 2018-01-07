package com.sunyard.insurance.socket.bean;

import java.io.Serializable;

public class BatchCheckBean implements Serializable {

	private static final long serialVersionUID = -4456641737052127343L;

	private String SERVICECODE;
	private String REQOBJ;
	private String BATCHID;
	private String BATCHVER;
	private String FILENUM;

	// private List<FileBean> FILES = new ArrayList<FileBean>();
	
	public BatchCheckBean(String sERVICECODE, String rEQOBJ, String bATCHID,
			String bATCHVER, String fILENUM) {
		super();
		SERVICECODE = sERVICECODE;
		REQOBJ = rEQOBJ;
		BATCHID = bATCHID;
		BATCHVER = bATCHVER;
		FILENUM = fILENUM;
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

	public String getFILENUM() {
		return FILENUM;
	}

	public void setFILENUM(String fILENUM) {
		FILENUM = fILENUM;
	}

}
