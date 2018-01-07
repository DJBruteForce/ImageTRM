package com.sunyard.insurance.cmapi.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import com.sunyard.insurance.cmapi.service.CMProessService;
import com.sunyard.insurance.cmapi.service.impl.CMProessServiceImpl;
import com.sunyard.insurance.cmapi.service.impl.FileNetCEProessServiceImpl;
import com.sunyard.insurance.cmapi.service.impl.FileNetForFAFProcessServiceImpl;
import com.sunyard.insurance.common.GlobalVar;

/**
 * 
  * @Title QueryBatchServer.java
  * @Package com.sunyard.insurance.server
  * @Description 
  * CM对外查询批次服务
  * @time 2012-8-10 下午03:12:25  @author xxw
  * @version 1.0
  *-------------------------------------------------------
 */
public class QueryBatchServer extends HttpServlet {

	private static final long serialVersionUID = -4993138461014780712L;
	private static final Logger log = Logger.getLogger(QueryBatchServer.class);

	public QueryBatchServer() {
		super();
	}
	public void init() throws ServletException {
		
	}
	public void destroy() {
		super.destroy(); 
	}

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CMProessService proService = null;
		if("FileNetCE".equals(GlobalVar.CMType)){
			 proService = new FileNetCEProessServiceImpl();
		}else if("IBMCM".equals(GlobalVar.CMType)){
			 proService = new CMProessServiceImpl();
		}else if("FileNetForFAF".equals(GlobalVar.CMType)){
			 proService = new FileNetForFAFProcessServiceImpl();
		}
		//URL访问路径
		String basePath = request.getScheme() + "://"+ request.getServerName() + ":" + request.getServerPort()+ request.getContextPath() + "/";
		//请求XML串，进行解码，xxw
		String queryStr = (String)request.getParameter("queryStr");
		Integer queryType = 0;
		if(null != request.getParameter("queryType")) {
			queryType = Integer.parseInt(request.getParameter("queryType"));
		}
//		String queryStr = CMCommonUtil.unescape(request.getParameter("queryStr"));
		queryStr = URLDecoder.decode(queryStr);
		log.info("CM查询的xml字符串["+queryStr+"]queryType查询类型(1表示只下载syd):"+queryType);
		String resultStr = proService.queryBatchForECM(queryStr,basePath,"ECM",queryType);
		log.info("CM查询后返回给客户端的报文："+resultStr);
		PrintWriter writer = response.getWriter();
		writer.print(resultStr);
		writer.flush();
		writer.close();
		writer = null;
			
	}
}