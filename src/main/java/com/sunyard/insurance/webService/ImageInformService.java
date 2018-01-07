package com.sunyard.insurance.webService;

import javax.jws.WebService;
import com.sunyard.insurance.entity.TRM_INFORM_RECORD;

/**
 * @Title ImageBusiService.java
 * @Package com.sunyard.insurance.webService
 * @Description 提供影像到达通知服务、
 * @author wuzelin
 * @time 2012-7-31 下午05:50:13
 * @version 1.0
 */

@WebService
public interface ImageInformService {

	/**
	 * 
	 *@Description 影像到达通知记录到表
	 *@param inform_record
	 *@return
	 */
	public boolean imageInformRecord(TRM_INFORM_RECORD inform_record);

}
