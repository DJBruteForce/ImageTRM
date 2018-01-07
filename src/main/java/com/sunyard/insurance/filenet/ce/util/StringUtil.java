package com.sunyard.insurance.filenet.ce.util;

import java.io.File;
import com.sunyard.insurance.common.GlobalVar;
import com.sunyard.insurance.util.DateUtil;

public class StringUtil {

	public static final String MSG_GET_MESSAGE_FAILDED = "Get message failded, message={x}, i={x}, pos={x}, replace={x}.";
	
	public static String getMessage(String message, String mark, Object... args) {
		StringBuilder msg = new StringBuilder();
		msg.append(message);
		if (args != null && args.length > 0) {
			for (int i = 0, pos = 0; i < args.length; i++) {
				String str = String.valueOf(args[i]);
				int start = msg.indexOf(mark, pos);
				int end = start + mark.length();
				if (start == -1 || end >= msg.length()) {
					throw new RuntimeException(getMessage(
							MSG_GET_MESSAGE_FAILDED, "{x}", message, i, pos, str));
				} else {
					msg.replace(start, end, str);
					pos = start + str.length();
				}
			}
		}
		return msg.toString();
	}
	
	public static boolean isValue(String value) {
		return (value != null && !"".equals(value));
	}
	
	/**
	 *@Description 
	 *CE查询缓存路径,例如:
	 *D:\TRM\CM\query\cache\20120822\5\5\5573c8ca501d4710b0877f5e5361acd6
	 *@param batchID
	 *@return
	 */
	public static String  getQueryCacheFolder(String batchID){
		StringBuilder sb = new StringBuilder();
		sb.append(GlobalVar.tempDir+File.separator+"CM_CACHE").append(File.separator);
		sb.append(DateUtil.getDateStrCompact()).append(File.separator);//年月日时间格式
		sb.append(batchID.substring(0,1)).append(File.separator);//批次号第一位
		sb.append(batchID.substring(1,2)).append(File.separator);//批次号第二位
		sb.append(batchID);
		return sb.toString();
	}
}
