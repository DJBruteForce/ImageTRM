package com.sunyard.insurance.cmapi.model;
/**
 * 
  * @Title AttrInfo.java
  * @Package com.sunyard.insurance.cmapi.model
  * @Description 
  * 扩展属性model对象
  * @time 2012-8-8 下午02:00:13  @author xxw
  * @version 1.0
  *-------------------------------------------------------
 */
public class AttrInfo {
	//属性编码
	private String attrCode;
	//属性值
	private String attrValue;

	public String getAttrCode() {
		return attrCode;
	}

	public void setAttrCode(String attrCode) {
		this.attrCode = attrCode;
	}

	public String getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(String attrValue) {
		this.attrValue = attrValue;
	}
}
