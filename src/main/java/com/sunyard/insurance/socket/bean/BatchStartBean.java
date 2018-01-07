package com.sunyard.insurance.socket.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BatchStartBean implements Serializable {

	private static final long serialVersionUID = -7889411459495471403L;

	private String SERVICECODE;
	private String REQOBJ;
	private String BATCHID;
	private String BATCHVER;
	private List<MetadataBean> METADATAS = new ArrayList<MetadataBean>();
	
	public BatchStartBean(String SERVICECODE,String REQOBJ,String BATCHID,String BATCHVER,List<MetadataBean> METADATAS) {
		this.SERVICECODE = SERVICECODE;
		this.REQOBJ = REQOBJ;
		this.BATCHID = BATCHID;
		this.BATCHVER = BATCHVER;
		this.METADATAS = METADATAS;
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

	public List<MetadataBean> getMETADATAS() {
		return METADATAS;
	}

	public void setMETADATAS(List<MetadataBean> mETADATAS) {
		METADATAS = mETADATAS;
	}

}
