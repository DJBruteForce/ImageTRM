package com.sunyard.insurance.socket.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BatchCheckBean implements Serializable {

	private static final long serialVersionUID = -4456641737052127343L;

	private String SERVICECODE;
	private String REQOBJ;
	private String BATCHID;
	private String BATCHVER;
	private Integer FILENUM;
	private List<FileBean> FILES = new ArrayList<FileBean>();
	
	public BatchCheckBean(String sERVICECODE, String rEQOBJ, String bATCHID,
			String bATCHVER, Integer fILENUM) {
		super();
		SERVICECODE = sERVICECODE;
		REQOBJ = rEQOBJ;
		BATCHID = bATCHID;
		BATCHVER = bATCHVER;
		FILENUM = fILENUM;
	}
	
	public BatchCheckBean(String sERVICECODE, String rEQOBJ, String bATCHID,
			String bATCHVER, Integer fILENUM, List<FileBean> FILES) {
		super();
		SERVICECODE = sERVICECODE;
		REQOBJ = rEQOBJ;
		BATCHID = bATCHID;
		BATCHVER = bATCHVER;
		FILENUM = fILENUM;
		this.FILES = FILES;
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

	public Integer getFILENUM() {
		return FILENUM;
	}

	public void setFILENUM(Integer fILENUM) {
		FILENUM = fILENUM;
	}

	public List<FileBean> getFILES() {
		return FILES;
	}

	public void setFILES(List<FileBean> fILES) {
		FILES = fILES;
	}

}
