package com.sunyard.insurance.server;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sunyard.insurance.webServiceImpl.ImageBusiServiceImpl;

public class ServiceServlet extends HttpServlet {

	private static final long serialVersionUID = 3742818371293611620L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String methodStr = (String)request.getParameter("methodStr");
		if("batchVerSynchro".equals(methodStr)) {
			String batchId = (String)request.getParameter("batchId");
			int batchVer = Integer.parseInt(request.getParameter("batchVer"));
			String ipStr = (String)request.getParameter("ipStr");
			int port = Integer.parseInt(request.getParameter("port"));
			String rspStr = new ImageBusiServiceImpl().batchVerSynchro(batchId, batchVer, ipStr, port);
			PrintWriter writer = response.getWriter();
			writer.print(rspStr);
			writer.flush();
			writer.close();
			writer = null;
		} else {
			PrintWriter writer = response.getWriter();
			writer.print("404@错误的methodStr");
			writer.flush();
			writer.close();
			writer = null;
		}
		
	}

}
