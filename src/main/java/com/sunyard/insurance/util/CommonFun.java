package com.sunyard.insurance.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import org.apache.log4j.Logger;

public class CommonFun {

	private static final Logger log = Logger.getLogger(CommonFun.class);

	/**
	 * 获取文件MD5码
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileMD5(File file) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			FileInputStream in = new FileInputStream(file);
			byte[] buffer = new byte[1024 * 1024];
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				messageDigest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			log.error("获取文件[" + file.getAbsolutePath() + "+]MD5码异常!",e);
			return "";
		}
		return toHexString(messageDigest.digest());
	}

	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(hexChar[(b[i] & 0xf0) >>> 4]);
			sb.append(hexChar[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
