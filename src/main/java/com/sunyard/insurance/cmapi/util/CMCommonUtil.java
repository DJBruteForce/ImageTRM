package com.sunyard.insurance.cmapi.util;

import java.io.File;
import com.sunyard.insurance.common.CMConstant;
import com.sunyard.insurance.util.DateUtil;

/**
 * 
  * @Title CMCommonUtil.java
  * @Package com.sunyard.insurance.cmapi.util
  * @Description CM操作常用工具类
  * @author xxw
  * @time 2012-9-5 下午03:23:03  
  * @version 1.0
 */
public class CMCommonUtil {
	
	/**
	 *@Description 
	 *CM查询缓存路径,例如:
	 *D:\TRM\CM\query\cache\20120822\5\5\5573c8ca501d4710b0877f5e5361acd6
	 *@param batchID
	 *@return
	 */
	public static String  getQueryCacheFolder(String batchID){
		StringBuilder sb = new StringBuilder();
		sb.append(CMConstant.queryCacheFolder).append(File.separator);
		sb.append(DateUtil.getDateStrCompact()).append(File.separator);//年月日时间格式
		sb.append(batchID.substring(0,1)).append(File.separator);//批次号第一位
		sb.append(batchID.substring(1,2)).append(File.separator);//批次号第二位
		sb.append(batchID);
		return sb.toString();
	}


	/**
	 * 冒泡排序----交换排序的一种
	 * 方法：相邻两元素进行比较，如有需要则进行交换，每完成一次循环就将最大元素排在最后（如从小到大排序），下一次循环是将其他的数进行类似操作。
	 * 性能：比较次数O(n^2),n^2/2；交换次数O(n^2),n^2/4
	 * @param data
	 *  要排序的数组
	 * @param sortType
	 *  排序类型
	 * @return
	 */
	public static void bubbleSort(int[] data, String sortType) {
		if (sortType.equals("asc")) { // 正排序，从小排到大
			// 比较的轮数
			for (int i = 1; i < data.length; i++) {
				// 将相邻两个数进行比较，较大的数往后冒泡
				for (int j = 0; j < data.length - i; j++) {
					if (data[j] > data[j + 1]) {
						// 交换相邻两个数
						swap(data, j, j + 1);
					}
				}
			}
		} else if (sortType.equals("desc")) { // 倒排序，从大排到小
			// 比较的轮数
			for (int i = 1; i < data.length; i++) {
				// 将相邻两个数进行比较，较大的数往后冒泡
				for (int j = 0; j < data.length - i; j++) {
					if (data[j] < data[j + 1]) {
						// 交换相邻两个数
						swap(data, j, j + 1);
					}
				}
			}
		} else {
			System.out.println("您输入的排序类型错误！");
		}
		// printArray(data);//输出冒泡排序后的数组值

	}

	/**
	 * 交换数组中指定的两元素的位置
	 * 
	 * @param data
	 * @param x
	 * @param y
	 */
	private static void swap(int[] data, int x, int y) {
		int temp = data[x];
		data[x] = data[y];
		data[y] = temp;
	}

	
	
	
	public static void main(String args[]){
		
	}

}
