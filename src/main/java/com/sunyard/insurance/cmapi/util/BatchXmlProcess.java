package com.sunyard.insurance.cmapi.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.cmapi.model.AttrInfo;
import com.sunyard.insurance.cmapi.model.ImgXmlInfo;
import com.sunyard.insurance.cmapi.model.cmquery.BatchVer;
import com.sunyard.insurance.cmapi.model.cmquery.InnerVer;
import com.sunyard.insurance.cmapi.model.cmquery.Result;
import com.sunyard.insurance.cmapi.model.cmquery.Results;
import com.sunyard.insurance.cmapi.model.cmquery.RootInfo;
import com.sunyard.insurance.util.ClassUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
/**
 * 
  * @Title BatchXmlProcess.java
  * @Package com.sunyard.insurance.cmapi
  * @Description 
  * 批次XML操作
  * @time 2012-8-8 下午01:59:06  @author xxw
  * @version 1.0
  *-------------------------------------------------------
 */
public class BatchXmlProcess {
	private static final Logger log = Logger.getLogger(BatchXmlProcess.class);
	/**
	 * 
	 *@Description 
	 *解析批次XML文件，获取业务代码、批次号和扩展属性
	 *@param xmlFile
	 *@param batchName
	 *@return
	 *@throws DocumentException
	 *@throws Exception
	 */
	public ImgXmlInfo readImgXml(File xmlFile,String batchName) throws DocumentException,Exception{
		
		BatchBean batchBean = ClassUtil.getSydUtilClass().getBatchBean(xmlFile.getAbsolutePath());
		
		ImgXmlInfo imgXmlInfo = new ImgXmlInfo();
		try{
			// 获取业务代码
			String appCode = batchBean.getAppCode();
			imgXmlInfo.setAppCode(appCode);
			// 获取批次号
			String realBatchID = batchBean.getBatch_id();
			imgXmlInfo.setRealBatchID(realBatchID);
			// 获取业务编号
			String busiNo = batchBean.getBusi_no();
			imgXmlInfo.setBusiNo(busiNo);
			// 获取批次版本号
			int batchVer = Integer.parseInt(batchBean.getBatch_ver());
			imgXmlInfo.setBatchVer(batchVer);
			// 获取内部版本号
			int innerVer = Integer.parseInt(batchBean.getInter_ver());
			imgXmlInfo.setInnerVer(innerVer);
			// 获取扩张属性
			List<AttrInfo> attrList = new ArrayList<AttrInfo>();
			
			for (Iterator<String> iter = batchBean.getProMap().keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				AttrInfo attrInfo = new AttrInfo();
				
		        attrInfo.setAttrCode(key);
				attrInfo.setAttrValue(batchBean.getProMap().get(key));
				attrList.add(attrInfo);
			}
			
			imgXmlInfo.setAttrList(attrList);
		} catch(Exception e){
			log.error("解析批次SYD文件失败，批次号："+batchName+"，Error：",e);
			throw new Exception("解析批次SYD文件失败，批次号："+batchName);
		}
		return imgXmlInfo;
	}
	
	/**
	 * 解析CM请求XML
	 * @param xmlStr
	 * @return
	 * @throws Exception
	 */
	public RootInfo readCMQueryXml(String xmlStr) throws Exception{
		RootInfo root = null;
		XStream sm = null;
		try{
			sm = this.initCMQueryXml();
			//读取xml
			root = (RootInfo)sm.fromXML(xmlStr); 
		 }catch(Exception e){
			 log.error("解析CM请求XML失败！Error：",e);
			 throw new Exception("解析CM请求XML失败");
		 }
		 return root;
	}
	/**
	 * 写CM请求XML
	 * @param xmlStr
	 * @return
	 * @throws Exception
	 */
	public String writeCMQueryXml(RootInfo root) throws Exception{
		String xmlStr = "";
		XStream sm = null;
		try{
			sm = this.initCMQueryXml();
			//写xml
			xmlStr = sm.toXML(root);
		 }catch(Exception e){
			 log.error("写CM请求XML失败！Error：",e);
			 throw new Exception("写CM请求XML失败");
		 }
		 return xmlStr;
	}
	/**
	 * 初始化CM请求XML节点信息
	 * @return
	 * @throws Exception
	 */
	public XStream initCMQueryXml() throws Exception{
		XStream sm = new XStream(new DomDriver());
		try{
			//改变节点显示名称 
			sm.alias("root",RootInfo.class);
			sm.alias("attr",AttrInfo.class);
			//节点属性名映射
			sm.aliasField("attrs", RootInfo.class, "attrs"); 
			//为节点设置属性
			sm.useAttributeFor(AttrInfo.class, "attrCode");
			sm.useAttributeFor(AttrInfo.class, "attrValue");
		 }catch(Exception e){
			 log.error("初始化CM请求XML节点信息失败！Error：",e);
			 throw new Exception("初始化CM请求XML节点信息失败");
		 }
		 return sm;
	}
	/**
	 * 生成CM查询返回信息
	 * @param results
	 * @return
	 * @throws Exception
	 */
	public String resultsToXml(Results results) throws Exception{
		XStream sm = null;
		String resultsStr = "";
		try{
			sm = initCMResultXml();
			//生成XML字符串
			resultsStr = sm.toXML(results); 
		 }catch(Exception e){
			 log.error("生成CM查询返回信息失败！Error：",e);
			 throw new Exception("生成CM查询返回信息失败");
		 }
		 return resultsStr;
	}
	/**
	 * 初始化CM查询返回信息XML
	 * @return  
	 * @throws Exception
	 */
	public XStream initCMResultXml() throws Exception{
		XStream sm = new XStream(new DomDriver());
		try{
			//改变节点显示名称 
			sm.alias("results",Results.class);
			sm.alias("result",Result.class);
			sm.alias("batchVer",BatchVer.class);
			/**********添加*************************/
			sm.alias("innerVer", InnerVer.class);
			sm.useAttributeFor(InnerVer.class, "value");
			/****************end*****************/
			//为节点设置属性
			sm.useAttributeFor(Result.class, "id");
			sm.useAttributeFor(BatchVer.class, "value");
			//去除不要显示的节点
			sm.addImplicitCollection(Results.class, "resultList");
		 }catch(Exception e){
			 log.error("初始化查询CM返回信息XML失败！Error：",e);
			 throw new Exception("初始化查询CM返回信息XML失败！");
		 }
		 return sm;
	}
	
	/**
	 * 封装CM查询的报文
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String plamoCMXML(Map map) {
		Map b = new TreeMap();
		b = map;
		StringBuilder xml = new StringBuilder();
		String appCode = b.get("APP_CODE").toString();
		xml.append("<?xml version='1.0' encoding='UTF-8' ?><root>");
		xml.append("<appCode>" + appCode + "</appCode>");
		xml.append("<attrs>");
		Iterator iter = b.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			if (!"APP_CODE".equals(entry.getKey())) {
				xml.append("<attr attrCode=\"" + entry.getKey() + "\" attrValue=\"" + entry.getValue() + "\"/>");
			}
		}
		xml.append("</attrs>");
		xml.append("</root>");
		return xml.toString();
	}
	
	public static void main(String[] args) {
		BatchXmlProcess process = new BatchXmlProcess();
		try{
				ImgXmlInfo imgXmlInfo = process.readImgXml(new File("C:\\scic\\scanFolder\\CM\\query\\cache\\1567767e03b643f38dcd68e4eee44a07\\1567767e03b643f38dcd68e4eee44a07_4.syd"),"");
				System.out.println(imgXmlInfo.getAppCode()+" "+imgXmlInfo.getRealBatchID());
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
