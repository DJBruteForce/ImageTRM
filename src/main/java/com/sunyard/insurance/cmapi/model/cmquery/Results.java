package com.sunyard.insurance.cmapi.model.cmquery;

import java.util.List;

/**
 * 
 * @Title Results.java
 * @Package com.sunyard.insurance.cmapi.model.cmquery
 * @Description
 * 
 * @time 2012-8-21 上午09:47:15 @author xxw @version 1.0
 *       -------------------------------------------------------
 */
public class Results {
	String resCode = "";

	List<Result> resultList = null;

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public List<Result> getResultList() {
		return resultList;
	}

	public void setResultList(List<Result> resultList) {
		this.resultList = resultList;
	}
}
