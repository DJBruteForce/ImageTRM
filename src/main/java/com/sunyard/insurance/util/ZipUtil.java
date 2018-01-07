package com.sunyard.insurance.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtil {

	public static boolean unZip(String zipFilePath, String savePath)
			throws IOException {

		ZipEntry zipEntry = null;
		InputStream in = null;
		FileOutputStream out = null;
		boolean b = false;
		ZipFile zipFile = null;

		try {
			zipFile = new ZipFile(zipFilePath, "GBK");
			java.util.Enumeration<?> e = zipFile.getEntries();
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				if (!zipEntry.isDirectory()) {
					String fName = zipEntry.getName();
					File f = new File(savePath + File.separator + fName);
					if ((!f.exists())) {
						f.createNewFile();
					}
					in = zipFile.getInputStream(zipEntry);
					out = new FileOutputStream(f);
					byte[] by = new byte[1024];
					int c;
					while ((c = in.read(by)) != -1) {
						out.write(by, 0, c);
					}
					if (null != out) {
						out.flush();
						out.close();
					}
					if (null != in) {
						in.close();
					}
				}
			}
			b = true;
		} catch (IOException e) {
			b = false;
			throw e;
		} finally {
			try {
				if (null != out) {
					out.flush();
					out.close();
				}
				if (null != in) {
					in.close();
				}
				if (null != zipFile) {
					zipFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return b;
	}

	public static boolean createZipFile(String filePath, String zipFilePath) throws Exception {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		try {
			fos = new FileOutputStream(zipFilePath);
			zos = new ZipOutputStream(fos);
			zos.setEncoding("GBK");
			writeZipFile(new File(filePath), zos, "");
			return true;
		} catch (FileNotFoundException e) {
			throw e;
		} finally {
			try {
				if (zos != null) {
					zos.close();
				}
			} catch (IOException e) {
				throw e;
			}
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				throw e;
			}
		}
	}

	public static void writeZipFile(File f, ZipOutputStream zos, String hiberarchy) throws Exception {
		if (f.exists()) {
			if (f.isDirectory()) {
				hiberarchy += f.getName() + "/";
				File[] fif = f.listFiles();
				for (int i = 0; i < fif.length; i++) {
					writeZipFile(fif[i], zos, hiberarchy);
				}
			} else {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(f);
					ZipEntry ze = new ZipEntry(hiberarchy + f.getName());
					zos.putNextEntry(ze);
					
					int c;
					while ((c = fis.read()) != -1)
		            {
						zos.write(c);
		            }
					
				} catch (FileNotFoundException e) {
					throw e;
				} catch (IOException e) {
					throw e;
				} finally {
					try {
						if (fis != null)
							fis.close();
					} catch (IOException e) {
						throw e;
					}
				}
			}
		}

	}
	
	public static void main(String[] args) {
		try {
			ZipUtil.unZip("E:/49a6aeafb36c3813a107f503afcdcfb7_1/c4da10d12075401185faea141813a68f_5.part1.zip", "E:/49a6aeafb36c3813a107f503afcdcfb7_1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
