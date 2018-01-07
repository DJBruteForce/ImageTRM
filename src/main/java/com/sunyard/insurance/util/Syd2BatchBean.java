package com.sunyard.insurance.util;

import com.sunyard.insurance.batch.bean.BatchBean;

public interface Syd2BatchBean {
	
	//获取文件夹下的syd文件
	public String getSydFileFullPath(String filePath);
	
	//将SYD文件转换为BatchBean对象
	public BatchBean getBatchBean(String sydPath) throws Exception ;
	
}
