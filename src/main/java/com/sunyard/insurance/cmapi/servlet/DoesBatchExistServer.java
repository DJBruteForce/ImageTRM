package com.sunyard.insurance.cmapi.servlet;

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

import com.sunyard.insurance.cmapi.CMProcessor;
import com.sunyard.insurance.cmapi.model.AttrInfo;
import com.sunyard.insurance.cmapi.model.cmquery.RootInfo;
import com.sunyard.insurance.cmapi.util.BatchXmlProcess;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.filenet.ce.depend.CeProcessor;
/**
 * 
  * @Title DoesBatchExistServer.java
  * @Package com.sunyard.insurance.cmapi.servlet
  * @Description 
  * @author xxw
  * @time 2012-9-26 上午10:25:02  
  * @version 1.0
 */
public class DoesBatchExistServer extends HttpServlet {

	private static final Logger log = Logger.getLogger(DoesBatchExistServer.class);
	private BatchXmlProcess queryXmlProcess = new BatchXmlProcess();
	 /**
	  * 
	  */
	private static final long serialVersionUID = 3178377288486313514L;
	/**
	 * Constructor of the object.
	 */
	public DoesBatchExistServer() {
		super();
	}
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		
	}
	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 默认返回XML串
		String resultStr = "<results><resCode>-1</resCode></results>";
		// 请求XML串
		String queryStr  = (String)request.getParameter("queryStr");
		log.debug("判断批次是否存在，开始查询：" + queryStr);
		if("FileNetCE".equals(GlobalVar.CMType)){
			try{
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
				RootInfo rootInfo = queryXmlProcess.readCMQueryXml(queryStr);
				String appCode = rootInfo.getAppCode();// 业务类型
				List<AttrInfo> attrsList = rootInfo.getAttrs();// 扩展属性结合
				int flag = new CeProcessor().existsBatch(attrsList, appCode, busiDate);// 判断批次是否存在
				resultStr = "<results><resCode>" + flag + "</resCode></results>";
			} catch (Exception e) {
				log.error("判断批次是否存在失败，queryStr:" + queryStr+ "，error：", e);
			}
		}else if("IBMCM".equals(GlobalVar.CMType)){
			try{
				RootInfo rootInfo = queryXmlProcess.readCMQueryXml(queryStr);
				String appCode = rootInfo.getAppCode();//业务类型
				List<AttrInfo> attrsList = rootInfo.getAttrs();//扩展属性结合
				//判断是否存在险种大类，如果存在则将appCode赋值为险种大类，将险种大类作为项类型
				if(attrsList!=null && attrsList.size()>0){
					for(int i=0;i<attrsList.size();i++){
						if(attrsList.get(i).getAttrCode().equals("INS_TYPE")){
							//如果存在险种大类则作为业务类型
							String insType = attrsList.get(i).getAttrValue();
							if(insType!=null && !insType.equals("")){
								appCode = insType;
							}
							break;
						}
					}
				}
				//判断批次是否存在
				int flag = CMProcessor.doesBatchExist(attrsList,appCode);
				resultStr = "<results><resCode>"+Integer.toString(flag)+"</resCode></results>";
			}catch(Exception ex){
				log.error("判断批次是否存在失败，queryStr:"+queryStr+"，error：",ex);
			}
		}

		    PrintWriter writer = response.getWriter();
			writer.print(resultStr);
			writer.flush();
			writer.close();
			writer = null;
	}

}
