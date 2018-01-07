package com.sunyard.insurance.util;

import java.util.Date;

import com.sunyard.insurance.batch.bean.BatchBean;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.entity.B_BATCHS_SRC;
import com.sunyard.insurance.entity.B_BATCHS_SRC_ID;
import com.sunyard.insurance.webServiceImpl.DBBusiServiceImpl;

public class PerSrcTable {
	
	public static void perSrcInfo(BatchBean batchBean,int verStr) {
		DBBusiServiceImpl dbService = new DBBusiServiceImpl();
		String batchVer = batchBean.getBatch_ver();
		String innerVer = batchBean.getInter_ver();
		for(int i=1;i<=verStr;i++) {
			//有没有i版本的SRC记录
			B_BATCHS_SRC srcTemp = dbService.findBatchSrc(batchBean.getBatch_id(), GlobalVar.svrInfoBean.getOrgCode(), i);
			if(null == srcTemp) {
				B_BATCHS_SRC_ID id = new B_BATCHS_SRC_ID(batchBean.getBatch_id(),GlobalVar.svrInfoBean.getOrgCode(), i);
				B_BATCHS_SRC srcBean = new B_BATCHS_SRC(id,i);
				srcBean.setInterVer(i);
				srcBean.setAppCode(batchBean.getAppCode());
				srcBean.setIsMigrate("0");
				srcBean.setIsPush("0");
				srcBean.setSrcUrl("");
				srcBean.setStatus(1);// 1-默认批次有效
				srcBean.setResourceType(0);// 存储模式存储在CM
				srcBean.setUpdateDate(new Date());
				srcBean.setUpdateDateStr(DateUtil.getDateTimeStr());
				
				//变更batchBean里面的版本号
				batchBean.setBatch_ver(""+i);
				batchBean.setInter_ver(""+i);
				
				dbService.saveOrUpdateBatchs(batchBean, srcBean);
			}
		}
		//用完了SET回去，不要破坏了数据，外面后续的方法还要使用这个对象。
		batchBean.setBatch_ver(batchVer);
		batchBean.setInter_ver(innerVer);
	}
	
	/**
	 *@Description 
	 *
	 *@param args
	 */
	public static void main(String[] args) {
		
		
		
	}

}
