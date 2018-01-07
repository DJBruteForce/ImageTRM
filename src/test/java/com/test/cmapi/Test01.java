package com.test.cmapi;

import com.Ostermiller.util.Base64;

public class Test01 {
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 参数解码
		String queryString = "ZGF0ZT0yMDE0MDcyOCZmaWxlbmFtZT0vaG9tZS9zZXJ2aWNlLWltYWdlL1RSTUNBQ0hFL3RtcC9DTV9DQUNIRS8yMDE0MDcyOC9mLzgvZjhiZmQ3OGU5NDk5ZjZiNTVkOTA3YmRjNTFlYTUwOWYvZjhiZmQ3OGU5NDk5ZjZiNTVkOTA3YmRjNTFlYTUwOWZfMy5zeWQ=";
		String queryDecode = Base64.decode(queryString);
		String dateStr = queryDecode.split("&")[0].split("=")[1];
		String file_name = queryDecode.split("&")[1].split("=")[1];
		System.out.println("dateStr="+dateStr+"  file_name="+file_name);
	}
	
}
