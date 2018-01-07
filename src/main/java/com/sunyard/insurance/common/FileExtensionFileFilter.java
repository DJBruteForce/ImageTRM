package com.sunyard.insurance.common;

import java.io.File;
import java.io.FileFilter;

/**
 * 
 * @Title FileExtensionFileFilter.java
 * @Package com.sunyard.insurance.common
 * @Description
 * 文件是否存在过滤器
 * @time 2012-8-8 下午03:06:56 @author xxw
 * @version 1.0 -------------------------------------------------------
 */
public class FileExtensionFileFilter implements FileFilter {

	private String extension;

	public FileExtensionFileFilter(String extension) {
		this.extension = extension;
	}

	/**
	 * Pass the File if it has the extension.
	 */
	public boolean accept(File file) {
		// Lowercase the filename for easier comparison
		String lCaseFilename = file.getName().toLowerCase();
		int index = lCaseFilename.lastIndexOf(".");

		return (file.isFile() && (lCaseFilename.substring(index + 1))
				.equalsIgnoreCase(extension)) ? true : false;
	}

}
