package com.sunyard.insurance.filenet.faf.cesupport.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileUtil {

	public static boolean isDirectory(File file) {
		if (null == file || !file.exists() || !file.isDirectory()) {
			return false;
		}
		return true;
	}

	public static boolean isFile(File file) {
		if (null == file || !file.exists() || !file.isFile()) {
			return false;
		}
		return true;
	}
	
	public static String getFileSuffix(String path) {
		File file = new File(path);
		return getFileSuffix(file);
	}
	
	public static String getFileSuffix(File file) {
		String suffix = "";
		if (isFile(file)) {
			String fileName = file.getName();
			int index = fileName.lastIndexOf('.');
			int start = index + 1;
			if (index > 0 && start < fileName.length()) {
				suffix = fileName.substring(start);
			}
		}
		return suffix;
	}
	
	public static File getFileByUsrDir(String path) {
		String srcPath = System.getProperty("user.dir");
		File file = new File(srcPath + path);
		if (!isFile(file)) {
			return null;
		}
		return file;
	}

	public static InputStream getFileInByUsrDir(String path) throws FileNotFoundException {
		InputStream in = null;
		String srcPath = System.getProperty("user.dir");
		File file = new File(srcPath + path);
		if (isFile(file)) {
			in = new FileInputStream(file);
		}
		return in;
	}

	public static InputStream getFileInByClsPth(String path) throws IOException {
		InputStream in = null;
		try {
			in = FileUtil.class.getClassLoader().getResourceAsStream(path);
		} finally {
			if (in != null) {
				in.close();
			}
		}
		return in;
	}
	
	public static URL getFileUrlByClsPth(String path) {
		URL url = FileUtil.class.getClassLoader().getResource(path);
		return url;
	}
	
	public static void writeFileWithInputStream(InputStream in, File file, boolean append) throws IOException {
		if (isFile(file)) {
			throw new RuntimeException(new StringBuilder().append("Target file is not a file, path:").append(file.getAbsolutePath()).toString());
		}

		if (!append) {
			file.delete();
			file.createNewFile();
		}

		byte[] data = new byte[1024 * 1024];
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file, append);
			if (in != null) {
				int n = 0;
				while ((n = in.read(data)) != -1) {
					out.write(data, 0, n);
				}
			}
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} finally {
				in = null;
			}
			try {
				if (out != null) {
					try {
						out.flush();
					} finally {
						out.close();
					}
				}
			} finally {
				out = null;
			}
		}
	}
	
	

}
