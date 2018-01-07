package com.sunyard.insurance.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sunyard.insurance.entity.TRM_ANNOTATION;
import com.sunyard.insurance.entity.TRM_ANNOTATION_ID;
import com.sunyard.insurance.webServiceImpl.DBBusiServiceImpl;

public class AnnotationServlet extends HttpServlet {

	private static final long serialVersionUID = -3357010356185603293L;
	private static final Logger loger = Logger.getLogger(AnnotationServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		String actionStr = (String)request.getParameter("action");
		String batchId = (String)request.getParameter("batchId");
		String pageId = (String)request.getParameter("pageId");
		String contentStr = (String)request.getParameter("contentStr");
		if("addAnnotation".equals(actionStr)) {
			TRM_ANNOTATION anno = new TRM_ANNOTATION();
			anno.setId(new TRM_ANNOTATION_ID(batchId,pageId));
			anno.setContent_str(contentStr);
			boolean b = new DBBusiServiceImpl().savePicAnnotation(anno);
			if(b) {
				PrintWriter writer = response.getWriter();
				writer.print("1");
				writer.flush();
				writer.close();
				writer = null;
			} else {
				PrintWriter writer = response.getWriter();
				writer.print("0");
				writer.flush();
				writer.close();
				writer = null;
			}
		} else if("queryAnnotation".equals(actionStr)) {
			TRM_ANNOTATION anno = new DBBusiServiceImpl().queryPicAnnotation(pageId);
			if(null == anno) {
				PrintWriter writer = response.getWriter();
				writer.print("");
				writer.flush();
				writer.close();
				writer = null;
			} else {
				PrintWriter writer = response.getWriter();
				writer.print(anno.getContent_str());
				writer.flush();
				writer.close();
				writer = null;
			}
		} else {
			loger.error("图片批注请求错误，不存在的操作类型!");
		}
		
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

}
