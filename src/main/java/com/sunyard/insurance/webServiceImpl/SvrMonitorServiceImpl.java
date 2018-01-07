package com.sunyard.insurance.webServiceImpl;

import javax.jws.WebService;

import org.apache.log4j.Logger;

import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.monitor.CpuUtil;
import com.sunyard.insurance.monitor.FileSysUtil;
import com.sunyard.insurance.monitor.RamUtil;
import com.sunyard.insurance.monitor.SysInfoUtil;
import com.sunyard.insurance.monitor.bean.MonitorInfoBean;
import com.sunyard.insurance.util.DateUtil;
import com.sunyard.insurance.webService.SvrMonitorService;
import com.thoughtworks.xstream.XStream;

@WebService(endpointInterface = "com.sunyard.insurance.webService.SvrMonitorService")
public class SvrMonitorServiceImpl implements SvrMonitorService {
	
	private static final Logger log = Logger.getLogger(SvrMonitorServiceImpl.class);

	public String getSvrResource() {
		MonitorInfoBean bean = new MonitorInfoBean();
		XStream xstream = new XStream();
		System.out.println("========资源监控调用========");
		try {
			bean.setOsName(SysInfoUtil.getVendorName());
			bean.setSvrId(GlobalVar.serverId);
			bean.setSvrName(GlobalVar.svrInfoBean.getSvrName());
			bean.setCpuPerc(CpuUtil.getCpuPerc());
			bean.setDiscPerc(FileSysUtil.getOneFilePerc(GlobalVar.svrInfoBean
					.getRootPath()));
			bean.setRamPerc(RamUtil.getRamPerc());
			bean.setMotTime(DateUtil.getDateTimeStr());
		} catch (Exception e) {
			log.error("获取资源监控信息异常!",e);
		}
		
		return xstream.toXML(bean);
	}

	public String getSvrStatus() {
		System.out.println("========状态监控调用========");
		String str = java.util.UUID.randomUUID().toString();
		if(str.length()>0) {
			return "1";
		} else {
			return "0";
		}
	}

}
