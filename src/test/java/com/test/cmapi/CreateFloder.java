package com.test.cmapi;

import java.io.File;

public class CreateFloder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "C:/Users/Administrator/Desktop/SunUSM更新20140401/"
		+"com.sunyard.insurance.service.impl";
		
		File f = new File(str.replace(".", "/"));
		
		if(!f.exists()) {
			f.mkdirs();
		}
	}

}
