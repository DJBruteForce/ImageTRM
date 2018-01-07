package com.sunyard.insurance.filenet.ce.depend;

import java.util.concurrent.Callable;

import com.sunyard.insurance.filenet.ce.proxy.CeProxy;

public class DelFldCall implements Callable<Object> {

	private String fldID = null;
	
	public DelFldCall (String fldID) {
		this.fldID = fldID;
	}
	
	public Object call() throws Exception {
		try {
			CeProxy proxy = CeProxy.authIn();
			proxy.deleteFolder(fldID);
		} finally {
			CeProxy.authOut();
		}
		return null;
	}

}
