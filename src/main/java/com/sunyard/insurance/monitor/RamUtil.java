package com.sunyard.insurance.monitor;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * 
 * @Title RamUtil.java
 * @Package com.sunyard.insurance.monitor
 * @Description 内存资源获取工具类
 * @author wuzelin
 * @time 2012-8-10 上午10:17:51
 * @version 1.0
 */
public class RamUtil {

	private static Sigar sigar = new Sigar();
	private static final Logger log = Logger.getLogger(RamUtil.class);
	private static Mem mem;

	public static String getRamPerc() {
		try {
			mem = sigar.getMem();
			double total = mem.getTotal() / 1024;
			double used = mem.getUsed() / 1024;
			DecimalFormat df = new DecimalFormat("0.0");
			double num = used / total;
			return df.format(num * 100) + "";
		} catch (SigarException e) {
			log.error("获取内存使用率异常!",e);
			return "";
		}

	}

	/**
	 *@Description
	 * 
	 *@param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(RamUtil.getRamPerc());
	}

}
