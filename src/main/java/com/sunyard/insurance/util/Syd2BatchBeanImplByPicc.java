package com.sunyard.insurance.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.batch.bean.BatchFileBean;
import com.sunyard.insurance.common.GlobalVar;

public class Syd2BatchBeanImplByPicc implements Syd2BatchBean {
	
	@SuppressWarnings("unchecked")
	public BatchBean getBatchBean(String sydPath) throws DocumentException {
		BatchBean batchBean = new BatchBean();
		File sydFile = new File(sydPath);
		String foldPath = sydFile.getParentFile().getAbsolutePath();

		SAXReader saxReader = new SAXReader();
		Document document = saxReader.read(sydFile);
		Element rootNode = document.getRootElement();
		// 获取批次属性
		String batch_id = rootNode.selectSingleNode("//BATCH_ID").getText();
		String batch_ver = rootNode.selectSingleNode("//BATCH_VER").getText();
		String inter_ver = rootNode.selectSingleNode("//INTER_VER").getText();
		String busi_no = rootNode.selectSingleNode("//BUSI_NO").getText();
		String app_code = rootNode.selectSingleNode("//APP_CODE").getText();
		
		/** 大地多Object Store支持获取SYD文件中的BUSI_DATE信息 */
		if(GlobalVar.isDynamicObjectStoreOpen == 1) {
			String busi_date = "";
			if(rootNode.selectSingleNode("//BUSI_DATE") != null) {
				busi_date = rootNode.selectSingleNode("//BUSI_DATE").getText();
			}
			batchBean.setBusi_date(busi_date);
		}
		//获取批次所属缓存机构代码，现在的PICC版本中SYD版本中没有该字段
		if(null!=rootNode.selectSingleNode("//BIZ_ORG")) {
			String bizOrg = rootNode.selectSingleNode("//BIZ_ORG").getText();
			batchBean.setBiz_org(bizOrg);
		}
		String create_user = rootNode.selectSingleNode("//DocInfo/CREATE_USER")
				.getText();
		String mod_user = rootNode.selectSingleNode("//DocInfo/MOD_USER")
				.getText();
		batchBean.setBatch_id(batch_id);
		batchBean.setBatch_ver(batch_ver);
		batchBean.setInter_ver(inter_ver);
		batchBean.setBusi_no(busi_no);
		batchBean.setAppCode(app_code);
		batchBean.setCreate_user(create_user);
		batchBean.setMod_user(mod_user);
		// 获取扩展属性
		Map<String, String> proMap = new HashMap<String, String>();
		List<Element> pros = rootNode.selectNodes("Property/Extend");
		for (int k = 0; k < pros.size(); k++) {
			String keyStr = pros.get(k).attributeValue("def_code").trim();
			String valueStr = pros.get(k).getTextTrim();
			proMap.put(keyStr, valueStr);
		}
		batchBean.setProMap(proMap);
		// 装入文件，第一个装入SYD文件
		BatchFileBean fileSydBean = new BatchFileBean();
		fileSydBean.setFileName(sydFile.getName());
		fileSydBean.setFileFullPath(sydFile.getAbsolutePath());
		batchBean.getBatchFileList().add(fileSydBean);
		// 获取当前版本所有文件
		List<Node> files = rootNode.selectNodes("//PAGE[PAGE_VER=" + batch_ver
				+ "]");

		for (int i = 0; i < files.size(); i++) {
			BatchFileBean fileB = new BatchFileBean();
			BatchFileBean fileB2 = new BatchFileBean();
			// 原图文件
			String fName = files.get(i).selectSingleNode("PAGE_URL").getText()
					.trim();
			String fileFullPath = foldPath + File.separator + fName;
			fileB.setFileName(fName);
			fileB.setFileFullPath(fileFullPath);
			File f1 = new File(fileFullPath);
			if (f1.exists()) {
				// 文件是否真实存在
				batchBean.getBatchFileList().add(fileB);
			}

			// 缩略图文件
			fName = files.get(i).selectSingleNode("THUM_URL").getText().trim();
			if (!fName.equals("")) {
				fileB2.setFileName(fName);
				String thumFullPath = foldPath + File.separator + fName;
				fileB2.setFileFullPath(thumFullPath);
				File f2 = new File(thumFullPath);
				if (f2.exists()) {
					// 文件是否真实存在
					batchBean.getBatchFileList().add(fileB2);
				}
			}
		}
		return batchBean;
	}

	public String getSydFileFullPath(String filePath) {
		File batchDir = new File(filePath);
		String[] fileList = batchDir.list();
		String maxSydName = "";
		for (int i = 0; i < fileList.length; i++) {
			File temFile = new File(fileList[i]);
			if (temFile.getName().toLowerCase().endsWith(".syd")) {
				if("".equals(maxSydName)) {
					maxSydName = temFile.getName();
				} else {
					if(temFile.getName().toUpperCase().compareTo(maxSydName.toUpperCase())>0) {
						maxSydName = temFile.getName();
					}
				}
			}
		}
		return maxSydName;
	}
	
	public static void main(String[] args) {
		try {
			BatchBean b = new Syd2BatchBeanImplByPicc().getBatchBean("D:/987dddaa59fc38e293009fdba2927db4_2.syd");
			b.getProMap();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
