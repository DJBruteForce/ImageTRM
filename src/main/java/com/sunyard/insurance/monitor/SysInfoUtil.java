package com.sunyard.insurance.monitor;

import org.hyperic.sigar.OperatingSystem;

/**
 * @Title SysInfoUtil.java
 * @Package com.sunyard.insurance.monitor
 * @Description 系统信息获取工具类
 * @author wuzelin
 * @time 2012-8-10 上午10:18:13
 * @version 1.0
 */
public class SysInfoUtil {

	private static OperatingSystem OS = OperatingSystem.getInstance();

	/**
	 * 
	 *@Description 操作系统名称
	 *@return
	 */
	public static String getOsName() {
		return OS.getName();
	}

	/**
	 * 
	 *@Description 操作系统全称
	 *@return
	 */
	public static String getVendorName() {
		return OS.getVendorName();
	}

	/**
	 *@Description
	 * 
	 *@param args
	 */
	public static void main(String[] args) {
		System.out.println(SysInfoUtil.OS.getVendorName());

	}

}
