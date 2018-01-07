package com.sunyard.insurance.socket.bean;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			//http://blog.csdn.net/xiaochunyong/article/details/7654523
			XStream xstream = new XStream();   
			xstream.alias("ROOT", ServiceCodeBean.class);   
			
			ServiceCodeBean serviceCodeBean = new ServiceCodeBean();
			serviceCodeBean.setSERVICECODE("CheckUser");
			
			//Serializing an object to XML   		
			String xml = xstream.toXML(serviceCodeBean);
			System.out.println(xml);   
			
			File xmlFile = new File("E:/Users/c_wuzelin/Desktop/新Socket传输交易/01用户校验/01CheckUser.xml");
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(xmlFile);
			Element rootNode = document.getRootElement();
			// 获取批次属性
			String SERVICECODE = rootNode.selectSingleNode("SERVICECODE").getText();
			String xml2 = document.asXML();
			System.out.println(xml2+"==="+SERVICECODE);
			
			ServiceCodeBean serviceCodeBean1 = (ServiceCodeBean) xstream.fromXML(xml);
			System.out.println(serviceCodeBean1.getSERVICECODE());
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
