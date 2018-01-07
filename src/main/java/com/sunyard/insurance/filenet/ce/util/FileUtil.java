package com.sunyard.insurance.filenet.ce.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class FileUtil {
	
	public static final int BUFFER_SIZE = 1024 * 1024;

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

	public static InputStream getFileInByUsrDir(String path)
			throws FileNotFoundException {
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
	
	public static void writeFile(InputStream in, File file, boolean append)
			throws IOException {
		if ((!append) && (isFile(file))) {
			file.delete();
			file.createNewFile();
		}

		BufferedOutputStream out = null;
		try {
			if (in != null) {
				byte[] data = new byte[BUFFER_SIZE];
				out = new BufferedOutputStream(new FileOutputStream(file,
						append), BUFFER_SIZE);
				in = new BufferedInputStream(in, BUFFER_SIZE);
				int n = 0;
				while ((n = in.read(data)) != -1) {
					out.write(data, 0, n);
				}
			}
		} finally {
			try {
				if (in != null)
					in.close();
			} finally {
				in = null;
			}
			try {
				if (out != null)
					try {
						out.flush();
					} finally {
						out.close();
					}
			} finally {
				out = null;
			}
		}
	}

	public static void unzip(String inputPath, String outputPath)
			throws IOException {
		File inFile = new File(inputPath);
		if (!isFile(inFile)) {
			return;
		}

		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		ZipEntry zipEntry = null;
		ZipFile zipFile = new ZipFile(inFile, "GBK");
		Enumeration<?> entries = zipFile.getEntries();
		try {
			while (entries.hasMoreElements()) {
				zipEntry = (ZipEntry) entries.nextElement();
				if (zipEntry.isDirectory()) {
					String name = zipEntry.getName();
					String outFilePath = outputPath + File.separator + name;
					File outFile = new File(outFilePath);
					if (!isDirectory(outFile))
						outFile.mkdirs();
				} else {
					String outFilePath = outputPath + File.separator
							+ zipEntry.getName();
					File outFile = new File(outFilePath);
					File parentDir = outFile.getParentFile();
					if (!isDirectory(parentDir)) {
						parentDir.mkdirs();
					}
					try {
						out = new BufferedOutputStream(new FileOutputStream(
								outFile, false), BUFFER_SIZE);
						in = new BufferedInputStream(zipFile
								.getInputStream(zipEntry), BUFFER_SIZE);
						byte[] data = new byte[BUFFER_SIZE];
						int n = 0;
						while ((n = in.read(data)) != -1)
							out.write(data, 0, n);
					} finally {
						try {
							if (in != null)
								in.close();
						} finally {
							in = null;
						}
						try {
							if (out != null)
								try {
									out.flush();
								} finally {
									out.close();
								}
						} finally {
							out = null;
						}
					}
				}
			}
		} finally {
			if (zipFile != null) {
				zipFile.close();
				zipFile = null;
			}
		}
	}

}
