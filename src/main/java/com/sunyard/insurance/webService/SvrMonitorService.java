package com.sunyard.insurance.webService;

import javax.jws.WebService;

@WebService
public interface SvrMonitorService {

	/**
	 * 
	 *@Description 获取服务器资源使用信息
	 *@return
	 */
	public String getSvrResource();

	/**
	 * 
	 *@Description 获取服务器状态信息
	 *@return
	 */
	public String getSvrStatus();

}
