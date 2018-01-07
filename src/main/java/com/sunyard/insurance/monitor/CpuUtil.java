package com.sunyard.insurance.monitor;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * @Title CpuUtil.java
 * @Package com.sunyard.insurance.monitor
 * @Description CPU资源获取工具类
 * @author wuzelin
 * @time 2012-8-10 上午10:18:35
 * @version 1.0
 */
public class CpuUtil {

	private static Sigar sigar = new Sigar();
	private static final Logger log = Logger.getLogger(CpuUtil.class);

	/**
	 * 
	 *@Description 获取CPU信息对象
	 *@return
	 */
	public static CpuInfo getCpuInfo() {
		try {
			CpuInfo[] cpuArr = sigar.getCpuInfoList();
			return cpuArr[0];
		} catch (SigarException e) {
			log.error("获取CPU信息异常!",e);
			return null;
		}
	}

	/**
	 * 
	 *@Description 获取CPU使用率
	 *@return
	 */
	public static String getCpuPerc() {
		try {
			CpuPerc[] cpuPercList = sigar.getCpuPercList();
			double num = 0;
			DecimalFormat df = new DecimalFormat("0.0");

			for (int i = 0; i < cpuPercList.length; i++) {
				double oneCpu = cpuPercList[i].getCombined();
				num += oneCpu;
			}
			if (num == 0) {
				return 0 + "";
			} else {
				return df.format((num/cpuPercList.length) * 100) + "";
			}
		} catch (SigarException e) {
			log.error("获取CPU使用率异常!",e);
			return "";
		}
	}

	public static void main(String[] args) {
		while(true) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("==" + CpuUtil.getCpuPerc());
		}
	}

}
