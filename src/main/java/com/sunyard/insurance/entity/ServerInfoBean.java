package com.sunyard.insurance.entity;

import java.io.Serializable;

/**
 * 
 * @Title ServerInfoBean.java
 * @Package com.sunyard.insurance.entity
 * @Description 服务器节点信息类
 * @time 2012-8-6 上午11:08:53 @author xxw
 * @version 1.0 -------------------------------------------------------
 */
public class ServerInfoBean implements Serializable {

	/**
	  * 
	  */
	private static final long serialVersionUID = 4279328427667398368L;
	/**
	 * 节点服务器ID
	 */
	private String svrId;
	/**
	 * 节点名称
	 */
	private String svrName;
	/**
	 * 节点物理ip
	 */
	private String svrIp;
	/**
	 * 机构代码
	 */
	private String orgCode;
	/**
	 * 服务器虚拟Ip
	 */
	private String orgVip;
	/**
	 * 节点等级，[越小越高]
	 */
	private Integer svrLevel;
	/**
	 * http端口
	 */
	private Integer http_port;
	/**
	 * socket端口
	 */
	private Integer socket_port;
	/**
	 * 缓存CM标志
	 */
	private String saveInCM;
	/**
	 * 存储路径
	 */
	private String rootPath;
	/**
	 * 日期类型
	 */
	private String dateType;
	/**
	 * 随机目录
	 */
	private Integer hashRand;
	/**
	 * 存储模式
	 */
	private String saveType;

	public String getSvrId() {
		return svrId;
	}

	public void setSvrId(String svrId) {
		this.svrId = svrId;
	}

	public String getSvrName() {
		return svrName;
	}

	public void setSvrName(String svrName) {
		this.svrName = svrName;
	}

	public String getSvrIp() {
		return svrIp;
	}

	public void setSvrIp(String svrIp) {
		this.svrIp = svrIp;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgVip() {
		return orgVip;
	}

	public void setOrgVip(String orgVip) {
		this.orgVip = orgVip;
	}

	public Integer getSvrLevel() {
		return svrLevel;
	}

	public void setSvrLevel(Integer svrLevel) {
		this.svrLevel = svrLevel;
	}

	public Integer getHttp_port() {
		return http_port;
	}

	public void setHttp_port(Integer httpPort) {
		http_port = httpPort;
	}

	public Integer getSocket_port() {
		return socket_port;
	}

	public void setSocket_port(Integer socketPort) {
		socket_port = socketPort;
	}

	public String getSaveInCM() {
		return saveInCM;
	}

	public void setSaveInCM(String saveInCM) {
		this.saveInCM = saveInCM;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	public Integer getHashRand() {
		return hashRand;
	}

	public void setHashRand(Integer hashRand) {
		this.hashRand = hashRand;
	}

	public String getSaveType() {
		return saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

}
