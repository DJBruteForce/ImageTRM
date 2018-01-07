package com.sunyard.insurance.util;

import java.util.Random;

/**
 * 
 * @Title NumberUtil.java
 * @Package com.sunyard.insurance.util
 * @Description 数值工具类
 * @author wuzelin
 * @time 2012-8-1 上午10:54:08
 * @version 1.0
 */
public class NumberUtil {

	/**
	 *@Description 生成一个nextInt以下大于0的随机数
	 *@param nextInt
	 *@return
	 */
	public static int getRandomNum(int nextInt) {
		int num = 0;
		Random ran = new Random();
		while (num == 0) {
			num = ran.nextInt(nextInt);
		}
		return num;
	}

}
