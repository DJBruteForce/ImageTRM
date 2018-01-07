package com.sunyard.insurance.filenet.ce.depend;

import java.util.Comparator;

public class SydFileComparator implements Comparator<SydFileBean> {

	public int compare(SydFileBean file1, SydFileBean file2) {
		int version1 = file1.getBatchVersionInt();
		int version2 = file2.getBatchVersionInt();
		if (version1 > version2) {
			return -1;
		} else if (version1 < version2) {
			return 1;
		}
		return 0;
	}
	
}
