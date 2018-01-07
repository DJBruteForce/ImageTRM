package com.sunyard.cm.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OutOfMemoryTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<Integer,Date> map=new HashMap<Integer, Date>();  
        for (int i = 0; i < 600000000; i++) {  
            map.put(i, new Date());  
        }  
	}

}
