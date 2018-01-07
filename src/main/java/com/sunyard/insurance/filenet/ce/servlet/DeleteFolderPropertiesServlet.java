package com.sunyard.insurance.filenet.ce.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

import com.sunyard.insurance.filenet.ce.depend.CeProcessor;

@SuppressWarnings("serial")
public class DeleteFolderPropertiesServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(DeleteFolderPropertiesServlet.class);	
	/**
	 * Constructor of the object.
	 */
	public DeleteFolderPropertiesServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String appCode = request.getParameter("appCode");
		String attrCode = request.getParameter("attrCode");
		try {
			new CeProcessor().deleteFolderProperties(appCode, attrCode);
			response.setStatus(HttpStatus.SC_OK);
		} catch (Exception e) {
			log.error("删除文件夹类属性失败！Error：", e);
			response.setStatus(HttpStatus.SC_EXPECTATION_FAILED);
		}
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
