package com.sunyard.insurance.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import com.sunyard.insurance.cmapi.model.cmquery.BatchVer;
import com.sunyard.insurance.cmapi.model.cmquery.InnerVer;
import com.sunyard.insurance.cmapi.model.cmquery.Result;
import com.sunyard.insurance.common.FileExtensionFileFilter;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.filenet.ce.depend.SydFileBean;
import com.sunyard.insurance.filenet.ce.depend.SydFileComparator;
import com.sunyard.insurance.filenet.ce.model.BatchBean;
import com.sunyard.insurance.filenet.ce.model.DocBean;
import com.sunyard.insurance.filenet.ce.model.NodeBean;
import com.sunyard.insurance.filenet.ce.model.PageBean;
import com.sunyard.insurance.filenet.ce.model.PageExtBean;
import com.sunyard.insurance.filenet.ce.model.PropBean;
import com.sunyard.insurance.filenet.ce.util.XmlUtil;

public class SydFileReaderByPicc implements SydFileReader{
	public SydFileReaderByPicc() {
		super();
	}
	/**
	 * 
	 *@Description 
	 *
	 *@param sydFile
	 *@return
	 *@throws DocumentException
	 */
	public DocBean readDocBean(File sydFile) throws DocumentException {
		Document document = XmlUtil.readXmlDocByFile(sydFile);
		Element root = document.getRootElement();
		Element docInfo = root.element("DocInfo");
		List<?> pageList = root.element("PageInfo").elements("PAGE");
		List<?> nodeList = root.element("VTree").elements("NODE");
		List<?> propList = root.element("Property").elements();
		BatchBean batchBean = null;
		if (docInfo != null) {
			String batchId = XmlUtil.getTrimText(docInfo.element("BATCH_ID"));
			String batchVer = XmlUtil.getTrimText(docInfo.element("BATCH_VER"));
			String interVer = XmlUtil.getTrimText(docInfo.element("INTER_VER"));
			String busiNo = XmlUtil.getTrimText(docInfo.element("BUSI_NO"));
			String busiName = XmlUtil.getTrimText(docInfo.element("BUSI_NAME"));
			String appCode = XmlUtil.getTrimText(docInfo.element("APP_CODE"));
			String status = XmlUtil.getTrimText(docInfo.element("STATUS"));
			String createUser = XmlUtil.getTrimText(docInfo.element("CREATE_USER"));
			String createDate = XmlUtil.getTrimText(docInfo.element("CREATE_DATE"));
			String modUser = XmlUtil.getTrimText(docInfo.element("MOD_USER"));
			String modDate = XmlUtil.getTrimText(docInfo.element("MOD_DATE"));
			batchBean = new BatchBean(batchId, batchVer, interVer, busiNo,
					busiName, appCode, status, createUser, createDate, modUser,modDate);
			/** 大地多Object Store支持获取SYD文件中的BUSI_DATE信息 */
			if(GlobalVar.isDynamicObjectStoreOpen == 1) {
				String busi_date = "";
				if(docInfo.element("BUSI_DATE") != null) {
					busi_date = XmlUtil.getTrimText(docInfo.element("BUSI_DATE"));
				}
				batchBean.setBusi_date(busi_date);
			}
		}
		
		List<PageBean> pageBeanList = null;
		if (pageList != null) {
			pageBeanList = new ArrayList<PageBean>();
			for (int i = 0; i < pageList.size(); i++) {
				Element pageElement = (Element) pageList.get(i);
				List<?> extList = pageElement.element("PAGE_EXT").elements();
				List<PageExtBean> extBeanList = new ArrayList<PageExtBean>();
				for (int j = 0; j < extList.size(); j++) {
					Element extElement = (Element) extList.get(j);
					String id = extElement.attributeValue("ID");
					String name = extElement.attributeValue("NAME");
					String value = extElement.attributeValue("VALUE");
					PageExtBean extBean = new PageExtBean(id, name, value);
					extBeanList.add(extBean);
				}

				String pageId = pageElement.attributeValue("PAGEID");
				String createUser = XmlUtil.getTrimText(pageElement.element("CREATE_USER"));
				String createTime = XmlUtil.getTrimText(pageElement.element("CREATE_TIME"));
				String modifyUser = XmlUtil.getTrimText(pageElement.element("MODIFY_USER"));
				String modifyTime = XmlUtil.getTrimText(pageElement.element("MODIFY_TIME"));
				String pageUrl = XmlUtil.getTrimText(pageElement.element("PAGE_URL"));
				String thumUrl = XmlUtil.getTrimText(pageElement.element("THUM_URL"));
				String isLocal = XmlUtil.getTrimText(pageElement.element("IS_LOCAL"));
				String pageVer = XmlUtil.getTrimText(pageElement.element("PAGE_VER"));
				String pageDesc = XmlUtil.getTrimText(pageElement.element("PAGE_DESC"));
				String uploadOrg = XmlUtil.getTrimText(pageElement.element("UPLOAD_ORG"));
				String pageCrc = XmlUtil.getTrimText(pageElement.element("PAGE_CRC"));
				String pageFormat = XmlUtil.getTrimText(pageElement.element("PAGE_FORMAT"));
				String pageEncrypt = XmlUtil.getTrimText(pageElement.element("PAGE_ENCRYPT"));
				String orginalName = XmlUtil.getTrimText(pageElement.element("ORGINAL_NAME"));

				PageBean pageBean = new PageBean(pageId, createUser,
						createTime, modifyUser, modifyTime, pageUrl, thumUrl,
						isLocal, pageVer, pageDesc, uploadOrg, pageCrc,
						pageFormat, pageEncrypt, orginalName, extBeanList);
				pageBeanList.add(pageBean);
			}
		}

		Map<String, String> nodeMap = new HashMap<String, String>();
		List<NodeBean> nodeBeanList = null;
		if (nodeList != null) {
			nodeBeanList = readNodeBeans(nodeList, nodeMap);
		}

		List<PropBean> propBeanList = null;
		if (propList != null) {
			propBeanList = new ArrayList<PropBean>();
			for (int i = 0; i < propList.size(); i++) {
				Element extElement = (Element) propList.get(i);
				String code = extElement.attributeValue("def_code");
				String value = XmlUtil.getTrimText(extElement);
				PropBean propBean = new PropBean(code, value);
				propBeanList.add(propBean);
			}
		}
		
		DocBean docBean = new DocBean(sydFile.getName(), batchBean,
				pageBeanList, propBeanList, nodeBeanList, nodeMap);
		return docBean;
	}
	/**
	 * 
	 *@Description 
	 *
	 *@param nodeList
	 *@param nodeMap
	 *@return
	 */
	public List<NodeBean> readNodeBeans(List<?> nodeList,
			Map<String, String> nodeMap) {
		List<NodeBean> nodeBeanList = new ArrayList<NodeBean>();
		for (int i = 0; i < nodeList.size(); i++) {
			Element nodeElement = (Element) nodeList.get(i);
			List<?> leafList = nodeElement.elements("LEAF");
			List<?> subNodeList = nodeElement.elements("NODE");
			List<NodeBean> subNodeBeanList = null;
			if (subNodeList != null) {
				subNodeBeanList = readNodeBeans(subNodeList, nodeMap);
			}

			String id = nodeElement.attributeValue("ID");
			String name = nodeElement.attributeValue("NAME");
			List<String> leafBeanList = null;
			if (leafList != null) {
				leafBeanList = new ArrayList<String>();
				for (int j = 0; j < leafList.size(); j++) {
					Element leafElement = (Element) leafList.get(j);
					String pageID = XmlUtil.getTrimText(leafElement);
					leafBeanList.add(pageID);
					nodeMap.put(pageID, id);
				}
			}
			NodeBean nodeBean = new NodeBean(id, name, subNodeBeanList,	leafBeanList);
			nodeBeanList.add(nodeBean);
		}
		return nodeBeanList;
	}
	
