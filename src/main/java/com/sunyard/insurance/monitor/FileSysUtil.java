package com.sunyard.insurance.monitor;

import java.text.DecimalFormat;
import org.apache.log4j.Logger;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * @Title FileSysUtil.java
 * @Package com.sunyard.insurance.monitor
 * @Description 硬盘资源获取工具类
 * @author wuzelin
 * @time 2012-8-10 上午10:18:52
 * @version 1.0
 */
public class FileSysUtil {

	private static Sigar sigar = new Sigar();
	private static final Logger log = Logger.getLogger(FileSysUtil.class);

	/**
	 *
	 *@Description 获取某磁盘路径资源使用率
	 *@param path
	 *@return
	 */
	public static String getOneFilePerc(String path) {
		FileSystemUsage usage = null;
		
		if(null==path || "".equals(path) || "null".equals(path)) {
			return "";
		}
		
		try {
			usage = sigar.getFileSystemUsage(path);
			DecimalFormat df = new DecimalFormat("0.0");

			double total = usage.getTotal() / 1024 / 1024;// MB
			double used = usage.getUsed() / 1024 / 1024;
			double num = used / total;
			return df.format(num * 100) + "";
		} catch (SigarException e) {
			log.error("获取文件路径[" + path + "]使用率异常!",e);
			return "";
		}
	}

	public static void getFilePerc() {
		try {
			FileSystem fslist[] = sigar.getFileSystemList();
			String dir = System.getProperty("user.home");// 当前用户文件夹路径
			System.out.println("===" + dir);
			for (int i = 0; i < fslist.length; i++) {
				FileSystem fs = fslist[i];
				System.out.println("fs.getDevName() = " + fs.getDevName());
				// System.out.println("fs.getDirName() = " + fs.getDirName());
				// System.out.println("fs.getTypeName() = " + fs.getTypeName());
				System.out.println("fs.getType() = " + fs.getType());
				if (fs.getType() == 2) {
					FileSystemUsage usage = null;
					usage = sigar.getFileSystemUsage("E:/SSH/");
					System.out.println(" Total = " + usage.getTotal() / 1024
							/ 1024 + "GB");
				}
			}
		} catch (SigarException e) {
			e.printStackTrace();
		}
	}

	/**
	 *@Description
	 * 
	 *@param args
	 */
	public static void main(String[] args) {
		String str = FileSysUtil.getOneFilePerc("G:\\IMAGE_FILE");
		System.out.println(str);
	}

}
