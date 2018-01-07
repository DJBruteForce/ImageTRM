package com.sunyard.insurance.scheduler.job;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Test {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File f = new File("E:/IMAGE/UWC/2015/01/09/46/45/fe9de2539c023630865c5293603afd5f_1/00002-6AAAB0FE-5AEC-48a3-9349-0B5CD507DC5F.jpg.jpg");
		try {
			FileUtils.forceDelete(f);
			//递归删除空的父目录
			String filePath = f.getParent();
			while(true) {
				File tPath = new File(filePath);
				if(tPath.listFiles().length==0) {
					FileUtils.forceDelete(tPath);
					filePath = tPath.getParent();
				} else {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