	/**
	 * 
	 *@Description 
	 *获取查询结果对象
	 *@param batchFile
	 *@param basePath
	 *@return
	 *@throws Exception
	 */
	public Result getQueryResult(File batchFile, String basePath) throws Exception {
		Result result = new Result();
		String batchPath = batchFile.getAbsolutePath();
		FileFilter sydFilter = new FileExtensionFileFilter("syd");
		File[] files = batchFile.listFiles(sydFilter);
		if (files.length <= 0) {
			return null;
		}
		SydFileBean[] sydFiles = new SydFileBean[files.length];
		for (int i = 0; i < sydFiles.length; i++) {
			sydFiles[i] = new SydFileBean(files[i]);
		}
		Arrays.sort(sydFiles, new SydFileComparator());
		
		List<BatchVer> batchVerList = new ArrayList<BatchVer>();
		for (int n = 0; n < sydFiles.length; n++) {
			BatchVer batchVerInfo = new BatchVer();
			batchVerInfo.setValue(sydFiles[n].getBatchVersion());
			batchVerList.add(batchVerInfo);
		}
		
		Document document = XmlUtil.readXmlDocByFile(sydFiles[0].getFile());
		String innerVersion = XmlUtil.getNodeText(document, "/doc/DocInfo/INTER_VER");
		String batchId = XmlUtil.getNodeText(document, "/doc/DocInfo/BATCH_ID");
		String appCode = XmlUtil.getNodeText(document, "/doc/DocInfo/APP_CODE");
		String busiNo = XmlUtil.getNodeText(document, "/doc/DocInfo/BUSI_NO");
		
	    List<InnerVer> innerVerList = new ArrayList<InnerVer>();
	    InnerVer innerVerInfo = new InnerVer();
	    innerVerInfo.setValue(innerVersion);
	    innerVerList.add(innerVerInfo);
	    
	    result.setInnerVers(innerVerList);
	    result.setBatchID(batchId);
	    result.setAppCode(appCode);
	    result.setBusiNo(busiNo);
		result.setXmlPath(basePath + "servlet/GetImage?filename=" + batchPath);
	    result.setBatchVers(batchVerList);
		
		return result;
	}
	
}
