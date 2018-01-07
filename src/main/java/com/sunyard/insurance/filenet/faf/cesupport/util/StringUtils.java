package com.sunyard.insurance.filenet.faf.cesupport.util;

import java.io.File;

import com.sunyard.insurance.filenet.faf.cesupport.CEConstant;
import com.sunyard.insurance.util.DateUtil;

public class StringUtils {
	/**
	 *@Description 
	 *CE查询缓存路径,例如:
	 *D:\TRM\CM\query\cache\20120822\5\5\5573c8ca501d4710b0877f5e5361acd6
	 *@param batchID
	 *@return
	 */
	public static String  getQueryCacheFolder(String batchID){
		StringBuilder sb = new StringBuilder();
		sb.append(CEConstant.queryCacheFolder).append(File.separator);
		sb.append(DateUtil.getDateStrCompact()).append(File.separator);//年月日时间格式
		sb.append(batchID.substring(0,1)).append(File.separator);//批次号第一位
		sb.append(batchID.substring(1,2)).append(File.separator);//批次号第二位
		sb.append(batchID);
		return sb.toString();
	}
}
