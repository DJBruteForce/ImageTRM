package com.sunyard.insurance.socket.bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import com.thoughtworks.xstream.XStream;


public class SocketXmlUtil {
	
	private static final Logger loger = Logger.getLogger(SocketXmlUtil.class);
	
	public static String getServiceCode(String xmlStr) throws DocumentException {
		String serviceCode = "";
		try {
			Document document = DocumentHelper.parseText(xmlStr);
			Element rootNode = document.getRootElement();
			// 获取批次属性
			serviceCode = rootNode.selectSingleNode("SERVICECODE").getText();
		} catch (DocumentException e) {
			loger.error("|传输交易|解析xml报文请求|获取SERVICECODE异常!XML:"+xmlStr,e);
			throw new DocumentException("|传输交易|解析xml报文请求|获取SERVICECODE异常!XML:"+xmlStr,e);
		}
		return serviceCode;
	}
	
	public static CheckUserBean getCheckUserBean(String xmlStr) throws Exception {
		CheckUserBean checkUserBean = null;
		try {
			XStream xstream = new XStream(); 
			xstream.alias("ROOT", CheckUserBean.class);
			checkUserBean = (CheckUserBean) xstream.fromXML(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|解析xml报文请求|获取CheckUserBean对象异常!",e);
			throw new Exception("|传输交易|解析xml报文请求|获取CheckUserBean对象异常!",e);
		}
		return checkUserBean;
	}
	
	public static String getCheckUserBeanXML(CheckUserBean checkUserBean) throws Exception {
		String xmlStr = "";
		try {
			XStream xstream = new XStream();   
			xstream.alias("ROOT", CheckUserBean.class); 
			xmlStr = xstream.toXML(checkUserBean);
			xmlStr = formarXmlToLine(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|将CheckUserBean对象转换成报文|CheckUserBean对象转换异常!",e);
			throw new Exception("|传输交易|将CheckUserBean对象转换成报文|CheckUserBean对象转换异常!",e);
		}
		return xmlStr;
	}
	
	public static CheckUserResBean getCheckUserResBean(String xmlStr) throws Exception {
		CheckUserResBean checkUserResBean = null;
		try {
			XStream xstream = new XStream(); 
			xstream.alias("ROOT", CheckUserResBean.class);
			checkUserResBean = (CheckUserResBean) xstream.fromXML(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|解析xml报文请求|获取CheckUserResBean对象异常!",e);
			throw new Exception("|传输交易|解析xml报文请求|获取CheckUserResBean对象异常!",e);
		}
		return checkUserResBean;
	}
	
	public static String getCheckUserResBeanXML(CheckUserResBean checkUserResBean) throws Exception {
		String xmlStr = "";
		try {
			XStream xstream = new XStream();   
			xstream.alias("ROOT", CheckUserResBean.class); 
			xmlStr = xstream.toXML(checkUserResBean);
			xmlStr = formarXmlToLine(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|将CheckUserResBean对象转换成报文|CheckUserResBean对象转换异常!",e);
			throw new Exception("|传输交易|将CheckUserResBean对象转换成报文|CheckUserResBean对象转换异常!",e);
		}
		return xmlStr;
	}
	
	
	public static BatchStartBean getBatchStartBean(String xmlStr) throws Exception {
		BatchStartBean batchStartBean = null;
		try {
			XStream xstream = new XStream(); 
			xstream.alias("ROOT", BatchStartBean.class);
			xstream.alias("METADATA", MetadataBean.class);
			batchStartBean = (BatchStartBean) xstream.fromXML(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|解析xml报文请求|获取BatchStartBean对象异常!",e);
			throw new Exception("|传输交易|解析xml报文请求|获取BatchStartBean对象异常!",e);
		}
		return batchStartBean;
	}
	
	public static String getBatchStartBeanXML(BatchStartBean batchStartBean) throws Exception {
		String xmlStr = "";
		try {
			XStream xstream = new XStream();   
			xstream.alias("ROOT", BatchStartBean.class);
			xstream.alias("METADATA", MetadataBean.class);
			xmlStr = xstream.toXML(batchStartBean);
			xmlStr = formarXmlToLine(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|将BatchStartBean对象转换成报文|BatchStartBean对象转换异常!",e);
			throw new Exception("|传输交易|将BatchStartBean对象转换成报文|BatchStartBean对象转换异常!",e);
		}
		return xmlStr;
	}
	
	
	
	public static BatchStartResBean getBatchStartResBean(String xmlStr) throws Exception {
		BatchStartResBean batchStartResBean = null;
		try {
			XStream xstream = new XStream(); 
			xstream.alias("ROOT", BatchStartResBean.class);
			batchStartResBean = (BatchStartResBean) xstream.fromXML(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|解析xml报文请求|获取BatchStartResBean对象异常!",e);
			throw new Exception("|传输交易|解析xml报文请求|获取BatchStartResBean对象异常!",e);
		}
		return batchStartResBean;
	}
	
	public static String getBatchStartResBeanXML(BatchStartResBean batchStartResBean) throws Exception {
		String xmlStr = "";
		try {
			XStream xstream = new XStream();   
			xstream.alias("ROOT", BatchStartResBean.class);
			xmlStr = xstream.toXML(batchStartResBean);
			xmlStr = formarXmlToLine(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|将BatchStartResBean对象转换成报文|BatchStartResBean对象转换异常!",e);
			throw new Exception("|传输交易|将BatchStartResBean对象转换成报文|BatchStartResBean对象转换异常!",e);
		}
		return xmlStr;
	}
	
	
	public static TranceFileBean getTranceFileBean(String xmlStr) throws Exception {
		TranceFileBean tranceFileBean = null;
		try {
			XStream xstream = new XStream(); 
			xstream.alias("ROOT", TranceFileBean.class);
			tranceFileBean = (TranceFileBean) xstream.fromXML(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|解析xml报文请求|获取TranceFileBean对象异常!",e);
			throw new Exception("|传输交易|解析xml报文请求|获取TranceFileBean对象异常!",e);
		}
		return tranceFileBean;
	}
	
	public static String getTranceFileBeanXML(TranceFileBean tranceFileBean) throws Exception {
		String xmlStr = "";
		try {
			XStream xstream = new XStream();   
			xstream.alias("ROOT", TranceFileBean.class);
			xmlStr = xstream.toXML(tranceFileBean);
			xmlStr = formarXmlToLine(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|将TranceFileBean对象转换成报文|TranceFileBean对象转换异常!",e);
			throw new Exception("|传输交易|将TranceFileBean对象转换成报文|TranceFileBean对象转换异常!",e);
		}
		return xmlStr;
	}
	
	
	public static TranceFileResBean getTranceFileResBean(String xmlStr) throws Exception {
		TranceFileResBean tranceFileResBean = null;
		try {
			XStream xstream = new XStream(); 
			xstream.alias("ROOT", TranceFileResBean.class);
			tranceFileResBean = (TranceFileResBean) xstream.fromXML(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|解析xml报文请求|获取TranceFileResBean对象异常!",e);
			throw new Exception("|传输交易|解析xml报文请求|获取TranceFileResBean对象异常!",e);
		}
		return tranceFileResBean;
	}
	
	public static String getTranceFileResBeanXML(TranceFileResBean tranceFileResBean) throws Exception {
		String xmlStr = "";
		try {
			XStream xstream = new XStream();   
			xstream.alias("ROOT", TranceFileResBean.class);
			xmlStr = xstream.toXML(tranceFileResBean);
			xmlStr = formarXmlToLine(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|将TranceFileResBean对象转换成报文|TranceFileResBean对象转换异常!",e);
			throw new Exception("|传输交易|将TranceFileResBean对象转换成报文|TranceFileResBean对象转换异常!",e);
		}
		return xmlStr;
	}
	
	
	public static FileCheckResBean getFileCheckResBean(String xmlStr) throws Exception {
		FileCheckResBean fileCheckResBean = null;
		try {
			XStream xstream = new XStream(); 
			xstream.alias("ROOT", FileCheckResBean.class);
			fileCheckResBean = (FileCheckResBean) xstream.fromXML(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|解析xml报文请求|获取FileCheckResBean对象异常!",e);
			throw new Exception("|传输交易|解析xml报文请求|获取FileCheckResBean对象异常!",e);
		}
		return fileCheckResBean;
	}
	
	
	public static String getFileCheckBeanXML(FileCheckBean fileCheckBean) throws Exception {
		String xmlStr = "";
		try {
			XStream xstream = new XStream();   
			xstream.alias("ROOT", FileCheckBean.class);
			xmlStr = xstream.toXML(fileCheckBean);
			xmlStr = formarXmlToLine(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|将FileCheckBean对象转换成报文|FileCheckBean对象转换异常!",e);
			throw new Exception("|传输交易|将FileCheckBean对象转换成报文|FileCheckBean对象转换异常!",e);
		}
		return xmlStr;
	}
	
	
	public static FileCheckBean getFileCheckBean(String xmlStr) throws Exception {
		FileCheckBean fileCheckBean = null;
		try {
			XStream xstream = new XStream(); 
			xstream.alias("ROOT", FileCheckBean.class);
			fileCheckBean = (FileCheckBean) xstream.fromXML(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|解析xml报文请求|获取FileCheckBean对象异常!",e);
			throw new Exception("|传输交易|解析xml报文请求|获取FileCheckBean对象异常!",e);
		}
		return fileCheckBean;
	}
	
	public static String getFileCheckResBeanXML(FileCheckResBean fileCheckResBean) throws Exception {
		String xmlStr = "";
		try {
			XStream xstream = new XStream();   
			xstream.alias("ROOT", FileCheckResBean.class);
			xmlStr = xstream.toXML(fileCheckResBean);
			xmlStr = formarXmlToLine(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|将FileCheckResBean对象转换成报文|FileCheckResBean对象转换异常!",e);
			throw new Exception("|传输交易|将FileCheckResBean对象转换成报文|FileCheckResBean对象转换异常!",e);
		}
		return xmlStr;
	}
	
	public static BatchCheckBean getBatchCheckBean(String xmlStr) throws Exception {
		BatchCheckBean batchCheckBean = null;
		try {
			XStream xstream = new XStream(); 
			xstream.alias("ROOT", BatchCheckBean.class);
			xstream.alias("FILEBEAN", FileBean.class);
			batchCheckBean = (BatchCheckBean) xstream.fromXML(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|解析xml报文请求|获取BatchCheckBean对象异常!",e);
			throw new Exception("|传输交易|解析xml报文请求|获取BatchCheckBean对象异常!",e);
		}
		return batchCheckBean;
	}
	
	public static String getBatchCheckBeanXML(BatchCheckBean batchCheckBean) throws Exception {
		String xmlStr = "";
		try {
			XStream xstream = new XStream();   
			xstream.alias("ROOT", BatchCheckBean.class);
			xstream.alias("FILEBEAN", FileBean.class);
//			xstream.omitField(FileBean.class, "CHECKFLAG");
			xmlStr = xstream.toXML(batchCheckBean);
			xmlStr = formarXmlToLine(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|将BatchCheckBean对象转换成报文|BatchCheckBean对象转换异常!",e);
			throw new Exception("|传输交易|将BatchCheckBean对象转换成报文|BatchCheckBean对象转换异常!",e);
		}
		return xmlStr;
	}
	
	public static BatchCheckResBean getBatchCheckResBean(String xmlStr) throws Exception {
		BatchCheckResBean batchCheckResBean = null;
		try {
			XStream xstream = new XStream(); 
			xstream.alias("ROOT", BatchCheckResBean.class);
			xstream.alias("FILEBEAN", FileBean.class);
			batchCheckResBean = (BatchCheckResBean) xstream.fromXML(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|解析xml报文请求|获取BatchCheckResBean对象异常!",e);
			throw new Exception("|传输交易|解析xml报文请求|获取BatchCheckResBean对象异常!",e);
		}
		return batchCheckResBean;
	}
	
	public static String getBatchCheckResBeanXML(BatchCheckResBean batchCheckResBean) throws Exception {
		String xmlStr = "";
		try {
			XStream xstream = new XStream();   
			xstream.alias("ROOT", BatchCheckResBean.class);
			xstream.alias("FILEBEAN", FileBean.class);
			xmlStr = xstream.toXML(batchCheckResBean);
			xmlStr = formarXmlToLine(xmlStr);
		} catch (Exception e) {
			loger.error("|传输交易|将BatchCheckResBean对象转换成报文|BatchCheckResBean对象转换异常!",e);
			throw new Exception("|传输交易|将BatchCheckResBean对象转换成报文|BatchCheckResBean对象转换异常!",e);
		}
		return xmlStr;
	}
	
	
	public static String formarXmlToLine(String xmlStr) throws Exception {
		String xml = "";
		try {
			Pattern p = Pattern.compile("\\s{2,}|\t|\r|\n");
			Matcher m = p.matcher(xmlStr);
			xml = m.replaceAll("");
		} catch (Exception e) {
			loger.error("将格式化的XML转换为一行异常!",e);
			throw new Exception("将格式化的XML转换为一行异常!",e);
		}
		return xml;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
//			File xmlFile = new File("C:/Users/Administrator/Desktop/新Socket传输交易/05批次校验结束/05BATCH_CHECK - 返回.xml");
//			SAXReader saxReader = new SAXReader();
//			Document document = saxReader.read(xmlFile);
//			BatchCheckResBean batchStartBean = SocketXmlUtil.getBatchCheckResBean(document.asXML());
//			System.out.println(batchStartBean.getSERVICECODE());
			
			
			String xml = "<ROOT><SERVICECODE>CHECK_USER</SERVICECODE><RESOBJ>SunTRM</RESOBJ><RESCODE>SUCCESS</RESCODE><RESMSG>1</RESMSG></ROOT>";
			SocketXmlUtil.getCheckUserResBean(xml);
			
			
//			System.out.println(SocketXmlUtil.getBatchCheckResBeanXML(batchStartBean));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
