package com.sunyard.cm.test;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class CmQuery {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String batchId = "哈哈557402d922f008e2999d020ecdc30292";
		String batchVer = "";
		String appCode = "FK";
		String ip = "127.0.0.1";
		String port = "7002";
		//多网络映射关系
		String url = "http://" + ip + ":" + port;
		
		HttpClient httpClient = null;
		PostMethod postMethod = null;
		try {
			url += "/SunTRM/QueryServlet";
			Document doc = DocumentHelper.createDocument();
			Element rootEle = doc.addElement("root");
			rootEle.addElement("appCode").setText(appCode);
			Element attrEle = rootEle.addElement("attrs").addElement("attr");
			attrEle.addAttribute("attrCode", "BATCH_ID");
			attrEle.addAttribute("attrValue", batchId);
			postMethod = new PostMethod(url);
			postMethod.setParameter("queryStr", doc.asXML());
			System.out.println("===请求==queryStr="+doc.asXML());
			httpClient = new HttpClient();
			int statusCode = httpClient.executeMethod(postMethod);
			
			if (statusCode == HttpStatus.SC_OK) {
			    byte[] bodyData = postMethod.getResponseBody();
			    String resultStr = new String(bodyData, "UTF-8");
			    System.out.println("===服务端返回==="+resultStr);
			    resultStr = resultStr.replaceAll("\n", "").replaceAll("\t", "");
			    if (resultStr != null && !"".equals(resultStr)) {
			    	doc = DocumentHelper.parseText(resultStr);
			    	String resCode = doc.selectSingleNode("results/resCode").getText();
			    	if ("1".equals(resCode)) {
			    		Element resultEle = (Element) doc.selectSingleNode("results/result");
				    	String xmlPath = resultEle.elementText("xmlPath");
				    	String [] xmlPathArray = xmlPath.split("\\?");
				    	String urlPath = xmlPathArray[0];
				    	String filePath = xmlPathArray[1];
				    	String fileName = batchId + "_" +batchVer + ".syd";
//						String date = DateTime.formatDateToStr("yyyyMMdd", new Date());
						String date = "";
						StringBuilder fileUrlBuilder = new StringBuilder();
						fileUrlBuilder.append("date=").append(date);
						fileUrlBuilder.append("&").append(filePath).append("/").append(fileName);
			    	} else {
			    		
			    	}
			    }
			} else {
				throw new Exception("通信失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (postMethod != null) {
			}
			if (httpClient != null) {
			}
		}
		
	}

}
