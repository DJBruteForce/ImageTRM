package com.sunyard.insurance.cmapi.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import com.opensymphony.xwork2.ActionSupport;

/**
 * 
 * action基类，所有action可以继承此类。提供基础类方法
 * 
 */
public class BaseAction extends ActionSupport {

	private static final long serialVersionUID = -5345304503310792460L;

	/**
	 * 获得request
	 * 
	 * @return
	 */
	public HttpServletRequest getRequest() {
		return ServletActionContext.getRequest();
	}

	/**
	 * 获得response
	 * 
	 * @return
	 */
	public HttpServletResponse getResponse() {
		return ServletActionContext.getResponse();
	}

	/**
	 * 获得session
	 * 
	 * @return
	 */
	public HttpSession getSession() {
		return getRequest().getSession();
	}

	/**
	 * 获得ServletContext
	 * 
	 * @return
	 */
	public ServletContext getServletContext() {
		return ServletActionContext.getServletContext();
	}

	/**
	 * 获取当前路径
	 * 
	 * @param path
	 * @return
	 */
	public String getRealyPath(String path) {
		return getServletContext().getRealPath(path);
	}

	/**
	 * 
	 * @return PrintWriter
	 */
	public PrintWriter getPrintWriter() {
		PrintWriter pw = null;
		try {
			pw = this.getResponse().getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pw;
	}

}
