package com.sunyard.insurance.filenet.ce.depend;

import java.io.File;

public class SydFileBean {

	private File file = null;
	private String pathname = "";
	private String batchVersion = "";
	private int batchVersionInt = 0;
	
	public SydFileBean(String pathname) {
		this(new File(pathname));
	}
	
	public SydFileBean(File file) {
		super();
		if (file == null) {
			throw new NullPointerException();
		}
		String pathname = file.getName();
		setFile(file);
		setPathname(pathname);
		int begin = pathname.lastIndexOf('_') + 1;
    	int end = pathname.lastIndexOf('.');
    	if (begin < end) {
    		String batchVersion = pathname.substring(begin, end);
    		setBatchVersion(batchVersion);
    		setBatchVersionInt(Integer.parseInt(batchVersion));
    	}
	}
	
	public File getFile() {
		return file;
	}

	private void setFile(File file) {
		this.file = file;
	}

	public String getPathname() {
		return pathname;
	}

	private void setPathname(String pathname) {
		this.pathname = pathname;
	}

	public String getBatchVersion() {
		return batchVersion;
	}

	private void setBatchVersion(String batchVersion) {
		this.batchVersion = batchVersion;
	}

	public int getBatchVersionInt() {
		return batchVersionInt;
	}

	private void setBatchVersionInt(int batchVersionInt) {
		this.batchVersionInt = batchVersionInt;
	}

}
