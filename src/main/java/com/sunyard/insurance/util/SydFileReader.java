package com.sunyard.insurance.util;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.dom4j.DocumentException;
import com.sunyard.insurance.cmapi.model.cmquery.Result;
import com.sunyard.insurance.filenet.ce.model.DocBean;
import com.sunyard.insurance.filenet.ce.model.NodeBean;

public interface SydFileReader {
	
	public DocBean readDocBean(File sydFile) throws DocumentException;
	
	public List<NodeBean> readNodeBeans(List<?> nodeList,Map<String, String> nodeMap);
	
	public Result getQueryResult(File batchFile, String basePath) throws Exception;
	
}
