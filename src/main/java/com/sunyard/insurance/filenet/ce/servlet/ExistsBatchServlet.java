package com.sunyard.insurance.filenet.ce.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.sunyard.insurance.cmapi.model.AttrInfo;
import com.sunyard.insurance.cmapi.model.cmquery.RootInfo;
import com.sunyard.insurance.cmapi.util.BatchXmlProcess;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.filenet.ce.depend.CeProcessor;

public class ExistsBatchServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(ExistsBatchServlet.class);
	private static final long serialVersionUID = -4469726155799896970L;
	
	private CeProcessor processor = null;
	
	/**
	 * Constructor of the object.
	 */
	public ExistsBatchServlet() {
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
		BatchXmlProcess queryXmlProcess = new BatchXmlProcess();
		// 默认返回XML串
		String resultStr = "<results><resCode>-1</resCode></results>";
		// 请求XML串
		String queryStr = "";
		try{
			// 请求XML串
			queryStr = request.getParameter("queryStr");
			String busiDate = "";
			if(GlobalVar.isDynamicObjectStoreOpen == 1) {
				SAXReader saxReader = new SAXReader();
				Document doc = saxReader.read(new ByteArrayInputStream(new String(queryStr.getBytes("utf-8"), "utf-8").getBytes("utf-8")));
				Element attrs = (Element)doc.selectSingleNode("root/attrs");
				List<Element> child = attrs.elements();
			    for (int i = 0; i < child.size(); i++) {
					Element temp = child.get(i);
					String attrCode = temp.attributeValue("attrCode");
					String attrValue = temp.attributeValue("attrValue");
					if("BUSI_DATE".equals(attrCode)) {
						busiDate = attrValue;
						attrs.remove(temp);
						break;
					}
				}
			    queryStr = doc.asXML();
			}
			log.debug("判断批次是否存在，开始查询：" + queryStr);
			RootInfo rootInfo = queryXmlProcess.readCMQueryXml(queryStr);
			// 业务类型
			String appCode = rootInfo.getAppCode();
			// 扩展属性结合
			List<AttrInfo> attrsList = rootInfo.getAttrs();
			// 判断批次是否存在
			int flag = processor.existsBatch(attrsList, appCode, busiDate);
			resultStr = "<results><resCode>" + flag + "</resCode></results>";
		} catch (Exception e) {
			log.error("判断批次是否存在失败，queryStr:" + queryStr+ "，error：", e);
		}
		PrintWriter writer = response.getWriter();
		writer.print(resultStr);
		writer.flush();
		writer.close();
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
